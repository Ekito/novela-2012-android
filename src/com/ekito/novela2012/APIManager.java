package com.ekito.novela2012;

import x.lib.AsyncHttpClient;
import x.lib.AsyncHttpResponse;
import x.lib.Debug;
import x.type.HttpParams;
import android.content.Context;

public class APIManager {
	
	private static final String API_URL = "http://novela2012.ekito.fr";
	
	private static final String MAP = "/map";
	private static final String USER_MAP = MAP+"/%s";
	private static final String API_SEND_LOCATION = "/location";
	private static final String API_CENTER = "/location/center";
	
	private static final String LAT = "lat";
	private static final String LON = "lon";
	private static final String IS_START = "isStart";
	private static final String USER_ID = "userId";
	private static final String ZOOM = "zoom";
	

	private final Context mContext;
	private AsyncHttpClient client;

	public APIManager(Context context)
	{
		this.mContext = context;
	}

	private AsyncHttpResponse createStandardResponse(final AsyncHttpResponse response)
	{
		return new AsyncHttpResponse()
		{
			@Override public void onBytesProcessed(int amountProcessed, int totalSize)
			{
				if (response == null) return;
				response.setConnectionInfo(getConnectionInfo());
				response.onBytesProcessed(amountProcessed, totalSize);
			}

			@Override public void beforeFinish()
			{
				if (response == null) return;
				response.setConnectionInfo(getConnectionInfo());
				response.beforeFinish();
			}

			@Override public void onBytesProcessed(byte[] chunk, int amountProcessed, int totalSize)
			{
				if (response == null) return;
				response.setConnectionInfo(getConnectionInfo());
				response.onBytesProcessed(chunk, amountProcessed, totalSize);
			}

			@Override public void onFailure(int responseCode, String responseMessage)
			{
				if (response == null) return;
				response.setConnectionInfo(getConnectionInfo());
				response.onFailure(responseCode, responseMessage);
			}

			@Override public void onFinish()
			{
				if (response == null) return;
				response.setConnectionInfo(getConnectionInfo());
				response.onFinish();
			}

			@Override public void onSend()
			{
				if (response == null) return;
				response.setConnectionInfo(getConnectionInfo());
				response.onSend();
			}

			@Override public void onSuccess(byte[] r)
			{
				if (response == null) return;
				response.setConnectionInfo(getConnectionInfo());
				response.onSuccess(r);
			}

			@Override public void onSuccess(Object r)
			{
				if (response == null) return;
				response.setConnectionInfo(getConnectionInfo());
				response.onSuccess(r);
			}

			@Override public void onFailure()
			{
				if (response == null) return;
				response.setConnectionInfo(getConnectionInfo());
				response.onFailure();
			}
		};
	}
	
	public void cancel()
	{
		if (client != null)
		{
			client.cancel();
		}
	}
	
	public String getMapURL()
	{
		return API_URL+MAP;
	}
	
	public String getUserMapURL(String userId)
	{
		return API_URL+String.format(USER_MAP, userId);
	}
	
	public void sendLocation(Double lat, Double lon, Boolean isStart, String userId, AsyncHttpResponse response) {
		HttpParams params = new HttpParams();
		params.addParam(LAT, lat.toString());
		params.addParam(LON, lon.toString());
		params.addParam(IS_START, isStart.toString());
		params.addParam(USER_ID, userId.toString());

		Debug.out("Sending data: lat="+lat.toString()+"\n"+
				"lon="+lon.toString()+"\n"+
				"isStart="+isStart.toString()+"\n"+
				"userId="+userId);
		
		client = new AsyncHttpClient();
		client.post(API_URL + API_SEND_LOCATION, new HttpParams(), params, null, createStandardResponse(response));
	}
	
	public void center(Double lat, Double lon, String userId, Integer zoom, AsyncHttpResponse response) {
		HttpParams params = new HttpParams();
		params.addParam(LAT, lat.toString());
		params.addParam(LON, lon.toString());
		params.addParam(USER_ID, userId.toString());
		params.addParam(ZOOM, zoom.toString());

		Debug.out("Center map: lat="+lat.toString()+"\n"+
				"lon="+lon.toString()+"\n"+
				"userId="+userId+"\n"+
				"zoom="+zoom.toString());
		
		client = new AsyncHttpClient();
		client.post(API_URL + API_CENTER, new HttpParams(), params, null, createStandardResponse(response));
	}
}
