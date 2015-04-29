package com.knuinnovation.knuattendacechecker;


import java.util.ArrayList;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * IntentService which receives all the currently visible beacons from the cache, looks for 3 beacons
 * which belong together (same major id), retrieves classroom dimension and beacon position information for
 * said beacons, calculates position by trilateration, and finally broadcasting result.
 * 
 * @author Gábor Proksa
 *
 */
public class LocationService extends IntentService {
	public static final String TAG = "LocationService";
	
	/**
	 * Constants for intent broadcasting
	 */
	public static final String BROADCAST_ACTION = "com.knuinnovation.knuattendacechecker.POSITION_BROADCAST";
	public static final String POSITION_X = "com.knuinnovation.knuattendacechecker.POSITION_X";
	public static final String POSITION_Y = "com.knuinnovation.knuattendacechecker.POSITION_Y";
	public static final String POSITION_VALID = "com.knuinnovation.knuattendacechecker.POSITION_VALID";
	public static final String BROADCAST_BEACON_ACTION = "com.knuinnovation.knuattendacechecker.BEACON_BROADCAST";
	public static final String BEACON0_RANGE = "com.knuinnovation.knuattendacechecker.BEACON0_RANGE";
	public static final String BEACON1_RANGE = "com.knuinnovation.knuattendacechecker.BEACON1_RANGE";
	public static final String BEACON2_RANGE = "com.knuinnovation.knuattendacechecker.BEACON2_RANGE";
	
	/**
	 * Collection of currently visible beacons extracted from the invoking intent.
	 */
	private ArrayList<Beacon> mVisibleBeacons;

	public LocationService() {
		super("LocationService");
	}


	@Override
	protected void onHandleIntent(Intent intent) {
		
		int closestBeaconIndex;
		ArrayList<Beacon> classroomBeacons = new ArrayList<Beacon>();
		Identifier classroomMajor;
		
		Log.v(TAG, "Location service called");
		
		// Extract visible beacons from intent
		mVisibleBeacons = intent.getParcelableArrayListExtra("visibleBeacons");
		
		closestBeaconIndex = findClosestBeaconIndex();
		classroomMajor = mVisibleBeacons.get(closestBeaconIndex).getId2();
		
		for (Beacon element : mVisibleBeacons) {
			if (element.getId2().toInt() == classroomMajor.toInt()) classroomBeacons.add(element);
			//Log.v("beacon." + element.getId3().toString(), Integer.toString(element.getRssi()) + ": " + Double.toString(element.getDistance()) + ": " + Integer.toString(element.getTxPower()));
		}
		
		
		//trilaterate here
		if (classroomBeacons.size() >= 3) {
			
			// classroom dimensions (xmin, ymin = 0,0)
			double xmax = 5.0;
			double ymax = 10.0;
			
			// beacon positions can be later obtained from server
			ArrayList<Point> data = new ArrayList<Point>();
			ArrayList<Double> range = new ArrayList<Double>();
			
			for (Beacon b : classroomBeacons) {
				
				switch (b.getId3().toInt()) {
				
				case 0:
					data.add(new Point(0.0, 5.0));
					break;
				case 1:
					data.add(new Point(5.0, 0.0));
					break;
				case 2:
					data.add(new Point(5.0, 5.0));
					break;
				
				}
				
				range.add(b.getDistance());
			}
			
			Point position = Trilaterator.calculatePosition(data, range);
			boolean inside = (position.x > 0 && position.x < xmax && position.y > 0 && position.y < ymax) ? true : false;
			
			//Broadcast the result
			Intent localIntent = new Intent(LocationService.BROADCAST_ACTION);
			localIntent.putExtra(LocationService.POSITION_X, position.x);
			localIntent.putExtra(LocationService.POSITION_Y, position.y);
			localIntent.putExtra(LocationService.POSITION_VALID, inside);
			
			LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
			
			Log.v(TAG, "UI update intent sent: " + position.toString() + " , " + inside);
			
		}
		else {
			
			Log.v(TAG, "Locaiton serveice failed - not enough visible beacons");
			
			Intent localIntent = new Intent(LocationService.BROADCAST_ACTION);
			localIntent.putExtra(LocationService.POSITION_X, -1.0);
			localIntent.putExtra(LocationService.POSITION_Y, -1.0);
			localIntent.putExtra(LocationService.POSITION_VALID, false);
			
			LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
		}
		
	}
	
	private int findClosestBeaconIndex() {
		int maxRssi = 0;
		int closestBeaconIndex = 0;
		
		for (Beacon element : mVisibleBeacons) {
			if (element.getRssi() > maxRssi) {
				closestBeaconIndex = mVisibleBeacons.indexOf(element);
			}
		}
		
		return closestBeaconIndex;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.v(TAG, "Location service done");
	}
}
