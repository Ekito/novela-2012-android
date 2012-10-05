package com.ekito.novela2012;

import x.ui.XUIWebView;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
public class MainActivity extends SherlockActivity {

	private APIManager mAPIManager;
	
	private XUIWebView mWebView;
	private Button mStartStopBtn;

	private String mUserId;
	private Float mLat;
	private Float mLon;
	private Boolean mIsStart; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mAPIManager = new APIManager(this);
		mUserId = User.getId(this);
		
		mWebView = (XUIWebView) findViewById(R.id.webview);
		mStartStopBtn = (Button) findViewById(R.id.start_stop);
		
		refreshStartStopBtn();
		
		setupWebview();
	}
	
	private void refreshStartStopBtn() {
		MainApplication app = (MainApplication) getApplication();
		
		mStartStopBtn.setText(app.isTracking()? getString(R.string.stop) : getString(R.string.start));
		
		int icon = app.isTracking()? R.drawable.ic_stop : R.drawable.ic_start;
		mStartStopBtn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(icon), null, null, null);	
	}
	
	private void setupWebview() {

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);

		if (Build.VERSION.SDK_INT >= 11) {
			webSettings.enableSmoothTransition();
		}

		mWebView.loadUrl(mAPIManager.getUserMapURL(mUserId));
	}
	
	@Override
    public void onResume() {
        super.onResume();

        // cancel any notification we may have received from TestBroadcastReceiver
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1234);

        // This demonstrates how to dynamically create a receiver to listen to the location updates.
        // You could also register a receiver in your manifest.
        final IntentFilter lftIntentFilter = new IntentFilter(LocationLibraryConstants.LOCATION_CHANGED_PERIODIC_BROADCAST_ACTION);
        registerReceiver(lftBroadcastReceiver, lftIntentFilter);
   }

    @Override
    public void onPause() {
        super.onResume();
        
        unregisterReceiver(lftBroadcastReceiver);
   }
	
	public void centerMap(View target) {
		// TODO
	}
	
	public void startStopTracking(View target) {
		MainApplication app = (MainApplication) getApplication();
		
		if (app.isTracking())	app.stopTracking();
		else					app.startTracking();
		
		refreshStartStopBtn();
	}
	
	private final BroadcastReceiver lftBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // extract the location info in the broadcast
            final LocationInfo locationInfo = (LocationInfo) intent.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
            
            // TODO send to remote server
        }
    };
}
