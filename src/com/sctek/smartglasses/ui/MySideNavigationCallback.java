package com.sctek.smartglasses.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.sctek.smartglasses.R;
import com.sctek.smartglasses.fragments.NativePhotoGridFragment;
import com.sctek.smartglasses.fragments.PhotoViewPagerFragment;
import com.sctek.smartglasses.fragments.NativeVideoGridFragment;

public class MySideNavigationCallback implements ISideNavigationCallback {

	private String TAG;
	private Context mContext;
	
	public MySideNavigationCallback(Context context) {
		mContext = context;
	}
	@Override
	public void onSideNavigationItemClick(int itemId) {
		// TODO Auto-generated method stub
		
		switch (itemId) {
		case R.id.photo_item:
			TAG = NativePhotoGridFragment.class.getName();
			NativePhotoGridFragment PhotoGF = (NativePhotoGridFragment)((FragmentActivity) mContext)
					.getSupportFragmentManager().findFragmentByTag(TAG);
			if(PhotoGF == null) {
				PhotoGF = new NativePhotoGridFragment();
				Bundle pBundle = new Bundle();
				pBundle.putInt("index", NativePhotoGridFragment.FRAGMENT_INDEX);
				PhotoGF.setArguments(pBundle);
			}
			
			((FragmentActivity)mContext).getSupportFragmentManager()
					.beginTransaction().replace(android.R.id.content, PhotoGF, TAG).commit();
			break;
		case R.id.video_item:
			TAG = NativeVideoGridFragment.class.getName();
			NativeVideoGridFragment VideoGF = (NativeVideoGridFragment)((FragmentActivity) mContext)
					.getSupportFragmentManager().findFragmentByTag(TAG);
			
			if(VideoGF == null) {
				
				VideoGF = new NativeVideoGridFragment();
				
				Bundle vBundle = new Bundle();
				vBundle.putInt("index", NativeVideoGridFragment.FRAGMENT_INDEX);
				VideoGF.setArguments(vBundle);
			}
			
			((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
					.replace(android.R.id.content, VideoGF, TAG).commit();
			break;
		case R.id.setting_item:
			break;
		case R.id.about_item:
			break;
		default:
			break;
		}

	}

}
