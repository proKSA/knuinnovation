package com.knuinnovation.knuattendacechecker;

import org.altbeacon.beacon.Beacon;


public class DetectedBeacon {
	
	private static final String TAG = "DetectedBeacon";

	private Beacon mBeacon;
	private long mDetectTime;
	
	public DetectedBeacon(Beacon beacon, long detectTime) {
		mBeacon = beacon;
		mDetectTime = detectTime;
	}
	
	public Beacon getBeacon() {
		return this.mBeacon;
	}

	public long getDetectTime() {
		return this.mDetectTime;
	}
	
	@Override
	public boolean equals(Object other) {
		if (! (other instanceof DetectedBeacon)) return false;
		
		// Two DetectedBeacons are equal if the Beacons are equal, detect time doesn't matter.
		DetectedBeacon otherBeacon = (DetectedBeacon) other;
		return this.getBeacon().equals(otherBeacon.getBeacon());
	}
	
	@Override
	public int hashCode() {
		return this.getBeacon().hashCode();
	}
}
