package com.sctek.smartglasses.ui;

import com.sctek.smartglasses.R;
import com.sctek.smartglasses.R.id;
import com.sctek.smartglasses.R.layout;
import com.sctek.smartglasses.R.menu;
import com.sctek.smartglasses.fragments.NativePhotoGridFragment;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class PhotoActivity extends FragmentActivity {

	private String TAG = "PhotoActivity";
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_photo);
		new Handler().post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				TAG = NativePhotoGridFragment.class.getName();
				NativePhotoGridFragment PhotoGF = (NativePhotoGridFragment)getFragmentManager().findFragmentByTag(TAG);
				if(PhotoGF == null)
					PhotoGF = new NativePhotoGridFragment();
				
				Bundle pBundle = new Bundle();
				pBundle.putInt("index", NativePhotoGridFragment.FRAGMENT_INDEX);
				PhotoGF.setArguments(pBundle);
				
				getFragmentManager().beginTransaction().replace(android.R.id.content, 
						PhotoGF, TAG).commit();
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
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
