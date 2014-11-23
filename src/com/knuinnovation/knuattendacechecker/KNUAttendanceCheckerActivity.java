package com.knuinnovation.knuattendacechecker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class KNUAttendanceCheckerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knuattendance_checker);
        
        // Temporary button to start the LocationService
        final Button locationStartButton = (Button) findViewById(R.id.button_LocationService_start);
        locationStartButton.setOnClickListener(new View.OnClickListener() {
        	
        	@Override
            public void onClick(View v) {
                Intent intent = new Intent(KNUAttendanceCheckerActivity.this, com.knuinnovation.knuattendacechecker.LocationService.class);
                startService(intent);
            }
        });
        
        // Temporary button to stop the LocationService
        final Button locationStopButton = (Button) findViewById(R.id.button_LocationService_stop);
        locationStopButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stopService(new Intent(getApplicationContext(), com.knuinnovation.knuattendacechecker.LocationService.class));
			}
		});

    }


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
}
