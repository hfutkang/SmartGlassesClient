package com.smartglass.device;

import com.ingenic.glass.api.sync.SyncChannel;
import com.ingenic.glass.api.sync.SyncChannel.CONNECTION_STATE;
import com.ingenic.glass.api.sync.SyncChannel.Packet;
import com.ingenic.glass.api.sync.SyncChannel.RESULT;
import com.ingenic.glass.api.sync.SyncChannel.onChannelListener;
import com.smartglass.device.WifiAdmin.WifiCipherType;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class GlassesService extends Service {

	private final static String TAG = "GlassesService";
	
	public final static int CONNET_WIFI_MSG = 1;
	public final static int SET_PHOTO_PIXEL = 2;
	public final static int SET_VEDIO_PIXEL = 3;
	public final static int SET_VEDIO_DURATION = 4;
	public final static int SWITCH_GLASSES = 5;
	public final static int SWITCH_ANTI_SHAKE = 6;
	public final static int SWITCH_TIME_STAMP = 7;
	public final static int SET_VOLUME = 8;
	public final static int SET_SSID = 9;
	public final static int SET_PW = 10;
	public final static int SET_WIFI_AP = 11;
	
	private WifiAdmin mWifiAdmin;
	private SyncChannel mSyncChannel;
	private SharedPreferences mPreferences;
	private AudioManager mAudioManager;
	
	private static final String[] lables = {"pixel", 
		"pixel", 
		"pixel", 
		"duration", 
		"sw", "sw", "sw", 
		"volume", "ssid", "pw"};
	
	private static final String[] keys = {"", "photo_pixel", 
		"vedio_pixel", 
		"duration", 
		"default_switch", 
		"anti_shake", 
		"timestamp"};
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		mWifiAdmin = new WifiAdmin(this);
		mSyncChannel = SyncChannel.create("00e04c68229b0", this, mOnChannelListener);
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return START_STICKY;
	}
	
	SyncChannel.onChannelListener mOnChannelListener = new onChannelListener() {
		
		private String preSsid = "";
		private String prePw	 = "";
		
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
			Editor editor = mPreferences.edit();
			
			switch (type) {
				case CONNET_WIFI_MSG:
					
					String ssid = data.getString("ssid");
					String pw = data.getString("pw");
					
					Log.e(TAG, ssid + " " + pw);
					if(!preSsid.equals(ssid) || !prePw.equals(pw)) {
						mWifiAdmin.connect(ssid, pw, WifiCipherType.WIFICIPHER_WPA);
						preSsid = ssid;
						prePw = pw;
					}
					break;
				case SET_WIFI_AP:
					String sid = data.getString("ssid");
					String spw = data.getString("pw");
					Log.e(TAG, sid + " " + spw);
					mWifiAdmin.connect(sid, spw, WifiCipherType.WIFICIPHER_WPA);
					
					pk.putInt("type", SET_WIFI_AP);
					pk.putString("ssid", sid);
					pk.putString("pw", spw);
					break;
				case SET_PHOTO_PIXEL:
				case SET_VEDIO_PIXEL:
					sValue = data.getString(lables[type-1]);
					pk.putInt("type", type);
					pk.putString(lables[type-1], sValue);
					
					editor.putString(keys[type-1], sValue);
					editor.commit();
					break;
				case SET_VEDIO_DURATION:
					sValue = data.getString(lables[type-1]);
					pk.putInt("type", type);
					pk.putString(lables[type-1], sValue);
					
					editor.putString(keys[type-1], sValue);
					editor.commit();
					break;
				case SWITCH_GLASSES:
				case SWITCH_ANTI_SHAKE:
				case SWITCH_TIME_STAMP:
					bvalue = data.getBoolean(lables[type-1]);
					pk.putInt("type", type);
					pk.putBoolean(lables[type-1], bvalue);
					
					editor.putBoolean(keys[type-1], bvalue);
					editor.commit();
					Log.e(TAG, "bvalue:" + bvalue);
					break;
				case SET_VOLUME:
					int volume = data.getInt(lables[type-1]);
					mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, volume, 0);
					
					pk.putInt("type", SET_VOLUME);
					pk.putInt("volume", volume);
					break;
					
//				case SET_SSID:
//					sValue = data.getString(lables[type-1]);
//					pk.putInt("type", SET_SSID);
//					pk.putString("ssid", sValue);
//					break;
//				case SET_PW:
//					sValue = data.getString(lables[type-1]);
//					pk.putInt("type", SET_PW);
//					pk.putString("pw", sValue);
//					break;
				default:
					return;
			}
			
//			if(type == SWITCH_ANTI_SHAKE)
//				onTakePictureButtonClicked();
//			if(type == SWITCH_TIME_STAMP)
//				onTakeVedioButtonClicked();
			
			if(type != 1) {
				mSyncChannel.sendPacket(pk);
			}
		}
	};

}
