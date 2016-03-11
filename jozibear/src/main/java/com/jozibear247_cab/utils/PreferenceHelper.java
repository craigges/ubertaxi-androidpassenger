package com.jozibear247_cab.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.maps.model.LatLng;

public class PreferenceHelper {
	// prefname
	private final String PREF_NAME = "JOZIBEAR247_USER_PREF";
	private static PreferenceHelper instance = null;
	private Context context;
	private SharedPreferences app_prefs;

	private final String USER_ID = "user_id";
	private final String EMAIL = "email";
	private final String PASSWORD = "password";
	private final String DEVICE_TOKEN = "device_token";
	private final String SESSION_TOKEN = "session_token";
	private final String REQUEST_ID = "request_id";
	private final String REQUEST_TIME = "request_time";
	private final String REQUEST_LATITUDE = "request_latitude";
	private final String REQUEST_LONGITUDE = "request_longitude";
	private final String LOGIN_BY = "login_by";
	private final String SOCIAL_ID = "social_id";
	private final String EMAIL_ACTIVATION = "email_validation";

	private PreferenceHelper() {
	}

	public static synchronized PreferenceHelper getInstance(Context context) {
		if(instance == null) {
			instance = new PreferenceHelper();
		}
		instance.setContext(context);

		return instance;
	}

	private void setContext(Context context) {
		this.context= context;
		app_prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}

	public void putUserId(String userId) {
		Editor edit = app_prefs.edit();
		edit.putString(USER_ID, userId);
		edit.commit();
	}

	public void putEmail(String email) {
		Editor edit = app_prefs.edit();
		edit.putString(EMAIL, email);
		edit.commit();
	}

	public String getEmail() {
		return app_prefs.getString(EMAIL, null);
	}

	public void putPassword(String password) {
		Editor edit = app_prefs.edit();
		edit.putString(PASSWORD, password);
		edit.commit();
	}

	public String getPassword() {
		return app_prefs.getString(PASSWORD, null);
	}

	public void putSocialId(String id) {
		Editor edit = app_prefs.edit();
		edit.putString(SOCIAL_ID, id);
		edit.commit();
	}

	public String getSocialId() {
		return app_prefs.getString(SOCIAL_ID, null);
	}

	public String getUserId() {
		return app_prefs.getString(USER_ID, null);

	}

	public void putDeviceToken(String deviceToken) {
		Editor edit = app_prefs.edit();
		edit.putString(DEVICE_TOKEN, deviceToken);
		edit.commit();
	}

	public String getDeviceToken() {
		return app_prefs.getString(DEVICE_TOKEN, null);

	}

	public void putSessionToken(String sessionToken) {
		Editor edit = app_prefs.edit();
		edit.putString(SESSION_TOKEN, sessionToken);
		edit.commit();
	}

	public String getSessionToken() {
		return app_prefs.getString(SESSION_TOKEN, null);

	}

	public void putRequestId(int requestId) {
		Editor edit = app_prefs.edit();
		edit.putInt(REQUEST_ID, requestId);
		edit.commit();
	}

	public int getRequestId() {
		return app_prefs.getInt(REQUEST_ID, Const.NO_REQUEST);

	}

	public void putLoginBy(String loginBy) {
		Editor edit = app_prefs.edit();
		edit.putString(LOGIN_BY, loginBy);
		edit.commit();
	}

	public String getLoginBy() {
		return app_prefs.getString(LOGIN_BY, Const.MANUAL);
	}

	public void putRequestTime(long time) {
		Editor edit = app_prefs.edit();
		edit.putLong(REQUEST_TIME, time);
		edit.commit();
	}

	public long getRequestTime() {
		return app_prefs.getLong(REQUEST_TIME, Const.NO_TIME);
	}

	public void putRequestLocation(LatLng latLang) {
		Editor edit = app_prefs.edit();
		edit.putString(REQUEST_LATITUDE, String.valueOf(latLang.latitude));
		edit.putString(REQUEST_LONGITUDE, String.valueOf(latLang.longitude));
		edit.commit();
	}

	public LatLng getRequestLocation() {
		LatLng latLng = new LatLng(0.0, 0.0);
		try {
			latLng = new LatLng(Double.parseDouble(app_prefs.getString(
					REQUEST_LATITUDE, "0.0")), Double.parseDouble(app_prefs
					.getString(REQUEST_LONGITUDE, "0.0")));
		} catch (NumberFormatException nfe) {
			latLng = new LatLng(0.0, 0.0);
		}
		return latLng;
	}

	public int getEmailActivation() { return app_prefs.getInt(EMAIL_ACTIVATION, 0); }

	public void putEmailActivation(int flag) {
		Editor edit = app_prefs.edit();
		edit.putInt(EMAIL_ACTIVATION, flag);
		edit.commit();
	}

	public void clearRequestData() {
		putRequestId(Const.NO_REQUEST);
		putRequestTime(Const.NO_TIME);
		putRequestLocation(new LatLng(0.0, 0.0));
//		new DBHelper(context).deleteAllLocations();
	}

	public void Logout() {
		clearRequestData();
//		new DBHelper(context).deleteUser();
		putUserId(null);
		putSessionToken(null);
		putSocialId(null);
		putLoginBy(Const.MANUAL);
	}

}
