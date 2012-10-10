package com.ekito.novela2012;

import x.lib.AsyncHttpResponse;
import x.lib.Debug;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

class Locationer implements LocationListener {

	private final APIManager mAPIManager;
	private final Context mContext;

	private Location mPrevLoc;

	public Locationer(Context context) {
		mAPIManager = new APIManager(context);
		mContext = context;
		mPrevLoc = null;
	}

	@Override
	public void onLocationChanged(final Location loc) {

		Debug.out("-------");
		Debug.out(loc);
		Debug.out("-------");

		if (loc == null) {
			return;
		}

		// low precision
		if (!loc.hasAccuracy() || loc.getAccuracy() > 100f) {
			Debug.out("accuracy too high: "+loc.getAccuracy());
			return;
		}

		final Double lat = loc.getLatitude();
		final Double lon = loc.getLongitude();
		final Float accuracy = loc.getAccuracy();
		final Long time = loc.getTime();

		if (mPrevLoc != null) {
			
			final Float speed = mPrevLoc.distanceTo(loc) / ((time - mPrevLoc.getTime()) /1000);
			Debug.out("speed="+speed);

			if (speed > 36) {	// 36 m/s ~= 130 km/h
				Debug.out("too fast to be real, speed="+speed);
				return;
			}

			// same as last one
			if (lat.equals(mPrevLoc.getLatitude()) && lon.equals(mPrevLoc.getLongitude())) {
				Debug.out("same position");
				return;
			}
		}

		mAPIManager.sendLocation(lat, lon, ((MainApplication) mContext.getApplicationContext()).nothingDrawn, User.getId(mContext), 
				new AsyncHttpResponse() {

			@Override
			public void onSend() {
				if (((MainApplication) mContext.getApplicationContext()).nothingDrawn)	
					((MainApplication) mContext.getApplicationContext()).nothingDrawn = false;
				mPrevLoc = loc;
			};

			@Override
			public void onSuccess(Object response) {
				//Toast.makeText(mContext, "location sent "+lat+" "+lon+" "+accuracy+" meters", Toast.LENGTH_SHORT).show();
				Debug.out("data sent");
			};

			@Override
			public void onFailure(int responseCode, String responseMessage) {
				Debug.out("failure with message: "+responseCode+" "+responseMessage);            				
			};
		});
	}
	
	public void cancelPendingRequests() {
		mAPIManager.cancel();
	}

	@Override
	public void onProviderDisabled(String provider) {
		//Toast.makeText(mContext, provider+" disabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		//Toast.makeText(mContext, provider+" enabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		//Toast.makeText(mContext, provider+" status: "+status, Toast.LENGTH_SHORT).show();
	}

}