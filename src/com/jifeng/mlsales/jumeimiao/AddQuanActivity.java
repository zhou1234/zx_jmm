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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddQuanActivity extends Activity implements OnClickListener {
	private ImageView quan_back;
	private Button bt_ok;
	private EditText et_conuponCode;
	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_quan_activity);
		((FBApplication) getApplication()).addActivity(this);
		init();
	}

	private void init() {
		dialog = new LoadingDialog(this);

		quan_back = (ImageView) findViewById(R.id.quan_back);
		bt_ok = (Button) findViewById(R.id.bt_ok);
		et_conuponCode = (EditText) findViewById(R.id.et_conuponCode);
		quan_back.setOnClickListener(this);
		bt_ok.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.quan_back:
			finish();
			break;
		case R.id.bt_ok:
			String conuponCode = et_conuponCode.getText().toString().trim();
			if (conuponCode.equals("") || conuponCode == null) {
				Toast.makeText(AddQuanActivity.this, "请输入激活码", 0).show();
			} else {
				getData(conuponCode);
			}

			break;
		default:
			break;
		}
	}

	private void getData(String conuponCode) {
		dialog.loading();
		String url = AllStaticMessage.URL_GetQuan_new + conuponCode
				+ "&UserId=" + AllStaticMessage.User_Id;

		HttpUtil.get(url, AddQuanActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@SuppressLint("UseSparseArrays")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								String Results = response.getString("Results");
								Toast.makeText(AddQuanActivity.this, Results, 0)
										.show();
								finish();
							} else {
								String Results = response.getString("Results");
								Toast.makeText(AddQuanActivity.this, Results, 0)
										.show();
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
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// 错误返回JSONObject
					}
				});
	}
}
