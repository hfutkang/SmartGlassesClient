package com.sctek.smartglasses.ui;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.sctek.smartglasses.fragments.NativePhotoGridFragment;
import com.sctek.smartglasses.fragments.PhotoViewPagerFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {
	
	private  String TAG = "MainActivity";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		initImageLoader(this);
		
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
	
	@SuppressLint("NewApi")
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		if(getFragmentManager().getBackStackEntryCount() != 0) {
	        getFragmentManager().popBackStack();
	    } else {
	        super.onBackPressed();
	    }
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		ImageLoader.getInstance().clearDiskCache();
		ImageLoader.getInstance().clearMemoryCache();
		ImageLoader.getInstance().stop();
		super.onPause();
	}
	
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		String cacheDir = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/.glasses_image_cache";
		File cacheFile = StorageUtils.getOwnCacheDirectory(context, cacheDir);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.threadPoolSize(1)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024) // 50 Mb
				.diskCache(new UnlimitedDiscCache(cacheFile))
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		
		ImageLoader.getInstance().init(config);
	}
}
