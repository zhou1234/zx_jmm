package com.jifeng.mlsales.jumeimiao;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.tools.OtherLogin;
import com.jifeng.tools.ShrefUtil;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class LoginActivity extends Activity {
	private Intent mIntent;
	private EditText mText_userName, mText_userPsd;
	private String userName, userPsd;// ��¼��¼�ߵ���Ϣ
	private ShrefUtil mShrefUtil;
	private LoadingDialog dialog;

	private SharedPreferences sp;
	// private boolean first_to_other = false;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0x01:
				doLogin(userName, userPsd, "web", "", "", "", "", "");
				break;
			case 0x02:
				// sina��¼
				doLogin("", "", "sina", AllStaticMessage.OpenId, "android",
						AllStaticMessage.Gender, AllStaticMessage.NickName,
						AllStaticMessage.Address);

				break;
			case 0x03:
				// ΢�ŵ�¼
				doLogin("", "", "tencent", AllStaticMessage.OpenId, "android",
						AllStaticMessage.Gender, AllStaticMessage.NickName,
						AllStaticMessage.Address);
				break;
			case 0x04:
				// qq��¼
				doLogin("", "", "tencent", AllStaticMessage.OpenId, "android",
						AllStaticMessage.Gender, AllStaticMessage.NickName,
						AllStaticMessage.Address);
				break;
			case 0x05:
				if (dialog != null) {
					dialog.stop();
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ShareSDK.initSDK(this);

		setContentView(R.layout.activity_login);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		mShrefUtil = new ShrefUtil(this, "data");
		findView();
		sp = getSharedPreferences(AllStaticMessage.SPNE, 0);
	}

	// ���ҿؼ�
	private void findView() {
		mText_userName = (EditText) findViewById(R.id.edt_username);
		mText_userPsd = (EditText) findViewById(R.id.edt_userpsd);
		mText_userName.setText(mShrefUtil.readString("user_name"));
		mText_userPsd.setText(mShrefUtil.readString("user_psd"));
	}

	// //xmlע�����¼���ʵ��
	@SuppressLint("ShowToast")
	public void doclick(View view) {
		new OtherLogin(LoginActivity.this);
		switch (view.getId()) {
		case R.id.login_back:
			setResult(8080);
			finish();
			break;
		case R.id.login_login:// ��¼
			userName = mText_userName.getText().toString().replace(" ", "")
					.trim();
			userPsd = mText_userPsd.getText().toString().replace(" ", "")
					.trim();
			if (userName.equals("") || userName == null) {
				Toast.makeText(this, "�������û���", 800).show();
				return;
			}
			if (userPsd.equals("") || userPsd == null) {
				Toast.makeText(this, "�������¼����", 800).show();
				return;
			}
			dialog.loading();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mHandler.sendEmptyMessage(0x01);
				}
			}, 300);
			break;
		case R.id.login_register:// ע��
			mIntent = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivityForResult(mIntent, 0x01);
			break;
		case R.id.login_forget_psd:// ��������
			mIntent = new Intent(LoginActivity.this, ForgetPsdActivity.class);
			startActivity(mIntent);
			break;
		case R.id.login_img_weibo:// sina��¼0x02
			dialog.loading();
			OtherLogin.Sina_login(mHandler, 0x02);
			break;
		case R.id.login_img_weixin:// ΢�ŵ�¼0x03
			dialog.loading();
			OtherLogin.WeiXin_login(mHandler, 0x03);
			break;
		case R.id.login_img_qq:// QQ��¼0x04
			dialog.loading();
			OtherLogin.QQ_login(mHandler, 0x04);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0x01:
				dialog.loading();
				mText_userName.setText(mShrefUtil.readString("user_name"));
				mText_userPsd.setText(mShrefUtil.readString("user_psd"));
				doLogin(mShrefUtil.readString("user_name"),
						mShrefUtil.readString("user_psd"), "web", "", "", "",
						"", "");
				break;

			default:
				break;
			}
		}

	}

	// ��¼
	private void doLogin(final String userName, final String psd,
			final String loginType, final String openid, String deviceType,
			final String gender, final String nickName, final String address) {

		String url = AllStaticMessage.URL_Login + userName + "&password=" + psd
				+ "&loginType=" + loginType + "&Photo="
				+ AllStaticMessage.userImage + "&openid=" + openid
				+ "&deviceType=" + deviceType + "&gender=" + gender
				+ "&nickName=" + nickName + "&address=" + address + "&udid="
				+ MyTools.getAndroidID(LoginActivity.this);
		// Log.i("11111", url);
		HttpUtil.get(url, LoginActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
								if (!openid.equals("")) {

								}

								AllStaticMessage.Login_Flag = response
										.getString("auth").toString();
								AllStaticMessage.User_Id = response.getString(
										"Id").toString();
								AllStaticMessage.User_JiFen = response
										.getString("Score").toString();
								AllStaticMessage.User_NickName = response
										.getString("NickName").toString();
								AllStaticMessage.User_Name = userName;
								AllStaticMessage.User_Psd = psd;

								mShrefUtil.write("user_name", userName);
								mShrefUtil.write("user_psd", psd);

								Editor editor = sp.edit();
								editor.remove(AllStaticMessage.NAME);
								editor.remove(AllStaticMessage.PSW);
								editor.remove(AllStaticMessage.TYPE);
								editor.remove(AllStaticMessage.OPEN_ID);
								editor.remove(AllStaticMessage.GENDER);
								editor.remove(AllStaticMessage.NICK_NAME);
								editor.remove(AllStaticMessage.ADDRESS);
								if (openid.equals("") || openid == null) {
									editor.remove(AllStaticMessage.USER_PATH);
								}
								editor.commit();

								editor.putString(AllStaticMessage.NAME,
										userName);
								editor.putString(AllStaticMessage.PSW, psd);
								editor.putString(AllStaticMessage.TYPE,
										loginType);
								editor.putString(AllStaticMessage.OPEN_ID,
										openid);
								editor.putString(AllStaticMessage.GENDER,
										gender);
								editor.putString(AllStaticMessage.NICK_NAME,
										nickName);
								editor.putString(AllStaticMessage.ADDRESS,
										address);
								String str = response.getString("Photo")
										.toString();
								if (openid.equals("") || openid == null) {
									editor.putString(
											AllStaticMessage.USER_PATH, str);
									AllStaticMessage.userImage = str;
								}
								editor.commit();

								setResult(RESULT_OK);
								finish();
							} else {
								Toast.makeText(
										LoginActivity.this,
										response.getString("Results")
												.toString(), 500).show();
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
						if (dialog != null) {
							dialog.stop();
						}
					}
				});
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
		// mIntent = null;
		// mText_userName = null;
		// mText_userPsd = null;
		// userName = null;
		// userPsd = null;// ��¼��¼�ߵ���Ϣ
		// mShrefUtil = null;
		// if (dialog != null) {
		// dialog.stop();
		// }
		// dialog = null;
		// mHandler = null;
		// setContentView(R.layout.view_null);
		// this.finish();
		// System.gc();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
