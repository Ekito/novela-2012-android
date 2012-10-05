package com.ekito.novela2012;

import x.ui.XUIWebView;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

public class MainActivity extends SherlockActivity {
	
	private APIManager mAPIManager;
	private XUIWebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mAPIManager = new APIManager(this);
        
        mWebView = (XUIWebView) findViewById(R.id.webview);
        mWebView.loadUrl(mAPIManager.getMapURL());
    }
}
