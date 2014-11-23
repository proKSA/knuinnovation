package com.knuinnovation.knuattendacechecker;

import java.util.Collection;

import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOBeaconRegionState;
import com.perples.recosdk.RECOMonitoringListener;
import com.perples.recosdk.RECORangingListener;
import com.perples.recosdk.RECOServiceConnectListener;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class LocationService extends Service implements RECOServiceConnectListener, RECOMonitoringListener, RECORangingListener {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Initialization
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Code that must run on every service start
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();

		super.onDestroy();
	}

	@Override
	public void onServiceConnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void didRangeBeaconsInRegion(Collection<RECOBeacon> arg0,
			RECOBeaconRegion arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void didDetermineStateForRegion(RECOBeaconRegionState arg0,
			RECOBeaconRegion arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void didEnterRegion(RECOBeaconRegion arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void didExitRegion(RECOBeaconRegion arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void didStartMonitoringForRegion(RECOBeaconRegion arg0) {
		// TODO Auto-generated method stub
		
	}

}
