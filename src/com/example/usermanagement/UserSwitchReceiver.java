package com.example.usermanagement;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.util.Log;

public class UserSwitchReceiver extends BroadcastReceiver {
	private static final String TAG = UserSwitchReceiver.class.getSimpleName();

	public static final String ACTION_USER_SWITCHED = "android.intent.action.USER_SWITCHED";
	public static final String ACTION_USER_ADDED    = "android.intent.action.USER_ADDED";
	public static final String ACTION_USER_REMOVED  = "android.intent.action.USER_REMOVED";
	public static final String EXTRA_USER_HANDLE    = "android.intent.extra.user_handle";

	Context context = null;
	UserInfo currentUser = null;
	
	public UserInfo getCurrentUser() { return currentUser; }
	public void ungerister() {
		context.unregisterReceiver(this);
	}
	
	public UserSwitchReceiver(Context context) {
		this.context = context;
		// register
		IntentFilter filter = new IntentFilter(ACTION_USER_SWITCHED);
		filter.addAction(ACTION_USER_ADDED);
		filter.addAction(ACTION_USER_REMOVED);
		context.registerReceiver(this, filter);
		currentUser = getCurrentUser(context);
	}
	
	public static Object getActivityManagerNative()
			throws ClassNotFoundException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    	Class clazz = ClassLoader.getSystemClassLoader().loadClass("android.app.ActivityManagerNative");
    	Method getDefault = clazz.getMethod("getDefault", null);
    	return getDefault.invoke(null, null);
	}
	
	UserInfo getCurrentUser(Context context) {
		UserInfo currentUser = null;
		try {
        	Object activityManagerNative = getActivityManagerNative();
        	Method getCurrentUser = activityManagerNative.getClass().getMethod("getCurrentUser", null);
        	currentUser = (UserInfo)getCurrentUser.invoke(activityManagerNative, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return currentUser;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (ACTION_USER_SWITCHED.equals(intent.getAction())) {
		   currentUser = getCurrentUser(context);
		   Log.d(TAG, "onReceive: currentUser="+currentUser);
		}
		
		Intent service = new Intent(context, UserSwitchService.class);
		context.startService(service);
	}

}
