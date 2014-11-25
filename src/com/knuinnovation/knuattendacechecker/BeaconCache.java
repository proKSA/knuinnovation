package com.knuinnovation.knuattendacechecker;

import java.util.ArrayList;

import org.altbeacon.beacon.Beacon;

import android.os.SystemClock;

/**
 * The <code>BeaconCache</code> class is a simple cacher that stores the last few detected beacons
 * 
 * @author Gábor Proksa
 *
 */
public class BeaconCache {
	public static final String TAG = "BeaconCache";
	
	/**
	 * The currently cached beacons are wrapped in the <code>DetectedBeacon</code> class
	 * and stored in this ArrayList.
	 */
	private ArrayList<DetectedBeacon> mCachedBeacons;
	
	/**
	 * The number of seconds a beacon has to be undetected for it to get removed
	 * from the cache.
	 */
	private int mMaxUndetectedSeconds;
	
	/**
	 * Basic constructor
	 * 
	 * If no parameters are given, beacons are removed after 10 seconds of
	 * being undetected.
	 */
	public BeaconCache() {
		mCachedBeacons = new ArrayList<DetectedBeacon>();
		mMaxUndetectedSeconds = 10;
	}
	
	/**
	 * Constructor with parameter
	 * 
	 * @param maxUndetectedSeconds The number of seconds before an undetected
	 * beacon gets removed
	 */
	public BeaconCache(int maxUndetectedSeconds) {
		mCachedBeacons = new ArrayList<DetectedBeacon>();
		mMaxUndetectedSeconds = maxUndetectedSeconds;
	}
	
	/**
	 * Method to add a <code>DetectedBeacon</code> to the beacon cache
	 * If the beacon is already present, it gets updated.
	 * Beacons are compared only on the basis of their identifiers.
	 * 
	 * The method also invokes the removal of timed out beacons.
	 * 
	 * @param beacon The beacon to be cached
	 */
	public void cache(DetectedBeacon beacon) {
		if (mCachedBeacons.contains(beacon)) {
			mCachedBeacons.set(mCachedBeacons.indexOf(beacon), beacon);
		} else {
			mCachedBeacons.add(beacon);
		}
		
		// Remove beacons that were cached too long ago
		pruneCache();
	}
	
	/**
	 * This method copies the currently detected <code>Beacon</code> instances
	 * from the cache. Used to pass the list of the beacons in an intent, since
	 * the <code>Beacon</code> class is Parcelable and additional information is not
	 * important.
	 * 
	 * @return ArrayList of currently cached beacons
	 */
	public ArrayList<Beacon> getCachedBeacons() {
		ArrayList<Beacon> beaconList = new ArrayList<Beacon>();
		
		for (DetectedBeacon element : mCachedBeacons) {
			beaconList.add(element.getBeacon()); // Extract the Beacons from the DetectedBeacon list.
		}
		
		return beaconList;
	}

	/**
	 * Returns the number of beacons currently in the cache.
	 * 
	 * @return number of beacons
	 */
	public int getNumberOfBeacons() {
		return mCachedBeacons.size();
	}
	
	/**
	 * This method removes the beacons that have not been detected for a certain time.
	 * This time can be given in seconds when constructing the class, otherwise the default value
	 * of 10 seconds will be used.
	 */
	private void pruneCache() {
		long pruneTime = SystemClock.elapsedRealtime();
		
		for (DetectedBeacon element : mCachedBeacons) {
			if ((element.getDetectTime() - pruneTime) > (mMaxUndetectedSeconds * 1000)) {
				mCachedBeacons.remove(element);
			}
		}
	}
}
