package cn.ingenic.glasssync.devicemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeChangedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		klilog.i("time changed received, action:"+intent.getAction());
		TimeSyncManager.getInstance().syncTime();
	}

}
