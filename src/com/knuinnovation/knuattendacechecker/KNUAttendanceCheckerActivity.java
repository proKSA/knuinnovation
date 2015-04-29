package com.knuinnovation.knuattendacechecker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

/**
 * This is the MainActivity for the application, which displays the current position,
 * whether that position is considered to be inside or outside the classroom borders and
 * range information about the three beacons in the classroom.
 * 
 */
public class KNUAttendanceCheckerActivity extends Activity {
	public static final String TAG = "KNUAttendanceCheckerActivity";
	
	/**
	 * This takes care of receiving and routing the broadcast intents for the activity
	 * The activity can get a <code>LocationService.BROADCAST_ACTION</code>, which is a message
	 * containing position and validity information; or get a <code>LocationService.BROADCAST_BEACON_ACTION</code>, which
	 * contains information regarding the range and visibility of the beacons.
	 */
	private BroadcastReceiver mLocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == LocationService.BROADCAST_ACTION)
            	updateUI_Pos(intent);
            if (intent.getAction() == LocationService.BROADCAST_BEACON_ACTION)
            	updateUI_Beacon(intent);
        }
    };
	
    /**
     * Standard Activity creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knuattendance_checker);
    }


    /**
     * When pausing the application, the broadcast receiver gets unregistered to ease system load
     */
    @Override
	protected void onPause() {
		// Unregister the reciever to ease system load
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(mLocationReceiver);
		super.onPause();
	}


    /**
     * When the app is resumed, the broadcast receivers get re-registered.
     * The intent filters for the two types of intents handled by this activity are defined here.
     */
	@Override
	protected void onResume() {
		// Register the receiver for location broadcasts
		IntentFilter filter = new IntentFilter();
        filter.addAction(LocationService.BROADCAST_ACTION);
        filter.addAction(LocationService.BROADCAST_BEACON_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLocationReceiver, filter);
		super.onResume();
	}
	
	/**
	 * This method updates the position and location part of the main display
	 * @param intent The received broadcast intent
	 */
	private void updateUI_Pos(Intent intent) {
		
		// Extract position information from intent
		Double posx = intent.getDoubleExtra(LocationService.POSITION_X, -1.0);
		Double posy = intent.getDoubleExtra(LocationService.POSITION_Y, -1.0);
		boolean posValid = intent.getBooleanExtra(LocationService.POSITION_VALID, false);
		
		
		// Update position information
		String position = "Unknown";
		// If the intent did not contain position information, or contained the default value, the display will read "Unknown"
		if (posx != -1.0 && posy != -1.0) {
			// Else, the position will be displayed
			position = String.format("(%.2f, %.2f)", posx, posy);
		}
		
		String validText = posValid ? "Inside" : "Outside";
		
		TextView txtPosValue = (TextView) findViewById(R.id.posValue);  	
    	TextView txtPosValid = (TextView) findViewById(R.id.locationValid);
    	txtPosValue.setText(position);
    	txtPosValid.setText(validText);
	}
	
	/**
	 * This method updates the beacon information part of the display.
	 * 
	 * @param intent The received broadcast intent
	 */
	private void updateUI_Beacon(Intent intent) {
		
		// Extract range information from intent
		Double beacon0_range = intent.getDoubleExtra(LocationService.BEACON0_RANGE, -1.0);
		Double beacon1_range = intent.getDoubleExtra(LocationService.BEACON1_RANGE, -1.0);
		Double beacon2_range = intent.getDoubleExtra(LocationService.BEACON2_RANGE, -1.0);

		// Update beacon ranges
		TextView txtBeacon0Range = (TextView) findViewById(R.id.beacon0_range);
		TextView txtBeacon0Valid = (TextView) findViewById(R.id.beacon0_visible);
		if (beacon0_range != -1.0) {
			// if the beacon range is not the default value, display the range and mark the beacon as visible
			txtBeacon0Range.setText(String.format("%.4f m", beacon0_range));
			txtBeacon0Valid.setText(R.string.inrange);
		}
		else {
			// else display "Unknown" range and mark the beacon out of range
			txtBeacon0Range.setText(R.string.unknown);
			txtBeacon0Valid.setText(R.string.outofrange);
		}

		// Above steps are repeated for beacon 1
		TextView txtBeacon1Range = (TextView) findViewById(R.id.beacon1_range);
		TextView txtBeacon1Valid = (TextView) findViewById(R.id.beacon1_visible);
		if (beacon1_range != -1.0) {
			txtBeacon1Range.setText(String.format("%.4f m", beacon1_range));
			txtBeacon1Valid.setText(R.string.inrange);
		}
		else {
			txtBeacon1Range.setText(R.string.unknown);
			txtBeacon1Valid.setText(R.string.outofrange);
		}

		// and finally for beacon 2
		TextView txtBeacon2Range = (TextView) findViewById(R.id.beacon2_range);
		TextView txtBeacon2Valid = (TextView) findViewById(R.id.beacon2_visible);
		if (beacon2_range != -1.0) {
			txtBeacon2Range.setText(String.format("%.4f m", beacon2_range));
			txtBeacon2Valid.setText(R.string.inrange);
		}
		else {
			txtBeacon2Range.setText(R.string.unknown);
			txtBeacon2Valid.setText(R.string.outofrange);
		}
	}
}
