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

import com.sctek.smartglasses.R;
import com.ingenic.glass.api.sync.SyncChannel;
import com.ingenic.glass.api.sync.SyncChannel.CONNECTION_STATE;
import com.ingenic.glass.api.sync.SyncChannel.Packet;
import com.ingenic.glass.api.sync.SyncChannel.RESULT;
import com.ingenic.glass.api.sync.SyncChannel.onChannelListener;
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
import com.sctek.smartglasses.ui.MySideNavigationCallback;
import com.sctek.smartglasses.ui.SideNavigationView;
import com.sctek.smartglasses.ui.TouchImageView;
import com.sctek.smartglasses.utils.CustomHttpClient;
import com.sctek.smartglasses.utils.GetRemoteVideoThumbWorks;
import com.sctek.smartglasses.utils.MediaData;
import com.sctek.smartglasses.utils.MultiMediaScanner;
import com.sctek.smartglasses.utils.WifiUtils;
import com.sctek.smartglasses.utils.XmlContentHandler;
import com.sctek.smartglasses.utils.GetRemoteVideoThumbWorks.GetRemoteVideoThumbListener;

import android.R.anim;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.telephony.TelephonyManager;
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

@SuppressLint("NewApi")
public class BaseFragment extends Fragment {
	
	public static final String TAG = BaseFragment.class.getName();
	
	public static final String URL_PREFIX = "http://192.168.5.122/";
	
	public static final String PHOTO_DOWNLOAD_FOLDER = 
			Environment.getExternalStorageDirectory().toString()	+ "/SmartGlasses/photos/";
	public static final String VIDEO_DOWNLOAD_FOLDER = 
			Environment.getExternalStorageDirectory().toString()	+ "/SmartGlasses/vedios";
	
	public static final String EXTERNEL_DIRCTORY_PATH = 
			Environment.getExternalStorageDirectory() + "/SmartGlasses/photos/";
	
	protected static final int WIFI_AP_STATE_DISABLED = 11;
	protected static final int WIFI_AP_STATE_ENABLED = 13;
	
	public static final String WIFI_AP_STATE_CHANGED_ACTION =
	        "android.net.wifi.WIFI_AP_STATE_CHANGED";
	public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
	public static final String EXTRA_PREVIOUS_WIFI_AP_STATE = "previous_wifi_state";
    
	public ArrayList<MediaData> mediaList;
	
	public ArrayList<MediaData> selectedMedias;
	
	public HashMap<Long, String> downloadIdImageMap;
	
	public ArrayList<CheckBox> checkBoxs;
	
	private DisplayImageOptions options;
	
	private SideNavigationView mSideNavigationView;
	
	public View deleteView;
	public View selectAllView;
	
	public boolean showImageCheckBox;
	public boolean wifi_msg_received = false;
	
	public ImageAdapter mImageAdapter;
	
	public TextView deleteTv;
	public TextView cancelTv;
	protected View enableApView;
	protected Button enableApBt;
	protected CheckBox selectAllCb;
	
	private int childIndex;
	
	public WifiManager mWifiManager;
	public SyncChannel mChannel;
	
	public Context mContext;
	public int preApState;
	public SetWifiAPTask mWifiATask;
	
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
		checkBoxs = new ArrayList<CheckBox>();
		
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		mChannel = SyncChannel.create("00e04c68229b0", mContext, mOnSyncListener);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_image_grid, container, false);
		
		selectAllView = view.findViewById(R.id.select_all_lo);
		deleteView = view.findViewById(R.id.delete_bt_lo);
		deleteTv = (TextView) view .findViewById(R.id.delete_tv);
		cancelTv = (TextView) view.findViewById(R.id.cancel_tv);
		enableApView = view.findViewById(R.id.wifi_ap_hint_lo);
		enableApBt = (Button)view.findViewById(R.id.wifi_ap_on_bt);
		selectAllCb = (CheckBox)view.findViewById(R.id.select_all_cb);
		
		cancelTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onCancelTvClicked();
				disCheckMedia();
//				selectedMedias.clear();
			}
		});
		
		selectAllCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked) {
					for(MediaData md : mediaList) {
						selectedMedias.add(md);
					}
					for(CheckBox cb : checkBoxs) {
						cb.setChecked(true);
					}
				}
				else {
					for(CheckBox cb : checkBoxs)
						cb.setChecked(false);
					selectedMedias.clear();
				}
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
				if(WIFI_AP_STATE_DISABLED == WifiUtils.getWifiAPState(mWifiManager))
					enableApView.setVisibility(View.VISIBLE);
				break;
			case NativeVideoGridFragment.FRAGMENT_INDEX:
				grid.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, false));
				grid.setOnItemClickListener(onVideoImageClickedListener);
				break;
			case RemoteVideoGridFragment.FRAGMENT_INDEX:
				grid.setOnItemClickListener(onVideoImageClickedListener);
				if(WIFI_AP_STATE_DISABLED == WifiUtils.getWifiAPState(mWifiManager))
					enableApView.setVisibility(View.VISIBLE);
				break;
		}
			
//		initNavigationMenu(view);
		
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
				Log.e(TAG, "null");
				view = inflater.inflate(R.layout.image_grid_item, parent, false);
				view.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
				holder = new ViewHolder();
				
				holder.imageView = (ImageView) view.findViewById(R.id.image);
				holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
				holder.imageName = (TextView)view.findViewById(R.id.image_name_tv);
				holder.imageCb = (CheckBox)view.findViewById(R.id.image_select_cb);
				
				view.setTag(holder);
				
				checkBoxs.add(holder.imageCb);
				
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			holder.imageCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				private int imageIndex = mPositoin;
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					Log.e(TAG, "onCheckedChanged");
					try {
						if(isChecked) {
							if(!selectedMedias.contains(mediaList.get(imageIndex)))
								selectedMedias.add(mediaList.get(imageIndex));
						}
						else {
							selectedMedias.remove(mediaList.get(imageIndex));
						}
					} catch (IndexOutOfBoundsException expected) {
						
					}
				}
			});
			
			if(selectedMedias.contains(mediaList.get(mPositoin))) {
				holder.imageCb.setChecked(true);
			} 
			else {
				holder.imageCb.setChecked(false);
			}
			
//			if(showImageCheckBox)
//				holder.imageCb.setVisibility(View.VISIBLE);
//			else
//				holder.imageCb.setVisibility(View.GONE);
			
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
			FragmentManager fragManager = getActivity().getFragmentManager();
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
			try {
			mWifiATask.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	public class SetWifiAPTask extends AsyncTask<Void, Void, Void> {
    	
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
			Log.e(TAG, "1234");
			try {
				WifiUtils.turnWifiApOn(getActivity(), mWifiManager);
			} catch(Exception e) {
				e.printStackTrace();
			}
		    return null;
		}
    }
	
	private void initNavigationMenu(View view) {
		
		mSideNavigationView = (SideNavigationView)view.findViewById(R.id.side_navigation_view);
		mSideNavigationView.setMenuItems(R.menu.side_navigation_menu);
		mSideNavigationView.setMenuClickCallback(new MySideNavigationCallback(getActivity()));
		
	}
	
	public void onCancelTvClicked() {
		
		for(CheckBox cb : checkBoxs) {
			cb.setVisibility(View.GONE);
		}
		deleteView.setVisibility(View.GONE);
		selectAllView.setVisibility(View.GONE);
	}
	
	public void disCheckMedia() {
		selectAllCb.setChecked(false);
	}
	
	public void onNativePhotoDeleteTvClicked(String type) {
		
//		ContentResolver cr = mContext.getContentResolver();
		String imagesPath[] = getMediaPath(type);
		
		if(imagesPath.length == 0)
			return;
		
		for(String path : imagesPath) {
			File file = new File(path);
			if(file.exists())
				file.delete();
		}
		
		MultiMediaScanner scanner = new MultiMediaScanner(mContext, imagesPath, null);
		scanner.connect();
		
//		mediaList.removeAll(selectedMedias);
		
		onMediaDeleted();
		
	}
	
	public void onMediaDeleted() {
		
		ArrayList<MediaData> tmp = new ArrayList<MediaData>(selectedMedias);
		
		disCheckMedia();
//		selectedMedias.clear();
		
		for(MediaData md : tmp) {
			int i = mediaList.indexOf(md);
			if(i != -1)
				mediaList.remove(i);
		}
		
		tmp.clear();
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
		
		for(int i = 0; i < selectedMedias.size(); i++) {
			
			MediaData data = selectedMedias.get(i);
			paths[i] = dirPath + data.name;
			
		}
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
			while(true) {
				
				ip = getConnectedGlassIP();
				if(isCancelled())
					return null;
				if(ip != null)
					break;
				try {
					Thread.sleep(5000);
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
    
	private MyOnSyncListener mOnSyncListener = new MyOnSyncListener();
	private class MyOnSyncListener implements SyncChannel.onChannelListener {
	
		@Override
		public void onReceive(RESULT arg0, Packet data) {
			// TODO Auto-generated method stub
			Log.e(TAG, "Channel onReceive");
			if(data.getBoolean("apres")) {
				wifi_msg_received = true;
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
		
		if(ip == null&& !wifi_msg_received) {
			Packet packet = mChannel.createPacket();
			packet.putInt("type", 1);
			
			String defaultSsid = ((TelephonyManager)mContext
					.getSystemService(mContext.TELEPHONY_SERVICE)).getDeviceId();
			String ssid = PreferenceManager.
					getDefaultSharedPreferences(mContext).getString("ssid", defaultSsid);
			String pw = PreferenceManager.getDefaultSharedPreferences(mContext).getString("pw", "12345678");
			
			packet.putString("ssid", ssid);
			packet.putString("pw", pw);
			mChannel.sendPacket(packet);
		}
		return ip;
	} 
	
	private void getMediaUrl(final String ip, String type) {
		
//		String mip = "192.168.5.122";
		String uri = String.format("http://" + ip + "/cgi-bin/listfiles?%s", type);
		Log.e(TAG,uri);
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
