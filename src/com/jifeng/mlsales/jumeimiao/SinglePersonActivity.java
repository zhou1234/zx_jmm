package com.jifeng.mlsales.jumeimiao;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.R;
import com.jifeng.tools.ShrefUtil;
import com.jifeng.url.AllStaticMessage;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SinglePersonActivity extends Activity {
	private Intent mIntent;
	private ShrefUtil mShrefUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_person);
		mShrefUtil = new ShrefUtil(this, "data");
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

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.single_person_back:
			finish();
			break;

		case R.id.single_person_rel_message:// 个人资料
			if (AllStaticMessage.Login_Flag.equals("")) {
				Toast.makeText(SinglePersonActivity.this, "抱歉,请重新登录", 500)
						.show();
			} else {
				mIntent = new Intent(SinglePersonActivity.this,
						PersonMessageActivity.class);
				startActivity(mIntent);
			}
			break;

		case R.id.single_person_rel_modifypsd:// 修改密码
			if (AllStaticMessage.Login_Flag.equals("")) {
				Toast.makeText(SinglePersonActivity.this, "抱歉,请重新登录", 500)
						.show();
			} else {
				mIntent = new Intent(SinglePersonActivity.this,
						Modify_Password_Activity.class);
				startActivity(mIntent);
			}
			break;
		case R.id.single_person_tuichu:
			AllStaticMessage.Login_Flag = "";
			AllStaticMessage.User_Id = "";
			mShrefUtil.write("user_name", "");
			mShrefUtil.write("user_psd", "");
			finish();
			break;
		default:
			break;
		}
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
