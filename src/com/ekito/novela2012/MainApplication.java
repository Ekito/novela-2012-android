package com.ekito.novela2012;

import x.lib.Debug;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

public class MainApplication extends Application {

	// TODO store that in shared prefs
	public Boolean nothingDrawn = true;

    @Override
	public void onCreate(){
        super.onCreate();
        
        Boolean isDebuggable = (0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE));		
        Debug.setDebugMode(isDebuggable);
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
