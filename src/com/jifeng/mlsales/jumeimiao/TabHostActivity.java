package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.tools.ShrefUtil;
import com.jifeng.tools.TasckActivity;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tencent.android.tpush.XGPushManager;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.Toast;

//@TargetApi(Build.VERSION_CODES.GINGERBREAD)
//@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class TabHostActivity extends TabActivity implements
		OnCheckedChangeListener {

	private TabHost mTabHost;
	private Intent mAIntent;
	private Intent mBIntent;
	private Intent mCIntent;
	private Intent mDIntent;
	private Intent mEIntent;
	// private Intent mIntent;
	private RadioButton button;
	private RadioButton button2;
	private RadioButton button3;
	private RadioButton button4;
	private RadioButton button5;
	private TasckActivity tasckActivity;

	private LoadingDialog dialog;
	private ShrefUtil mShrefUtil;
	private SharedPreferences sp;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectDiskReads().detectDiskWrites().detectNetwork() //
		// ���������.detectAll()����
		// .penaltyLog().build());
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		// .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
		// .build());
		setContentView(R.layout.maintabs);
		((FBApplication) getApplication()).addActivity(this);
		button = (RadioButton) findViewById(R.id.radio_button_xianshi);
		button2 = (RadioButton) findViewById(R.id.radio_button_fenlei);
		button3 = (RadioButton) findViewById(R.id.radio_button_meirifaxian);
		button4 = (RadioButton) findViewById(R.id.radio_button_shoppingcar);
		button5 = (RadioButton) findViewById(R.id.radio_button_zhanghu);

		init();
		mTabHost.setCurrentTab(0);
		// button.setBackgroundResource(R.drawable.tab_dibu_select_bg_1);
		tasckActivity = new TasckActivity();
		tasckActivity.pushActivity(TabHostActivity.this);

		MobclickAgent.updateOnlineConfig(this);

		dialog = new LoadingDialog(this);
		mShrefUtil = new ShrefUtil(this, "data");
		sp = getSharedPreferences(AllStaticMessage.SPNE, 0);

		login();
		getTagsData();
		Context context = getApplicationContext();
		XGPushManager.registerPush(context);

		AllStaticMessage.qudaoString = MyTools
				.getChannelCode(TabHostActivity.this);
	}

	private void login() {
		String openid = sp.getString(AllStaticMessage.OPEN_ID, "");
		String userName = sp.getString(AllStaticMessage.NAME, "");
		String psd = sp.getString(AllStaticMessage.PSW, "");
		String loginType = sp.getString(AllStaticMessage.TYPE, "");
		String gender = sp.getString(AllStaticMessage.GENDER, "");
		String nickName = sp.getString(AllStaticMessage.NICK_NAME, "");
		String address = sp.getString(AllStaticMessage.ADDRESS, "");

		if (userName.equals("") && openid.equals("")) {

		} else {
			doLogin(userName, psd, loginType, openid, "android", gender,
					nickName, address);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (AllStaticMessage.Back_to_XianShiTm) {
			AllStaticMessage.Back_to_XianShiTm = false;
			nextView(mTabHost.getCurrentTab(), 0);
			// } else if (AllStaticMessage.Back_to_ShoppingCar) {
			// AllStaticMessage.Back_to_ShoppingCar = false;
			// nextView(mTabHost.getCurrentTab(), 3);
		} else if (AllStaticMessage.Back_to_Classion) {
			AllStaticMessage.Back_to_Classion = false;
			nextView(mTabHost.getCurrentTab(), 1);
//		} else if (AllStaticMessage.Back_to_Find) {
//			AllStaticMessage.Back_to_Find = false;
//			nextView(mTabHost.getCurrentTab(), 2);
		} else if (AllStaticMessage.Back_to_ZhangHu) {
			if (AllStaticMessage.guideRegister) {
				guideDoLogin(mShrefUtil.readString("user_name"),
						mShrefUtil.readString("user_psd"), "web", "", "", "",
						"", "");
			}
			if (AllStaticMessage.Register) {
				guideDoLogin(mShrefUtil.readString("user_name"),
						mShrefUtil.readString("user_psd"), "web", "", "", "",
						"", "");
			}

			else {
				AllStaticMessage.Back_to_ZhangHu = false;
				nextView(mTabHost.getCurrentTab(), 4);
			}
		}
	}

	private void nextView(int before, int after) {
		switch (before) {
		case 0:
			button.setChecked(true);
			button.setTextColor(getResources().getColor(R.color.white));
			Drawable drawable = this.getResources().getDrawable(
					R.drawable.tab_first_2);
			button.setCompoundDrawablesWithIntrinsicBounds(null, drawable,
					null, null);
			break;
		case 1:
			button2.setChecked(true);
			button2.setTextColor(getResources().getColor(R.color.white));
			Drawable drawable1 = this.getResources().getDrawable(
					R.drawable.tab_second_2);
			button2.setCompoundDrawablesWithIntrinsicBounds(null, drawable1,
					null, null);
			break;
		case 2:
			button3.setChecked(true);
			button3.setTextColor(getResources().getColor(R.color.white));
			Drawable drawable2 = this.getResources().getDrawable(
					R.drawable.tab_thread_2);
			button3.setCompoundDrawablesWithIntrinsicBounds(null, drawable2,
					null, null);

			break;
		case 3:
			button4.setChecked(true);
			button4.setTextColor(getResources().getColor(R.color.white));
			Drawable drawable3 = null;
			if (AllStaticMessage.ShoppingCar) {
				drawable3 = this.getResources().getDrawable(
						R.drawable.tab_forth_2_2);
			} else {
				drawable3 = this.getResources().getDrawable(
						R.drawable.tab_forth_2);
			}
			button4.setCompoundDrawablesWithIntrinsicBounds(null, drawable3,
					null, null);
			// if (AllStaticMessage.ShoppingCar) {
			// drawable3 = this.getResources().getDrawable(
			// R.drawable.tab_forth_2_2);
			// } else {
			// }
			break;
		case 4:
			button5.setChecked(true);
			button5.setTextColor(getResources().getColor(R.color.white));
			Drawable drawable4 = this.getResources().getDrawable(
					R.drawable.tab_fifth_2);
			button5.setCompoundDrawablesWithIntrinsicBounds(null, drawable4,
					null, null);
			break;
		default:
			break;
		}
		mTabHost.setCurrentTab(after);
		setView(after);
	}

	private void setView(int num) {
		switch (num) {
		case 0:
			button.setChecked(true);
			// button.setBackgroundResource(R.drawable.tab_dibu_select_bg_2);
			break;
		case 1:
			button2.setChecked(true);
			break;
		case 2:
			button3.setChecked(true);
			break;
		case 3:
			button4.setChecked(true);
			break;
		case 4:
			button5.setChecked(true);
			break;
		default:
			break;
		}
	}

	private void init() {
		this.mAIntent = new Intent(this, FirstActivity.class);
		this.mBIntent = new Intent(this, MeiMiaoQuanActivity.class);

		this.mCIntent = new Intent(this, ClassActivityNew.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		this.mDIntent = new Intent(this, ShoppingCarActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// �������,ʵ��ҳ��ˢ��
		this.mDIntent.putExtra("showflag", "no");
		this.mEIntent = new Intent(this, MySelfActivity.class);// �������,ʵ��ҳ��ˢ��

		button.setOnCheckedChangeListener(this);
		button2.setOnCheckedChangeListener(this);
		button3.setOnCheckedChangeListener(this);
		button4.setOnCheckedChangeListener(this);
		button5.setOnCheckedChangeListener(this);
		setupIntent();
	}

	@SuppressLint("NewApi")
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			int currentTab = this.mTabHost.getCurrentTab();
			// �ָ�Ĭ��
			reBack();
			switch (buttonView.getId()) {
			case R.id.radio_button_xianshi:
				// button.setBackgroundResource(R.drawable.tab_dibu_select_bg_1);
				button.setTextColor(getResources().getColor(R.color.white));
				Drawable drawable = this.getResources().getDrawable(
						R.drawable.tab_first_2);
				button.setCompoundDrawablesWithIntrinsicBounds(null, drawable,
						null, null);
				setCurrentTabWithAnim(currentTab, 0, "A_TAB");
				// this.mTabHost.setCurrentTabByTag("A_TAB");
				break;
			case R.id.radio_button_fenlei:
				button2.setTextColor(getResources().getColor(R.color.white));
				Drawable drawable2_1 = this.getResources().getDrawable(
						R.drawable.tab_second_2);
				button2.setCompoundDrawablesWithIntrinsicBounds(null,
						drawable2_1, null, null);
				setCurrentTabWithAnim(currentTab, 1, "B_TAB");
				// this.mTabHost.setCurrentTabByTag("B_TAB");
				break;
			case R.id.radio_button_meirifaxian:
				button3.setTextColor(getResources().getColor(R.color.white));
				Drawable drawable3_11 = this.getResources().getDrawable(
						R.drawable.tab_thread_2);
				button3.setCompoundDrawablesWithIntrinsicBounds(null,
						drawable3_11, null, null);
				setCurrentTabWithAnim(currentTab, 2, "C_TAB");
				// this.mTabHost.setCurrentTabByTag("C_TAB");
				break;
			case R.id.radio_button_shoppingcar:
				button4.setTextColor(getResources().getColor(R.color.white));
				Drawable drawable4_111 = null;
				if (AllStaticMessage.ShoppingCar) {
					drawable4_111 = this.getResources().getDrawable(
							R.drawable.tab_forth_2_2);
				} else {
					drawable4_111 = this.getResources().getDrawable(
							R.drawable.tab_forth_2);

				}
				button4.setCompoundDrawablesWithIntrinsicBounds(null,
						drawable4_111, null, null);
				setCurrentTabWithAnim(currentTab, 3, "D_TAB");
				// this.mTabHost.setCurrentTabByTag("D_TAB");
				break;
			case R.id.radio_button_zhanghu:
				button5.setTextColor(getResources().getColor(R.color.white));
				Drawable drawable5_1111 = this.getResources().getDrawable(
						R.drawable.tab_fifth_2);
				button5.setCompoundDrawablesWithIntrinsicBounds(null,
						drawable5_1111, null, null);
				setCurrentTabWithAnim(currentTab, 4, "MORE_TAB");
				// this.mTabHost.setCurrentTabByTag("MORE_TAB");
				break;
			}
		}
	}

	private void reBack() {
		button.setBackgroundResource(R.drawable.tab_dibu_select_bg_2);
		button.setTextColor(getResources().getColor(R.color.text_color));
		Drawable drawable_1 = this.getResources().getDrawable(
				R.drawable.tab_first_1);
		button.setCompoundDrawablesWithIntrinsicBounds(null, drawable_1, null,
				null);

		button2.setTextColor(getResources().getColor(R.color.text_color));
		Drawable drawable2 = this.getResources().getDrawable(
				R.drawable.tab_second_1);
		button2.setCompoundDrawablesWithIntrinsicBounds(null, drawable2, null,
				null);

		button3.setTextColor(getResources().getColor(R.color.text_color));
		Drawable drawable3 = this.getResources().getDrawable(
				R.drawable.tab_thread_1);
		button3.setCompoundDrawablesWithIntrinsicBounds(null, drawable3, null,
				null);

		button4.setTextColor(getResources().getColor(R.color.text_color));
		Drawable drawable4 = null;
		if (AllStaticMessage.ShoppingCar) {
			drawable4 = this.getResources().getDrawable(
					R.drawable.tab_forth_1_1);
		} else {
			drawable4 = this.getResources().getDrawable(R.drawable.tab_forth_1);
		}
		button4.setCompoundDrawablesWithIntrinsicBounds(null, drawable4, null,
				null);

		button5.setTextColor(getResources().getColor(R.color.text_color));
		Drawable drawable5 = this.getResources().getDrawable(
				R.drawable.tab_fifth_1);
		button5.setCompoundDrawablesWithIntrinsicBounds(null, drawable5, null,
				null);
	}

	private void setupIntent() {
		this.mTabHost = getTabHost();
		TabHost localTabHost = this.mTabHost;

		localTabHost.addTab(buildTabSpec("A_TAB", "��ʱ����",
				R.drawable.tab_fifth_2, this.mAIntent));

		localTabHost.addTab(buildTabSpec("B_TAB", "����Ȧ",
				R.drawable.tab_second_1, this.mBIntent));

		localTabHost.addTab(buildTabSpec("C_TAB", "����",
				R.drawable.tab_thread_1, this.mCIntent));

		localTabHost.addTab(buildTabSpec("D_TAB", "���ﳵ",
				R.drawable.tab_forth_1, this.mDIntent));

		localTabHost.addTab(buildTabSpec("MORE_TAB", "��",
				R.drawable.tab_fifth_1, this.mEIntent));
	}

	private TabHost.TabSpec buildTabSpec(String tag, String resLabel,
			int resIcon, final Intent content) {
		return this.mTabHost.newTabSpec(tag)
				.setIndicator(resLabel, getResources().getDrawable(resIcon))
				.setContent(content);
	}

	// ��ǩ��Ӷ���
	private void setCurrentTabWithAnim(int now, int next, String tag) {
		// ��������ǹؼ��������ж϶��������ķ���
		if (now > next) {
			mTabHost.getCurrentView().startAnimation(
					AnimationUtils.loadAnimation(this, R.anim.out_leftright));
			mTabHost.setCurrentTabByTag(tag);
			mTabHost.getCurrentView().startAnimation(
					AnimationUtils.loadAnimation(this, R.anim.in_leftright));
		} else {
			mTabHost.getCurrentView().startAnimation(
					AnimationUtils.loadAnimation(this, R.anim.out_rightleft));
			mTabHost.setCurrentTabByTag(tag);
			mTabHost.getCurrentView().startAnimation(
					AnimationUtils.loadAnimation(this, R.anim.in_rightleft));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	// ��¼
	private void doLogin(final String userName, final String psd,
			final String loginType, final String openid, String deviceType,
			final String gender, final String nickName, final String address) {

		String url = AllStaticMessage.URL_Login + userName + "&password=" + psd
				+ "&loginType=" + loginType + "&openid=" + openid
				+ "&deviceType=" + deviceType + "&gender=" + gender
				+ "&nickName=" + nickName + "&address=" + address + "&udid="
				+ MyTools.getAndroidID(TabHostActivity.this);
		// Log.i("11111", url);
		HttpUtil.get(url, TabHostActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@SuppressLint("ShowToast")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
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

								AllStaticMessage.userImage = sp.getString(
										AllStaticMessage.USER_PATH, "");

								Editor editor = sp.edit();
								editor.remove(AllStaticMessage.NAME);
								editor.remove(AllStaticMessage.PSW);
								editor.remove(AllStaticMessage.TYPE);
								editor.remove(AllStaticMessage.OPEN_ID);
								editor.remove(AllStaticMessage.GENDER);
								editor.remove(AllStaticMessage.NICK_NAME);
								editor.remove(AllStaticMessage.ADDRESS);
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
								editor.commit();
								getCarNum();

							} else {
								Toast.makeText(
										TabHostActivity.this,
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

	// ��ȡ���ﳵ��Ʒ����
	private void getCarNum() {
		String url = AllStaticMessage.URL_GetShoppingCarNum
				+ AllStaticMessage.User_Id + "&udid="
				+ MyTools.getAndroidID(TabHostActivity.this);
		HttpUtil.get(url, TabHostActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
								AllStaticMessage.Car_num = Integer
										.parseInt(response.getString("Results")
												.toString());
								Drawable drawable4_111 = null;
								if (AllStaticMessage.Car_num > 0) {
									AllStaticMessage.ShoppingCar = true;
									drawable4_111 = TabHostActivity.this
											.getResources().getDrawable(
													R.drawable.tab_forth_1_1);
								} else {
									AllStaticMessage.ShoppingCar = false;
									drawable4_111 = TabHostActivity.this
											.getResources().getDrawable(
													R.drawable.tab_forth_1);
								}
								button4.setCompoundDrawablesWithIntrinsicBounds(
										null, drawable4_111, null, null);
							} else {
								AllStaticMessage.ShoppingCar = false;
							}
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
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);

					}
				});
	}

	// ��¼
	private void guideDoLogin(final String userName, final String psd,
			final String loginType, final String openid, String deviceType,
			final String gender, final String nickName, final String address) {
		dialog.loading();
		String url = AllStaticMessage.URL_Login + userName + "&password=" + psd
				+ "&loginType=" + loginType + "&Photo="
				+ AllStaticMessage.userImage + "&openid=" + openid
				+ "&deviceType=" + deviceType + "&gender=" + gender
				+ "&nickName=" + nickName + "&address=" + address + "&udid="
				+ MyTools.getAndroidID(TabHostActivity.this);
		// Log.i("11111", url);
		HttpUtil.get(url, TabHostActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {

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
								AllStaticMessage.Back_to_ZhangHu = false;
								AllStaticMessage.guideRegister = false;
								AllStaticMessage.Register = false;
								nextView(mTabHost.getCurrentTab(), 4);
							} else {
								Toast.makeText(
										TabHostActivity.this,
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

	private void getTagsData() {
		String url = AllStaticMessage.URL_TagsList;
		HttpUtil.get(url, TabHostActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
								JSONArray array = response
										.getJSONArray("Results");
								List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
								for (int i = 0; i < array.length(); i++) {
									jsonObjects.add(array.getJSONObject(i));
								}
								AllStaticMessage.jsonObjectsTag = jsonObjects;
							}
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
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);

					}
				});
	}
}
