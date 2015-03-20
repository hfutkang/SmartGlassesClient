package com.sctek.smartglasses_device.camera;

import com.sctek.smartglasses_device.R;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity {
	
	public final static int TAKE_PICTURE_ACTION = 0;
	public final static int TAKE_VEDIO_ACTION = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void onTakePictureButtonClicked(View v) {
		Intent intent = new Intent(MainActivity.this, CameraActivity.class);
		intent.putExtra("action", TAKE_PICTURE_ACTION);
		startActivity(intent);
	}
	
	public void onTakeVedioButtonClicked(View v) {
		Intent intent = new Intent(MainActivity.this, CameraActivity.class);
		intent.putExtra("action", TAKE_VEDIO_ACTION);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
