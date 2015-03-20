package com.sctek.smartglasses_device.camera;

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

import com.sctek.smartglasses_device.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
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
	private Preview preview;
	private Button buttonClick;
	private Camera mCamera;
	private Activity act;
	private Context ctx;
	private MediaRecorder mMediaRecorder;
	private SurfaceView mSurfaceView;
	private File vedioOutFile;

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
			if(numCams > 0){
				try{
					mCamera = Camera.open(0);
					mCamera.startPreview();
					preview.setCamera(mCamera, MainActivity.TAKE_PICTURE_ACTION);
				} catch (RuntimeException ex){
					ex.printStackTrace();
				}
			}
			
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.e(TAG, "autoFocus");
					mCamera.autoFocus(CameraActivity.this);
				}
			}, 1000);
		}
		
		if(getIntent().getIntExtra("action", MainActivity.TAKE_PICTURE_ACTION) == 
				MainActivity.TAKE_VEDIO_ACTION) {
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					prepareVideoRecorder();
					mMediaRecorder.start();
				}
			}, 1000);
			
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
		releaseMediaRecorder();
		releaseCamera();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onBackPressed");
		mMediaRecorder.stop();
		if(vedioOutFile != null)
			refreshGallery(vedioOutFile);
		super.onBackPressed();
	}

	private void resetCam() {
		mCamera.startPreview();
		preview.setCamera(mCamera, MainActivity.TAKE_PICTURE_ACTION);
	}

	private void refreshGallery(File file) {
		Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(Uri.fromFile(file));
		sendBroadcast(mediaScanIntent);
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			//			 Log.d(TAG, "onShutter'd");
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			//			 Log.d(TAG, "onPictureTaken - raw");
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.e(TAG, "onPictureTaken - jpeg");
			new SaveImageTask().execute(data);
//			finish();
			
		}
	};

	private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

		@Override
		protected Void doInBackground(byte[]... data) {
			FileOutputStream outStream = null;

			// Write to SD Card
			try {
				File sdCard = Environment.getExternalStorageDirectory();
				File dir = new File (sdCard.getAbsolutePath() + "/camtest");
				dir.mkdirs();				

				String fileName = String.format("%d.jpg", System.currentTimeMillis());
				File outFile = new File(dir, fileName);

				outStream = new FileOutputStream(outFile);
				outStream.write(data[0]);
				outStream.flush();
				outStream.close();

				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

				refreshGallery(outFile);
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

		getCameraInstance(MainActivity.TAKE_VEDIO_ACTION);
	    
	    mMediaRecorder = new MediaRecorder();

	    // Step 1: Unlock and set camera to MediaRecorder
	    mCamera.unlock();
	    mMediaRecorder.setCamera(mCamera);

	    // Step 2: Set sources
	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

	    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
	    mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

	    // Step 4: Set output file
	    File sdCard = Environment.getExternalStorageDirectory();
	    File dir = new File (sdCard.getAbsolutePath() + "/camtest");
	    if(!dir.exists())
			dir.mkdirs();				

	    String fileName = String.format("%d.mp4", System.currentTimeMillis());
	    vedioOutFile = new File(dir, fileName);
	    
	    mMediaRecorder.setOutputFile(vedioOutFile.getAbsolutePath());
	    // Step 5: Set the preview output
	    mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());

	    // Step 6: Prepare configured MediaRecorder
	    try {
	        mMediaRecorder.prepare();
	    } catch (IllegalStateException e) {
	        Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    return true;
	}
	
	@SuppressLint("NewApi")
	private void getCameraInstance(int action) {
		int numCams = Camera.getNumberOfCameras();
		if(numCams > 0){
			try{
				mCamera = Camera.open(0);
				mCamera.startPreview();
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
	
		    String fileName = String.format("%d.jpg", System.currentTimeMillis());
		    File imageFile = new File(dir, fileName);
		    if(!imageFile.exists())
				imageFile.createNewFile();
		    
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


