package com.sctek.smartglasses;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.ingenic.glass.api.sync.SyncChannel;
import com.ingenic.glass.api.sync.SyncChannel.CONNECTION_STATE;
import com.ingenic.glass.api.sync.SyncChannel.Packet;
import com.ingenic.glass.api.sync.SyncChannel.RESULT;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

public class SmartGlassService extends Service{
	
	private final static String TAG = "SmartGlassService";
	
	private SyncChannel mSyncChannel;
	private TelephonyManager mTelephonyManager;
	private int phoneState = TelephonyManager.CALL_STATE_IDLE;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onCreate");
		mSyncChannel = SyncChannel.create("00e04c68229b1", this, mOnSyncListener);
		mTelephonyManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onStartCommand");
		return START_STICKY;
	}
	
	private MyOnSyncListener mOnSyncListener = new MyOnSyncListener();
	private class MyOnSyncListener implements SyncChannel.onChannelListener {
	
		@Override
		public void onReceive(RESULT arg0, Packet data) {
			// TODO Auto-generated method stub
			Log.e(TAG, "Channel onReceive");
			boolean callBtClicked = data.getBoolean("clicked");
			Log.e(TAG, "clicked:" + callBtClicked);
			if(callBtClicked) {
				switch(phoneState) {
					case TelephonyManager.CALL_STATE_OFFHOOK:
						endTheCall();
						break;
					case TelephonyManager.CALL_STATE_RINGING:
						answerRingCall();
						break;
					default:
						break;
				}
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
	
	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		
		public void onCallStateChanged(int state, String incomingNumber) {
			Log.e(TAG, "onCallStateChanged:" + state);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
			case TelephonyManager.CALL_STATE_OFFHOOK:
			case TelephonyManager.CALL_STATE_RINGING:
				phoneState = state;
				break;
			default:
				break;
			}
		}
	};
	
	private void endTheCall() {
		
		try {
			Method method = mTelephonyManager.getClass().getDeclaredMethod("getITelephony");
			method.setAccessible(true);
			ITelephony iTelehpony = (ITelephony)method.invoke(mTelephonyManager);
			iTelehpony.endCall();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void answerRingCall() {
		Log.e(TAG, "answerRingCall");
		try {
			Method method = mTelephonyManager.getClass().getDeclaredMethod("getITelephony");
			method.setAccessible(true);
			ITelephony iTelephony = (ITelephony)method.invoke(mTelephonyManager);
			iTelephony.answerRingingCall();
		} catch (Exception e) {
			e.printStackTrace();
			try{
				Log.e("Sandy", "for version 4.1 or larger");
				Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
				KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
				intent.putExtra("android.intent.extra.KEY_EVENT",keyEvent);
				sendOrderedBroadcast(intent,"android.permission.CALL_PRIVILEGED");
			} catch (Exception e2) {
				Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
				KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
				meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT,keyEvent);
				sendOrderedBroadcast(meidaButtonIntent, null);
			}
		}
	}
}
