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
	 * 弹出对话框通知用户更新程序
	 */
	private void sendMsg() {
		Message message = new Message();
		message.what = 0x02;
		mhandler.sendMessage(message);
	}

	protected void showUpdataDialog() {
		final UpdateDialog alertDialog = new UpdateDialog(context);
//		alertDialog
//				.setTitle("1.修复订单显示空白问题。\n2.优化了用户体验，更多活动上新，抢购更快速。\n3.修复各种bug，app运行更流畅。");
		alertDialog.setPositiveButton("取消", new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mhandler != null) {
					sendMsg();
					alertDialog.dismiss();
				}
			}
		});
		alertDialog.setNegativeButton1("确定", new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				downLoadApk();
				alertDialog.dismiss();
			}
		});

		// AlertDialog.Builder builer = new Builder(context);
		// builer.setTitle("版本升级");
		// // builer.setMessage(info.getDescription());
		// builer.setMessage("发现新版本" + code + ",建议立即更新使用.");
		// // 当点确定按钮时从服务器上下载 新的apk 然后安装
		// builer.setPositiveButton("确定", new OnClickListener() {
		// public void onClick(DialogInterface dialog, int which) {
		//
		// downLoadApk();
		// }
		// });
		// // 当点取消按钮时进行登录
		// builer.setNegativeButton("取消", new OnClickListener() {
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
	 * 从服务器中下载APK
	 */
	protected void downLoadApk() {
		final ProgressDialog pd; // 进度条对话框
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载更新");
		pd.show();
		pd.setCanceledOnTouchOutside(false);

		new Thread() {
			@Override
			public void run() {
				try {

					File file = getFileFromServer(path, pd);
					sleep(3000);
					installApk(file);
					pd.dismiss(); // 结束掉进度条对话框
				} catch (Exception e) {
					Message msg = new Message();
					msg.what = 2;
					handler.sendMessage(msg);
					e.printStackTrace();
				}
			}
		}.start();
	}

	// 安装apk
	protected void installApk(File file) {
		Intent intent = new Intent();
		// 执行动作
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);

	}

	/*
	 * 获取当前程序的版本号
	 */
	private String getVersionName() throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		return packInfo.versionName;
	}

	public static File getFileFromServer(String path, ProgressDialog pd)
			throws Exception {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			// 获取到文件的大小
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
				// 获取当前下载量
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
	 * 从服务器获取xml解析并进行比对版本号
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
									// 从资源文件获取服务器 地址
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
														"已经是最新版本", 1000).show();
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
										// Toast.makeText(context,"已经是最新版本",
										// 1000).show();
										// }
									}

								}
							} catch (Exception e) {
								// 待处理
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
				// 对话框通知用户升级程序
				showUpdataDialog();
				break;
			case 1:
				// 服务器超时
				Toast.makeText(context.getApplicationContext(), "获取服务器更新信息失败",
						1).show();
				if (mhandler != null) {
					sendMsg();
				}
				break;
			case 2:
				// 下载apk失败
				Toast.makeText(context.getApplicationContext(), "下载新版本失败", 1)
						.show();
				if (mhandler != null) {
					sendMsg();
				}
				break;
			}
		}
	};
}
