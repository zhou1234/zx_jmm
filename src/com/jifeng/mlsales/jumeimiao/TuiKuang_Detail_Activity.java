package com.jifeng.mlsales.jumeimiao;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class TuiKuang_Detail_Activity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tuikuan_detail);
		((FBApplication) getApplication()).addActivity(this);
		initData();
		findView();
		register();
	}

	// ���ҿؼ�
	private void findView() {

	}

	// ע���¼�
	private void register() {

	}

	/*
	 * ��ʼ������
	 */
	private void initData() {

	}

	// //xmlע�����¼���ʵ��
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.tuikuang_detail_back:// ����
			finish();
			break;
		default:
			break;
		}
	}

	private void getData() {
		String url = "";
		// HttpUtil.get(TuiKuang_Detail_Activity.this,url, new
		// JsonHttpResponseHandler() {
		//
		// @Override
		// public void onSuccess(int statusCode, Header[] headers,JSONArray
		// response) {
		// // TODO Auto-generated method stub
		// super.onSuccess(statusCode, headers, response);
		// //�ɹ�����JSONArray
		// }
		// @Override
		// public void onSuccess(int statusCode, Header[] headers,
		// JSONObject response) {
		// // TODO Auto-generated method stub
		// super.onSuccess(statusCode, headers, response);
		// //�ɹ�����JSONObject
		// }
		// @Override
		// public void onSuccess(int statusCode, Header[] headers,
		// String responseString) {
		// // TODO Auto-generated method stub
		// super.onSuccess(statusCode, headers, responseString);
		// //�ɹ�����String
		// }
		// @Override
		// public void onStart() {
		// super.onStart();
		// //����ʼ
		// }
		// @Override
		// public void onFinish() {
		// super.onFinish();
		// //�������
		// }
		//
		// @Override
		// public void onFailure(int statusCode, Header[] headers,String
		// responseString, Throwable throwable) {
		// // TODO Auto-generated method stub
		// super.onFailure(statusCode, headers, responseString, throwable);
		// //���󷵻�String
		// }
		//
		// @Override
		// public void onFailure(int statusCode, Header[] headers,
		// Throwable throwable, JSONArray errorResponse) {
		// // TODO Auto-generated method stub
		// super.onFailure(statusCode, headers, throwable, errorResponse);
		// //���󷵻�JSONArray
		// }
		//
		// @Override
		// public void onFailure(int statusCode, Header[] headers,
		// Throwable throwable, JSONObject errorResponse) {
		// // TODO Auto-generated method stub
		// super.onFailure(statusCode, headers, throwable, errorResponse);
		// //���󷵻�JSONObject
		// }
		// });
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		ShareSDK.stopSDK(this);
		setContentView(R.layout.view_null);
		super.onDestroy();

		this.finish();
		System.gc();
	}

	// /*
	// * ˫���˳�
	// */
	// private long mExitTime;
	//
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// if ((System.currentTimeMillis() - mExitTime) > 2000) {
	// Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
	// mExitTime = System.currentTimeMillis();
	// } else {
	// this.finish();
	// System.exit(0);
	// }
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }
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
