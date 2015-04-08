package com.jifeng.mlsales.jumeimiao;

import com.jifeng.mlsales.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class noActivity extends Activity {
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

	}

	// 注册事件
	private void register() {

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
		default:
			break;
		}
	}
}
