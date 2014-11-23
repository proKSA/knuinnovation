package com.knuinnovation.knuattendacechecker;


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
	}
}
