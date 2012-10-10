package com.ekito.novela2012;


import x.lib.Debug;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
// for level below 11

public class LocationTracker extends Service {
	private NotificationManager mNM;
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private final int NOTIFICATION = R.string.local_service_started;
	private long last_time;
	private LocationManager mgr;
	private Locationer gps_locationer, network_locationer;
	private NotificationCompat.Builder builder;
	private gpsStatusListener gpslistener;

	private static final String DEBUG_TAG = "MyService";

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}
		@Override
		public void handleMessage(Message msg) {
			
			mgr = (LocationManager) getSystemService(LOCATION_SERVICE);  
			
			gps_locationer = new Locationer(LocationTracker.this);
			network_locationer = new Locationer(LocationTracker.this);

			//gpslistener = new gpsStatusListener();
			//mgr.addGpsStatusListener(gpslistener);
			//mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 5, gps_locationer);
			//mgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 5, gps_locationer);
			//Location lastKnownLocation = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			Criteria criteria = new Criteria();
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW);       

			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			String providerFine = mgr.getBestProvider(criteria, true);

			criteria.setAccuracy(Criteria.ACCURACY_COARSE);
			String providerCoarse = mgr.getBestProvider(criteria, true);

			if (providerCoarse != null) {
				//mgr.requestLocationUpdates(providerCoarse, 2000, 0, network_locationer);
			}
			if (providerFine != null) {
				mgr.requestLocationUpdates(providerFine, 2000, 0, gps_locationer);
			}
		}
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		//	    LocalBroadcastManager.getInstance(this).registerReceiver(
		//	    		gpsStatusReceiver, new IntentFilter("locationer"));
		//	    
		// Display a notification about us starting.  We put an icon in the status bar.
		showNotification();
		// Start up the thread running the service.  Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block.  We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler 
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "local service is started ", Toast.LENGTH_SHORT).show();
		Debug.out("=====tracking is started=====");
		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}


	@Override
	public void onDestroy() {
		// Cancel the persistent notification.
		gps_locationer.cancelPendingRequests();
		network_locationer.cancelPendingRequests();
		mNM.cancel(NOTIFICATION);
		mgr.removeUpdates(gps_locationer);
		mgr.removeUpdates(network_locationer);
		mgr.removeGpsStatusListener(gpslistener);
		// Tell the user we stopped.
		Toast.makeText(this, "local service is stopped", Toast.LENGTH_SHORT).show();
		Debug.out("=====tracking is stopped=====");
	}


	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	/**
	 * Show a notification while this service is running.
	 */
	private void showNotification() {
		// In this sample, we'll use the same text for the ticker and the expanded notification
		CharSequence text = getText(R.string.local_service_started);
		// The PendingIntent to launch our activity if the user selects this notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), 0);

		builder = new NotificationCompat.Builder(getBaseContext())
		.setContentTitle("You are being tracked...")
		.setContentText(text)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentIntent(contentIntent)
		.setOngoing(true);

		Notification notification = builder.getNotification();

		// Send the notification.
		mNM.notify(NOTIFICATION, notification);
	}

	private final BroadcastReceiver gpsStatusReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// get the info from the intent we received
			last_time = intent.getLongExtra("lasttime", 0);
		}
	};

	private class gpsStatusListener implements Listener {

		public gpsStatusListener() {
		}

		@Override
		public void onGpsStatusChanged(int event) {
			boolean isGPSFix = false;

			switch (event) {
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				if(last_time != 0) 	{
					isGPSFix = (SystemClock.elapsedRealtime() - last_time) < 10000;
				}
				if (isGPSFix) { // A fix has been acquired.
					// Do something.
					Debug.out("GPS has a fix");
					Log.d(DEBUG_TAG, "GPS has a fix");
				} else { // The fix has been lost.
					// Do something.
					Debug.out("GPS does not has a fix");
					Log.d(DEBUG_TAG, "GPS does not have a fix");
				}

				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				// Do something.
				isGPSFix = true;
				Debug.out("GPS first fix");
				Log.d(DEBUG_TAG, "GPS first fix");
				break;
			case GpsStatus.GPS_EVENT_STARTED:
				Debug.out("GPS started");
				Log.i("GPS", "Started!");
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				Debug.out("GPS stopped");
				Log.i("GPS", "Stopped");
				break;
			}

		}

	}
}