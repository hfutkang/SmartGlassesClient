package cn.ingenic.glasssync.devicemanager;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import cn.ingenic.glasssync.BindGlassActivity;
import cn.ingenic.glasssync.DefaultSyncManager;

public class DeviceReceiver extends DeviceAdminReceiver {
        private static final String TAG = "DeviceReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if(intent.getAction().equals(Commands.ACTION_GLASS_UNBIND))
		    unBond(context);
	}

	@Override
	public void onEnabled(Context context, Intent intent) {
		super.onEnabled(context, intent);
		setAdminEnable(context, true);
	}

	@Override
	public void onDisabled(Context context, Intent intent) {
		super.onDisabled(context, intent);
		setAdminEnable(context, false);
	}

	private void setAdminEnable(Context context, boolean enable){
		SharedPreferences pref = context.getSharedPreferences("device_manager", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean("lock_screen", enable);
		editor.commit();
	}

    private void unBond(final Context context) {
	new Thread(new Runnable() {
		@Override
		    public void run() {
		    DefaultSyncManager manager = DefaultSyncManager.getDefault();
		    manager.setLockedAddress("",false);
		    Log.d(TAG, "unbind setlockeraddress ok");
		    try {
			Thread.sleep(1000);
		    } catch (Exception e) {
		    }

		    GlassDetect glassDetect = (GlassDetect)GlassDetect.getInstance(context);
		    glassDetect.set_audio_disconnect(GlassDetect.AUDIO_STRATEGY_DISCONNECT);

		    manager.disconnect();
		    
//		    if(Fragment_MainActivity.sMainActivity != null){
//			Intent intent = new Intent(Fragment_MainActivity.sMainActivity,BindGlassActivity.class);	    
//			Fragment_MainActivity.sMainActivity.startActivity(intent);
//			Fragment_MainActivity.sMainActivity.finish();
//		    }
		    Log.d(TAG, "unBond out");
		}
	    }).start();
    }

}
