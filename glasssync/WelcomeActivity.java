package cn.ingenic.glasssync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.sctek.smartglasses.ui.MainActivity;
import com.sctek.smartglasses.utils.MyDialog;

import android.view.View;
import android.view.View.OnTouchListener;
import android.content.Context;
import android.util.Log;
import android.app.Dialog;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.provider.Settings;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.widget.Toast;
import cn.ingenic.glasssync.DefaultSyncManager;
import cn.ingenic.glasssync.Enviroment;
import com.sctek.smartglasses.R;
public class WelcomeActivity extends Activity {
	
	private DefaultSyncManager mManager;
	private SharedPreferences mSharedPreferences;
	private Editor mEditor;
	private static final int REQUEST_ENABLE_BT = 0;
	private BluetoothAdapter mAdapter;
        private boolean mFirst = true;
	private final String TAG = "WelcomeActivity";
        private Dialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome_activity);
		Log.e(TAG, "onCreate");
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(mBluetoothReceiver, filter);
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		
		mManager = DefaultSyncManager.getDefault();
		
		if(mAdapter.isEnabled()==false){
		    mDialog = new MyDialog(this, R.style.MyDialog,getApplication().getResources().getString(R.string.dialog_title), getApplication().getResources().getString(R.string.dialog_ok),getApplication().getResources().getString(R.string.dialog_cancle),new MyDialog.LeaveMeetingDialogListener() {
			    @Override
				public void onClick(View view) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Dialog=====click" + view.getId()
				      + "dialog_tv_cancel_two"
				      + R.id.dialog_tv_cancel_two
				      + "dialog_tv_ok" + R.id.dialog_tv_ok);
				switch (view.getId()) {
				case R.id.dialog_tv_ok:
				    mDialog.cancel();
				    finish();
				    break;
				case R.id.dialog_tv_cancel_two:
				    mDialog.cancel();
				    Intent intent =  new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);  
				    startActivity(intent);
				
				    break;
				    
				}
			    }
			});
		    mDialog.setCanceledOnTouchOutside(false);
		    mDialog.setCancelable(false);
		    mDialog.show();
		    return;
		}		
//		mSharedPreferences = getSharedPreferences("Install", MODE_PRIVATE);
//		mEditor = mSharedPreferences.edit();
//		mEditor.putBoolean("root", isRooted());
//		mEditor.commit();
		startActivity();
	}

    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
	    @Override
		public void onReceive(Context context, Intent intent) {
		    if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
		    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
						   BluetoothAdapter.ERROR);
		    if (state == BluetoothAdapter.STATE_ON){
//			mSharedPreferences = getSharedPreferences("Install", MODE_PRIVATE);
//			mEditor = mSharedPreferences.edit();
//			mEditor.putBoolean("root", isRooted());
//			mEditor.commit();
			startActivity();
		    }
		}
	    }
	};
	// public boolean getRootAhth() {
	//
	// Process process = null;
	// DataOutputStream os = null;
	// try {
	// process = Runtime.getRuntime().exec("su");
	// os = new DataOutputStream(process.getOutputStream());
	// os.writeBytes("exit\n");
	// os.flush();
	// int exitValue = process.waitFor();
	// if (exitValue == 0) {
	// return true;
	// } else {
	// return false;
	// }
	// } catch (Exception e) {
	// Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: "
	// + e.getMessage());
	// return false;
	// } finally {
	// try {
	// if (os != null) {
	// os.close();
	// }
	// process.destroy();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// public boolean isRoot() {
	// boolean root = false;
	//
	// try {
	// if ((!new File("/system/bin/su").exists())
	// && (!new File("/system/xbin/su").exists())) {
	// root = false;
	// } else {
	// root = true;
	// }
	//
	// } catch (Exception e) {
	// }
	//
	// return root;
	// }
//	public DataInputStream Terminal(String command) throws Exception {
//		Process process = Runtime.getRuntime().exec("su");
//		OutputStream outstream = process.getOutputStream();
//		DataOutputStream DOPS = new DataOutputStream(outstream);
//		InputStream instream = process.getInputStream();
//		DataInputStream DIPS = new DataInputStream(instream);
//		String temp = command + "\n";
//		DOPS.writeBytes(temp);
//		DOPS.flush();
//		DOPS.writeBytes("exit\n");
//		DOPS.flush();
//		process.waitFor();
//		return DIPS;
//	}
//
//	public boolean isRooted() {
//		DataInputStream stream;
//		boolean flag = false;
//		try {
//			stream = Terminal("ls /data/");
//			if (stream.readLine() != null)
//				flag = true;
//		} catch (Exception e1) {
//			e1.printStackTrace();
//
//		}
//
//		return flag;
//	}

	private void startActivity() {
		Log.e(TAG, "startActivity");
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
			    
			    

			    if (!mManager.getLockedAddress().equals("")) {
					Intent intent = new Intent(WelcomeActivity.this,
							MainActivity.class);
					startActivity(intent);
					finish();
				} else {
					Intent intent = new Intent(WelcomeActivity.this,
							BindGlassActivity.class);
					startActivity(intent);
					finish();
				}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, 2000);
	}
    @Override
	protected void onDestroy() {
	super.onDestroy();
       	    unregisterReceiver(mBluetoothReceiver);
    }
    //When the user returns from the Setting , the Bluetooth is off  will exit the application
    @Override
    	protected void onResume() {
    	super.onResume();
    	if(mFirst){ 
    	    mFirst = false;
    	    return;
    	}
    	if(mAdapter.isEnabled()==false){
    	    Toast.makeText(this, R.string.bluetooth_off,
    			   Toast.LENGTH_SHORT).show();
    	    finish();
    	}
}
}
