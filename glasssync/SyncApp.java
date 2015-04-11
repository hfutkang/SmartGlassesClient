package cn.ingenic.glasssync;

import java.io.IOException;
import java.lang.reflect.Constructor;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Application;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;
import cn.ingenic.glasssync.DefaultSyncManager;
import cn.ingenic.glasssync.Enviroment;
import cn.ingenic.glasssync.LogTag;
import cn.ingenic.glasssync.SystemModule;
import cn.ingenic.glasssync.devicemanager.DeviceModule;
import cn.ingenic.glasssync.phone.PhoneModule;
import cn.ingenic.glasssync.services.SyncModule;
import android.widget.Toast;

public class SyncApp extends Application implements
		Enviroment.EnviromentCallback {

	private final static String TAG = "SyncApp";
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "onCreate");
		if (LogTag.V) {
			Log.d(LogTag.APP, "Sync App created.");
		}
		Enviroment.init(this);
		DefaultSyncManager manager = DefaultSyncManager.init(this);

		SystemModule systemModule = new SystemModule();

		//share
//		ShareModule sharemodule=ShareModule.getInstance(this);
//		VoiceShareModule.getInstance(this);

		  //for get position by baiduSDK
//		PositionModule positionModule=PositionModule.getInstance(this);
//		HTTPGetModule httpgetModule=HTTPGetModule.getInstance(this);


		  //for city collection 
//		CitiesModule citiesModule=CitiesModule.getInstance(this);

		if (manager.registModule(systemModule)) {
		 	Log.i(LogTag.APP, "SystemModule is registed.");
		 }

		//UpdaterModule um = new UpdaterModule();
		// if (manager.registModule(um)) {
		// 	Log.i(LogTag.APP, "UpdaterModule is registed.");
		// }

		PhoneModule pm = new PhoneModule();
		 if (manager.registModule(pm)) {
		 	Log.i(LogTag.APP, "PhoneModule  registed");
		 }

//		CameraModule cm = new CameraModule();
//		 if (manager.registModule(cm)) {
//		 	Log.i(LogTag.APP, "CameraModule  registed");
//		 }

		DeviceModule dm = DeviceModule.getInstance();
	        if (manager.registModule(dm)) {
		    Log.i(LogTag.APP, "DeviceModule  registed");
		}

//		SyncModule contactLite = ContactsLiteModule.getInstance(this);
//		contactLite.getMidTableManager().startObserve();
		
		// app manager
//		AppManagerModule.getInstance(this);
		
		//sms
//		SmsModule module=SmsModule.getInstance(this);
//                SMSSendModule sendModule = SMSSendModule.getInstance(this);
//		module.getMidTableManager().startObserve();
		
		//screen controller
//		ScreenControlModule.getInstance(this);
//		ScreenModule.getInstance(this);
//		
//		ImeSyncModule.getInstance(this);
//		if (android.os.Build.VERSION.SDK_INT >= 14) {
		    //CalendarModule calm = new CalendarModule();
			// if (manager.registModule(calm)) {
			// 	Log.i(LogTag.APP, "CalendarModule registed");
			// }
//		}

//		XmlResourceParser parser = getResources().getXml(R.xml.modules);
//		try {
//			XmlUtils.beginDocument(parser, "modules");
//			loadModules(parser);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			parser.close();
//		}
		// Intent intent = new Intent(
		// DefaultSyncManager.RECEIVER_ACTION_SYNC_SERVICE_COMPLETE);
		// sendBroadcast(intent);

//		    GlassSyncWifiManager wifimanage = GlassSyncWifiManager.getInstance(this);
//		    MultiMediaManager mmmg = MultiMediaManager.getInstance(this);
//		    GlassSyncLbsManager gslbs = GlassSyncLbsManager.getInstance(this);
//
//		    GlassDetect gdt = GlassDetect.getInstance(this);
	}

//	private void loadModules(XmlResourceParser parser) {
//		if (parser != null) {
//			while (true) {
//				try {
//					XmlUtils.nextElement(parser);
//					if (!"module".equals(parser.getName())) {
//						break;
//					}
//					registModule(parser);
//				} catch (XmlPullParserException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//			}
//		}
//	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void registModule(XmlResourceParser parser) {
		String className = parser.getAttributeValue(null, "class");
		try {
			Class c = Class.forName(className);
			Constructor constructor = c.getConstructor(Context.class);
			constructor.newInstance(this);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	@Override
	public Enviroment createEnviroment() {
		return new PhoneEnviroment(this);
	}

}