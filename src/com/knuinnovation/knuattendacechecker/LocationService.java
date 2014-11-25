package com.knuinnovation.knuattendacechecker;


import java.util.ArrayList;

import org.altbeacon.beacon.Beacon;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class LocationService extends IntentService {
	public static final String TAG = "LocationService";

	public LocationService() {
		super("LocationService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.v(TAG, "service called");
		
		ArrayList<Beacon> visibleBeacons = new ArrayList<Beacon>();
		visibleBeacons = intent.getParcelableArrayListExtra("visibleBeacons");
		
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.v(TAG, "location service done");
	}
}
