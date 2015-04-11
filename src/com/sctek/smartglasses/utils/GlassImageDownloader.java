package com.sctek.smartglasses.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import com.nostra13.universalimageloader.utils.IoUtils;

import android.net.Uri;
import android.util.Log;

public class GlassImageDownloader {
	
	public static final String TAG = "GlassImageDownloader";
	
	public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
	
	public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds
	
	protected static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
	
	protected static final int MAX_REDIRECT_COUNT = 5;
	
	public InputStream getInputStream(String uri) throws IOException {
		
		HttpURLConnection conn = createConnection(uri);
		
//		int redirectCount = 0;
//		while (conn.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT) {
//			conn = createConnection(conn.getHeaderField("Location"));
//			redirectCount++;
//		}
//		
		InputStream imageStream;
		try {
			conn.setDoInput(true); 
			conn.connect();
			imageStream = conn.getInputStream();
		} catch (IOException e) {
			// Read all data to allow reuse connection (http://bit.ly/1ad35PY)
			IoUtils.readAndCloseStream(conn.getErrorStream());
			throw e;
		}
		if (conn.getResponseCode() != 200) {
			IoUtils.closeSilently(imageStream);
			throw new IOException("Image request failed with response code " + conn.getResponseCode());
		}

		return imageStream;
	}
	
	public HttpURLConnection createConnection(String url) throws IOException {
		
		String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
		HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
		conn.setConnectTimeout(DEFAULT_HTTP_CONNECT_TIMEOUT);
		conn.setReadTimeout(DEFAULT_HTTP_READ_TIMEOUT);
		
		return conn;
		
	}
	
	public static boolean deleteRequestExecute(HttpClient httpclient, HttpGet httpget) {
		
		BufferedReader in = null;
		
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
			if(result != null)
				Log.e(TAG, result.toString());
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
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

}
