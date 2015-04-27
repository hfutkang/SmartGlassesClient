package com.sctek.smartglasses;

import com.ingenic.glass.api.sync.SyncChannel;
import com.ingenic.glass.api.sync.SyncChannel.CONNECTION_STATE;
import com.ingenic.glass.api.sync.SyncChannel.Packet;
import com.ingenic.glass.api.sync.SyncChannel.RESULT;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;


public class GlassesAplication extends Application{
	
	private static final String TAG = "GlassesApplication";
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		mSyncChannel = SyncChannel.create("00e04c68229b0", this, mOnSyncListener);
//		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		Intent intent = new Intent(this, SmartGlassService.class);
		startService(intent);
	}

}
