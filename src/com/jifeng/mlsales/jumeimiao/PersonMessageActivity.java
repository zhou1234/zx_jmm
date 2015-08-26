package com.jifeng.mlsales.jumeimiao;

import java.util.Calendar;

import net.simonvt.datepicker.DatePickDialog;
import net.simonvt.datepicker.DatePickDialog.IgetDate;

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
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PersonMessageActivity extends Activity implements OnClickListener {
	private Calendar mCalendar = null;
	private TextView mText_Date, mText_Sex, tv_save;
	private EditText mEdit_NiCheng;
	private ImageView iv_nan, iv_nv;
	private LoadingDialog dialog;
	private String nicheng = "", shengri = "";
	private String sex;
	private RelativeLayout person_message_date;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_person_message);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		findView();
		register();
		get_person_Msg("", "", "");
	}

	// ���ҿؼ�
	private void findView() {
		mText_Date = (TextView) findViewById(R.id.person_message_text_date);
		mEdit_NiCheng = (EditText) findViewById(R.id.text_nicheng);
		mText_Sex = (TextView) findViewById(R.id.text_sex_1);
		iv_nan = (ImageView) findViewById(R.id.iv_nan);
		iv_nv = (ImageView) findViewById(R.id.iv_nv);
		tv_save = (TextView) findViewById(R.id.tv_save);
		person_message_date = (RelativeLayout) findViewById(R.id.person_message_date);
		tv_save.setOnClickListener(this);
		iv_nan.setOnClickListener(this);
		iv_nv.setOnClickListener(this);
		person_message_date.setOnClickListener(this);
	}

	// ע���¼�
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
				// ��������
				// get_person_Msg("modify","",mEdit_NiCheng.getText().toString());
			}
		});
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();

	}

	// //xmlע�����¼���ʵ��
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.person_message_back:// ����
			// if (nicheng.equals(mEdit_NiCheng.getText().toString())
			// && shengri.equals(mText_Date.getText().toString())) {
			// finish();
			// } else {
			// get_person_Msg("modify", mText_Date.getText().toString(),
			// mEdit_NiCheng.getText().toString());
			// }
			finish();
			break;
		default:
			break;
		}
	}

	private final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker datePicker, int year, int month,
				int dayOfMonth) {
			// Calendar�·��Ǵ�0��ʼ,����monthҪ��1
			mText_Date.setText(year + "-" + (month + 1) + "-" + dayOfMonth);

			// get_person_Msg("modify",mText_Date.getText().toString(),"");
			// ��������
			// // ��ѯ����
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
					+ "&nickname=" + nickname + "&sex=" + sex;
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
						// �ɹ�����JSONObject
						try {
							if (flag.equals("modify")) {
								if (response.getString("Status").toString()
										.equals("true")) {
									AllStaticMessage.User_NickName = nickname;
									Toast.makeText(PersonMessageActivity.this,
											"��Ϣ�޸ĳɹ�", 0).show();
								} else {
									Toast.makeText(PersonMessageActivity.this,
											"��Ǹ,��Ϣ����ʧ��,����������һ��", 0).show();
								}
								// finish();
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
										// mText_Sex.setText("Ů");
										sex="Ů";
										iv_nv.setImageResource(R.drawable.img_sex);
									} else {
										// mText_Sex.setText("��");
										sex="��";
										iv_nan.setImageResource(R.drawable.img_sex);
									}
								} else {
									Toast.makeText(PersonMessageActivity.this,
											"��Ǹ,������Ϣ��ȡʧ��,�����´�", 500).show();
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
						// ����ʼ
					}

					@Override
					public void onFinish() {
						super.onFinish();
						// �������
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// ���󷵻�JSONObject
						dialog.stop();
					}
				});
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			/* ��������� */
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

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_nan:
			sex = "��";
			iv_nan.setImageResource(R.drawable.img_sex);
			iv_nv.setImageResource(R.drawable.img_sex_1);
			break;
		case R.id.iv_nv:
			sex = "Ů";
			iv_nv.setImageResource(R.drawable.img_sex);
			iv_nan.setImageResource(R.drawable.img_sex_1);
			break;
		case R.id.tv_save:
			get_person_Msg("modify", mText_Date.getText().toString(),
					mEdit_NiCheng.getText().toString());
			break;
		case R.id.person_message_date:
			DatePickDialog datePickDialog = new DatePickDialog(
					PersonMessageActivity.this, new IgetDate() {
						@Override
						public void getDate(int year, int month, int day) {
							mText_Date.setText(year + "-" + (month + 1) + "-"
									+ day);
						}

					}, "����ѡ��", "ȷ��", "ȡ��");
			datePickDialog.show();
			break;
		default:
			break;
		}

	}
}
