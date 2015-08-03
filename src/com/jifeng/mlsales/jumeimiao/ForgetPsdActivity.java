package com.jifeng.mlsales.jumeimiao;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.tools.TimeThread;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgetPsdActivity extends Activity {
	private Intent mIntent;
	private EditText mText_phone, mText_code;
	private Button mBtn_code;
	private String phonenum, code;
	private TimeThread timeThread;
	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_psd);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		findView();
	}

	// ���ҿؼ�
	private void findView() {
		mText_phone = (EditText) findViewById(R.id.forget_psd_edt_phone);
		mText_code = (EditText) findViewById(R.id.forget_psd_edt_code);
		mBtn_code = (Button) findViewById(R.id.forget_psd_btn_code);

	}

	// //xmlע�����¼���ʵ��
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.forget_psd_back:// ����
			if (timeThread != null) {
				timeThread.setThreadState(false);
				timeThread = null;
			}
			finish();
			break;
		case R.id.forget_psd_btn_code:
			phonenum = mText_phone.getText().toString().trim();
			if (MyTools.isMobileNO(phonenum)) {

				getCode(phonenum);

			} else {
				Toast.makeText(ForgetPsdActivity.this, "��������ȷ���ֻ�����", 500)
						.show();
			}
			break;
		case R.id.forget_psd_btn_check:
			phonenum = mText_phone.getText().toString().trim();
			if (!MyTools.isMobileNO(phonenum)) {
				Toast.makeText(ForgetPsdActivity.this, "��������ȷ���ֻ�����", 500)
						.show();
				return;
			}
			code = mText_code.getText().toString().trim();
			if (code != null && !code.equals("")) {

				getData(phonenum, code);

			} else {
				Toast.makeText(ForgetPsdActivity.this, "�����������֤��", 500).show();
			}
			break;
		default:
			break;
		}
	}

	private Handler timeHandler = new Handler() {
		public void handleMessage(Message msg) {
			String message = "";
			if (msg.what < 10) {
				message = "0" + msg.what;
			} else {
				message = String.valueOf(msg.what);
			}
			if (message.equals("120")) {
				mBtn_code.setText("��ȡ������֤");
			} else {
				mBtn_code.setText(message + "��");
			}

		}
	};

	private void timeDes(int num) {
		if (timeThread == null) {
			// ������ʱ�߳�
			timeThread = new TimeThread(ForgetPsdActivity.this, timeHandler,
					num);
			timeThread.start();
		} else {
			timeThread.setThreadState(false);
			timeThread = new TimeThread(ForgetPsdActivity.this, timeHandler,
					num);
			timeThread.start();
		}
	}

	// ��ȡ������֤��
	private void getCode(String phone) {

		HttpUtil.get(AllStaticMessage.URL_GetCode + phone + "&getType=1",
				ForgetPsdActivity.this, dialog, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
								// Toast.makeText(RegisterActivity.this,
								// response.getString("Results").toString(),
								// 500).show();
								timeDes(120);
							} else {
								Toast.makeText(
										ForgetPsdActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}
							// dialog.stop();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// ���󷵻�JSONObject

					}
				});
	}

	// ��֤������
	private void getData(final String phone, String code) {
		String url = AllStaticMessage.URL_Cheak_Code + phone + "&mcode=" + code;
		HttpUtil.get(url, ForgetPsdActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								mIntent = new Intent(ForgetPsdActivity.this,
										ChongZhiPsdActivity.class);
								mIntent.putExtra("phonenum", phone);
								startActivity(mIntent);
								finish();
							} else {
								Toast.makeText(
										ForgetPsdActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// ���󷵻�JSONObject
					}
				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (timeThread != null) {
				timeThread.setThreadState(false);
				timeThread = null;
			}
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
