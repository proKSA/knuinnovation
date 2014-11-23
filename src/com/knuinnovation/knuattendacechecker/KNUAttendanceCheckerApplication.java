package com.knuinnovation.knuattendacechecker;

import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;

import android.app.Application;

public class KNUAttendanceCheckerApplication extends Application implements BootstrapNotifier {
	
	private BackgroundPowerSaver backgroundPowerSaver;

	@Override
	public void onCreate() {
		super.onCreate();
		
		// Creating this class and holding a reference enables battery saving (see android beacon library documentation)
		backgroundPowerSaver = new BackgroundPowerSaver(this);
	}

	@Override
	public void didDetermineStateForRegion(int arg0, Region arg1) {
		// Don't care
		
	}

	@Override
	public void didEnterRegion(Region arg0) {
		// TODO start locationService (intent service) to check location
		
	}

	@Override
	public void didExitRegion(Region arg0) {
		// Don't care
		
	}

}
