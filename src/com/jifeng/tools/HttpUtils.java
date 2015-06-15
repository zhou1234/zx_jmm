package com.jifeng.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class HttpUtils {
	// ����һ����̬�ķ�����ȡJSON����
	// �����path��web�������ַ
	// URLEncoder.encode(mySpinner.getSelectedItem().toString(), "utf-8")
	// public static String getJsonContent(String path, String param) {
	// try {
	// // ����·������URL��ַ
	// URL url = new URL(path + "?" + param);
	// // ͨ��url��ַ������
	// Log.v("URL��", url.toString());
	// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	// // ���ó�ʱʱ��
	// conn.setConnectTimeout(10000);
	// // ��������ʽ
	// conn.setRequestMethod("GET");
	// // ��������
	// // ���ø������Ƿ������
	// conn.setDoInput(true);
	// int code = conn.getResponseCode();
	// System.out.println(code + "****");
	// if (code == 200) {
	// return changeInputString(conn.getInputStream());
	// }
	// // else {
	// // return changeInputString(conn.getErrorStream());
	// // }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return "";
	// }

	/*
	 * �Զ��巽������io���õ��ַ���
	 */
//	public static String changeInputString(InputStream is) {
//		String jsonString = "";
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		byte[] data = new byte[1024];
//		int len = 0;
//		try {
//			if (is != null) {
//				while ((len = is.read(data)) != -1) {
//					baos.write(data, 0, len);
//				}
//				jsonString = new String(baos.toByteArray(),
//						Charset.defaultCharset());
//				is.close();
//			}
//			baos.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return jsonString;
//	}

	public static String myPost(String url, String Post_String) {
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		OutputStream outputStrean;

		try {
			conn = (HttpURLConnection) (new URL(url)).openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		conn.setDoInput(true);
		conn.setDoOutput(true);
		// conn.setConnectTimeout(5000);
		try {
			conn.setRequestMethod("POST");
			outputStrean = conn.getOutputStream();
			outputStrean.write(Post_String.getBytes());
			outputStrean.flush();
			inputStream = conn.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getString(inputStream);
	}

	protected static String getString(InputStream inputstream) {

		int length = 10000;

		StringBuffer stringBuffer = new StringBuffer();
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputstream, HTTP.UTF_8);
			char buffer[] = new char[length];
			int count;
			while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
				stringBuffer.append(buffer, 0, count);
			}
		} catch (Exception e) {
			return null;
		}
		return stringBuffer.toString();
	}

	/**
	 * ��ȡ����ͼƬ��Դ
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getHttpBitmap(String url) {
		URL myFileURL;
		Bitmap bitmap = null;
		try {
			myFileURL = new URL(url);
			// �������
			HttpURLConnection conn = (HttpURLConnection) myFileURL
					.openConnection();
			// ���ó�ʱʱ��Ϊ6000���룬conn.setConnectionTiem(0);��ʾû��ʱ������
			conn.setConnectTimeout(6000);
			// �������û��������
			conn.setDoInput(true);
			// ��ʹ�û���
			conn.setUseCaches(false);
			// �����п��ޣ�û��Ӱ��
			// conn.connect();
			// �õ�������
			InputStream is = conn.getInputStream();
			// �����õ�ͼƬ
			bitmap = BitmapFactory.decodeStream(is);
			// �ر�������
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;

	}

	/*
	 * ��ȡ�ֻ��������ӹ�����󣨰�����wi-fi,net�����ӵĹ���
	 */
	public static boolean checkNet(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// ��ȡ�������ӹ���Ķ���
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// �жϵ�ǰ�����Ƿ��Ѿ�����
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Boolean sendJsonContent(String path, JSONObject jsonObject) {
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(path);
		try {
			// jsonObject = new JSONObject();
			// jsonObject.put("name", "ze");
			StringEntity entity = new StringEntity(jsonObject.toString());
			post.setEntity(entity);
			HttpResponse responString = client.execute(post);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
