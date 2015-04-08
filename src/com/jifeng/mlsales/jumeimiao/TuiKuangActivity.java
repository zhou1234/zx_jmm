package com.jifeng.mlsales.jumeimiao;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TuiKuangActivity extends Activity {
	private String OrderId, GoodsId, price;
	int themeCheckedId = -1;
	private Button mButton;
	private EditText mEditText;
	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tuikuan);
		dialog = new LoadingDialog(this);

		OrderId = getIntent().getStringExtra("orderId").toString();
		GoodsId = getIntent().getStringExtra("goodsId").toString();
		price = getIntent().getStringExtra("price").toString();
		((TextView) findViewById(R.id.tuikuan_price)).setText("￥" + price);
		findView();
	}

	// 查找控件
	private void findView() {
		mButton = (Button) findViewById(R.id.tuikuang_btn_yuanying);
		mEditText = (EditText) findViewById(R.id.tuikuang_shuoming);
	}

	// xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.tuikuang_back:// 返回
			finish();
			break;
		case R.id.img_tuikuanyuanying:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setSingleChoiceItems(AllStaticMessage.tuikuanyuanying,
					themeCheckedId, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							themeCheckedId = which;// 保存选中的选项索引
						}
					}).setPositiveButton("确定",// 点击确定按钮事件
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (themeCheckedId > -1) {
								mButton.setText(AllStaticMessage.tuikuanyuanying[themeCheckedId]);
							}
						}
					}).setNegativeButton("取消", null);
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			break;
		case R.id.tuikuang_ok:
			if (themeCheckedId > -1) {
				dialog.loading();
				sendApplication(mEditText.getText().toString().trim());
			}
			break;
		default:
			break;
		}
	}

	private void sendApplication(String message) {
		String url = AllStaticMessage.URL_TuiHuo + AllStaticMessage.User_Id
				+ "&OrderId=" + OrderId + "&goodsId=" + GoodsId + "&reasonId="
				+ String.valueOf(themeCheckedId) + "&remark=" + message;
		HttpUtil.get(url, TuiKuangActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								AllStaticMessage.orderDetailFlag = true;
								Toast.makeText(
										TuiKuangActivity.this,
										response.getString("Results")
												.toString(), 500).show();
								finish();
							} else {
								Toast.makeText(
										TuiKuangActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}
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
		OrderId = null;
		GoodsId = null;
		mButton = null;
		mEditText = null;
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
