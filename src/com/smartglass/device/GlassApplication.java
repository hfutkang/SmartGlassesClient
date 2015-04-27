package com.smartglass.device;

import com.ingenic.glass.api.sync.SyncChannel;
import com.ingenic.glass.api.sync.SyncChannel.CONNECTION_STATE;
import com.ingenic.glass.api.sync.SyncChannel.Packet;
import com.ingenic.glass.api.sync.SyncChannel.RESULT;
import com.ingenic.glass.api.sync.SyncChannel.onChannelListener;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class GlassApplication extends Application {
	
	private final static String TAG = "GlassApplication";
	
	private SyncChannel mCallChannel;
	private KeyEventBroadcastReceiver mKeyEventBroadcastReceiver;
	
	public void onCreate() {
		
		Log.e(TAG, "onCreate");
		
		mCallChannel = SyncChannel.create("00e04c68229b1", this, mOnChannelListener);
		mKeyEventBroadcastReceiver = new KeyEventBroadcastReceiver(this, mCallChannel);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction("ACTION_KEY_A_SHORT_PRESSED");
		filter.addAction("ACTION_KEY_A_LONG_PRESSED");
		filter.addAction("ACTION_KEY_B_SHORT_PRESSED");
		filter.addAction("ACTION_KEY_B_LONG_PRESSED");
		registerReceiver(mKeyEventBroadcastReceiver, filter);
		
		Intent intent = new Intent(this, GlassesService.class);
		startService(intent);
		
		super.onCreate();
	}
	
	private onChannelListener mOnChannelListener = new onChannelListener() {
		
		@Override
		public void onStateChanged(CONNECTION_STATE arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onSendCompleted(RESULT arg0, Packet arg1) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onReceive(RESULT arg0, Packet arg1) {
			// TODO Auto-generated method stub
			
		}
	};
}
