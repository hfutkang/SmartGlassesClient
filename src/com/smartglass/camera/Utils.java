package com.smartglass.camera;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

public class Utils {
	
	private static final String TAG = "Utils";
	
	public static String getLocalIpAddress() {
		String ip = null;
		try {
		    // 遍历网络接口
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
			        .hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				if(intf.getName().contains("wlan")) {
					// 遍历IP地址
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
					        .hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						byte addrs[] = inetAddress.getAddress();
						
						Log.e(TAG, inetAddress.getHostAddress().toString());
						// 非回传地址时返回
						if (!inetAddress.isLoopbackAddress()) {
							String addr = inetAddress.getHostAddress();
							Pattern pattern = Pattern.compile("[192.[0-9]*.[0-9]*.[0-9]*");
							Matcher matcher = pattern.matcher(addr);
							if(matcher.matches())
								ip = inetAddress.getHostAddress().toString();
			            }
			        }
			    }
			}
		return ip;
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}
}
