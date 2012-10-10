package com.ekito.novela2012;

import x.lib.AsyncHttpResponse;
import x.lib.Dimension;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ekito.novela2012.SingleLocationer.LocationResult;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {

	private APIManager mAPIManager;
	private SingleLocationer mLocationer;
	
	private Boolean mIsCentering;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		setBehindRightContentView(R.layout.frag_about_ext);
		
		Dimension dimension = new Dimension(this);
		getSlidingMenu().setBehindWidth(dimension.getPercentageWidthValue(90), SlidingMenu.RIGHT);
		
		ActionBar ab = getSupportActionBar();
		ab.setDisplayShowHomeEnabled(false);
		ab.setDisplayShowCustomEnabled(true);
		ab.setCustomView(R.layout.action_bar);

		mAPIManager = new APIManager(this);
		mLocationer = new SingleLocationer(this);
		mIsCentering = false;
	}
	
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		
		// center btn
		MenuItem centerBtn = menu.findItem(R.id.menu_center);
		centerBtn.setEnabled(!mIsCentering);
		
		// start stop btn
		MainApplication app = (MainApplication) getApplication();
		MenuItem startStopBtn = menu.findItem(R.id.menu_start_stop);
		startStopBtn.setTitle(app.isTracking()? getString(R.string.stop) : getString(R.string.start));
		
		int icon = app.isTracking()? R.drawable.ic_stop : R.drawable.ic_start;
		startStopBtn.setIcon(getResources().getDrawable(icon));
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_center:
			centerMap();
			break;
		case R.id.menu_start_stop:
			startStopTracking();
			break;
		case R.id.menu_about:
			toggle(SlidingMenu.RIGHT);
			break;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}

	
	public void centerMap() {
		LocationResult result = new LocationResult() {
			
			@Override
			public void gotLocation(Location loc) {
				
				AsyncHttpResponse response = new AsyncHttpResponse() {
					@Override
					public void onSuccess(Object response) {
						super.onSuccess(response);
						Toast.makeText(MainActivity.this, "you've been succesfully centered", Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onFailure(int responseCode, String responseMessage) {
						super.onFailure();
						Toast.makeText(MainActivity.this, "an error occured when trying to center the map", Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onFinish() {
						super.onFinish();
						mIsCentering = false;
						invalidateOptionsMenu();
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

		Toast.makeText(this, "centering...", Toast.LENGTH_SHORT).show();
		mIsCentering = true;
		invalidateOptionsMenu();
	}
	
	public void startStopTracking() {
		MainApplication app = (MainApplication) this.getApplication();
		
		if (app.isTracking()) {
			app.stopTracking();
		}
		else {

			final LocationManager mgr = (LocationManager) this.getSystemService(LOCATION_SERVICE);
			if ( !mgr.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
		        buildAlertMessageNoGps();
		    } else {
		    	app.startTracking();
		    }
		}
		
		invalidateOptionsMenu();
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}
}
