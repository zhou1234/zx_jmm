package com.jifeng.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
public class DownPic {
	
	
	
	
	// œ¬‘ÿÕº∆¨
		public static void downloadLy1(String url) throws Exception {
			final Handler handler = new Handler() {
				public void handleMessage(android.os.Message msg) {
					Bitmap bitmap1 = null;
					byte[] data1 = (byte[]) msg.obj;
					if (data1 != null) {
						bitmap1 = BitmapFactory.decodeByteArray(data1, 0,
								data1.length);
						saveImg(bitmap1);
					}
				};
			};
			final String url1 = url;
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						byte[] data = getImage(url1);
						Message msg = handler.obtainMessage();
						msg.what = 001;
						msg.obj = data;
						handler.sendMessage(msg);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}).start();

		}

	private static byte[] getImage(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		InputStream inStream = conn.getInputStream();
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return readStream(inStream);
		}
		return null;
	}

	private static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

	//
	private static void saveImg(Bitmap bitmap) {
		if (bitmap != null) {
			FileOutputStream fos = null;
			try {
				String path = android.os.Environment
						.getExternalStorageDirectory() + "/JuMeiMiao/pic/";
				File file = new File(path);
				if (!file.exists()) {
					file.mkdirs();
				}
				File file2 = null;
				file2 = new File(path + "Myshare.jpg");

				fos = new FileOutputStream(file2);
				if (null != fos) {
					bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
					fos.flush();
					fos.close();
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}
