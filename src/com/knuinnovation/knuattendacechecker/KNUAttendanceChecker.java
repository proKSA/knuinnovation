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
import android.os.RemoteException;
import android.util.Log;

public class KNUAttendanceChecker extends Application implements BootstrapNotifier, RangeNotifier {
	
	public static final String TAG = "KNUAttendanceChecker";
	
	@SuppressWarnings("unused")
	private BackgroundPowerSaver mBackgroundPowerSaver;
	private BeaconManager mBeaconManager;
	@SuppressWarnings("unused")
	private RegionBootstrap mRegionBootstrap;
	private Region mAllKnuBeaconsRegion;

	@Override
	public void onCreate() {
		super.onCreate();
		
		// Creating this class and holding a reference enables battery saving (see android beacon library documentation)
		mBackgroundPowerSaver = new BackgroundPowerSaver(this);
		
		// Enabling the detection of RECO iBeacons
		mBeaconManager = BeaconManager.getInstanceForApplication(this);
		mBeaconManager.getBeaconParsers().add(new BeaconParser(). setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
		
		// Waking up only when KNU Beacons are detected
		mAllKnuBeaconsRegion = new Region("beacons.knu.all", Identifier.parse("24ddf411-8cf1-440c-87cd-e368daf9c93e"), null, null);
		mRegionBootstrap = new RegionBootstrap(this, mAllKnuBeaconsRegion);
		
		// Set this class to receive Ranging information
		mBeaconManager.setRangeNotifier(this);
		
		Log.v(TAG, "Started background monitoring");
	}

	@Override
	public void didDetermineStateForRegion(int arg0, Region arg1) {
		// Don't care
		
	}

	@Override
	public void didEnterRegion(Region arg0) {
		
		Log.v(TAG, "Entered KNU beacon region");
		
		// Start ranging
		try {
			Log.v(TAG, "Starting ranging");
			mBeaconManager.startRangingBeaconsInRegion(mAllKnuBeaconsRegion);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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

	@Override
	public void didRangeBeaconsInRegion(Collection<Beacon> arg0, Region arg1) {
		
		Log.v(TAG, "Ranging result recieved");
		
		ArrayList<Beacon> rangingResult = new ArrayList<Beacon>(arg0);
		
		Intent serviceIntent = new Intent(this, LocationService.class);
		serviceIntent.putParcelableArrayListExtra("rangingResult", rangingResult);
		this.startService(serviceIntent);
		    
		Log.v(TAG, "Starting location service");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		
		Log.v(TAG, "Application terminating");
	}

}
