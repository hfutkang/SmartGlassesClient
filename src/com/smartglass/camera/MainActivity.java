package com.smartglass.camera;


import com.ingenic.glass.api.sync.SyncChannel;
import com.ingenic.glass.api.sync.SyncChannel.CONNECTION_STATE;
import com.ingenic.glass.api.sync.SyncChannel.Packet;
import com.ingenic.glass.api.sync.SyncChannel.RESULT;
import com.ingenic.glass.api.sync.SyncChannel.onChannelListener;
import com.smartglass.camera.WifiAdmin.WifiCipherType;
import com.smartglass.device.R;

import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class MainActivity extends ActionBarActivity {
	
	private static final String TAG = "MainActivity";
	
	public final static int TAKE_PICTURE_ACTION = 0;
	public final static int TAKE_VEDIO_ACTION = 1;
	
	public final static int CONNET_WIFI_MSG = 1;
	public final static int SET_PHOTO_PIXEL = 2;
	public final static int SET_VEDIO_PIXEL = 3;
	public final static int SET_VEDIO_DURATION = 4;
	public final static int SWITCH_GLASSES = 5;
	public final static int SWITCH_ANTI_SHAKE = 6;
	public final static int SWITCH_TIME_STAMP = 7;
	
	private static final String[] lables = {"pixel", "pixel", "pixel", "duration", "sw", "sw", "sw"};
	
	private WifiAdmin mWifiAdmin;
	private SyncChannel mSyncChannel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mWifiAdmin = new WifiAdmin(this);
		mSyncChannel = SyncChannel.create("00e04c68229b0", this, mOnChannelListener);
		
	}
	
	public void onTakePictureButtonClicked(View v) {
		Intent intent = new Intent(MainActivity.this, CameraActivity.class);
		intent.putExtra("action", TAKE_PICTURE_ACTION);
		startActivity(intent);
	}
	
	public void onTakeVedioButtonClicked(View v) {
		Intent intent = new Intent(MainActivity.this, CameraActivity.class);
		intent.putExtra("action", TAKE_VEDIO_ACTION);
		startActivity(intent);
	}
	
	public void onTakeVedioButtonClicked() {
		Intent intent = new Intent(MainActivity.this, CameraActivity.class);
		intent.putExtra("action", TAKE_VEDIO_ACTION);
		startActivity(intent);
	}
	
	public void onTakePictureButtonClicked() {
		Intent intent = new Intent(MainActivity.this, CameraActivity.class);
		intent.putExtra("action", TAKE_PICTURE_ACTION);
		startActivity(intent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.e(TAG, "" + keyCode);
//		if(keyCode == 4 && event.getAction() == 0) {
//			onTakePictureButtonClicked();
////			onTakeVedioButtonClicked();
//		}
		return super.onKeyDown(keyCode, event);
	}
	
	SyncChannel.onChannelListener mOnChannelListener = new onChannelListener() {
		
		@Override
		public void onStateChanged(CONNECTION_STATE arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onSendCompleted(RESULT arg0, Packet arg1) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onReceive(RESULT arg0, Packet data) {
			// TODO Auto-generated method stub
			Log.e(TAG, "Channel onReceive");
			
			int type = data.getInt("type");
			Log.e(TAG, "type:" + type);
			
			String sValue = "";
			boolean bvalue;
			Packet pk = mSyncChannel.createPacket();
			
			switch (type) {
				case CONNET_WIFI_MSG:
					String ssid = data.getString("ssid");
					mWifiAdmin.connect(ssid, "", WifiCipherType.WIFICIPHER_NOPASS);
					break;
				case SET_PHOTO_PIXEL:
				case SET_VEDIO_PIXEL:
					sValue = data.getString(lables[type-1]);
					pk.putInt("type", type);
					pk.putString(lables[type-1], sValue);
					break;
				case SET_VEDIO_DURATION:
					sValue = data.getString(lables[type-1]);
					pk.putInt("type", type);
					pk.putString(lables[type-1], sValue);
					break;
				case SWITCH_GLASSES:
				case SWITCH_ANTI_SHAKE:
				case SWITCH_TIME_STAMP:
					bvalue = data.getBoolean(lables[type-1]);
					pk.putInt("type", type);
					pk.putBoolean(lables[type-1], bvalue);
					Log.e(TAG, "bvalue:" + bvalue);
					break;
				default:
					return;
			}
			
			if(type != 1) {
				mSyncChannel.sendPacket(pk);
			}
		}
	};
	
	private BroadcastReceiver wifiStateBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(intent.getAction())) {
				boolean connected = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);
				Log.e(TAG, "connected:" + connected);
			}
			if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
				NetworkInfo ni = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				Log.e(TAG, "wifi is connected:" + ni.isConnected());
				if(ni.isConnected()) {
					String ip = Utils.getLocalIpAddress();
					SyncChannel.Packet pkt = mSyncChannel.createPacket();
					pkt.putString("ip", ip);
					mSyncChannel.sendPacket(pkt);
				}
			}
		}
	};
}
