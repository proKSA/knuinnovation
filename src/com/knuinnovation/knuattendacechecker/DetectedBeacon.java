package com.knuinnovation.knuattendacechecker;

import org.altbeacon.beacon.Beacon;

import android.os.SystemClock;

/**
 * Wrapper class to the <code>Beacon</code> class using composition.
 * It's purpose is to allow the storage of the detection timestamp for each beacon.
 * 
 * @author Gábor Proksa
 *
 */
public class DetectedBeacon {
	private static final String TAG = "DetectedBeacon";

	/**
	 * The wrapped Beacon object
	 */
	private Beacon mBeacon;
	
	/**
	 * The timestamp of detection
	 */
	private long mDetectTime;
	
	/**
	 * Constructor
	 * The detection time is measured by the SystemClock.elapsedRealtime()
	 * 
	 * @param beacon The detected Beacon object
	 */
	public DetectedBeacon(Beacon beacon) {
		mBeacon = beacon;
		mDetectTime = SystemClock.elapsedRealtime();
	}
	
	/**
	 * Returns the wrapped <code>Beacon</code> object.
	 * 
	 * @return wrapped beacon
	 */
	public Beacon getBeacon() {
		return this.mBeacon;
	}

	/**
	 * @return timestamp of detection
	 */
	public long getDetectTime() {
		return this.mDetectTime;
	}
	
	/**
	 * Equality only depends on the equality of the wrapped Beacons.
	 * Two Beacons are equal if their identifiers match.
	 */
	@Override
	public boolean equals(Object other) {
		if (! (other instanceof DetectedBeacon)) return false;
		
		// Two DetectedBeacons are equal if the Beacons are equal, detect time doesn't matter.
		DetectedBeacon otherBeacon = (DetectedBeacon) other;
		return this.getBeacon().equals(otherBeacon.getBeacon());
	}
	
	/**
	 * We use the method of the wrapped Beacon object
	 */
	@Override
	public int hashCode() {
		return this.getBeacon().hashCode();
	}
}
