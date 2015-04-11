package cn.ingenic.glasssync.devicemanager;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.os.Message;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import java.util.List;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;

public class GlassDetect {
    private static final String TAG = "GlassDetect";
    private static boolean DEBUG = true;
    private Context mContext;
    private GlassDetectReceiver nReceiver;
    private static GlassDetect sInstance;
    private String mAddress;
    private int mAudioStrategy;
    private Handler mCallBackHandler = null;
    private BluetoothAdapter mBTAdapter;
    private BluetoothHeadset mBluetoothHeadset = null;
    private BluetoothDevice mBluetoothDevice;
    public static final String ACTION_CONNECTION_STATE_CHANGED = "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED";
	public static final int AUDIO_STRATEGY_DISCONNECT = 0;
    public static final int AUDIO_STRATEGY_CONNECT = 1;
    public static final int NULL_AUDIO_STRATEGY = 2;
    public static final String PHONE_AUDIO_SYNC = "BLUETOOTHHEADSET";
    private final int PHONE_AUDIO_CONNECT = 6;
    private final int PHONE_AUDIO_DISCONNECT = 7;
    private GlassDetect(Context context){
	Log.e(TAG, "GlassDetect");
	mContext = context;

	mAudioStrategy = NULL_AUDIO_STRATEGY;

	init_bluetoothHeadset();

	init_receiver(mContext);
    }

    protected void finalize(){
	mContext.unregisterReceiver(nReceiver);
    }

    public static GlassDetect getInstance(Context c) {
	if (null == sInstance)
	    sInstance = new GlassDetect(c);
	return sInstance;
    }

    public void init_receiver(Context c){
	if (DEBUG)Log.e(TAG, "init_receiver in");
        nReceiver = new GlassDetectReceiver();
        IntentFilter filter = new IntentFilter();
	filter.addAction(ACTION_CONNECTION_STATE_CHANGED);
	filter.addAction(Commands.ACTION_BLUETOOTH_STATUS);
        c.registerReceiver(nReceiver,filter);
    }

    @SuppressLint("NewApi")
	private void init_bluetoothHeadset() {
	mBTAdapter = BluetoothAdapter.getDefaultAdapter();
	if (mBTAdapter == null){
	    Log.e(TAG, "do_audio_strategy getDefaultAdapter fail");
	    return;
	}

	mBTAdapter.getProfileProxy(mContext, mProfileListener, BluetoothProfile.HEADSET);
    }

    public void set_audio_connect(int audioStrategy){
	mAudioStrategy = audioStrategy;
	mBluetoothDevice = getConnectedDevice();
	if(mBluetoothDevice != null)
	    return;
	BluetoothDevice btd = mBTAdapter.getRemoteDevice(mAddress);
	if (btd == null){
	    Log.e(TAG, "set_audio_strategy getRemoteDevice failure");
	    return;
	}else{
	    //Compile in Eclipse shielding
//	    if (DEBUG)Log.e(TAG, "connect headset "+" mBluetoothHeadset.getPriority(btd)="+
//			    mBluetoothHeadset.getPriority(btd));
//	    if (mBluetoothHeadset.getPriority(btd) < BluetoothProfile.PRIORITY_ON)
//		mBluetoothHeadset.setPriority(btd, BluetoothProfile.PRIORITY_ON);
//	    mBluetoothHeadset.connect(btd);
	}

    }

    public void set_audio_disconnect(int audioStrategy){
	if (DEBUG)Log.e(TAG, "disconnect headset");
	mAudioStrategy = audioStrategy;
	mBluetoothDevice = getConnectedDevice();
	if(mBluetoothDevice == null)
	    return;
	//Compile in Eclipse shielding
//	if(mBluetoothHeadset.getPriority(mBluetoothDevice) > BluetoothProfile.PRIORITY_OFF)
//	    mBluetoothHeadset.setPriority(mBluetoothDevice, BluetoothProfile.PRIORITY_OFF);
//	mBluetoothHeadset.disconnect(mBluetoothDevice);
    }

    @SuppressLint("NewApi")
	private BluetoothDevice getConnectedDevice(){
	if(mBluetoothHeadset == null)
	    return null;
	List<BluetoothDevice> lcon =  mBluetoothHeadset.getConnectedDevices();
	if (DEBUG)Log.e(TAG, "List<BluetoothDevice> lcon = "+lcon);
	for (BluetoothDevice lbt : lcon){
	    if (lbt.getAddress().equals(mAddress)){
		return lbt;
	    }
	}
	return null;
    }

    @SuppressLint("NewApi")
	private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
	    public void onServiceConnected(int profile, BluetoothProfile proxy) {
		Log.e(TAG, "onServiceConnected");
		if (profile == BluetoothProfile.HEADSET) {
		    mBluetoothHeadset = (BluetoothHeadset) proxy;
		}
	    }
	    public void onServiceDisconnected(int profile) {
		Log.e(TAG, "onServiceDisconnected");
		if (profile == BluetoothProfile.HEADSET) {
		    mBluetoothHeadset = null;
		}
	    }
	};

    class GlassDetectReceiver extends BroadcastReceiver{
	private String TAG = "GlassDetectReceiver";
	
        @Override
	    public void onReceive(Context context, Intent intent) {
	    Log.e(TAG, "onReceive " + intent.getAction());
	    if (intent.getAction().equals(ACTION_CONNECTION_STATE_CHANGED)) {
		int connectState = intent.getIntExtra(BluetoothHeadset.EXTRA_STATE, BluetoothHeadset.STATE_CONNECTED);
		Log.e(TAG, "connectState = " + connectState);
		if (mAudioStrategy == AUDIO_STRATEGY_CONNECT && 
		    connectState == BluetoothProfile.STATE_CONNECTED){       	
		    Log.d(TAG,"phone audio is connected");
		    if(mCallBackHandler == null) return;
		    Message msg1 = mCallBackHandler.obtainMessage();
		    msg1.obj = PHONE_AUDIO_SYNC;
		    msg1.what = PHONE_AUDIO_CONNECT;
		    msg1.sendToTarget();
		}else if (mAudioStrategy == AUDIO_STRATEGY_DISCONNECT && 
			  connectState == BluetoothProfile.STATE_DISCONNECTED){
		    Log.d(TAG,"phone audio is disconnected");
		    if(mCallBackHandler == null) return;
		    Message message = mCallBackHandler.obtainMessage();
		    message.obj = PHONE_AUDIO_SYNC;
		    message.what = PHONE_AUDIO_DISCONNECT;
		    message.sendToTarget();
		}

	    }else if (intent.getAction().equals(Commands.ACTION_BLUETOOTH_STATUS)){
		boolean blcon = intent.getBooleanExtra("data", false);
		if (DEBUG)Log.e(TAG, "ACTION_BLUETOOTH_STATUS");
		if(mAudioStrategy == NULL_AUDIO_STRATEGY)
		    return;
		else if(mAudioStrategy == AUDIO_STRATEGY_CONNECT)
		    set_audio_connect(AUDIO_STRATEGY_CONNECT);
		else
		    set_audio_disconnect(AUDIO_STRATEGY_DISCONNECT);
	    }
        }
    }

    public void setCallBack(Handler handler) {
	mCallBackHandler = handler;
    }

    public void setLockedAddress(String address) {
	mAddress = address;
    }

    public BluetoothHeadset getBluetoothHeadset() {
	return mBluetoothHeadset;
    }

}