package com.ekito.novela2012;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class MainApplication extends Application {

	// TODO store that in shared prefs
	public Boolean nothingDrawn = true;

    @Override
	public void onCreate(){
        super.onCreate();
    }

	public Boolean isTracking() {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("com.ekito.novela2012.LocationTracker".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	public void stopTracking() {
		nothingDrawn = true;
        // Cancel a previous call to startService().  Note that the
        // service will not actually stop at this point if there are
        // still bound clients.
        stopService(new Intent(this, LocationTracker.class));
	}

	public void startTracking() {
    	Intent i = new Intent(this, LocationTracker.class);
        startService(i);
	}
}
