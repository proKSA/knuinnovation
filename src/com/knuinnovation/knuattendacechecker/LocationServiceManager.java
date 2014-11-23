package com.knuinnovation.knuattendacechecker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LocationServiceManager extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Implement starting of LocationService after boot-complete message.
		// TODO Check app properties if service should start on boot (switchable in settings)
		// note: service should be started with serviceStart(), so it won't destroy if unbound from
		
	}

}
