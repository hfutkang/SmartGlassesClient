package com.sctek.smartglasses.ui;

import java.io.BufferedReader;
import java.io.FileReader;

import com.ingenic.glass.api.sync.SyncChannel;
import com.ingenic.glass.api.sync.SyncChannel.CONNECTION_STATE;
import com.ingenic.glass.api.sync.SyncChannel.Packet;
import com.ingenic.glass.api.sync.SyncChannel.RESULT;
import com.sctek.smartglasses.R;
import com.sctek.smartglasses.fragments.BaseFragment.SetWifiAPTask;
import com.sctek.smartglasses.utils.WifiUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class VedioPlayerActivity extends Activity {
	
	private final static String TAG = "VedioPlayerActivity";
	
	protected static final int WIFI_AP_STATE_DISABLED = 11;
	protected static final int WIFI_AP_STATE_ENABLED = 13;
	
	public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
	public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
	public static final String EXTRA_PREVIOUS_WIFI_AP_STATE = "previous_wifi_state";
	
	private VideoView mVideoView;
	private Button mButton;
	private ProgressBar mProgressBar;
	private boolean playing = false;
	
	private int preApState;
	private View enableApView;
	private Button enableApBt;
	private SetWifiAPTask mWifiATask;
	private WaitGlassConnectTask mConnectTask;
	private WifiManager mWifiManager;
	private String glassIp;
	private SyncChannel mChannel;
	
	private boolean msgReceived = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_vedio_player);
		Log.e(TAG, "onCreate");
		
		mVideoView = (VideoView)findViewById(R.id.video_player_vv);
//		mMediaController = (MediaController)findViewById(R.id.video_controller);
		mButton = (Button)findViewById(R.id.play_bt);
		enableApView = (View)findViewById(R.id.wifi_ap_hint_lo);
		enableApBt = (Button)findViewById(R.id.wifi_ap_on_bt);
		mProgressBar = (ProgressBar)findViewById(R.id.video_pb);
		
		mWifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
		mChannel = SyncChannel.create("00e04c68229b0", this, mOnSyncListener);
		
		preApState = WifiUtils.getWifiAPState(mWifiManager);
		mConnectTask = new WaitGlassConnectTask();
		mWifiATask = new SetWifiAPTask(true, false);
		
		initVideoPlayerView();
		
		IntentFilter filter = new IntentFilter(WIFI_AP_STATE_CHANGED_ACTION);
		registerReceiver(mApStateBroadcastReceiver,filter);
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mConnectTask.cancel(true);
		if(mVideoView.isPlaying())
			mVideoView.stopPlayback();
		super.onDestroy();
	}
	
	private void initVideoPlayerView() {
		
		if(preApState == WIFI_AP_STATE_DISABLED) {
			enableApView.setVisibility(View.VISIBLE);
			mVideoView.setVisibility(View.GONE);
		}
		else {
			mButton.setVisibility(View.VISIBLE);
		}
		
		enableApBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mWifiATask.execute();
			}
		});
		
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!playing) {
					mButton.setVisibility(View.GONE);
					mProgressBar.setVisibility(View.VISIBLE);
					
					mVideoView.setVideoURI(Uri.parse(
							"rtsp://218.204.223.237:554/live/1/66251FC11353191F/e7ooqwcfbqjoo80j.sdp"));
					mVideoView.requestFocus();
				}
				else {
					mVideoView.stopPlayback();
					mButton.setText("start");
					playing = false;
				}
				
			}
		});
		
		mVideoView.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mProgressBar.setVisibility(View.GONE);
				playing = true;
				mButton.setText("stop");
				mVideoView.start();
			}
		});
		
		mVideoView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(!mButton.isShown()) {
					mButton.setVisibility(View.VISIBLE);
				}
				else {
					mButton.setVisibility(View.GONE);
				}
				return false;
			}
		});
		
		mVideoView.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				Log.e(TAG, "error:" + what);
				mButton.setText("start");
				playing = false;
				mVideoView.stopPlayback();
				mButton.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);
				return false;
			}
		});
		
	}
	
	private BroadcastReceiver mApStateBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(WIFI_AP_STATE_CHANGED_ACTION.equals(intent.getAction())) {
				int cstate = intent.getIntExtra(EXTRA_WIFI_AP_STATE, -1);
				Log.e(TAG, WIFI_AP_STATE_CHANGED_ACTION + ":" + cstate);
				if(cstate == WIFI_AP_STATE_ENABLED && preApState != WIFI_AP_STATE_ENABLED) {
					enableApView.setVisibility(View.GONE);
					BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
					if(!adapter.isEnabled()) {
						adapter.enable();
					}
					mConnectTask.execute();
				}
				preApState = cstate;
			}
			
		}
	};
	
	public class SetWifiAPTask extends AsyncTask<Void, Void, Void> {
    	
		private boolean mMode;
		private ProgressDialog d = new ProgressDialog(VedioPlayerActivity.this);
		
		public SetWifiAPTask(boolean mode, boolean finish) {
		    mMode = mode;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			d.setTitle("Turning WiFi AP " + (mMode?"on":"off") + "...");
			d.setMessage("...please wait a moment.");
			d.show();
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			Log.e(TAG, "onPostExecute");
			try {d.dismiss();} catch (IllegalArgumentException e) {};
			//updateStatusDisplay();
//			if (mFinish) mContext.finish();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				WifiUtils.turnWifiApOn(VedioPlayerActivity.this, mWifiManager);
			} catch(Exception e) {
				e.printStackTrace();
			}
		    return null;
		}
    }
	
	public class WaitGlassConnectTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog mProgressDialog;
		
		public WaitGlassConnectTask() {
			mProgressDialog = new ProgressDialog(VedioPlayerActivity.this);
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			mProgressDialog.setTitle(R.string.video_live);
			mProgressDialog.setMessage("waiting for device connect...");
			mProgressDialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			mProgressDialog.dismiss();
			enableApView.setVisibility(View.GONE);
			mButton.setVisibility(View.VISIBLE);
			mVideoView.setVisibility(View.VISIBLE);
			super.onPostExecute(result);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			while(true) {
				glassIp = getConnectedGlassIP();
				if(isCancelled())
					return null;
				if(glassIp != null)
					break;
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		
	}
	
	public String getConnectedGlassIP() { 
		
		BufferedReader br = null;  
		String line;  
		String ip = null;
		try {  
			br = new BufferedReader(new FileReader("/proc/net/arp"));
			while ((line = br.readLine()) != null) { 
				String[] splitted = line.split(" +");
				if (!"IP".equals(splitted[0])) {
					ip = splitted[0];
					break;
				}
			}
			br.close();
		} catch (Exception e) { 
			e.printStackTrace();  
		}  
		
		if(ip == null&& !msgReceived) {
			Packet packet = mChannel.createPacket();
			packet.putInt("type", 1);
			
			String defaultSsid = ((TelephonyManager)getSystemService(TELEPHONY_SERVICE)).getDeviceId();
			String ssid = PreferenceManager.
					getDefaultSharedPreferences(this).getString("ssid", defaultSsid);
			String pw = PreferenceManager.getDefaultSharedPreferences(this).getString("pw", "12345678");
			
			packet.putString("ssid", ssid);
			packet.putString("pw", pw);
			mChannel.sendPacket(packet);
		}
		
		return ip;
	} 
	
	private MyOnSyncListener mOnSyncListener = new MyOnSyncListener();
	private class MyOnSyncListener implements SyncChannel.onChannelListener {
	
		@Override
		public void onReceive(RESULT arg0, Packet data) {
			// TODO Auto-generated method stub
			Log.e(TAG, "Channel onReceive");
			if(data.getBoolean("apres")) {
				msgReceived = true;
			}
		}
	
		@Override
		public void onSendCompleted(RESULT arg0, Packet arg1) {
			// TODO Auto-generated method stub
			
			Log.e(TAG, "onSendCompleted");
		}
	
		@Override
		public void onStateChanged(CONNECTION_STATE arg0) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onStateChanged:" + arg0.toString());
		}
		
	}
}
