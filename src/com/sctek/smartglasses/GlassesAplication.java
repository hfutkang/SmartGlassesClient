package com.sctek.smartglasses;

import com.ingenic.glass.api.sync.SyncChannel;
import com.ingenic.glass.api.sync.SyncChannel.CONNECTION_STATE;
import com.ingenic.glass.api.sync.SyncChannel.Packet;
import com.ingenic.glass.api.sync.SyncChannel.RESULT;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;


public class GlassesAplication extends Application{
	
	private static final String TAG = "GlassesApplication";
	private static SyncChannel mSyncChannel;
	private static SharedPreferences mPreferences;
	
	private static final String[] lables = {"pixel", "pixel", "pixel", "duration", "sw", "sw", "sw"};
	private static final String[] keys = {"", "photo_pixel", "vedio_pixel", "duration", "sw", "sw", "sw"};
	
	public final static int CONNET_WIFI_MSG = 1;
	public final static int SET_PHOTO_PIXEL = 2;
	public final static int SET_VEDIO_PIXEL = 3;
	public final static int SET_VEDIO_DURATION = 4;
	public final static int SWITCH_GLASSES = 5;
	public final static int SWITCH_ANTI_SHAKE = 6;
	public final static int SWITCH_TIME_STAMP = 7;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		mSyncChannel = SyncChannel.create("00e04c68229b0", this, mOnSyncListener);
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	public SyncChannel getChannel() {
		return mSyncChannel;
	}
	
	private MyOnSyncListener mOnSyncListener = new MyOnSyncListener();
	private class MyOnSyncListener implements SyncChannel.onChannelListener {
	
		@Override
		public void onReceive(RESULT arg0, Packet data) {
			// TODO Auto-generated method stub
			Log.e(TAG, "Channel onReceive");
			
			int type = data.getInt("type");
			Log.e(TAG, "type:" + type);
			switch (type) {
			case CONNET_WIFI_MSG:
				break;
			case SET_PHOTO_PIXEL:
			case SET_VEDIO_PIXEL:
			case SET_VEDIO_DURATION:
				String value = data.getString(lables[type]);
				Editor editor = mPreferences.edit();
				editor.putString(keys[type], value);
				editor.commit();
				break;
			case SWITCH_GLASSES:
			case SWITCH_ANTI_SHAKE:
			case SWITCH_TIME_STAMP:
				boolean bvalue = data.getBoolean(lables[type]);
				Editor beditor = mPreferences.edit();
				beditor.putBoolean(keys[type], bvalue);
				beditor.commit();
				break;
			default:
				break;
			}
			
		}
	
		@Override
		public void onSendCompleted(RESULT result, Packet arg1) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onSendCompleted:" + result.toString());
		}
	
		@Override
		public void onStateChanged(CONNECTION_STATE arg0) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onStateChanged:" + arg0.toString());
		}
		
	}

}
