package com.example.usermanagement;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();

	List<UserInfo> users = null;
	TextView messageText = null;
	Spinner userSpinner = null;
	Button switchButton = null;
	int selected = 0;
	
	LoaderManager.LoaderCallbacks<List<UserInfo>> usersCallbacks =
			new LoaderManager.LoaderCallbacks<List<UserInfo>>() {

				@Override
				public Loader<List<UserInfo>> onCreateLoader(int id, Bundle args) {
					Loader<List<UserInfo>> loader = new UserLoader(MainActivity.this);
					loader.forceLoad();
					return loader;
				}

				@Override
				public void onLoadFinished(Loader<List<UserInfo>> loader,
						List<UserInfo> data) {
					if (data != null && data.size() > 0) {
						users = data;
						List<String> names = new ArrayList<String>();
						for (UserInfo u: users) {
							names.add(u.name);
						}
						ArrayAdapter adapter = 
								new ArrayAdapter<String>(
										MainActivity.this,
										android.R.layout.simple_list_item_1,
										names);
						userSpinner.setAdapter(adapter);
					} 
				}

				@Override
				public void onLoaderReset(Loader<List<UserInfo>> arg0) {
					// TODO Auto-generated method stub
					
				}};
				
	IUpdateListener updateListener = new IUpdateListener.Stub() {
		
		@Override
		public void update() throws RemoteException {
			init();
		}
	};
	
	IUserSwitchService userSwitchService = null;
	
	ServiceConnection userSwitchConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			userSwitchService = IUserSwitchService.Stub.asInterface(service);
			try {
				userSwitchService.addListener(updateListener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			userSwitchService = null;
			try {
				userSwitchService.removeListener(updateListener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}};
		
	AdapterView.OnItemSelectedListener selectedListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> adapter, View view, int pos,
				long id) {
			selected = pos;
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapter) {
			//
		}
	};
		
	View.OnClickListener switchUserListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
        	try {
				Object activityManagerNative = UserSwitchReceiver.getActivityManagerNative();
				Method switchUser = null;
				Method[] methods = activityManagerNative.getClass().getDeclaredMethods();
				for (Method m: methods) if (m.getName().equals("switchUser")) switchUser = m;
				Log.d(TAG, "onClick: switchUser="+switchUser+", selected="+selected);
				switchUser.invoke(activityManagerNative, new Object[]{selected});
			} catch (Exception e) {
				e.printStackTrace();
			}
 		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        messageText = (TextView)findViewById(R.id.message_text);
        userSpinner = (Spinner)findViewById(R.id.user_spinner);
        switchButton = (Button)findViewById(R.id.switch_user_button);
        
        userSpinner.setOnItemSelectedListener(selectedListener);
        
        switchButton.setOnClickListener(switchUserListener);

        init();
        
        bindService(new Intent(IUserSwitchService.class.getName()),
        		userSwitchConnection,
        		BIND_AUTO_CREATE);
    }
    
    void init() {
    	UserSwitchReceiver receiver = new UserSwitchReceiver(this);
    	UserInfo currentUser = receiver.getCurrentUser();
    	if (currentUser != null) {
    		messageText.setText(currentUser.name);
    	}
    	
        getLoaderManager().initLoader(0, null, usersCallbacks);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	protected void onDestroy() {
		unbindService(userSwitchConnection);

		super.onDestroy();
	}
    
}
