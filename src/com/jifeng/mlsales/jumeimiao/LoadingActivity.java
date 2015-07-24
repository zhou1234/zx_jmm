package com.jifeng.mlsales.jumeimiao;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.ApkModify;
import com.jifeng.tools.MyTools;
import com.jifeng.tools.ShrefUtil;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class LoadingActivity extends Activity {
	private Intent mIntent;
	private ShrefUtil mShrefUtil;
	LoadingDialog dialog;
	private boolean isFirstRun;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0x02:
				onlyOne();
				break;
			case 0x01:
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// if (isFirstRun) {
						// mIntent = new Intent(LoadingActivity.this,
						// WelcomeActivity.class);
						// } else {
						// mIntent = new Intent(LoadingActivity.this,
						// TabHostActivity.class);
						// }
						// startActivity(mIntent);
						// overridePendingTransition(R.anim.in_from_right,
						// R.anim.out_to_left);
						// LoadingActivity.this.finish();

						mIntent = new Intent(LoadingActivity.this,
								TabHostActivity.class);
						startActivity(mIntent);
						finish();
					}
				}, 700);

				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectDiskReads().detectDiskWrites().detectNetwork() //
		// 这里或者用.detectAll()方法
		// .penaltyLog().build());
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		// .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
		// .build());
		setContentView(R.layout.activity_loading);
		((FBApplication) getApplication()).addActivity(this);

		dialog = new LoadingDialog(this);
		mShrefUtil = new ShrefUtil(this, "data");

		if (MyTools.checkNetWorkStatus(LoadingActivity.this)) {
			ApkModify apkModify = new ApkModify(this, handler);
			apkModify.new CheckVersionTask().run();
		} else {

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void onlyOne() {
		SharedPreferences sharedPreferences = this.getSharedPreferences(
				"share", MODE_PRIVATE);
		isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
		Editor editor = sharedPreferences.edit();

		if (isFirstRun) {
			false_save();
			// Log.d("debug", "第一次运行");
			AllStaticMessage.guideString = true;
			editor.putBoolean("isFirstRun", false);
			editor.commit();
			handler.sendEmptyMessage(0x01);
		} else {
			// mImageView.setVisibility(View.VISIBLE);
			AllStaticMessage.User_Name = mShrefUtil.readString("user_name");
			AllStaticMessage.User_Psd = mShrefUtil.readString("user_psd");
			if (!AllStaticMessage.User_Name.equals("")) {
				if (MyTools.checkNetWorkStatus(LoadingActivity.this)) {
					doLogin(AllStaticMessage.User_Name,
							AllStaticMessage.User_Psd, "web", "", "", "", "",
							"");
				} else {
					handler.sendEmptyMessage(0x01);
				}
			} else {
				handler.sendEmptyMessage(0x01);
			}
		}
	}

	private void false_save() {
		mShrefUtil.write("user_name", "");
		mShrefUtil.write("user_psd", "");
		mShrefUtil.write("songhuo_address", "");
	}

	private void doLogin(String userName, String psd, String loginType,
			String openid, String deviceType, String gender, String nickName,
			String address) {

		String url = AllStaticMessage.URL_Login + userName + "&password=" + psd
				+ "&loginType=" + loginType + "&openid=" + openid
				+ "&deviceType=" + deviceType + "&gender=" + gender
				+ "&nickName=" + nickName + "&address=" + address + "&udid="
				+ MyTools.getAndroidID(LoadingActivity.this);
		// Log.i("11111", url);
		HttpUtil.get(url, LoadingActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
								AllStaticMessage.Login_Flag = response
										.getString("auth").toString();
								AllStaticMessage.User_Id = response.getString(
										"Id").toString();
								AllStaticMessage.User_JiFen = response
										.getString("Score").toString();
								AllStaticMessage.User_NickName = response
										.getString("NickName").toString();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						handler.sendEmptyMessage(0x01);
					}

					@Override
					public void onStart() {
						super.onStart();
						// 请求开始

					}

					@Override
					public void onFinish() {
						super.onFinish();
						// 请求结束
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// 错误返回JSONObject
						handler.sendEmptyMessage(0x01);
					}
				});
	}
}
