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

	// ���ҿؼ�
	private void findView() {
		mTextView_banben = (TextView) findViewById(R.id.setting_banben);
		tv_cacheSize = (TextView) findViewById(R.id.tv_cacheSize);
		try {
			// ��ȡpackagemanager��ʵ��
			PackageManager packageManager = this.getPackageManager();
			// getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ
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

	// //xmlע�����¼���ʵ��
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.setting_back:// ����
			finish();
			break;
		case R.id.setting_rel_cleanHuancun:// �������
			final CustomerAlertDialog alertDialog = new CustomerAlertDialog(
					this, false);
			alertDialog.setTitle("�Ƿ��������?");
			alertDialog.setPositiveButton("ȡ��", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.dismiss();
				}
			});
			alertDialog.setNegativeButton1("ȷ��", new View.OnClickListener() {

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
