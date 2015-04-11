package cn.ingenic.glasssync;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import android.R.layout;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ListView;

import java.util.List;
import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.lang.reflect.Method;

import com.sctek.smartglasses.R;
import com.sctek.smartglasses.ui.MainActivity;

import cn.ingenic.glasssync.DefaultSyncManager;
import android.widget.AdapterView.OnItemClickListener;
public class BindGlassActivity extends Activity implements OnClickListener {
    private final String TAG = "OtherBindActivity";
    private final static int PAIRED = 0;
    private final static int FOUND = 1;
    public final static int BIND_TIMEOUT = 2;
    public final static int CONNECT = 3;
    public final static int REQUEST_PAIR = 4;
    private static final boolean DEBUG = true;
    private DefaultSyncManager mManager;
    private TextView tv_bindstate,tv_name_info,tv_address_info;
    private BluetoothAdapter mAdapter;
    private LinearLayout mDevice_Info;
    private boolean mIsScan=false;
    private boolean mStartDiscovery = false;
    private static final String REMOTE_BT_MAC="coldwave";
    private TextView mOtherBind;
    private ListView mListView,mFoundListView;
    private ShowBtAdapter mBtAdapter;
    private ShowBtFoundAdapter mBtFoundAdapter;
    private List<BluetoothDevice> mList = new ArrayList<BluetoothDevice>();
    private List<BluetoothDevice> mFoundList = new ArrayList<BluetoothDevice>();
    private Handler mHandler = new Handler(){  
	    @Override  
	    public void handleMessage(Message msg) {  
		switch(msg.what){
		case PAIRED:
		    mBtAdapter = new ShowBtAdapter(mList,BindGlassActivity.this,mHandler);
		    mListView.setAdapter(mBtAdapter);
		    break;
		case FOUND:
		    mBtFoundAdapter = new ShowBtFoundAdapter(mFoundList,BindGlassActivity.this,mHandler);
		    mFoundListView.setAdapter(mBtFoundAdapter);
		    break;
		case BIND_TIMEOUT:
		    mHandler.removeMessages(BIND_TIMEOUT);
		    Toast.makeText(BindGlassActivity.this, R.string.bind_timeout,Toast.LENGTH_SHORT).show();
		    tv_bindstate.setText(R.string.key_binding);
		    break;
		case CONNECT:
		    tv_bindstate.setText(R.string.bind_device);
		    mStartDiscovery = false;
		    mAdapter.cancelDiscovery();
		    String address = (String)msg.obj;
		    try {
		    	mManager.connect(address);
		    } catch (Exception e) {
		    	// TODO Auto-generated catch block
		    	e.printStackTrace();
		    }
		    break;
		case REQUEST_PAIR:
		    tv_bindstate.setText(R.string.bluetooth_pairing);
		    mStartDiscovery = false;
		    mAdapter.cancelDiscovery();
		    BluetoothDevice bluetoothDevice = (BluetoothDevice)msg.obj;
		    try {			
			Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
			createBondMethod.invoke(bluetoothDevice);			
		    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		    break;
		}
	    }
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.other_bind_activity);
	tv_bindstate = (TextView) findViewById(R.id.tv_bindstate);
	// tv_name_info = (TextView) findViewById(R.id.tv_name_info);
	// tv_address_info = (TextView) findViewById(R.id.tv_address_info);
	//mDevice_Info=(LinearLayout)findViewById(R.id.device_info);
	mOtherBind = (TextView)findViewById(R.id.other);
	mListView = (ListView)findViewById(R.id.paired_listView);
	mFoundListView = (ListView)findViewById(R.id.ot_listView);
	
	mOtherBind.setOnClickListener(this);
	tv_bindstate.setOnClickListener(this);
	mManager = DefaultSyncManager.getDefault();
	mAdapter = BluetoothAdapter.getDefaultAdapter();
	initAdapter();

	IntentFilter filter = new IntentFilter();
	filter.addAction(DefaultSyncManager.RECEIVER_ACTION_STATE_CHANGE);
	filter.addAction(BluetoothDevice.ACTION_FOUND);
	filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
	filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
	filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	filter.addAction(DefaultSyncManager.RECEIVER_ACTION_DISCONNECTED);
	registerReceiver(mBluetoothReceiver, filter);
    }
    private void initAdapter(){
	new Thread(){
	    @Override 
	    public void run() {


		Message msg = mHandler.obtainMessage();
		msg.what = PAIRED;
		mHandler.sendMessageDelayed(msg,0);

		Message msg1 = mHandler.obtainMessage();
		msg1.what = FOUND;
		mHandler.sendMessageDelayed(msg1,0);
		scanDevice();

	    }
	}.start();	
    }

    private void scanDevice() {
	if(mAdapter.isDiscovering()){
	    // if "phone system" settings has already startDiscovery to scan bluetooth device, 
	    // we need to cancelDiscovery;
	    mAdapter.cancelDiscovery();
	    tv_bindstate.setText(R.string.key_binding);
	    return;
	}
	mStartDiscovery = true;
	mAdapter.startDiscovery();
    }

    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
	    @Override
		public void onReceive(Context context, Intent intent) {
		if (DEBUG)
		    Log.d(TAG, "rcv " + intent.getAction());
		if(DefaultSyncManager.RECEIVER_ACTION_DISCONNECTED.equals(intent.getAction())){
		    tv_bindstate.setText(R.string.key_binding);
		    mHandler.removeMessages(BIND_TIMEOUT);
		}
		if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
		    BluetoothDevice scanDevice = intent
			.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

		    if (DEBUG)Log.d(TAG, scanDevice.getName()+"bonded state");
		
		    if(scanDevice == null || scanDevice.getName() == null)return;

		    switch (scanDevice.getBondState()) {
		    case BluetoothDevice.BOND_NONE:
			if (DEBUG)Log.d(TAG, "BluetoothDevice.BOND_NONE");
			//if (!scanDevice.getName().equals("IGlass")) {//IGlass

			    mIsScan=true;
			    tv_bindstate.setText(R.string.pair_device);
			    if (!mFoundList.contains(scanDevice)){
				mFoundList.add(scanDevice);
				mBtFoundAdapter.notifyDataSetChanged();
				setListViewHeightBasedOnChildren(mFoundListView);				
			    }
			    //}
			break;
		    case BluetoothDevice.BOND_BONDED:
			if (DEBUG)Log.d(TAG, "device ===== bonded");
			//if(scanDevice.getName().equals("IGlass")){
			    mIsScan=true;
			    if (!mList.contains(scanDevice)){
				mList.add(scanDevice);
				mBtAdapter.notifyDataSetChanged();
				setListViewHeightBasedOnChildren(mListView);
			    }
			    break;
		    }								
		}else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent
									   .getAction())) {
		    if (DEBUG)
			Log.e(TAG, "ACTION_BOND_STATE_CHANGED");
		    BluetoothDevice device = intent
			.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		    if (DEBUG)Log.d(TAG, device.getBondState() + "Other activity===bonded");
		    switch (device.getBondState()) {
		    case BluetoothDevice.BOND_BONDED:
			//	if (device.getName().equals("IGlass")) {
			    tv_bindstate.setText(R.string.pair_success);
			    if (!mList.contains(device)){
				mList.add(device);
				mBtAdapter.notifyDataSetChanged();
				setListViewHeightBasedOnChildren(mListView);
			    }
			    //}
		    break;
		    case BluetoothDevice.BOND_BONDING:
		    if (DEBUG)
			Log.e(TAG, "BOND_BONDING");
			    tv_bindstate.setText(R.string.bluetooth_pairing);
			    break;
		    case BluetoothDevice.BOND_NONE:
			tv_bindstate.setText(R.string.pair_fail);
			break;
		    }
		}else if (DefaultSyncManager.RECEIVER_ACTION_STATE_CHANGE
			 .equals(intent.getAction())) {
			try {
		    int state = intent.getIntExtra(DefaultSyncManager.EXTRA_STATE,
						   DefaultSyncManager.IDLE);
		    boolean isConnect = (state == DefaultSyncManager.CONNECTED) ? true : false;
		    if (DEBUG) Log.d(TAG, isConnect + "    isConnect");
		    if (isConnect) {
			String addr = mManager.getLockedAddress();
			if(addr.equals("")){
			      //local has disconnect last,but remote not get notification
			      //notify again
			    Log.d(TAG, "local has disconnect,but remote not get notificaton.notify again!");
			    mManager.disconnect();
			}else{
			    mManager.setLockedAddress(addr);
			      //unregisterReceiver(mBluetoothReceiver);
			    mHandler.removeMessages(BIND_TIMEOUT);
			    Intent bind_intent = new Intent(BindGlassActivity.this,
							    MainActivity.class);
			    startActivity(bind_intent);
			    finish();
			}
		    }else{
			if( tv_bindstate.getText().toString().equals(context.getString(R.string.bind_device)) )
			    tv_bindstate.setText(R.string.disconnect);
		    }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())){
		    if (DEBUG)Log.d(TAG, "Discovery finished ");
		    if(!mStartDiscovery)
			return;
		    if(mIsScan){
		    	tv_bindstate.setText(R.string.key_binding);
		    }else{
		    	tv_bindstate.setText(R.string.no_found_device);
		    }
		    mStartDiscovery = false;			
		}
	    }
	};

    @Override
	public void onClick(View v) {
	// TODO Auto-generated method stub
	switch (v.getId()) {
	case R.id.tv_bindstate:
	    //tv_binding.setText(R.string.scan_device);	   
	    if(mAdapter.isDiscovering()) {
		tv_bindstate.setText(R.string.pair_device);
		return;
	    }
	    mList.clear();
	    mFoundList.clear();
	    mBtAdapter.notifyDataSetChanged();
	    mBtFoundAdapter.notifyDataSetChanged();
	    setListViewHeightBasedOnChildren(mFoundListView);
	    setListViewHeightBasedOnChildren(mListView);
	    scanDevice();	
	    return;
	case R.id.other:
//	    Intent intent=new Intent(BindGlassActivity.this,QRCodeActivity.class);
//	    startActivity(intent);
//	    finish();
	    return;
	default: break;
	}
    }

    @Override
	protected void onDestroy() {
	super.onDestroy();			
	if(DEBUG){
	    Log.d(TAG, "unregisterRecevicer");
	}
	unregisterReceiver(mBluetoothReceiver);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
      
	ListAdapter listAdapter = listView.getAdapter();    
	if (listAdapter == null) {    
	    return;  
	}
    
	ViewGroup.LayoutParams params = listView.getLayoutParams();  
  
	if(listAdapter.getCount()==0){
	    params.height = 0;     
	    listView.setLayoutParams(params);  
	}else{
	    View listItem = listAdapter.getView(0, null, listView);
	    if(listItem == null)return;
	    listItem.measure(0, 0);
	    int height = listItem.getMeasuredHeight();
	    params.height = (height + listView.getDividerHeight())*listAdapter.getCount();     
	    listView.setLayoutParams(params);     
	}
    }
}
