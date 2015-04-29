package com.knuinnovation.knuattendacechecker;

import java.util.ArrayList;
import java.util.Collection;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Main application class of the attendance checker application.
 * The interface BootstrapNotifier provides background beacon monitoring, and the
 * RangeNotifier interface enables the class to receive ranging information.
 * 
 * @author Gábor Proksa
 *
 */
public class KNUAttendanceChecker extends Application implements BootstrapNotifier, RangeNotifier {
	public static final String TAG = "KNUAttendanceChecker";
	
	/**
	 * Region definition that matches all KNU Beacons (proximity id: 24ddf411-8cf1-440c-87cd-e368daf9c93e)
	 * Necessary to only awaken application when KNU Beacons are in range.
	 */
	private Region mAllKnuBeaconsRegion;
	
	/**
	 * Holding this reference enables battery saving.
	 * (see android beacon library documentation)
	 */
	private BackgroundPowerSaver mBackgroundPowerSaver;
	
	/**
	 * This <code>BeaconCache</code> instance will keep track of the detected beacons.
	 * Caching is necessary because sometimes not all beacons in range are detected, and we need at least 3 beacons
	 * to trilaterate our position.
	 */
	private BeaconCache mBeaconCache;
	
	/**
	 * The BeaconManager instance of the application
	 */
	private BeaconManager mBeaconManager;
	
	/**
	 * The RegionBootsrap instance of the application
	 */
	private RegionBootstrap mRegionBootstrap;
	
	private int mInterval = 1000;
	private Handler mHandler;
	
	Runnable mBeaconChecker = new Runnable() {
		public void run() {
			updateUI();
			mHandler.postDelayed(mBeaconChecker, mInterval);
		}
	};

	/**
	 * This method initializes the beacon monitoring and ranging. After call, beacons will be
	 * detected even when the application is in the background or terminated.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		// Enabling battery saving
		mBackgroundPowerSaver = new BackgroundPowerSaver(this);
		
		// Enabling the detection of RECO iBeacons
		mBeaconManager = BeaconManager.getInstanceForApplication(this);
		mBeaconManager.getBeaconParsers().add(new BeaconParser(). setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
		
		// Waking up only when KNU Beacons are detected
		mAllKnuBeaconsRegion = new Region("beacons.knu.all", Identifier.parse("24ddf411-8cf1-440c-87cd-e368daf9c93e"), null, null);
		mRegionBootstrap = new RegionBootstrap(this, mAllKnuBeaconsRegion);
		
		// Set this class to receive Ranging information
		mBeaconManager.setRangeNotifier(this);
		
		// Cache the detected beacons
		mBeaconCache = new BeaconCache(60);
		
		// Reset the User interface
		resetUI();
		
		// Periodic update
		mHandler = new Handler();
		mBeaconChecker.run();
		
		Log.v(TAG, "Started background monitoring");
	}

	@Override
	public void didDetermineStateForRegion(int arg0, Region arg1) {
		// Don't care
		
	}

	/**
	 * This callback is called when the first KNU beacon is detected, we start
	 * ranging then.
	 */
	@Override
	public void didEnterRegion(Region arg0) {
		
		Log.v(TAG, "Entered KNU beacon region");
		
		// Launch activity
		Intent intent = new Intent(this, KNUAttendanceCheckerActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
		
		// Start ranging
		try {
			Log.v(TAG, "Starting ranging");
			mBeaconManager.startRangingBeaconsInRegion(mAllKnuBeaconsRegion);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This callback is called when the last KNU beacon gets out of range, ranging is stopped to save
	 * battery.
	 */
	@Override
	public void didExitRegion(Region arg0) {
		
		Log.v(TAG, "Left KNU beacon region");
		
		// Stop ranging
		try {
			Log.v(TAG, "Stopping ranging");
			mBeaconManager.stopRangingBeaconsInRegion(mAllKnuBeaconsRegion);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method is called every second while ranging is active, returning a collection with the
	 * results of the ranging. These results are put in the beacon cache.
	 * 
	 * After caching if at least 3 beacons are present, we invoke the <code>LocationService</code>
	 */
	@Override
	public void didRangeBeaconsInRegion(Collection<Beacon> arg0, Region arg1) {
		
		Log.v(TAG, "Ranging result recieved");
		
		// Get the ranging result and put it in the cache
		ArrayList<Beacon> rangingResult = new ArrayList<Beacon>(arg0);

		for (Beacon beacon : rangingResult) {
			DetectedBeacon dBeacon = new DetectedBeacon(beacon);
			mBeaconCache.cache(dBeacon);
			
			//Log.v("beacon." + beacon.getId3().toString(), Integer.toString(beacon.getRssi()) + ": " + Double.toString(beacon.getDistance()));
			Log.v("beacon." + beacon.getId3().toString(), Integer.toString(beacon.getRssi()) + ": " + Double.toString(beacon.getDistance()) + ": " + Integer.toString(beacon.getTxPower()));
			
		}
		
		// update UI
		updateUI();
		
		// Invoke the location service if there are at least 3 beacons visible
		if (mBeaconCache.getNumberOfBeacons() >= 3) {
			
		    Intent serviceIntent = new Intent(this, LocationService.class);
		    serviceIntent.putParcelableArrayListExtra("visibleBeacons", mBeaconCache.getCachedBeacons());
		    this.startService(serviceIntent);
		    
		    Log.v(TAG, "Starting location service");
		}
		else {
			resetUI();
		}
		    
	}
	
	public void resetUI() {
		Intent localIntent = new Intent(LocationService.BROADCAST_ACTION);
		localIntent.putExtra(LocationService.POSITION_X, -1.0);
		localIntent.putExtra(LocationService.POSITION_Y, -1.0);
		localIntent.putExtra(LocationService.POSITION_VALID, false);
		
		LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
		
		Log.v(TAG, "UI reset intent sent: (-1.0, -1.0), false");
	}
	public void updateUI() {
		// Update UI with fresh beacon data

		mBeaconCache.pruneCache();

		Intent localIntent = new Intent(LocationService.BROADCAST_BEACON_ACTION);

		for (Beacon b : mBeaconCache.getCachedBeacons()) {
			switch (b.getId3().toInt()) {

			case 0:
				localIntent.putExtra(LocationService.BEACON0_RANGE, b.getDistance());
				break;
			case 1:
				localIntent.putExtra(LocationService.BEACON1_RANGE, b.getDistance());
				break;
			case 2:
				localIntent.putExtra(LocationService.BEACON2_RANGE, b.getDistance());
				break;
			}
		}


		LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
		Log.v(TAG, "Beacon information update intent sent");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		
		// stop periodic update
		mHandler.removeCallbacks(mBeaconChecker);
		
		Log.v(TAG, "Application terminating");
	}

}
