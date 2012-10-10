package com.ekito.novela2012;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class SingleLocationer {
    
	private final Context mContext;

	private Timer mTimer;
	private LocationManager mLocMgr;
	private LocationResult mLocResult;
	private Boolean gps_enabled;
	private Boolean network_enabled;
    
    public SingleLocationer(Context context) {
    	mContext = context;
    	gps_enabled=false;
    	network_enabled=false;
    }

    public boolean getLocation(LocationResult result)
    {
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        mLocResult=result;
        if(mLocMgr==null)
            mLocMgr = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        //exceptions will be thrown if provider is not permitted.
        try{gps_enabled=mLocMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=mLocMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

        //don't start listeners if no provider is enabled
        if(!gps_enabled && !network_enabled)
            return false;

        if(gps_enabled)
            mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        if(network_enabled)
            mLocMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        mTimer=new Timer();
        mTimer.schedule(new GetLastLocation(), 20000);
        return true;
    }

    LocationListener locationListenerGps = new LocationListener() {
        @Override
		public void onLocationChanged(Location location) {
            mTimer.cancel();
            mLocResult.gotLocation(location);
            mLocMgr.removeUpdates(this);
            mLocMgr.removeUpdates(locationListenerNetwork);
        }
        @Override
		public void onProviderDisabled(String provider) {}
        @Override
		public void onProviderEnabled(String provider) {}
        @Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        @Override
		public void onLocationChanged(Location location) {
            mTimer.cancel();
            mLocResult.gotLocation(location);
            mLocMgr.removeUpdates(this);
            mLocMgr.removeUpdates(locationListenerGps);
        }
        @Override
		public void onProviderDisabled(String provider) {}
        @Override
		public void onProviderEnabled(String provider) {}
        @Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
             mLocMgr.removeUpdates(locationListenerGps);
             mLocMgr.removeUpdates(locationListenerNetwork);

             Location net_loc=null, gps_loc=null;
             if(gps_enabled)
                 gps_loc=mLocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
             if(network_enabled)
                 net_loc=mLocMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

             //if there are both values use the latest one
             if(gps_loc!=null && net_loc!=null){
                 if(gps_loc.getTime()>net_loc.getTime())
                     mLocResult.gotLocation(gps_loc);
                 else
                     mLocResult.gotLocation(net_loc);
                 return;
             }

             if(gps_loc!=null){
                 mLocResult.gotLocation(gps_loc);
                 return;
             }
             if(net_loc!=null){
                 mLocResult.gotLocation(net_loc);
                 return;
             }
             mLocResult.gotLocation(null);
        }
    }

    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }
}
