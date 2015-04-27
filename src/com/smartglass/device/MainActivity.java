package com.smartglass.device;


import java.io.IOException;

import com.ingenic.glass.api.sync.SyncChannel;
import com.ingenic.glass.api.sync.SyncChannel.CONNECTION_STATE;
import com.ingenic.glass.api.sync.SyncChannel.Packet;
import com.ingenic.glass.api.sync.SyncChannel.RESULT;
import com.ingenic.glass.api.sync.SyncChannel.onChannelListener;
import com.smartglass.device.R;
import com.smartglass.device.WifiAdmin.WifiCipherType;

import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

public class MainActivity extends ActionBarActivity {
	
	private static final String TAG = "MainActivity";
	
	public final static int TAKE_PICTURE_ACTION = 0;
	public final static int TAKE_VEDIO_ACTION = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
	
	private boolean checkCameraHardware(Context context)
    {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA))
        {
            // this device has a camera
            return true;
        }
        else
        {
            // no camera on this device
            return false;
        }
    }
	
}
