package com.jifeng.mlsales.jumeimiao;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.R;
import com.jifeng.mlsales.wxapi.WXPayEntryActivity;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class CheckOrderActivity extends Activity {
	private Intent mIntent;
	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkorderstatus);
		dialog = new LoadingDialog(this);
		if (!getIntent().getStringExtra("orderNum").equals("")) {
			checkOrderStatus(getIntent().getStringExtra("orderNum"));
		} else {
			finish();
		}
	}

	private void checkOrderStatus(String orderId) {
		dialog.loading();
		String url = AllStaticMessage.URL_CheckOrderStatus
				+ AllStaticMessage.User_Id + "&OrderId=" + orderId;
		HttpUtil.get(url, CheckOrderActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
								AllStaticMessage.OrderId = response
										.getJSONArray("Results")
										.getJSONObject(0).getString("OrderId");
								AllStaticMessage.OrderPrice = response
										.getJSONArray("Results")
										.getJSONObject(0).getString("Amount");
								AllStaticMessage.OrderStatus = response
										.getJSONArray("Results")
										.getJSONObject(0)
										.getString("OrderStatus");//
								mIntent = new Intent(CheckOrderActivity.this,
										MyPayActivity.class);
								mIntent.putExtra("allprice",
										AllStaticMessage.OrderPrice);
								if (response.getJSONArray("Results")
										.getJSONObject(0).getString("PayWay")
										.contains("支付宝")) {
									mIntent.putExtra("payway", "zfb");
								} else if (response.getJSONArray("Results")
										.getJSONObject(0).getString("PayWay")
										.contains("微信")) {
									mIntent.putExtra("payway", "wx");
								}

								mIntent.putExtra("orderid",
										AllStaticMessage.OrderId);
								startActivity(mIntent);
								finish();

							} else {
								Toast.makeText(
										CheckOrderActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}
							AllStaticMessage.Back_to_ZhangHu = true;
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
						// TODO Auto-generated method stub
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		ShareSDK.stopSDK(this);
		setContentView(R.layout.view_null);
		super.onDestroy();
		mIntent = null;
		dialog = null;

		this.finish();
		System.gc();
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
