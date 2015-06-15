package com.jifeng.mlsales.jumeimiao;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.tools.ApkModify;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SettingActivity extends Activity {
	private Intent mIntent;
	private TextView mTextView_banben;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		((FBApplication) getApplication()).addActivity(this);
		initData();
		findView();
		register();
	}

	// ���ҿؼ�
	private void findView() {
		mTextView_banben = (TextView) findViewById(R.id.setting_banben);
		try {
			// ��ȡpackagemanager��ʵ��
			PackageManager packageManager = this.getPackageManager();
			// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
			PackageInfo packInfo = packageManager.getPackageInfo(
					this.getPackageName(), 0);
			mTextView_banben.setText(String.valueOf(packInfo.versionName));
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ע���¼�
	private void register() {

	}

	// ����ʵ��
	private void setView(int i) {

	}

	/*
	 * ��ʼ������
	 */
	private void initData() {

	}

	// //xmlע�����¼���ʵ��
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.setting_back:// ����
			finish();
			break;
		case R.id.setting_rel_cleanHuancun:// �������
			ImageLoader.getInstance().clearDiskCache();
			ImageLoader.getInstance().clearMemoryCache();
			break;
		case R.id.setting_rel_kefudianhua:// �ͷ��绰
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_CALL); // ֱ�Ӳ���ACTION_CALL
													// ���벦�Ž���ACTION_DIAL
			intent.setData(Uri.parse("tel:4009696876"));
			startActivity(intent);
			break;   
		case R.id.setting_rel_yijianfankui:// �������
			mIntent = new Intent(SettingActivity.this, AdviceActivity.class);
			startActivity(mIntent);
			break;
		case R.id.setting_rel_banbengengxin:// �汾����
			ApkModify apkModify = new ApkModify(SettingActivity.this);
			apkModify.new CheckVersionTask().run();
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
		mIntent = null;
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
