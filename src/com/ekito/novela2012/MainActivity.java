package com.ekito.novela2012;

import x.lib.Dimension;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		setBehindRightContentView(R.layout.frag_about);
		
		Dimension dimension = new Dimension(this);
		getSlidingMenu().setBehindWidth(dimension.getPercentageWidthValue(90), SlidingMenu.RIGHT);
	}
	
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_about:
			toggle(SlidingMenu.RIGHT);
			break;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
