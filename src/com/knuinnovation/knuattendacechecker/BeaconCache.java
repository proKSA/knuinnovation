package com.knuinnovation.knuattendacechecker;

import java.util.ArrayList;

import org.altbeacon.beacon.Beacon;

import android.os.SystemClock;

public class BeaconCache {
	public static final String TAG = "BeaconCache";
	
	private ArrayList<DetectedBeacon> mCachedBeacons;
	private int mPruneInterval;
	
	public BeaconCache() {
		mCachedBeacons = new ArrayList<DetectedBeacon>();
		mPruneInterval = 10;
	}
	
	public BeaconCache(int pruneInterval) {
		mCachedBeacons = new ArrayList<DetectedBeacon>();
		mPruneInterval = pruneInterval;
	}
	
	public void cache(DetectedBeacon beacon) {
		if (mCachedBeacons.contains(beacon)) {
			mCachedBeacons.set(mCachedBeacons.indexOf(beacon), beacon);
		} else {
			mCachedBeacons.add(beacon);
		}
		
		// Remove beacons that were cached too long ago
		pruneCache();
	}
	
	public ArrayList<Beacon> getCachedBeacons() {
		ArrayList<Beacon> beaconList = new ArrayList<Beacon>();
		
		for (DetectedBeacon element : mCachedBeacons) {
			beaconList.add(element.getBeacon());
		}
		
		return beaconList;
	}

	public int getNumberOfBeacons() {
		return mCachedBeacons.size();
	}
	
	private void pruneCache() {
		long pruneTime = SystemClock.elapsedRealtime();
		
		for (DetectedBeacon element : mCachedBeacons) {
			if ((element.getDetectTime() - pruneTime) > (mPruneInterval * 1000)) {
				mCachedBeacons.remove(element);
			}
		}
	}
}
