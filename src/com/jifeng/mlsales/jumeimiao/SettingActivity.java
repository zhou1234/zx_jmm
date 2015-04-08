package com.jifeng.mlsales.jumeimiao;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.R;
import com.jifeng.tools.ApkModify;
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
		initData();
		findView();
		register();
	}

	// 查找控件
	private void findView() {
		mTextView_banben = (TextView) findViewById(R.id.setting_banben);
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = this.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(
					this.getPackageName(), 0);
			mTextView_banben.setText(String.valueOf(packInfo.versionName));
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 注册事件
	private void register() {

	}

	// 其他实现
	private void setView(int i) {

	}

	/*
	 * 初始化数据
	 */
	private void initData() {

	}

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.setting_back:// 返回
			finish();
			break;
		case R.id.setting_rel_cleanHuancun:// 清除缓存

			break;
		case R.id.setting_rel_kefudianhua:// 客服电话
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_CALL); // 直接拨号ACTION_CALL
													// 进入拨号界面ACTION_DIAL
			intent.setData(Uri.parse("tel:4009696876"));
			startActivity(intent);
			break;
		case R.id.setting_rel_yijianfankui:// 意见反馈
			mIntent = new Intent(SettingActivity.this, AdviceActivity.class);
			startActivity(mIntent);
			break;
		case R.id.setting_rel_banbengengxin:// 版本更新
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
