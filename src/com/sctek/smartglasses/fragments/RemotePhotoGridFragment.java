package com.sctek.smartglasses.fragments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sctek.smartglasses.fragments.BaseFragment.GetRemoteMediaUrlTask;
import com.sctek.smartglasses.ui.MySideNavigationCallback;
import com.sctek.smartglasses.ui.SideNavigationView;
import com.sctek.smartglasses.ui.TouchImageView;
import com.sctek.smartglasses.utils.CustomHttpClient;
import com.sctek.smartglasses.utils.GlassImageDownloader;
import com.sctek.smartglasses.utils.MediaData;
import com.sctek.smartglasses.utils.MultiMediaScanner;
import com.sctek.smartglasses.utils.WifiUtils;
import com.sctek.smartglasses.utils.XmlContentHandler;

import android.R.anim;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.audiofx.BassBoost;
import android.net.Uri;
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
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
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

public class RemotePhotoGridFragment extends BaseFragment {
	
	public static final int FRAGMENT_INDEX = 3;
	private static final String TAG = RemotePhotoGridFragment.class.getName();
	private static final String REMOTE_PHOTO_URL = "http://192.168.5.122:7766/sdcard/DCIM/Camera/";
	private GetRemoteMediaUrlTask mMediaUrlTask;
	
	private static final int PHOTO_NOTIFICATION_ID = 0;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "onCreate");
		
		mediaList = new ArrayList<MediaData>();
		preApState = WifiUtils.getWifiAPState(mWifiManager);
		mWifiATask = new SetWifiAPTask(true, false);
		mMediaUrlTask = new GetRemoteMediaUrlTask();
		
		getActivity().setTitle(R.string.remote_photo);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		getActivity().getActionBar().setHomeButtonEnabled(false);
		
		IntentFilter filter = new IntentFilter(WIFI_AP_STATE_CHANGED_ACTION);
		mContext.registerReceiver(mApStateBroadcastReceiver,filter);
		
		if(preApState == WIFI_AP_STATE_ENABLED) 
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					new GetRemoteMediaUrlTask().execute("photos");
				}
			}, 0);
		
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
		
		mContext.unregisterReceiver(mApStateBroadcastReceiver);
		mMediaUrlTask.cancel(true);
		ImageLoader.getInstance().stop();
		
		super.onDestroy();
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onDestroyView");
		super.onDestroyView();
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onDetach");
		super.onDetach();
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.e(TAG, "onCreateOptionsMenu");
		inflater.inflate(R.menu.remote_photo_fragment_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.e(TAG, "onOptionsItemSelected");
		switch (item.getItemId()) {
			case R.id.download_item:
				deleteView.setVisibility(View.VISIBLE);
				selectAllView.setVisibility(View.VISIBLE);
				
				for(CheckBox cb : checkBoxs) {
					cb.setVisibility(View.VISIBLE);
				}
				
				deleteTv.setText(R.string.download);
				deleteTv.setOnClickListener(onPhotoDownloadClickListener);
				break;
			case R.id.remote_photo_delete_item:
				
				deleteView.setVisibility(View.VISIBLE);
				selectAllView.setVisibility(View.VISIBLE);
				
				for(CheckBox cb : checkBoxs) {
					cb.setVisibility(View.VISIBLE);
				}	
				deleteTv.setText(R.string.delete);
				deleteTv.setOnClickListener(onRemotePhotoDeleteClickListener);
				break;
			default:
				return false;
		}
		return true;
	}
	
//	private void getImagePath(final String ip) {
//		
//		String uri = "http://" + ip + "/data/apache/cgi-bin/listfiles";
//		final HttpClient httpClient = CustomHttpClient.getHttpClient();
//		final HttpGet httpGet = new HttpGet(uri);
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				String result = httpRequestExecute(httpClient, httpGet);
//				
//				if(result != null) {
//					ArrayList<String> names = getImageNames(result);
//					imagesName = new String[names.size()];
//					names.toArray(imagesName);
//					for(int i = 0; i<names.size(); i++) {
//						String url = "http://" + ip + "/data/" + names.get(i);
//						imageUrls.add(url);
//					}
//					getActivity().sendBroadcast(new Intent(GET_IMAGESURL_DONE));
//				}
//			}
//		}).start();
//		
//	}
//	
	
//	
//	private ArrayList<String> getImageNames(String result) {
//		try {
//			XmlContentHandler xmlHandler = new XmlContentHandler();
//			StringReader reader = new StringReader(result);
//			SAXParserFactory factory = SAXParserFactory.newInstance();    
//			SAXParser parser = factory.newSAXParser();    
//			XMLReader xmlReader = parser.getXMLReader(); 
//			xmlReader.setContentHandler(xmlHandler);
//			xmlReader.parse(new InputSource(reader));
//			
//			return xmlHandler.getNames();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	private OnClickListener onPhotoDownloadClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onPotoDownloadTvClicked();
			onCancelTvClicked();
		}
	};
	
	private OnClickListener onRemotePhotoDeleteClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new PhotoDeleteTask().execute();
			onCancelTvClicked();
		}
	};
	
	private BroadcastReceiver mApStateBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(WIFI_AP_STATE_CHANGED_ACTION.equals(intent.getAction())) {
				int cstate = intent.getIntExtra(EXTRA_WIFI_AP_STATE, -1);
				Log.e(TAG, WIFI_AP_STATE_CHANGED_ACTION + ":" + cstate);
				if(cstate == WIFI_AP_STATE_ENABLED
						&& preApState != WIFI_AP_STATE_ENABLED) {
					enableApView.setVisibility(View.GONE);
					
					BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
					if(!adapter.isEnabled()) {
						adapter.enable();
					}
					
					try {
						mMediaUrlTask.execute("photos");
					} catch (Exception e){
						e.printStackTrace();
					}
				}
				preApState = cstate;
			}
			
		}
	};
	
	public void onPotoDownloadTvClicked() {
		
		new PhotoDownloadTask().execute();
		
	}
	
	private class PhotoDownloadTask extends AsyncTask<String, Integer, Void> {
		
		private ProgressDialog progressDialog;
		private int totalcount;
		private int downloadcount;
		private GlassImageDownloader imageDownloader;
		
		private NotificationManager notificationManager;
		private Notification notification;
		
		@SuppressLint("NewApi")
		public PhotoDownloadTask() {
			
			progressDialog = new ProgressDialog(getActivity());
			totalcount = selectedMedias.size();
			downloadcount = 0;
			
			imageDownloader = new GlassImageDownloader();
			
			notificationManager =  (NotificationManager)(getActivity().getSystemService(mContext.NOTIFICATION_SERVICE));
			notification = new Notification();
			
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			notification.contentView = new RemoteViews(mContext.getPackageName(), R.layout.notification_view);
			notification.icon = R.drawable.ic_stub;
			notification.contentView.setProgressBar(R.id.donwload_progress, 100, 100, true);
			notificationManager.notify(PHOTO_NOTIFICATION_ID, notification);
			
			String msg = String.format("downloading(%d/%d)...", downloadcount, totalcount);
			progressDialog.setMessage(msg);
			progressDialog.show();
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.e(TAG, "onProstExecute");
			progressDialog.dismiss();
			
			String msg = String.format("同步完成(%d/%d)...", downloadcount, totalcount);
			notification.contentView.setTextViewText(R.id.download_lable_tv, msg);
			notification.vibrate = new long[]{0,100,200,300}; 
			notificationManager.notify(PHOTO_NOTIFICATION_ID, notification);
			
			if(downloadcount != 0) {
				refreshGallery("photos");
			}
			
			disCheckMedia();
			selectedMedias.clear();
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			Log.e(TAG, "onProgressUpdate");
			try{
			String msg = String.format("图片同步中(%d/%d)...", downloadcount, totalcount);
			progressDialog.setMessage(msg);
			notification.contentView.setTextViewText(R.id.download_lable_tv, msg);
			notificationManager.notify(PHOTO_NOTIFICATION_ID, notification);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.e(TAG, "PhotoDownloadTask");
			try {
			for(int i = 0; i < selectedMedias.size(); i++) {
				
				MediaData data = selectedMedias.get(i);
				
					InputStream in = imageDownloader.getInputStream(data.url);
					
					File dir = new File(PHOTO_DOWNLOAD_FOLDER);
					if(!dir.exists())
						dir.mkdirs();
					
					File file = new File(PHOTO_DOWNLOAD_FOLDER, data.name);
					if(file.exists()) {
						downloadcount++;
						publishProgress();
						in.close();
						continue;
					}
					
					FileOutputStream os = new FileOutputStream(file);
					
					byte[] buffer = new byte[1024];
					int len = 0;
					while((len = in.read(buffer)) != -1) {
						os.write(buffer, 0, len);
					}
					
					downloadcount++;
					publishProgress();
					
					os.close();
					in.close();
				
			}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	private class PhotoDeleteTask extends AsyncTask<Void, Boolean, Void> {
		
		private ProgressDialog mProgressDialog;
		
		@SuppressLint("NewApi")
		public PhotoDeleteTask () {
			mProgressDialog = new ProgressDialog(getActivity());
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			mProgressDialog.setMessage("deleting...");
			mProgressDialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			mProgressDialog.dismiss();
			super.onPostExecute(result);
		}
		
		@Override
		protected void onProgressUpdate(Boolean... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			if(!values[0])
				Toast.makeText(mContext, "connection error", Toast.LENGTH_LONG).show();
			else
			{
//				mediaList.removeAll(selectedMedias);
				onMediaDeleted();
			}
		}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String ip = getConnectedGlassIP();
			String urlPref = String.format("http://%s/cgi-bin/deletefiles?", ip);
			StringBuffer urlBuffer = new StringBuffer(urlPref);
			urlBuffer.append("photos");
			
			for(MediaData data : selectedMedias) {
				urlBuffer.append("&" + data.name);
			}
			
			Log.e(TAG, "delete url" + urlBuffer.toString());
			HttpClient httpClient = CustomHttpClient.getHttpClient();
			HttpGet httpGet = new HttpGet(urlBuffer.toString());
			if(GlassImageDownloader.deleteRequestExecute(httpClient, httpGet)) {
				publishProgress(true);
			}
			else
				publishProgress(false);
				
			return null;
		}
		
	}
}
