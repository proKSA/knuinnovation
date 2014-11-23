package com.knuinnovation.knuattendacechecker;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import android.app.Application;
import android.content.Intent;

public class KNUAttendanceChecker extends Application implements BootstrapNotifier {
	
	public static final String TAG = ".KNUAttendanceChecker";
	
	@SuppressWarnings("unused")
	private BackgroundPowerSaver mBackgroundPowerSaver;
	private BeaconManager mBeaconManager;
	@SuppressWarnings("unused")
	private RegionBootstrap mRegionBootstrap;

	@Override
	public void onCreate() {
		super.onCreate();
		
		// Creating this class and holding a reference enables battery saving (see android beacon library documentation)
		mBackgroundPowerSaver = new BackgroundPowerSaver(this);
		
		// Enabling the detection of RECO iBeacons
		mBeaconManager = BeaconManager.getInstanceForApplication(this);
		mBeaconManager.getBeaconParsers().add(new BeaconParser(). setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
		
		// Waking up only when KNU Beacons are detected
		Region region = new Region("beacons.knu.all", Identifier.parse("24ddf411-8cf1-440c-87cd-e368daf9c93e"), null, null);
		mRegionBootstrap = new RegionBootstrap(this, region);
	}

	@Override
	public void didDetermineStateForRegion(int arg0, Region arg1) {
		// Don't care
		
	}

	@Override
	public void didEnterRegion(Region arg0) {
		// TODO Check if context is appropriate
		Intent serviceIntent = new Intent(this, LocationService.class);
		this.startService(serviceIntent);
	}

	@Override
	public void didExitRegion(Region arg0) {
		// Don't care
		
	}

}
