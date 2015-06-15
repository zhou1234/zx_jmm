package com.jifeng.mlsales.jumeimiao;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Modify_Password_Activity extends Activity {
	private EditText mEdit_psd_old, mEdit_psd_new_1, mEdit_psd_new_2;
	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_password);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		initData();
		findView();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		ShareSDK.stopSDK(this);
		setContentView(R.layout.view_null);
		super.onDestroy();
		mEdit_psd_old = null;
		mEdit_psd_new_1 = null;
		mEdit_psd_new_2 = null;
		dialog = null;
		this.finish();
		System.gc();
	}

	// ���ҿؼ�
	private void findView() {
		mEdit_psd_old = (EditText) findViewById(R.id.modify_edt_password_old);
		mEdit_psd_new_1 = (EditText) findViewById(R.id.modify_edt_password_new_1);
		mEdit_psd_new_2 = (EditText) findViewById(R.id.modify_edt_password_new_2);
	}

	/*
	 * ��ʼ������
	 */
	private void initData() {

	}

	// xmlע�����¼���ʵ��
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.modify_psd_back:// ����
			finish();
			break;
		case R.id.modify_psd_ok:
			String psd_old = mEdit_psd_old.getText().toString().trim();
			String psd_new_1 = mEdit_psd_new_1.getText().toString().trim();
			String psd_new_2 = mEdit_psd_new_2.getText().toString().trim();
			if (psd_old.equals("") || psd_old == null) {
				Toast.makeText(Modify_Password_Activity.this, "�����������", 500)
						.show();
				return;
			}
			if (psd_new_1.equals("") || psd_new_1 == null) {
				Toast.makeText(Modify_Password_Activity.this, "������������", 500)
						.show();
				return;
			}
			if (!psd_new_1.equals(psd_new_2)) {
				Toast.makeText(Modify_Password_Activity.this, "��Ǹ,��������������Ĳ�һ��",
						500).show();
				return;
			}
			modifyPsd(psd_old, psd_new_1);
			break;
		default:
			break;
		}
	}

	private void modifyPsd(String psd_old, String psd_new) {
		dialog.loading();
		String url = AllStaticMessage.URL_Modify_Psd + AllStaticMessage.User_Id
				+ "&newPassword=" + psd_new + "&oldPassword=" + psd_old;
		HttpUtil.get(url, Modify_Password_Activity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								Toast.makeText(Modify_Password_Activity.this,
										"�����޸ĳɹ�", 500).show();
								finish();
							} else {
								Toast.makeText(
										Modify_Password_Activity.this,
										response.getString("Results")
												.toString(), 500).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						dialog.stop();
					}

					@Override
					public void onStart() {
						super.onStart();
						// ����ʼ
					}

					@Override
					public void onFinish() {
						super.onFinish();
						// �������
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// ���󷵻�JSONObject
						dialog.stop();
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
