package com.sctek.smartglasses.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.ingenic.glass.api.sync.SyncChannel;
import com.ingenic.glass.api.sync.SyncChannel.CONNECTION_STATE;
import com.ingenic.glass.api.sync.SyncChannel.Packet;
import com.ingenic.glass.api.sync.SyncChannel.RESULT;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sctek.smartglasses.R;
import com.sctek.smartglasses.ui.MySideNavigationCallback;
import com.sctek.smartglasses.ui.SideNavigationView;
import com.sctek.smartglasses.ui.TouchImageView;
import com.sctek.smartglasses.utils.CustomHttpClient;
import com.sctek.smartglasses.utils.GetRemoteVideoThumbWorks;
import com.sctek.smartglasses.utils.GetRemoteVideoThumbWorks.GetRemoteVideoThumbListener;
import com.sctek.smartglasses.utils.MediaData;
import com.sctek.smartglasses.utils.MultiMediaScanner;
import com.sctek.smartglasses.utils.XmlContentHandler;

import android.R.anim;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

//= {"http://h.hiphotos.baidu.com/image/w%3D310/sign=6c58b6e7b1119313c743f9b155380c10/a6efce1b9d16fdfa904abecbb78f8c5494ee7bf4.jpg",
//		"http://d.hiphotos.baidu.com/image/pic/item/d0c8a786c9177f3e3455af2873cf3bc79f3d56b5.jpg",
//		"http://b.hiphotos.baidu.com/image/pic/item/71cf3bc79f3df8dc885cdcb4ce11728b471028b5.jpg",
//		"http://c.hiphotos.baidu.com/image/pic/item/ae51f3deb48f8c54bec4960a39292df5e0fe7ffb.jpg",
//		"http://e.hiphotos.baidu.com/image/pic/item/dc54564e9258d1098f42fa97d258ccbf6c814d75.jpg",
//		"http://h.hiphotos.baidu.com/image/pic/item/bd3eb13533fa828be182cec6fe1f4134970a5a75.jpg",
//		"http://b.hiphotos.baidu.com/image/pic/item/ca1349540923dd546f64f44dd209b3de9c8248fb.jpg",
//		"http://h.hiphotos.baidu.com/image/pic/item/960a304e251f95ca9ee2c193ca177f3e670952fb.jpg",
//		"http://c.hiphotos.baidu.com/image/pic/item/43a7d933c895d1432fcfb70670f082025aaf075d.jpg",
//		"http://f.hiphotos.baidu.com/image/pic/item/faedab64034f78f07ab2c9d67a310a55b3191c5d.jpg",
//		"http://f.hiphotos.baidu.com/image/pic/item/f9198618367adab4ef69486688d4b31c8701e45e.jpg",
//		"http://e.hiphotos.baidu.com/image/pic/item/b21c8701a18b87d68bfbf83a040828381f30fd5e.jpg",
//		"http://h.hiphotos.baidu.com/image/pic/item/d1160924ab18972ba32d53efe5cd7b899e510afb.jpg",
//		"http://f.hiphotos.baidu.com/image/pic/item/e7cd7b899e510fb3720e24b2da33c895d1430cfb.jpg",
//		"http://photo.poco.cn/lastphoto-htx-id-4072622-p-0.xhtml?spread_id=FXuU",
//		"http://a.hiphotos.baidu.com/image/pic/item/9922720e0cf3d7caa3976c53f11fbe096b63a9b6.jpg",
//		"http://h.hiphotos.baidu.com/image/pic/item/024f78f0f736afc3f61b6c40b019ebc4b74512fb.jpg",
//		"http://f.hiphotos.baidu.com/image/pic/item/b219ebc4b74543a9170ca9e91d178a82b90114fb.jpg"};

public class BaseFragment extends Fragment {
	
	public static final String TAG = BaseFragment.class.getName();
	
	public static final String URL_PREFIX = "http://192.168.5.122/";
	
	public static final String PHOTO_DOWNLOAD_FOLDER = "/SmartGlasses/photos/";
	public static final String VIDEO_DOWNLOAD_FOLDER = "/SmartGlasses/videos";
	
	public static final String EXTERNEL_DIRCTORY_PATH = 
			Environment.getExternalStorageDirectory() + "/SmartGlasses/photos/";
	
	protected static final int WIFI_AP_STATE_UNKNOWN = -1;
	protected static final int WIFI_AP_STATE_DISABLING = 10;
	protected static final int WIFI_AP_STATE_DISABLED = 11;
	protected static final int WIFI_AP_STATE_ENABLING = 12;
	protected static final int WIFI_AP_STATE_ENABLED = 13;
	protected static final int WIFI_AP_STATE_FAILED = 14;
	
	public static final String WIFI_AP_STATE_CHANGED_ACTION =
	        "android.net.wifi.WIFI_AP_STATE_CHANGED";
	public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
	public static final String EXTRA_PREVIOUS_WIFI_AP_STATE = "previous_wifi_state";
	
	private static final String GLASS_MAC = "d0:31:10:f2:c1:66";
    
	public ArrayList<MediaData> mediaList;
	
	public ArrayList<MediaData> selectedMedias;
	
	public HashMap<Long, String> downloadIdImageMap;
	
	private DisplayImageOptions options;
	
	private SideNavigationView mSideNavigationView;
	
	public View deleteView;
	
	public boolean showImageCheckBox;
	
	public ImageAdapter mImageAdapter;
	
	public TextView deleteTv;
	public TextView cancelTv;
	protected View enableApView;
	protected Button enableApBt;
	
	private int childIndex;
	
	private WifiManager mWifiManager;
	private SyncChannel mChannel;
	
	public Context mContext;
	public int preApState;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "onCreate");
		setHasOptionsMenu(true);
		
		mContext = (Context)getActivity().getApplicationContext();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.resetViewBeforeLoading(true)
		.cacheOnDisk(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();
		
		childIndex = getArguments().getInt("index");
		selectedMedias = new ArrayList<MediaData>();
		showImageCheckBox = false;
		mImageAdapter = new ImageAdapter();
		downloadIdImageMap = new HashMap<Long, String>();
		
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		mChannel = SyncChannel.create("00e04c68229b0", mContext, mOnSyncListener);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_image_grid, container, false);
		
		deleteView = view.findViewById(R.id.delete_bt_lo);
		deleteTv = (TextView) view .findViewById(R.id.delete_tv);
		cancelTv = (TextView) view.findViewById(R.id.cancel_tv);
		enableApView = view.findViewById(R.id.wifi_ap_hint_lo);
		enableApBt = (Button)view.findViewById(R.id.wifi_ap_on_bt);
		
		cancelTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onCancelTvClicked();
			}
		});
		
		enableApBt.setOnClickListener(onWifiApOnButtonClickedListener);
		
		GridView grid = (GridView) view.findViewById(R.id.grid);
		grid.setAdapter(mImageAdapter);
		
		switch (childIndex) {
			case NativePhotoGridFragment.FRAGMENT_INDEX:
				grid.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, false));
				grid.setOnItemClickListener(onPhotoImageClickedListener);
				break;
			case RemotePhotoGridFragment.FRAGMENT_INDEX:
				grid.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, false));
				grid.setOnItemClickListener(onPhotoImageClickedListener);
				if(WIFI_AP_STATE_DISABLED == getWifiAPState())
					enableApView.setVisibility(View.VISIBLE);
				break;
			case NativeVideoGridFragment.FRAGMENT_INDEX:
				grid.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, false));
				grid.setOnItemClickListener(onVideoImageClickedListener);
				break;
			case RemoteVideoGridFragment.FRAGMENT_INDEX:
				grid.setOnItemClickListener(onVideoImageClickedListener);
				if(WIFI_AP_STATE_DISABLED == getWifiAPState())
					enableApView.setVisibility(View.VISIBLE);
				break;
		}
			
		initNavigationMenu(view);
		
		return view;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onStart");
		super.onStart();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onResume");
		super.onResume();
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onPause");
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onDestroy");
		super.onDestroy();
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onDestroyView");
		super.onDestroyView();
	}
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onDetach");
		super.onDetach();
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.e(TAG, "onCreateOptionsMenu");
		inflater.inflate(R.menu.native_photo_fragment_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.e(TAG, "onOptionsItemSelected");
		switch (item.getItemId()) {
			case android.R.id.home:
				mSideNavigationView.toggleMenu();
				return true;
			default:
				return false;
		}
	}
	
	public class ImageAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private GetRemoteVideoThumbWorks thumbWork;

		ImageAdapter() {
			inflater = LayoutInflater.from(mContext);
			thumbWork = GetRemoteVideoThumbWorks.getInstance();
		}

		@Override
		public int getCount() {
			return mediaList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.e(TAG, "getView");
			final ViewHolder holder;
			final int mPositoin = position;
			View view = convertView;
			if (view == null) {
				
				view = inflater.inflate(R.layout.image_grid_item, parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.imageView = (ImageView) view.findViewById(R.id.image);
				holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
				holder.imageName = (TextView)view.findViewById(R.id.image_name_tv);
				holder.imageCb = (CheckBox)view.findViewById(R.id.image_select_cb);
				
				view.setTag(holder);
				
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			holder.imageCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				private int imageIndex = mPositoin;
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked) {
						selectedMedias.add(mediaList.get(imageIndex));
					}
					else
						selectedMedias.remove(mediaList.get(imageIndex));
				}
			});
			
			if(showImageCheckBox)
				holder.imageCb.setVisibility(View.VISIBLE);
			else
				holder.imageCb.setVisibility(View.GONE);
			
			if(childIndex != RemoteVideoGridFragment.FRAGMENT_INDEX) {
				ImageLoader.getInstance()
						.displayImage(mediaList.get(position).url, holder.imageView, options, new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri, View view) {
								holder.progressBar.setProgress(0);
								holder.progressBar.setVisibility(View.VISIBLE);
								holder.imageName.setVisibility(View.GONE);
							}
	
							@Override
							public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
								holder.progressBar.setVisibility(View.GONE);
								holder.imageName.setVisibility(View.VISIBLE);
								holder.imageName.setText(mediaList.get(mPositoin).name);
							}
	
							@Override
							public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
								holder.progressBar.setVisibility(View.GONE);
								holder.imageName.setVisibility(View.VISIBLE);
								holder.imageName.setText(mediaList.get(mPositoin).name);
							}
						}, new ImageLoadingProgressListener() {
							@Override
							public void onProgressUpdate(String imageUri, View view, int current, int total) {
								holder.progressBar.setProgress(Math.round(100.0f * current / total));
							}
						});
			}
			else {
				holder.imageName.setText(mediaList.get(mPositoin).name);
				holder.progressBar.setVisibility(View.GONE);
				holder.imageView.setImageResource(R.drawable.ic_stub);
				
				thumbWork.getRemoteVideoThumb(mediaList.get(position).url, new GetRemoteVideoThumbListener() {
					
					@Override
					public void onGetRemoteVideoThumbDone(Bitmap bitmap) {
						// TODO Auto-generated method stub
						Log.e(TAG, "5");
						holder.imageView.setImageBitmap(bitmap);
					}
				});
			}

			return view;
		}
	}

	static class ViewHolder {
		ImageView imageView;
		ProgressBar progressBar;
		TextView imageName;
		CheckBox imageCb;
	}
	
	private OnItemClickListener onPhotoImageClickedListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			FragmentManager fragManager = getActivity().getSupportFragmentManager();
			FragmentTransaction transcaction = fragManager.beginTransaction();
			String tag = PhotoViewPagerFragment.class.getName();
			PhotoViewPagerFragment photoFm = (PhotoViewPagerFragment)fragManager.findFragmentByTag(tag);
			if(photoFm == null)
				photoFm = new PhotoViewPagerFragment();
			
			Bundle bundle = new Bundle();
			bundle.putInt("position", position);
			bundle.putParcelableArrayList("data", mediaList);
			photoFm.setArguments(bundle);
			
			transcaction.replace(android.R.id.content, photoFm, tag);
			transcaction.addToBackStack(null);
			transcaction.commit();
		}
		
	};
	
	private OnItemClickListener onVideoImageClickedListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			Uri uri = Uri.parse(mediaList.get(position).url);
//			Uri uri = Uri.parse("http://192.168.5.253/pub/sct/tracker/VID_20150130_180836.mp4");
			Intent intent = new Intent(Intent.ACTION_VIEW	);
			intent.setData(uri);
			startActivity(intent);
		}
	};
	
	private OnClickListener onWifiApOnButtonClickedListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			turnWifiApOn();
		}
	};
	
	private void initNavigationMenu(View view) {
		
		mSideNavigationView = (SideNavigationView)view.findViewById(R.id.side_navigation_view);
		mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
		mSideNavigationView.setMenuClickCallback(new MySideNavigationCallback(getActivity()));
		
	}
	
	public void onCancelTvClicked() {
		showImageCheckBox = false;
		deleteView.setVisibility(View.GONE);
		mImageAdapter.notifyDataSetChanged();
	}
	
	public void onNativePhotoDeleteTvClicked(String type) {
		
//		ContentResolver cr = mContext.getContentResolver();
		String imagesPath[] = getMediaPath(type);
		
//		Uri uri = null;
//		if(childIndex == NativePhotoGridFragment.FRAGMENT_INDEX)
//			uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//		else if(childIndex == NativeVideoGridFragment.FRAGMENT_INDEX)
//			uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//		
//		for(int i = 0; i < imagesPath.length; i++) {
//			
//			File file = new File(imagesPath[i]);
//			if(file.exists())
//				file.delete();
//			Log.e(TAG, imagesPath[i]);
//			try{
//				cr.delete(uri, MediaStore.MediaColumns.DATA + "=?", new String[]{imagesPath[i]});
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		
		for(String path : imagesPath) {
			File file = new File(path);
			if(file.exists())
				file.delete();
		}
		
		MultiMediaScanner scanner = new MultiMediaScanner(mContext, imagesPath, null);
		scanner.connect();
		
		mediaList.removeAll(selectedMedias);
		selectedMedias.clear();
		mImageAdapter.notifyDataSetChanged();
		
	}
	
	public void onRemotePhotoDeleteTvClicked() {
		
		DownloadManager mDownloadManager = (DownloadManager)mContext
				.getSystemService(mContext.DOWNLOAD_SERVICE);
//		DownloadManager.Request request = new DownloadManager.Request(uri)
	}
	
	private String[] getMediaPath(String type) {
		
		String paths[] = new String[selectedMedias.size()];
		String dirPath = Environment.getExternalStorageDirectory().toString()
				+ "/SmartGlasses/" + type + "/";
		int i = 0;
		for(MediaData md : selectedMedias)
			paths[i++] = dirPath + md.name;
		return paths;
	}
	
	private String[] getImagesId(ArrayList<String> imagesUrl) {
		String ids[] = new String[imagesUrl.size()];
		int i = 0;
		for(String url : imagesUrl) {
			int idIndex = url.lastIndexOf("/");
			ids[i++] = url.substring(idIndex + 1);
			Log.e(TAG, ids[i-1]);
		}
		return ids;
	}
	
	private void turnWifiApOn() {
		
		WifiConfiguration wcfg = new WifiConfiguration();
		wcfg.SSID = new String("glass_ap");
		wcfg.networkId = 1;
		wcfg.allowedAuthAlgorithms.clear();
		wcfg.allowedGroupCiphers.clear();
		wcfg.allowedKeyManagement.clear();
		wcfg.allowedPairwiseCiphers.clear();
		wcfg.allowedProtocols.clear();
        
		//wcfg.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN, true);
		wcfg.wepKeys[0] = "";    
		wcfg.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);    
		wcfg.wepTxKeyIndex = 0;
		
		try {
			Method method = mWifiManager.getClass().getMethod("setWifiApConfiguration", wcfg.getClass());
			                                           
			Boolean rt = (Boolean)method.invoke(mWifiManager, wcfg);
			Log.d("setconfig", " " + rt);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			Log.d("setconfig", " no method");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			Log.d("setconfig", " illegeal argument");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			Log.d("setconfig", " illegal access");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			Log.d("setconfig", " invocation failed");
			e.printStackTrace();
		}
		toggleWifi();
	}
	
	private void toggleWifi() {
        boolean wifiApIsOn = getWifiAPState()==WIFI_AP_STATE_ENABLED || getWifiAPState()==WIFI_AP_STATE_ENABLING;
        new SetWifiAPTask(!wifiApIsOn,false).execute();
    }
	
	public int getWifiAPState() {
			int state = WIFI_AP_STATE_UNKNOWN;
			try {
				Method method2 = mWifiManager.getClass().getMethod("getWifiApState");
				state = (Integer) method2.invoke(mWifiManager);
			} catch (Exception e) {}
			Log.d("WifiAP", "getWifiAPState.state " + state);
			return state;
    }
	
	private int setWifiApEnabled(boolean enabled) {
		
		Log.d("WifiAP", "*** setWifiApEnabled CALLED **** " + enabled);
		if (enabled && mWifiManager.getConnectionInfo() !=null) {
			mWifiManager.setWifiEnabled(false);
			
			try {Thread.sleep(1500);} catch (Exception e) {}
		}
		
		int state = WIFI_AP_STATE_UNKNOWN;
		try {
			mWifiManager.setWifiEnabled(false);
			Method method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
			    WifiConfiguration.class, boolean.class);
			method1.invoke(mWifiManager, null, enabled); // true
			Method method2 = mWifiManager.getClass().getMethod("getWifiApState");
			state = (Integer) method2.invoke(mWifiManager);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
		if (!enabled) {
			int loopMax = 10;
			while (loopMax>0 && (getWifiAPState()==WIFI_AP_STATE_DISABLING
					|| getWifiAPState()==WIFI_AP_STATE_ENABLED
					|| getWifiAPState()==WIFI_AP_STATE_FAILED)) {
						try {Thread.sleep(500);loopMax--;} catch (Exception e) {}
			}
			mWifiManager.setWifiEnabled(true);
		} 
		else if (enabled) {
			int loopMax = 10;
			while (loopMax>0 && (getWifiAPState()==WIFI_AP_STATE_ENABLING
					|| getWifiAPState()==WIFI_AP_STATE_DISABLED
					|| getWifiAPState()==WIFI_AP_STATE_FAILED)) {
						try {Thread.sleep(500);loopMax--;} catch (Exception e) {}
			}
		}
		return state;
	}
	
	class GetRemoteMediaUrlTask extends AsyncTask<String, Integer, String> {

		private ProgressDialog mProgressDialog ;
		
		public GetRemoteMediaUrlTask() {
			mProgressDialog = new ProgressDialog(getActivity());
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialog.setMessage("waiting for device connect...");
			mProgressDialog.show();
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try { mProgressDialog.dismiss(); } catch (Exception e) {};
		}
		@Override
		protected String doInBackground(String... type) {
			// TODO Auto-generated method stub
			String ip = null;
			while((ip = getConnectedGlassIP()) == null) {
				try {
					Thread.sleep(3000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			publishProgress(1);
			getMediaUrl(ip, type[0]);
			publishProgress(2);
			return ip;
		}
		
		@Override
		protected void onProgressUpdate(Integer ...values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			switch(values[0]) {
			case 1:
				mProgressDialog.setMessage("downloading file list...");
				break;
			case 2:
				mImageAdapter.notifyDataSetChanged();
				break;
			}
		}
		
	}
    
    class SetWifiAPTask extends AsyncTask<Void, Void, Void> {
    	
		private boolean mMode;
		private boolean mFinish;
		private ProgressDialog d = new ProgressDialog(getActivity());
		
		public SetWifiAPTask(boolean mode, boolean finish) {
		    mMode = mode;
		    mFinish = finish;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			d.setTitle("Turning WiFi AP " + (mMode?"on":"off") + "...");
			d.setMessage("...please wait a moment.");
			try {
			d.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			try {d.dismiss();} catch (IllegalArgumentException e) {};
			//updateStatusDisplay();
//			if (mFinish) mContext.finish();
		}

		@Override
		protected Void doInBackground(Void... params) {
		    setWifiApEnabled(mMode);
		    return null;
		}
    }
    
    private MyOnSyncListener mOnSyncListener = new MyOnSyncListener();
	private class MyOnSyncListener implements SyncChannel.onChannelListener {

		@Override
		public void onReceive(RESULT arg0, Packet arg1) {
			// TODO Auto-generated method stub
			String ip = "Glass ip:" + arg1.getString("ip");
			Log.e(TAG, "onReceive:" + ip);
			Toast.makeText(mContext, ip, Toast.LENGTH_LONG).show();
		}

		@Override
		public void onSendCompleted(RESULT arg0, Packet arg1) {
			// TODO Auto-generated method stub

			Log.e(TAG, "onSendCompleted:" + arg1.getString("version"));
		}

		@Override
		public void onStateChanged(CONNECTION_STATE arg0) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onStateChanged:" + arg0.toString());
		}
		
	}
	
	public BroadcastReceiver mDownloadBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
				ContentResolver cr = mContext.getContentResolver();
				long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				if(!downloadIdImageMap.containsKey(id))
					return;
				String imageName = downloadIdImageMap.get(id);
				String imagePath = EXTERNEL_DIRCTORY_PATH + imageName;
				try {
					MediaStore.Images.Media.insertImage(cr, imagePath, imageName, null	);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
		}
	};
	
	public String getConnectedGlassIP() { 
		
		BufferedReader br = null;  
		String line;  
		String ip = null;
		try {  
			br = new BufferedReader(new FileReader("/proc/net/arp"));
			while ((line = br.readLine()) != null) {  
				String[] splitted = line.split(" +");
				if (GLASS_MAC.equals(splitted[3])) {
					ip = splitted[0];
				}
			}
			br.close();
		} catch (Exception e) { 
			e.printStackTrace();  
		}  
		return ip;
	} 
	
	private void getMediaUrl(final String ip, String type) {
		
//		String mip = "192.168.5.122";
		String uri = String.format("http://" + ip + "/cgi-bin/listfiles?%s", type);
//		String uri = String.format("http://%s:7766/data/apache/GlassData/photos", ip);
		final HttpClient httpClient = CustomHttpClient.getHttpClient();
		final HttpGet httpGet = new HttpGet(uri);
		String result = null;
		try {
		result = httpRequestExecute(httpClient, httpGet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(result != null) {
			Log.e(TAG, result);
			mediaList = getMediaData(result, ip);
			}
	}
	
	public void refreshGallery(String type) {
//		Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//		mediaScanIntent.setData(Uri.fromFile(file));
//		getActivity().getApplicationContext().sendBroadcast(mediaScanIntent);
		String path[] = getMediaPath(type);
		MultiMediaScanner scanner = new MultiMediaScanner(mContext, path, null);
		scanner.connect();
	}
	
	private ArrayList<MediaData> getMediaData(String result, String ip) {
		try {
			XmlContentHandler xmlHandler = new XmlContentHandler(ip);
			StringReader reader = new StringReader(result);
			SAXParserFactory factory = SAXParserFactory.newInstance();    
			SAXParser parser = factory.newSAXParser();    
			XMLReader xmlReader = parser.getXMLReader(); 
			xmlReader.setContentHandler(xmlHandler);
			xmlReader.parse(new InputSource(reader));
			
			return xmlHandler.getMedias();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String httpRequestExecute(HttpClient httpclient, HttpGet httpget) {
		
		BufferedReader in = null;
		int retry = 3;
		while (retry-- != 0) {
			Log.e(TAG, "123");
			try{	
				HttpResponse response = httpclient.execute(httpget);
				in = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				
				StringBuffer result = new StringBuffer();
				String line;
				
				while((line = in.readLine()) != null){
					result.append(line);
				}
				
				in.close();
				
				return result.toString();
			}catch (Exception e) {
				e.printStackTrace();
			}
			finally {
				if (in != null) {
					try {
						in.close();
					}catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}
}
