package com.sctek.smartglasses.fragments;

import java.util.HashMap;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import com.ingenic.glass.api.sync.SyncChannel;
import com.ingenic.glass.api.sync.SyncChannel.CONNECTION_STATE;
import com.ingenic.glass.api.sync.SyncChannel.Packet;
import com.ingenic.glass.api.sync.SyncChannel.RESULT;
import com.sctek.smartglasses.R;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.sctek.smartglasses.GlassesAplication;

@SuppressLint("NewApi")
public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener
							,Preference.OnPreferenceClickListener {
	
	private final static String TAG = SettingFragment.class.getName();
	
	public final static int CONNET_WIFI_MSG = 1;
	public final static int SET_PHOTO_PIXEL = 2;
	public final static int SET_VEDIO_PIXEL = 3;
	public final static int SET_VEDIO_DURATION = 4;
	public final static int SWITCH_GLASSES = 5;
	public final static int SWITCH_ANTI_SHAKE = 6;
	public final static int SWITCH_TIME_STAMP = 7;
	
	public final static int SETTING_DELAY_TIME = 3000;
	
	private static final String[] lables = {"pixel", "pixel", "pixel", "duration", "sw", "sw", "sw"};
	private static final String[] keys = {"", "photo_pixel", "vedio_pixel", "duration", 
		"default_switch", "anti_shake", "timestamp"};
	
	private ListPreference mPhotoPixelPreference;
	private ListPreference mVedioPixelPreference;
	private ListPreference mVedioDurationPreference;
	private SwitchPreference mDefaultSwitchPreference;
	private SwitchPreference mAntiShakePreference;
	private SwitchPreference mTimeStampPreference;
	
	private SyncChannel mChannel;
	
	private BluetoothAdapter mBluetoothAdapter;
	
	private boolean setBack = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting_preference);
		
		getActivity().setTitle(R.string.setting);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		getActivity().getActionBar().setHomeButtonEnabled(false);
		
		mChannel = SyncChannel.create("00e04c68229b0", getActivity(), mOnSyncListener);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if(!mBluetoothAdapter.isEnabled())
			mBluetoothAdapter.enable();
		
		initPrefereceView();
		
	}
	
	private void initPrefereceView() {
		mPhotoPixelPreference = (ListPreference)findPreference("photo_pixel");
		mVedioPixelPreference = (ListPreference)findPreference("vedio_pixel");
		mVedioDurationPreference = (ListPreference)findPreference("duration");
		mDefaultSwitchPreference = (SwitchPreference)findPreference("default_switch");
		mAntiShakePreference = (SwitchPreference)findPreference("anti_shake");
		mTimeStampPreference = (SwitchPreference)findPreference("timestamp");
		
		mPhotoPixelPreference.setOnPreferenceChangeListener(this);
		mVedioPixelPreference.setOnPreferenceChangeListener(this);
		mVedioDurationPreference.setOnPreferenceChangeListener(this);
		mDefaultSwitchPreference.setOnPreferenceChangeListener(this);
		mAntiShakePreference.setOnPreferenceChangeListener(this);
		mTimeStampPreference.setOnPreferenceChangeListener(this);
		
//		mPhotoPixelPreference.setOnPreferenceClickListener(this);
//		mVedioPixelPreference.setOnPreferenceClickListener(this);
//		mVedioDurationPreference.setOnPreferenceClickListener(this);
//		mDefaultSwitchPreference.setOnPreferenceClickListener(this);
//		mAntiShakePreference.setOnPreferenceClickListener(this);
//		mTimeStampPreference.setOnPreferenceClickListener(this);
		
	}
	
	private void onPreferenceChanged(Preference preference, Object value) {
		
		String key = preference.getKey();
		Packet pk = mChannel.createPacket();
		
		if("photo_pixel".equals(key)) {
			
			String pixel = (String)value;
			pk.putInt("type", SET_PHOTO_PIXEL);
			pk.putString("pixel", pixel);
			
		}
		else if("vedio_pixel".equals(key)) {
			
			String pixel = (String)value;
			pk.putInt("type", SET_VEDIO_PIXEL);
			pk.putString("pixel", pixel);
			
		}
		else if("duration".equals(key)) {
			
			String duration = (String)value;
			pk.putInt("type", SET_VEDIO_DURATION);
			pk.putString("duration", duration);
			
		}
		else if("default_switch".equals(key)) {
			
			boolean sw = (Boolean)value;
			pk.putInt("type", SWITCH_GLASSES);
			pk.putBoolean("sw", sw);
			
		}
		else if("anti_shake".equals(key)) {
			
			boolean sw = (Boolean)value;
			pk.putInt("type", SWITCH_ANTI_SHAKE);
			pk.putBoolean("sw", sw);
			
		}
		else if("timestamp".equals(key)) {
			
			boolean sw = (Boolean)value;
			pk.putInt("type", SWITCH_TIME_STAMP);
			pk.putBoolean("sw", sw);
			
		}
		
		mChannel.sendPacket(pk);
		Log.e(TAG, key);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onPreferenceChange:" + mChannel.isConnected());
		
		if(setBack) {
			setBack = false;
			return true;
		}
		String key = preference.getKey();
		if(mChannel.isConnected()) {
			onPreferenceChanged(preference, newValue);
			try {
				Thread.sleep(SETTING_DELAY_TIME);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		else {
			Toast.makeText(getActivity(), R.string.bluetooth_error, Toast.LENGTH_LONG).show();
			return false;
		}
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onPreferenceClick");
		return true;
	}
	
	private MyOnSyncListener mOnSyncListener = new MyOnSyncListener();
	private class MyOnSyncListener implements SyncChannel.onChannelListener {
	
		@Override
		public void onReceive(RESULT arg0, Packet data) {
			// TODO Auto-generated method stub
			Log.e(TAG, "Channel onReceive");
			
			int type = data.getInt("type");
			String sValue = null;
			boolean bValue = false;
			Message msg = null;
			
			Log.e(TAG, "type:" + type);
			switch (type) {
			case CONNET_WIFI_MSG:
				break;
			case SET_PHOTO_PIXEL:
				sValue = data.getString(lables[type -1]);
				msg = handler.obtainMessage(SET_PHOTO_PIXEL, sValue);
				msg.sendToTarget();
				break;
			case SET_VEDIO_PIXEL:
				sValue = data.getString(lables[type -1]);
				msg = handler.obtainMessage(SET_VEDIO_PIXEL, sValue);
				msg.sendToTarget();
			case SET_VEDIO_DURATION:
				sValue = data.getString(lables[type -1]);
				msg = handler.obtainMessage(SET_VEDIO_DURATION, sValue);
				msg.sendToTarget();
				break;
			case SWITCH_GLASSES:
				bValue = data.getBoolean(lables[type - 1]);
				msg = handler.obtainMessage(SWITCH_GLASSES, bValue);
				msg.sendToTarget();
				break;
			case SWITCH_ANTI_SHAKE:
				bValue = data.getBoolean(lables[type - 1]);
				msg = handler.obtainMessage(SWITCH_ANTI_SHAKE, bValue);
				msg.sendToTarget();
				break;
			case SWITCH_TIME_STAMP:
				bValue = data.getBoolean(lables[type - 1]);
				msg = handler.obtainMessage(SWITCH_TIME_STAMP, bValue);
				msg.sendToTarget();
				break;
			default:
				break;
			}
			
		}
	
		@Override
		public void onSendCompleted(RESULT result, Packet arg1) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onSendCompleted:" + result.name());
		}
	
		@Override
		public void onStateChanged(CONNECTION_STATE arg0) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onStateChanged:" + arg0.name());
		}
		
	}
	
	private class SettingRunnable implements Runnable{
		
		private String key;
		private boolean bValue;
		private String sValue;
		private Preference mPref;
		private boolean isBoolean;
		
		public SettingRunnable(String key, boolean value, Preference pref) {
			
			this.key = key;
			bValue = value;
			isBoolean = true;
			mPref = pref;
		}
		
		public SettingRunnable(String key, String value, Preference pref) {
			this.key = key;
			sValue = value;
			isBoolean = false;
			mPref = pref;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.e(TAG, "run");
			setBack = true;
			
			if(isBoolean) 
				((SwitchPreference)mPref).setChecked(bValue);
			else
				((ListPreference)mPref).setValue(sValue);
			
		}
		
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			int type = msg.what;
			String sValue;
			boolean bValue;
			
			switch (type) {
				case SET_PHOTO_PIXEL:
					setBack = true;
					sValue = (String)msg.obj;
					mPhotoPixelPreference.setValue(sValue);
					break;
				case SET_VEDIO_PIXEL:
					setBack = true;
					sValue = (String)msg.obj;
					mVedioPixelPreference.setValue(sValue);
					break;
				case SET_VEDIO_DURATION:
					setBack = true;
					sValue = (String)msg.obj;
					mVedioPixelPreference.setValue(sValue);
					break;
				case SWITCH_GLASSES:
					setBack = true;
					bValue = (Boolean)msg.obj;
					mDefaultSwitchPreference.setChecked(bValue);
					break;
				case SWITCH_ANTI_SHAKE:
					setBack = true;
					bValue = (Boolean)msg.obj;
					mAntiShakePreference.setChecked(bValue);
					break;
				case SWITCH_TIME_STAMP:
					setBack = true;
					bValue = (Boolean)msg.obj;
					mTimeStampPreference.setChecked(bValue);
					break;
					default:
						break;
			}
		}
	};
}
