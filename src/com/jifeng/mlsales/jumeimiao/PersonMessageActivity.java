package com.jifeng.mlsales.jumeimiao;

import java.util.Calendar;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PersonMessageActivity extends Activity {
	private Calendar mCalendar = null;
	private TextView mText_Date, mText_Sex;
	private EditText mEdit_NiCheng;
	private LoadingDialog dialog;
	private String nicheng = "", shengri = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_message);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		findView();
		register();
		get_person_Msg("", "", "");
	}

	// 查找控件
	private void findView() {
		mText_Date = (TextView) findViewById(R.id.person_message_text_date);
		mEdit_NiCheng = (EditText) findViewById(R.id.text_nicheng);
		mText_Sex = (TextView) findViewById(R.id.text_sex_1);
	}

	// 注册事件
	private void register() {
		mEdit_NiCheng.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				// 更新数据
				// get_person_Msg("modify","",mEdit_NiCheng.getText().toString());
			}
		});
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		setContentView(R.layout.view_null);
		super.onDestroy();
		mCalendar = null;
		mText_Date = null;
		mText_Sex = null;
		mEdit_NiCheng = null;

		nicheng = null;
		shengri = null;
		dialog = null;
		this.finish();
		System.gc();
	}

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.person_message_back:// 返回
			if (nicheng.equals(mEdit_NiCheng.getText().toString())
					&& shengri.equals(mText_Date.getText().toString())) {
				finish();
			} else {
				get_person_Msg("modify", mText_Date.getText().toString(),
						mEdit_NiCheng.getText().toString());
			}
			break;
		case R.id.person_message_date:
			mCalendar = Calendar.getInstance();
			Dialog dialogyear = new DatePickerDialog(
					PersonMessageActivity.this, dateListener,
					mCalendar.get(Calendar.YEAR),
					mCalendar.get(Calendar.MONTH),
					mCalendar.get(Calendar.DAY_OF_MONTH));
			dialogyear.show();
			break;
		default:
			break;
		}
	}

	private final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker datePicker, int year, int month,
				int dayOfMonth) {
			// Calendar月份是从0开始,所以month要加1
			mText_Date.setText(year + "-" + (month + 1) + "-" + dayOfMonth);

			// get_person_Msg("modify",mText_Date.getText().toString(),"");
			// 更新数据
			// // 查询数据
			// month = month + 1;
			// String m = month + "";
			// if (m.length() == 1)
			// m = "0" + m;
			//
			// String d = dayOfMonth + "";
			// if (d.length() == 1)
			// d = "0" + d;
		}
	};

	private void get_person_Msg(final String flag, String birthday,
			final String nickname) {
		dialog.loading();
		String url = "";
		if (flag.equals("modify")) {
			// dialog=ProgressDialog.show(PersonMessageActivity.this, null,
			// "Saving......", true);
			// dialog.setCanceledOnTouchOutside(true);
			url = url + AllStaticMessage.URL_Modify_person_msg
					+ AllStaticMessage.User_Id + "&birthday=" + birthday
					+ "&nickname=" + nickname;
		} else {
			// dialog=ProgressDialog.show(PersonMessageActivity.this, null,
			// "Loading......", true);
			// dialog.setCanceledOnTouchOutside(true);
			url = url + AllStaticMessage.URL_Get_person_msg
					+ AllStaticMessage.User_Id;

		}

		HttpUtil.get(url, PersonMessageActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (flag.equals("modify")) {
								if (response.getString("Status").toString()
										.equals("true")) {
									AllStaticMessage.User_NickName = nickname;
									Toast.makeText(PersonMessageActivity.this,
											"信息修改成功", 0).show();
								} else {
									Toast.makeText(PersonMessageActivity.this,
											"抱歉,信息更新失败,请重新再试一次", 0).show();
								}
								finish();
							} else {
								if (response.getString("Status").toString()
										.equals("true")) {
									JSONObject object = response
											.getJSONObject("Results");
									shengri = object.getString("Birth")
											.toString();
									nicheng = object.getString("NickName")
											.toString();
									mText_Date.setText(object
											.getString("Birth").toString());
									mEdit_NiCheng.setText(object.getString(
											"NickName").toString());
									if (object.getString("Sex").toString()
											.equals("0")) {
										mText_Sex.setText("女");
									} else {
										mText_Sex.setText("男");
									}
								} else {
									Toast.makeText(PersonMessageActivity.this,
											"抱歉,个人信息获取失败,请重新打开", 500).show();
								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
						dialog.stop();
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
						dialog.stop();
					}
				});
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			/* 隐藏软键盘 */
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (inputMethodManager.isActive()) {
				inputMethodManager.hideSoftInputFromWindow(
						PersonMessageActivity.this.getCurrentFocus()
								.getWindowToken(), 0);
			}

			return true;
		}
		return super.dispatchKeyEvent(event);
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
