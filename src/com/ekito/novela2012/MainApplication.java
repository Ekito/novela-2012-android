package com.ekito.novela2012;

import x.lib.Debug;
import android.app.Application;
import android.content.IntentFilter;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;

public class MainApplication extends Application {

	private MainBroadcastReceiver mAppReceiver;
	private Boolean mIsTracking;

	@Override
	public void onCreate() {
		super.onCreate();

		Debug.out("onCreate()");

		mIsTracking = false;
		
		// output debug to LogCat, with tag LittleFluffyLocationLibrary
		LocationLibrary.showDebugOutput(true);

		// in most cases the following initialising code using defaults is probably sufficient:
		//
		// LocationLibrary.initialiseLibrary(getBaseContext());
		//
		// however for the purposes of the test app, we will request unrealistically frequent location broadcasts
		// every 1 minute, and force a location update if there hasn't been one for 2 minutes.

		LocationLibrary.initialiseLibrary(getBaseContext(), 60 * 1000, 2 * 60 * 1000);
		mAppReceiver = new MainBroadcastReceiver();
	}

	public synchronized void startTracking() {
		// start the alarm and listener when the user decides
		LocationLibrary.startAlarmAndListener(getBaseContext());
		IntentFilter filter = new IntentFilter("com.littlefluffytoys.littlefluffylocationlibrary.LOCATION_CHANGED");
		registerReceiver(mAppReceiver, filter);
		
		mIsTracking = true;
	}

	public synchronized void stopTracking() {
		try {
			// stop alarm and listeners, unregister for new location updates
			LocationLibrary.stopAlarmAndListener(getBaseContext());
			unregisterReceiver(mAppReceiver);
			mIsTracking = false;
		} catch(IllegalArgumentException e) {
			Debug.out("The tracker is already stopped");
		} 
	}
	
	public Boolean isTracking() {
		return mIsTracking;
	}
}
