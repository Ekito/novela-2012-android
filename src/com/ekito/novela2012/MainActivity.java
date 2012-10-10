package com.ekito.novela2012;

import x.lib.AsyncHttpResponse;
import x.ui.XUIWebView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.ekito.novela2012.SingleLocationer.LocationResult;

@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
public class MainActivity extends SherlockActivity {

	private APIManager mAPIManager;
	private SingleLocationer mLocationer;
	
	private XUIWebView mWebView;
	private Button mCenterBtn, mStartStopBtn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mAPIManager = new APIManager(this);
		mLocationer = new SingleLocationer(this);

		mCenterBtn = (Button) findViewById(R.id.center);
		mStartStopBtn = (Button) findViewById(R.id.start_stop);
		mWebView = (XUIWebView) findViewById(R.id.webview);
		
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

		String userId = User.getId(this);
		mWebView.loadUrl(mAPIManager.getUserMapURL(userId));
	}
	
	public void centerMap(View target) {
		LocationResult result = new LocationResult() {
			
			@Override
			public void gotLocation(Location loc) {
				
				AsyncHttpResponse response = new AsyncHttpResponse() {
					@Override
					public void onSuccess(Object response) {
						super.onSuccess(response);
						Toast.makeText(MainActivity.this, "you've been succesfully centered", Toast.LENGTH_SHORT).show();
						mCenterBtn.setEnabled(true);
					}
					@Override
					public void onFailure(int responseCode, String responseMessage) {
						super.onFailure();
						Toast.makeText(MainActivity.this, "an error occured when trying to center the map", Toast.LENGTH_SHORT).show();
						mCenterBtn.setEnabled(true);
					}
					@Override
					public void onFinish() {
						super.onFinish();
						mCenterBtn.setEnabled(true);
					}
				};
				
				
				mAPIManager.center(
						loc.getLatitude(), 
						loc.getLongitude(), 
						User.getId(MainActivity.this), 
						0, 
						response);
			}
		};
		mLocationer.getLocation(result);

		Toast.makeText(MainActivity.this, "centering...", Toast.LENGTH_SHORT).show();
		mCenterBtn.setEnabled(false);
	}
	
	public void startStopTracking(View target) {
		MainApplication app = (MainApplication) getApplication();
		
		if (app.isTracking()) {
			app.stopTracking();
		}
		else {

			final LocationManager mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
			if ( !mgr.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
		        buildAlertMessageNoGps();
		    } else {
		    	app.startTracking();
		    }
		}
		
		refreshStartStopBtn();
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}
}
