package com.jifeng.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jifeng.mlsales.jumeimiao.SettingActivity;
import com.jifeng.mlsales.model.UpdateDialog;
import com.jifeng.url.AllStaticMessage;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

@SuppressLint({ "HandlerLeak", "ShowToast" })
public class ApkModify {
	private Context context;
	RequestQueue request;
	private String path;
	private String code = "";
	private Handler mhandler = null;

	public ApkModify(Context context) {
		this.context = context;
		request = Volley.newRequestQueue(context);
	}

	public ApkModify(Context context, Handler handler) {
		this.mhandler = handler;
		this.context = context;
		request = Volley.newRequestQueue(context);

	}

	/*
	 * 
	 * �����Ի���֪ͨ�û����³���
	 */
	private void sendMsg() {
		Message message = new Message();
		message.what = 0x02;
		mhandler.sendMessage(message);
	}

	protected void showUpdataDialog() {
		final UpdateDialog alertDialog = new UpdateDialog(context);
		// alertDialog
		// .setTitle("1.�޸�������ʾ�հ����⡣\n2.�Ż����û����飬�������£����������١�\n3.�޸�����bug��app���и�������");
		alertDialog.setPositiveButton("ȡ��", new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mhandler != null) {
					sendMsg();
					alertDialog.dismiss();
				}
			}
		});
		alertDialog.setNegativeButton1("ȷ��", new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				downLoadApk();
				alertDialog.dismiss();
			}
		});

		// AlertDialog.Builder builer = new Builder(context);
		// builer.setTitle("�汾����");
		// // builer.setMessage(info.getDescription());
		// builer.setMessage("�����°汾" + code + ",������������ʹ��.");
		// // ����ȷ����ťʱ�ӷ����������� �µ�apk Ȼ��װ
		// builer.setPositiveButton("ȷ��", new OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		//
		// downLoadApk();
		// }
		// });
		// // ����ȡ����ťʱ���е�¼
		// builer.setNegativeButton("ȡ��", new OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		// LoginMain();
		// if (mhandler != null) {
		// sendMsg();
		// }
		// }
		// });
		// AlertDialog dialog = builer.create();
		// dialog.show();
		// dialog.setCanceledOnTouchOutside(false);
	}

	/*
	 * �ӷ�����������APK
	 */
	protected void downLoadApk() {
		final ProgressDialog pd; // �������Ի���
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("�������ظ���");
		pd.show();
		pd.setCanceledOnTouchOutside(false);

		new Thread() {
			@Override
			public void run() {
				try {

					File file = getFileFromServer(path, pd);
					sleep(3000);
					installApk(file);
					pd.dismiss(); // �������������Ի���
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = 2;
					handler.sendMessage(msg);
					e.printStackTrace();
				}
			}
		}.start();
	}

	// ��װapk
	protected void installApk(File file) {
		Intent intent = new Intent();
		// ִ�ж���
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		// ִ�е���������
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);

	}

	/*
	 * ��ȡ��ǰ����İ汾��
	 */
	private String getVersionName() throws Exception {
		// ��ȡpackagemanager��ʵ��
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		return packInfo.versionName;
	}

	public static File getFileFromServer(String path, ProgressDialog pd)
			throws Exception {
		// �����ȵĻ���ʾ��ǰ��sdcard�������ֻ��ϲ����ǿ��õ�
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			// ��ȡ���ļ��Ĵ�С
			pd.setMax(conn.getContentLength());
			InputStream is = conn.getInputStream();
			File updateDir = new File(Environment.getExternalStorageDirectory()
					+ "/Jumeimiao/tmp/apk/");
			if (!updateDir.exists()) {
				updateDir.mkdirs();
			}
			File file = new File(updateDir + "/", "Jumeimiao.apk");
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				// ��ȡ��ǰ������
				pd.setProgress(total);
			}
			fos.close();
			bis.close();
			is.close();
			return file;
		} else {
			return null;
		}
	}

	/*
	 * �ӷ�������ȡxml���������бȶ԰汾��
	 */
	public class CheckVersionTask implements Runnable {

		public void run() {

			JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
					Request.Method.GET, AllStaticMessage.URL_Modify_Apk, null,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							try {

								// Log.i("11111",ss);
								if (response != null) {
									// ����Դ�ļ���ȡ������ ��ַ
									path = response.getString("appUrl")
											.toString();
									code = response.getString("versionName")
											.toString();
									char c = code.charAt(code.length() - 1);
									char d = getVersionName().charAt(
											getVersionName().length() - 1);
									if (response.getString("versionName")
											.equals(getVersionName()) || c < d) {
										if (mhandler != null) {
											sendMsg();
										} else {
											if (context.getClass() == SettingActivity.class) {
												Toast.makeText(context,
														"�Ѿ������°汾", 1000).show();
											}
										}
									} else {
										Message msg = new Message();
										msg.what = 0;
										handler.sendMessage(msg);
									}
								} else {
									if (mhandler != null) {
										sendMsg();
									} else {
										// if(context.getClass()!=FirstActivity.class){
										// Toast.makeText(context,"�Ѿ������°汾",
										// 1000).show();
										// }
									}

								}
							} catch (Exception e) {
								// ������
								Message msg = new Message();
								msg.what = 1;
								handler.sendMessage(msg);
								e.printStackTrace();
							}
						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError arg0) {
							Message msg = new Message();
							msg.what = 1;
							handler.sendMessage(msg);
							System.out.println("sorry,Error");
						}
					});

			request.add(jsonObjectRequest);
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				// �Ի���֪ͨ�û���������
				showUpdataDialog();
				break;
			case 1:
				// ��������ʱ
				Toast.makeText(context.getApplicationContext(), "��ȡ������������Ϣʧ��",
						1).show();
				if (mhandler != null) {
					sendMsg();
				}
				break;
			case 2:
				// ����apkʧ��
				Toast.makeText(context.getApplicationContext(), "�����°汾ʧ��", 1)
						.show();
				if (mhandler != null) {
					sendMsg();
				}
				break;
			}
		}
	};
}
