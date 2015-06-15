package com.jifeng.mlsales.jumeimiao;


import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.tools.ShrefUtil;
import com.jifeng.tools.TimeThread;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class RegisterActivity extends Activity {
	private EditText mEdit_Phone, mEdit_Psd_1, mEdit_Code;// , mEdit_Psd_2
	// private RadioButton mBtn_Sex_1, mBtn_Sex_2;
	private Button mButton;
	private String Register_Sex = "";// 0 Ů 1��
	private LoadingDialog dialog;
	private TimeThread timeThread;
	private ShrefUtil mShrefUtil;
	private String imgcode = "1234";
	private EditText m_code;
	private ImageView img_code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgcode = MyTools.getRandom();
		setContentView(R.layout.activity_register);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		mShrefUtil = new ShrefUtil(this, "data");
		findView();
	}

	// ���ҿؼ�
	private void findView() {
		img_code = (ImageView) findViewById(R.id.img_code);
		m_code = (EditText) findViewById(R.id.m_code);
		ImageLoader.getInstance().displayImage(
				AllStaticMessage.URL_GetImgCode + imgcode, img_code);
		mEdit_Phone = (EditText) findViewById(R.id.register_phone);
		mEdit_Psd_1 = (EditText) findViewById(R.id.register_psd_1);
		// mEdit_Psd_2 = (EditText) findViewById(R.id.register_psd_2);
		// mBtn_Sex_1 = (RadioButton) findViewById(R.id.register_woman);
		// mBtn_Sex_2 = (RadioButton) findViewById(R.id.register_man);
		mEdit_Code = (EditText) findViewById(R.id.register_code);
		mButton = (Button) findViewById(R.id.register_btn_getcode);
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		setContentView(R.layout.view_null);
		super.onDestroy();
		mEdit_Phone = null;
		mEdit_Psd_1 = null;
		// mEdit_Psd_2 = null;
		mEdit_Code = null;
		// mBtn_Sex_1 = null;
		// mBtn_Sex_2 = null;
		// mButton=null;
		Register_Sex = null;
		this.finish();
		System.gc();
	}


	// //xmlע�����¼���ʵ��
	@SuppressLint("ShowToast")
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.register_back:// ����
			if (timeThread != null) {
				timeThread.setThreadState(false);
			}
			finish();
			break;

		case R.id.fresh_code:
			imgcode = MyTools.getRandom();
			ImageLoader.getInstance().displayImage(
					AllStaticMessage.URL_GetImgCode + imgcode, img_code);

			// Toast.makeText(this, imgcode, 500).show();
			break;
		// case R.id.register_woman:
		// set_register_sex(1);
		// Register_Sex = "0";
		// break;
		// case R.id.register_man:
		// set_register_sex(2);
		// Register_Sex = "1";
		// break;
		case R.id.register_btn_getcode:
			String phone_1 = mEdit_Phone.getText().toString().replace(" ", "").trim();
			if (phone_1 == null || phone_1.equals("")) {
				Toast.makeText(this, "�������ֻ�����", 500).show();
				return;
			}
			if (!MyTools.isMobileNO(phone_1)) {
				Toast.makeText(this, "��Ǹ,��������ȷ���ֻ�����", 500).show();
				return;
			}
			getCode(phone_1);
			break;
		case R.id.register_btn_ok:
			final String phone = mEdit_Phone.getText().toString().replace(" ", "").trim();
			final String psd_1 = mEdit_Psd_1.getText().toString().replace(" ", "").trim();

			// final String psd_2 = mEdit_Psd_2.getText().toString().trim();
			final String code = "111";// mEdit_Code.getText().toString().trim();
			// ͼƬ��֤
			final String txt_code = m_code.getText().toString().replace(" ", "").trim();
			if (phone == null || phone.equals("")) {
				Toast.makeText(this, "�������ֻ�����", 500).show();
				return;
			}
			if (!MyTools.isMobileNO(phone)) {
				Toast.makeText(this, "��Ǹ,��������ȷ���ֻ�����", 500).show();
				return;
			}

			if (txt_code == null || txt_code.equals("")) {
				Toast.makeText(this, "��������֤��", 500).show();
				return;
			}

			if (!txt_code.equals(imgcode)) {
				Toast.makeText(this, "��������ȷ��֤��", 500).show();
				return;
			}

			/*
			 * if (code == null || code.equals("")) { Toast.makeText(this,
			 * "�����������֤", 500).show(); return; }
			 */
			if (psd_1 == null || psd_1.equals("")) {
				Toast.makeText(this, "����������", 500).show();
				return;
			}
			if (psd_1.length() < 6) {
				Toast.makeText(this, "������6λ��������", 500).show();
				return;
			}
			// if (psd_2 == null || psd_2.equals("")) {
			// Toast.makeText(this, "������ȷ������", 500).show();
			// return;
			// }
			// if (!psd_1.equals(psd_2)) {
			// Toast.makeText(this, "�������벻һ������������", 500).show();
			// return;
			// }
			// if (Register_Sex == null || Register_Sex.equals("")) {
			// Toast.makeText(this, "��ѡ�������Ա�", 500).show();
			// return;
			// }
			Register_Sex = "2";
			dialog.loading();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					doRegister(phone, code, psd_1, Register_Sex);
				}
			}, 300);
			break;
		default:
			break;
		}
	}

	// private void set_register_sex(int num) {
	// Drawable drawable =
	// this.getResources().getDrawable(R.drawable.register_select_1);
	// mBtn_Sex_1.setCompoundDrawablesWithIntrinsicBounds(drawable, null,
	// null, null);
	// mBtn_Sex_2.setCompoundDrawablesWithIntrinsicBounds(drawable, null,
	// null, null);
	// Drawable mDrawable = this.getResources().getDrawable(
	// R.drawable.register_select_2);
	// switch (num) {
	// case 1:
	// mBtn_Sex_1.setCompoundDrawablesWithIntrinsicBounds(mDrawable, null,
	// null, null);
	// break;
	// case 2:
	// mBtn_Sex_2.setCompoundDrawablesWithIntrinsicBounds(mDrawable, null,
	// null, null);
	// break;
	// default:
	// break;
	// }
	// }

	// ��ȡ������֤��
	private void getCode(String phone) {
		dialog.loading();
		HttpUtil.get(AllStaticMessage.URL_GetCode + phone + "&getType=2",
				RegisterActivity.this, dialog, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
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
										RegisterActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}
							// dialog.stop();
						} catch (JSONException e) {
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
						if (dialog != null) {
							dialog.stop();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// ���󷵻�JSONObject
						if (dialog != null) {
							dialog.stop();
						}
					}
				});
	}

	// ע��
	private void doRegister(final String phone, String code, final String psd,
			String sex) {
		String url = AllStaticMessage.URL_Register + phone + "&password=" + psd
				+ "&sex=" + sex + "&mcode=" + code;
		HttpUtil.get(url, RegisterActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
								Toast.makeText(RegisterActivity.this, "ע��ɹ�",
										500).show();
								mShrefUtil.write("user_name", phone);
								mShrefUtil.write("user_psd", psd);
								RegisterActivity.this.setResult(RESULT_OK);
								RegisterActivity.this.finish();
							} else {
								Toast.makeText(
										RegisterActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}
							// dialog.stop();
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

	//
	private Handler timeHandler = new Handler() {
		public void handleMessage(Message msg) {
			String message = "";
			if (msg.what < 10) {
				message = "0" + msg.what;
			} else {
				message = String.valueOf(msg.what);
			}
			if (message.equals("120")) {
				mButton.setText("��ȡ������֤");
			} else {
				mButton.setText(message + "��");
			}

		}
	};

	private void timeDes(int num) {
		if (timeThread == null) {
			// ������ʱ�߳�
			timeThread = new TimeThread(RegisterActivity.this, timeHandler, num);
			timeThread.start();
		} else {
			timeThread.setThreadState(false);
			timeThread = new TimeThread(RegisterActivity.this, timeHandler, num);
			timeThread.start();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (timeThread != null) {
				timeThread.setThreadState(false);
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
