package com.jifeng.mlsales.jumeimiao;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.mlsales.model.CustomerAlertDialog;
import com.jifeng.tools.ApkModify;
import com.jifeng.tools.DataCleanManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SettingActivity extends Activity {
	private Intent mIntent;
	private TextView mTextView_banben, tv_cacheSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		((FBApplication) getApplication()).addActivity(this);
		findView();
	}

	// 查找控件
	private void findView() {
		mTextView_banben = (TextView) findViewById(R.id.setting_banben);
		tv_cacheSize = (TextView) findViewById(R.id.tv_cacheSize);
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = this.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo = packageManager.getPackageInfo(
					this.getPackageName(), 0);
			mTextView_banben.setText(String.valueOf(packInfo.versionName));

			String string = DataCleanManager.getCacheSize(SettingActivity.this
					.getCacheDir());
			tv_cacheSize.setText(string);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.setting_back:// 返回
			finish();
			break;
		case R.id.setting_rel_cleanHuancun:// 清除缓存
			final CustomerAlertDialog alertDialog = new CustomerAlertDialog(
					this, false);
			alertDialog.setTitle("是否清除缓存?");
			alertDialog.setPositiveButton("取消", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.dismiss();
				}
			});
			alertDialog.setNegativeButton1("确定", new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					ImageLoader.getInstance().clearDiskCache();
					ImageLoader.getInstance().clearMemoryCache();
					try {
						DataCleanManager.clearCache(
								SettingActivity.this.getCacheDir(),
								SettingActivity.this);
						String string = DataCleanManager
								.getCacheSize(SettingActivity.this
										.getCacheDir());
						tv_cacheSize.setText(string);
					} catch (Exception e) {
						e.printStackTrace();
					}

					alertDialog.dismiss();
				}
			});

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
		ShareSDK.stopSDK(this);
		super.onDestroy();
		// setContentView(R.layout.view_null);
		// mIntent = null;
		// this.finish();
		// System.gc();
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
