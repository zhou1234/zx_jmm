package com.jifeng.mlsales.jumeimiao;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChongZhiPsdActivity extends Activity {
	private EditText mText_psd_1, mText_psd_2;
	private String psd_1, psd_2;
	private LoadingDialog dialog;
	private String phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_psd_2);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		findView();
		phone = getIntent().getStringExtra("phonenum").toString();
	}

	// 查找控件
	private void findView() {
		mText_psd_1 = (EditText) findViewById(R.id.chongzhi_psd_edt_1);
		mText_psd_2 = (EditText) findViewById(R.id.chongzhi_psd_edt_2);

	}

	// //xml注册点击事件的实现
	@SuppressLint("ShowToast")
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.chongzhi_psd_back:// 返回
			finish();
			break;
		case R.id.chongzhi_psd_btn_ok:
			psd_1 = mText_psd_1.getText().toString().trim();
			psd_2 = mText_psd_2.getText().toString().trim();
			if (psd_1 == null || psd_1.equals("")) {
				Toast.makeText(ChongZhiPsdActivity.this, "请输入新密码", 500).show();
				return;
			}
			if (psd_1.equals(psd_2)) {
				dialog.loading();

				getData(phone, psd_1);

			} else {
				Toast.makeText(ChongZhiPsdActivity.this, "两次密码输入不一致", 500)
						.show();
			}
			break;
		default:
			break;
		}
	}

	private void getData(String phone, String psd) {
		String url = AllStaticMessage.URL_ChongZhi_Psd + phone + "&newPwd="
				+ psd;
		HttpUtil.get(url, ChongZhiPsdActivity.this, dialog,
				new JsonHttpResponseHandler() {

					@SuppressLint("ShowToast")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								finish();
							} else {
								Toast.makeText(
										ChongZhiPsdActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}
						} catch (JSONException e) {
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
