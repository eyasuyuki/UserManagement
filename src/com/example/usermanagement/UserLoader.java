package com.example.usermanagement;

import java.lang.reflect.Method;
import java.util.List;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserManager;
import android.util.Log;

public class UserLoader extends AsyncTaskLoader<List<UserInfo>> {
	private static final String TAG = UserLoader.class.getSimpleName();
	
	Context context = null;
	
	UserInfo currentUser = null;

	public UserLoader(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public List<UserInfo> loadInBackground() {
		List<UserInfo> result = null;
		
		UserManager um = (UserManager)context.getSystemService(Context.USER_SERVICE);
		try {
			Method getUsers = um.getClass().getMethod("getUsers", null);
			Log.d(TAG, "loadInBackground: getUsers="+getUsers);
			result = (List<UserInfo>)getUsers.invoke(um, null);
			Log.d(TAG, "loadInBackground: result="+result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
