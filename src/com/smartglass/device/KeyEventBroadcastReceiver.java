package com.smartglass.device;

import com.ingenic.glass.api.sync.SyncChannel;
import com.ingenic.glass.api.sync.SyncChannel.CONNECTION_STATE;
import com.ingenic.glass.api.sync.SyncChannel.Packet;
import com.ingenic.glass.api.sync.SyncChannel.RESULT;
import com.ingenic.glass.api.sync.SyncChannel.onChannelListener;

import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

public class KeyEventBroadcastReceiver extends BroadcastReceiver{
	
	private final static String TAG = "KeyEventBroadcastReceiver";
	
	private final static String A_SHORT_PRESSED_ACTION = "ACTION_KEY_A_SHORT_PRESSED";
	private final static String A_LOANG_PRESSED_ACTION = "ACTION_KEY_A_LONG_PRESSED";
	private final static String B_SHORT_PRESSED_ACTION = "ACTION_KEY_B_SHORT_PRESSED";
	private final static String B_LONG_PRESSED_ACTION = "ACTION_KEY_B_LONG_PRESSED";
	
	private final static String TAKE_PICTRUE_DONE_ACTION = "ACTION_TAKE_PICTRUE_DONE";
	private final static String TAKE_VEDIO_DONE_ACTION = "ACTION_TAKE_VEDIO_DONE";
	
	private final static String CHANNEL_UUID = "00e04c68229b1";
	
	public final static int TAKE_PICTURE_ACTION = 0;
	public final static int TAKE_VEDIO_ACTION = 1;
	
	private final static int GLASS_STATE_IDLE = 0;
	private final static int GLASS_STATE_TAKING_PHOTO = 1;
	private final static int GLASS_STATE_TAKING_VEDIO = 2;
	private final static int GLASS_STATE_MAKING_PHONE = 3;
	
	private SyncChannel mChannel;
	private Context  mContext;
	
	private int state;
	
	public KeyEventBroadcastReceiver(Context context, SyncChannel channel) {
		
		state = GLASS_STATE_IDLE;
		mContext  = context;
		mChannel = channel;
		
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		Log.e(TAG, "" + action);
		if(A_SHORT_PRESSED_ACTION.equals(action)) {
			if(state == GLASS_STATE_IDLE) {
				onTakePictureButtonClicked();
				state = GLASS_STATE_TAKING_PHOTO;
			} 
		}
		else if(A_LOANG_PRESSED_ACTION.equals(action)) {
			if(state == GLASS_STATE_IDLE) {
				onTakeVedioButtonClicked();
				state = GLASS_STATE_TAKING_VEDIO;
			}
		}
		else if(B_SHORT_PRESSED_ACTION.equals(action)) {
			if(mChannel.isConnected()) {
				Log.e(TAG, "channel connected");
				onCallButtonClicked();
			}
			else 
				Log.e(TAG, "channel disconnected");
		}
		else if(B_LONG_PRESSED_ACTION.equals(action)) {
			
		}
		else if(TAKE_PICTRUE_DONE_ACTION.equals(action)) {
			state = GLASS_STATE_IDLE;
		}
		else if(TAKE_VEDIO_DONE_ACTION.equals(action)) {
			state = GLASS_STATE_IDLE;
		}
	}
	
	public void onTakeVedioButtonClicked() {
		Intent intent = new Intent(mContext, CameraActivity.class);
		intent.putExtra("action", TAKE_VEDIO_ACTION);
		mContext.startActivity(intent);
	}
	
	public void onTakePictureButtonClicked() {
		Intent intent = new Intent(mContext, CameraActivity.class);
		intent.putExtra("action", TAKE_PICTURE_ACTION);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}
	
	public void onCallButtonClicked() {
		Packet pk = mChannel.createPacket();
		pk.putBoolean("clicked", true);
		mChannel.sendPacket(pk);
	}
}
