package com.ekito.novela2012;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class MyUtility {
	
	public String parseTime(long t) {
		String format = "yyyy-MM-dd-HH-mm-ss";
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
		sdf.setTimeZone(TimeZone.getDefault());
		String gmtTime = sdf.format(t);
		return gmtTime;
	}
}