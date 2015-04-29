package com.knuinnovation.knuattendacechecker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class KNUAttendanceCheckerActivity extends Activity {
	public static final String TAG = "KNUAttendanceCheckerActivity";
	
	private BroadcastReceiver LocationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == LocationService.BROADCAST_ACTION)
            	updateUI_Pos(intent);
            if (intent.getAction() == LocationService.BROADCAST_BEACON_ACTION)
            	updateUI_Beacon(intent);
        }
    };
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knuattendance_checker);

    }


    @Override
	protected void onPause() {
		// Unregister the reciever to ease system load
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(LocationReceiver);
		super.onPause();
	}


	@Override
	protected void onResume() {
		// Registrer the reciever for location broadcasts
		IntentFilter filter = new IntentFilter();
        filter.addAction(LocationService.BROADCAST_ACTION);
        filter.addAction(LocationService.BROADCAST_BEACON_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(LocationReceiver, filter);
		super.onResume();
	}
	
	private void updateUI_Pos(Intent intent) {
		
		// Extract position information from intent
		Double posx = intent.getDoubleExtra(LocationService.POSITION_X, -1.0);
		Double posy = intent.getDoubleExtra(LocationService.POSITION_Y, -1.0);
		boolean posValid = intent.getBooleanExtra(LocationService.POSITION_VALID, false);
		
		
		// Update position information
		String position = "Unknown";
		if (posx != -1.0 && posy != -1.0) {
			position = String.format("(%.2f, %.2f)", posx, posy);
		}
		
		String validText = posValid ? "Inside" : "Outside";
		
		TextView txtPosValue = (TextView) findViewById(R.id.posValue);  	
    	TextView txtPosValid = (TextView) findViewById(R.id.locationValid);
    	txtPosValue.setText(position);
    	txtPosValid.setText(validText);
	}
	
	private void updateUI_Beacon(Intent intent) {
		// Extract range information from intent
		Double beacon0_range = intent.getDoubleExtra(LocationService.BEACON0_RANGE, -1.0);
		Double beacon1_range = intent.getDoubleExtra(LocationService.BEACON1_RANGE, -1.0);
		Double beacon2_range = intent.getDoubleExtra(LocationService.BEACON2_RANGE, -1.0);

		// Update beacon ranges
		TextView txtBeacon0Range = (TextView) findViewById(R.id.beacon0_range);
		TextView txtBeacon0Valid = (TextView) findViewById(R.id.beacon0_visible);
		if (beacon0_range != -1.0) {
			txtBeacon0Range.setText(String.format("%.4f m", beacon0_range));
			txtBeacon0Valid.setText(R.string.inrange);
		}
		else {
			txtBeacon0Range.setText(R.string.unknown);
			txtBeacon0Valid.setText(R.string.outofrange);
		}

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


	/*
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.knuattendance_checker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */
}
