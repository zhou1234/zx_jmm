package com.jifeng.mlsales.jumeimiao;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdviceActivity extends Activity {
	private EditText mEditText;
	private Button mButton;
	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advice);
		dialog = new LoadingDialog(this);
		findView();
	}

	@SuppressLint("ShowToast")
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.advice_back:
			finish();
			break;
		case R.id.advice_btn:
			String msg = mEditText.getText().toString().trim();
			if (msg.equals("") || msg == null) {
				Toast.makeText(AdviceActivity.this, "请输入您的宝贵意见", 500).show();
				return;
			}
			dialog.loading();
			sendMessage(msg);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		ShareSDK.stopSDK(this);
		setContentView(R.layout.view_null);
		super.onDestroy();
		dialog = null;

		mEditText = null;
		mButton = null;
		this.finish();
		System.gc();
	}

	private void findView() {
		mEditText = (EditText) findViewById(R.id.advice_edt);
		mButton = (Button) findViewById(R.id.advice_btn);
	}

	private void sendMessage(String message) {
		String url = AllStaticMessage.URL_Advice + AllStaticMessage.User_Id
				+ "&info=" + message;
		HttpUtil.get(url, AdviceActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@SuppressLint("ShowToast")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								Toast.makeText(AdviceActivity.this, "ok", 500)
										.show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (dialog != null) {
							dialog.stop();
						}
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
						if (dialog != null) {
							dialog.stop();
						}
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
