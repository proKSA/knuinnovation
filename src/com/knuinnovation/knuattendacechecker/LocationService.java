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

	/**
	 * Parameterless constructor
	 */
	public LocationService() {
		super("LocationService");
	}


	/**
	 * This method will be called when the app dispatches the location finding task. After the beacon ranging result is received, and the cache
	 * contains at least 3 beacons, an intent is filled with the data of the visible beacons and this service will be called.
	 * 
	 * This will select the closest beacon from the list, search for the other 2 beacons belonging to the same classroom, and do the trilateration
	 * based on those locations. Finally, a broadcast intent will be sent, which is received by the main activity, so that the calculated position
	 * can be displayed.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		
		// The index of the closest beacon in the list
		int closestBeaconIndex;
		
		// An empty ArrayList, which will be populated with the 3 classroom beacons
		ArrayList<Beacon> classroomBeacons = new ArrayList<Beacon>();
		
		// The identifier which belongs to the classroom of the closest beacon
		Identifier classroomMajor;
		
		Log.v(TAG, "Location service called");
		
		// Extract visible beacons from intent
		mVisibleBeacons = intent.getParcelableArrayListExtra("visibleBeacons");
		
		// find the closest beacon
		closestBeaconIndex = findClosestBeaconIndex();
		
		// The major ID of the closest beacon is the classroom we are most likely in
		classroomMajor = mVisibleBeacons.get(closestBeaconIndex).getId2();
		
		// Pick all beacons from the list of visible beacons that belong to the same classroom
		for (Beacon element : mVisibleBeacons) {
			if (element.getId2().toInt() == classroomMajor.toInt()) classroomBeacons.add(element);
			//Log.v("beacon." + element.getId3().toString(), Integer.toString(element.getRssi()) + ": " + Double.toString(element.getDistance()) + ": " + Integer.toString(element.getTxPower()));
		}
		
		
		// If we see all 3 beacons belonging to one classroom, we can start the trilateration
		if (classroomBeacons.size() >= 3) {
			
			// classroom dimensions (xmin, ymin = 0,0)
			// These values can later be obtained from a database, they are hardcoded for testing now
			double xmax = 5.0;
			double ymax = 10.0;
			
			// The data variable will hold the known coordinates of the beacons
			ArrayList<Point> data = new ArrayList<Point>();
			
			// The range variable will hold the measured ranges fron the beacons extracted from the intent
			ArrayList<Double> range = new ArrayList<Double>();
			
			// beacon positions can be later obtained from server, hardcoded for testing now
			for (Beacon b : classroomBeacons) {
				
				switch (b.getId3().toInt()) {
				
				case 0:
					// Beacon 0 is at (0,5)
					data.add(new Point(0.0, 5.0));
					break;
				case 1:
					// Beacon 1 is at (5,0)
					data.add(new Point(5.0, 0.0));
					break;
				case 2:
					// Beacon 2 is at (5,5)
					data.add(new Point(5.0, 5.0));
					break;
				
				}
				// We add the range from the beacon object to the range list
				range.add(b.getDistance());
			}
			
			// This calls the trilaterator, with the positions and the ranges
			Point position = Trilaterator.calculatePosition(data, range);
			
			// See if the calculated position is inside or outside the classroom dimensions
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
			
			// We did not see enough beacons from the same classroom, so the LocationService must return with a false value
			Log.v(TAG, "Locaiton serveice failed - not enough visible beacons");
			
			Intent localIntent = new Intent(LocationService.BROADCAST_ACTION);
			localIntent.putExtra(LocationService.POSITION_X, -1.0);
			localIntent.putExtra(LocationService.POSITION_Y, -1.0);
			localIntent.putExtra(LocationService.POSITION_VALID, false);
			
			LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
		}
		
	}
	
	/**
	 * This function iterated over the visible beacons and finds the one with the smallest distance
	 * @return Index of the beacon with the smallest distance
	 */
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

	/**
	 * Terminate the location service
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		Log.v(TAG, "Location service done");
	}
}
