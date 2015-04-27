package com.smartglass.device;

/**
 * @author Jose Davis Nidhin
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.datatype.Duration;

import com.smartglass.device.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CameraActivity extends Activity implements Camera.AutoFocusCallback, PreviewCallback{
	
	private static final String TAG = "CamTestActivity";
	private static final String GLASSED_DATA_PATH = "/data/apache/GlassData/";
//	private static final String GLASSED_DATA_PATH = "/sdcard/cameratest/";
	private Preview preview;
	private Button buttonClick;
	private Camera mCamera;
	private Activity act;
	private Context ctx;
	private MediaRecorder mMediaRecorder;
	private SurfaceView mSurfaceView;
	private File vedioOutFile;
	
	private int vedioDuration = 0;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "onCreate");
		ctx = this;
		act = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.camera_preview);

		mSurfaceView = (SurfaceView)findViewById(R.id.surfaceView);
		preview = new Preview(this, mSurfaceView);
		preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		((FrameLayout) findViewById(R.id.layout)).addView(preview);
		preview.setKeepScreenOn(true);
			
		if(getIntent().getIntExtra("action", MainActivity.TAKE_PICTURE_ACTION) == 
				MainActivity.TAKE_PICTURE_ACTION) {
			
			int numCams = Camera.getNumberOfCameras();
			Log.e(TAG, "cam num:" + numCams);
			if(numCams > 0){
				try{
					mCamera = Camera.open(0);
					preview.setCamera(mCamera, MainActivity.TAKE_PICTURE_ACTION);
				} catch (RuntimeException ex){
					ex.printStackTrace();
				}
			}
			
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.e(TAG, "takePicture");
//					mCamera.autoFocus(CameraActivity.this);
					try {
						mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
					} catch (Exception e) {
						e.printStackTrace();
						finish();
					}
				}
			}, 2000);
		}
		
		if(getIntent().getIntExtra("action", MainActivity.TAKE_PICTURE_ACTION) == 
				MainActivity.TAKE_VEDIO_ACTION) {
			
			vedioDuration = PreferenceManager.getDefaultSharedPreferences(this).getInt("duration", 10);
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						if(prepareVideoRecorder())
							mMediaRecorder.start();
					} catch (Exception e) {
						e.printStackTrace();
						finish();
					}
				}
			}, 2000);
			
		}

				buttonClick = (Button) findViewById(R.id.btnCapture);
				
				buttonClick.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Log.e(TAG,"onClicke");
		//				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
						mCamera.setOneShotPreviewCallback(CameraActivity.this);
					}
				});
		//		
		//		buttonClick.setOnLongClickListener(new OnLongClickListener(){
		//			@Override
		//			public boolean onLongClick(View arg0) {
		//				camera.autoFocus(new AutoFocusCallback(){
		//					@Override
		//					public void onAutoFocus(boolean arg0, Camera arg1) {
		//						//camera.takePicture(shutterCallback, rawCallback, jpegCallback);
		//					}
		//				});
		//				return true;
		//			}
		//		});
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.e(TAG, "onPause");
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onDestroy");
		releaseMediaRecorder();
		releaseCamera();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onBackPressed");
		
		if(mMediaRecorder != null)
			mMediaRecorder.stop();
		
		super.onBackPressed();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onKeyDown");
		return true;
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onKeyUp");
		return true;
	}
//	@Override
//	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		Log.e(TAG, "onKeyUp");
//		if(mMediaRecorder != null)
//			mMediaRecorder.stop();
//		mMediaRecorder = null;
//		
//		if(vedioOutFile != null)
//			refreshGallery(vedioOutFile);
//		return super.onKeyUp(keyCode, event);
//		
//	}
	
	private void resetCam() {
		mCamera.startPreview();
		preview.setCamera(mCamera, MainActivity.TAKE_PICTURE_ACTION);
	}

	private void refreshGallery(File file) {
//		MultiMediaScanner scanner = 
//				new MultiMediaScanner(this, new String[]{file.getAbsolutePath()}, null);
//		scanner.connect();
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
						 Log.e(TAG, "onShutter");
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
						 Log.e(TAG, "onPictureTaken raw");
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.e(TAG, "onPictureTaken");
			new SaveImageTask().execute(data);
//			finish();
			
		}
	};

	private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			finish();
		}
		@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
		@Override
		protected Void doInBackground(byte[]... data) {
			FileOutputStream outStream = null;
			
			// Write to SD Card
			try {
				File dir = new File(GLASSED_DATA_PATH + "photos");
				if(!dir.exists())
					dir.mkdirs();		
				
				SimpleDateFormat format = new SimpleDateFormat("yyyymmdd_HHmmss");
				long time = System.currentTimeMillis();
				Date date = new Date(time);
				String name = format.format(date);
				
				String fileName = String.format("%s.jpg", name);
				File outFile = new File(dir, fileName);
				Log.e(TAG, outFile.getAbsolutePath());
				if(!outFile.exists()) {
					outFile.createNewFile();
					Runtime.getRuntime().exec("chmod 777 " + outFile.getAbsolutePath());
				}
				
				outStream = new FileOutputStream(outFile);
				outStream.write(data[0]);
				outStream.flush();
				outStream.close();

				Log.e(TAG, "onPictureTaken - wrote bytes: " + data[0].length + " to " + outFile.getAbsolutePath());

//				refreshGallery(outFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			return null;
		}

	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onAutoFocus");
		camera.takePicture(shutterCallback, rawCallback, jpegCallback);
	}
	
	private boolean prepareVideoRecorder(){

		if(mCamera == null) {
			getCameraInstance(MainActivity.TAKE_VEDIO_ACTION);
			mMediaRecorder = new MediaRecorder();
		    // Step 1: Unlock and set camera to MediaRecorder
		}
		
		mCamera.unlock();
	    mMediaRecorder.setCamera(mCamera);

	    // Step 2: Set sources
	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

	    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
	    mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

	    // Step 4: Set output file
	    File dir = new File (GLASSED_DATA_PATH + "vedios");
	    if(!dir.exists())
			dir.mkdirs();			
	    
	    Log.e(TAG, "4444");
	    
		SimpleDateFormat format = new SimpleDateFormat("yyyymmdd_HHmmss");
		long time = System.currentTimeMillis();
		Date date = new Date(time);
		String fileName = format.format(date);
		
	    vedioOutFile = new File(dir, fileName + ".mp4");
	    if(!vedioOutFile.exists()) {
	    	try {
				vedioOutFile.createNewFile();
				Runtime.getRuntime().exec("chmod 777 " + vedioOutFile.getAbsolutePath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    mMediaRecorder.setOutputFile(vedioOutFile.getAbsolutePath());
	    // Step 5: Set the preview output
	    mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
	    
	    mMediaRecorder.setMaxDuration(vedioDuration*1000*10);
	    
	    mMediaRecorder.setOnInfoListener(myOnInfoListener);
	    
	    mMediaRecorder.setOnErrorListener(myOnErrorListener);

	    // Step 6: Prepare configured MediaRecorder
	    try {
	        mMediaRecorder.prepare();
	    } catch (IllegalStateException e) {
	        Log.e(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.e(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    return true;
	}
	
	private OnErrorListener myOnErrorListener = new OnErrorListener() {
		
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			// TODO Auto-generated method stub
			Log.e(TAG, "erro:" + what);
			finish();
		}
	};
	
	private OnInfoListener myOnInfoListener = new OnInfoListener() {

		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			// TODO Auto-generated method stub
			Log.e(TAG, "info:" + what);
			if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
				try {
					
					mMediaRecorder.stop();
					mMediaRecorder.reset();
					finish();
//					if(prepareVideoRecorder())
//						mMediaRecorder.start();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	};
	
	@SuppressLint("NewApi")
	private void getCameraInstance(int action) {
		int numCams = Camera.getNumberOfCameras();
		if(numCams > 0){
			try{
				mCamera = Camera.open(0);
				preview.setCamera(mCamera, action);
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	private void releaseMediaRecorder(){
		try {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	private void releaseCamera() {
		try {
		if(mCamera != null) {
			mCamera.stopPreview();
			preview.setCamera(null, -1);
			mCamera.release();
			mCamera = null;
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onPreviewFrame");
		Parameters parameters = camera.getParameters();
		Size size = parameters.getPreviewSize();
		
		ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
		ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
		FileOutputStream fileOut = null;
		try {
			
			YuvImage yuvImage = new YuvImage(data, 17, size.width, size.height, null);
			yuvImage.compressToJpeg(new Rect(0, 0, size.width, size.height), 100, bos1);
			
			File sdCard = Environment.getExternalStorageDirectory();
		    File dir = new File (sdCard.getAbsolutePath() + "/camtest");
		    if(!dir.exists())
				dir.mkdirs();				
		    Log.e(TAG, "12344");
		    String fileName = String.format("%d.jpg", System.currentTimeMillis());
		    File imageFile = new File(dir, fileName);
		    if(!imageFile.exists()) {
				imageFile.createNewFile();
				Runtime.getRuntime().exec("chmod 777 " + imageFile.getAbsolutePath());
		    }
		    
		    fileOut = new FileOutputStream(imageFile);
		    
		    fileOut.write(bos1.toByteArray());
		    fileOut.flush();
		    fileOut.close();
		    bos1.close();
		    bos2.close();
		    
		    refreshGallery(imageFile);
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}


