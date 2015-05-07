package com.sctek.smartglasses.ui;

import com.sctek.smartglasses.fragments.NativeVideoGridFragment;
import android.support.v4.app.FragmentActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;

public class VideoActivity extends FragmentActivity {

	private String TAG;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new Handler().post(new Runnable() {
			
			@SuppressLint("NewApi")
			@Override
			public void run() {
				// TODO Auto-generated method stub
				TAG = NativeVideoGridFragment.class.getName();
				NativeVideoGridFragment VideoGF = (NativeVideoGridFragment)getFragmentManager().findFragmentByTag(TAG);
				if(VideoGF == null) {
					VideoGF = new NativeVideoGridFragment();
					
				}
				Bundle vBundle = new Bundle();
				vBundle.putInt("index", NativeVideoGridFragment.FRAGMENT_INDEX);
				VideoGF.setArguments(vBundle);
				getFragmentManager().beginTransaction()
						.replace(android.R.id.content, VideoGF, TAG).commit();
				
			}
		});
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		int stackCount = getFragmentManager().getBackStackEntryCount();
		if(stackCount != 0) {
			getFragmentManager().popBackStack();
		}
		else 
			super.onBackPressed();
	}
}
