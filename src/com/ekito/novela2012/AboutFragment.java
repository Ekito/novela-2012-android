package com.ekito.novela2012;

import java.util.Locale;

import x.ui.XUIWebView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.actionbarsherlock.app.SherlockFragment;

@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
public class AboutFragment extends SherlockFragment {

	private APIManager mAPIManager;
	
	private XUIWebView mWebView;
	
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.frag_about, container);
		mWebView = (XUIWebView) v.findViewById(R.id.webview);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mAPIManager = new APIManager(getSherlockActivity());
		
		setupWebview();
	}
	
	private void setupWebview() {

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);

		mWebView.setVerticalScrollBarEnabled(true);


		String language = Locale.getDefault().getLanguage();
		mWebView.loadUrl(mAPIManager.getAboutURL(language));
	}
}
