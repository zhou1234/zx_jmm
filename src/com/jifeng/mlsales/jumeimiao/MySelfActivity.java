package com.jifeng.mlsales.jumeimiao;

import java.io.File;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.UserInfo;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.mlsales.photo.BitmapUtil;
import com.jifeng.mlsales.photo.PathManager;
import com.jifeng.myview.BadgeView;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.FileImageUpload;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MySelfActivity extends Activity {
	private Intent mIntent;
	private TextView mText_NickName, mText_Jifen;
	private LinearLayout mLayout_Show;// ��¼����ʾ����
	private TextView mTextFlag;
	private LoadingDialog dialog;
	private ImageView myself_rel_daifukuan, myself_rel_daifahuo,
			myself_rel_daishouhuo;
	private BadgeView badgeView1, badgeView2, badgeView3;

	private TextView tv_number;// ��ʾ�Ż�ȯ��ʹ�õĸ���
	private ImageView img_touxiang;
	private Bitmap bit;
	private SharedPreferences sp;
	private String str;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0000:
				try {
					if (!str.equals("")) {
						JSONObject object = new JSONObject(str);
						if (object.getString("Status").toString()
								.equals("true")) {
							String strUrl = object.getString("Results")
									.toString();
							AllStaticMessage.userImage = strUrl;
							ImageLoader.getInstance().loadImage(strUrl,
									new ImageLoadingListener() {

										@Override
										public void onLoadingStarted(
												String arg0, View arg1) {

										}

										@Override
										public void onLoadingFailed(
												String arg0, View arg1,
												FailReason arg2) {

										}

										@Override
										public void onLoadingComplete(
												String arg0, View arg1,
												Bitmap arg2) {
											img_touxiang.setImageBitmap(arg2);
											dialog.stop();
										}

										@Override
										public void onLoadingCancelled(
												String arg0, View arg1) {

										}
									});

							Editor editor = sp.edit();
							editor.remove(AllStaticMessage.USER_PATH);
							editor.commit();

							editor.putString(AllStaticMessage.USER_PATH, strUrl);
							editor.commit();

						} else {
							Toast.makeText(MySelfActivity.this, "�ϴ�ʧ��",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(MySelfActivity.this, "�ϴ�ʧ��",
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myself);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		sp = getSharedPreferences(AllStaticMessage.SPNE, 0);
		findView();
		if (!AllStaticMessage.Login_Flag.equals("")) {
			mTextFlag.setVisibility(View.GONE);
			mLayout_Show.setVisibility(View.VISIBLE);
		} else {
			mLayout_Show.setVisibility(View.GONE);
			mTextFlag.setVisibility(View.VISIBLE);
			if (AllStaticMessage.User_NickName == null
					|| AllStaticMessage.User_NickName.equals("")) {
				mText_NickName.setText(AllStaticMessage.User_Name);
			} else {
				mText_NickName.setText(AllStaticMessage.User_NickName);
			}
		}

	}

	// ���ҿؼ�
	private void findView() {

		mText_NickName = (TextView) findViewById(R.id.myself_nickname);
		mText_Jifen = (TextView) findViewById(R.id.myself_text_jifen);
		mTextFlag = (TextView) findViewById(R.id.textc);
		mLayout_Show = (LinearLayout) findViewById(R.id.myself_message);

		myself_rel_daifukuan = (ImageView) findViewById(R.id.myself_rel_daifukuan);
		myself_rel_daifahuo = (ImageView) findViewById(R.id.myself_rel_daifahuo);
		myself_rel_daishouhuo = (ImageView) findViewById(R.id.myself_rel_daishouhuo);

		badgeView1 = new BadgeView(MySelfActivity.this, myself_rel_daifukuan);
		badgeView1.setBadgeMargin(0);
		badgeView2 = new BadgeView(MySelfActivity.this, myself_rel_daifahuo);
		badgeView2.setBadgeMargin(0);
		badgeView3 = new BadgeView(MySelfActivity.this, myself_rel_daishouhuo);
		badgeView3.setBadgeMargin(0);

		tv_number = (TextView) findViewById(R.id.tv_number);

		img_touxiang = (ImageView) findViewById(R.id.img_touxiang);
		bit = BitmapFactory.decodeResource(getResources(), R.drawable.my_icon);
		img_touxiang.setImageBitmap(bit);
		if (!AllStaticMessage.userImage.equals("")) {
			dialog.loading();
			ImageLoader.getInstance().loadImage(AllStaticMessage.userImage,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String arg0, View arg1) {

						}

						@Override
						public void onLoadingFailed(String arg0, View arg1,
								FailReason arg2) {

						}

						@Override
						public void onLoadingComplete(String arg0, View arg1,
								Bitmap arg2) {
							img_touxiang.setImageBitmap(arg2);
							dialog.stop();
						}

						@Override
						public void onLoadingCancelled(String arg0, View arg1) {

						}
					});
		}

	}

	/***
	 * xmlע�����¼���ʵ��
	 * */
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.img_touxiang:
			if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
				mIntent = new Intent(MySelfActivity.this, LoginActivity.class);
				startActivityForResult(mIntent, 1111);
			} else {
				if (sp.getString(AllStaticMessage.OPEN_ID, "").equals("")) {
					AllStaticMessage.user_flag = true;
					Intent mIntent = new Intent(MySelfActivity.this,
							ActivityCapture.class);
					startActivityForResult(mIntent, 0x110);
				}
			}
			break;
		case R.id.my_rel:
		case R.id.rel_top:
			if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
				mIntent = new Intent(MySelfActivity.this, LoginActivity.class);
				startActivityForResult(mIntent, 1111);
			} else {
				mIntent = new Intent(MySelfActivity.this,
						SinglePersonActivity.class);
				startActivityForResult(mIntent, 0x00);
			}
			break;
		case R.id.myself_rel_daifukuan:
			if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
				mIntent = new Intent(MySelfActivity.this, LoginActivity.class);
				startActivityForResult(mIntent, 2222);
			} else {
				mIntent = new Intent(MySelfActivity.this,
						DaiZhiFuActivity.class);
				mIntent.putExtra("title", "�������");
				startActivity(mIntent);
			}
			break;
		case R.id.myself_rel_daifahuo:
			if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
				mIntent = new Intent(MySelfActivity.this, LoginActivity.class);
				startActivityForResult(mIntent, 3333);
			} else {
				mIntent = new Intent(MySelfActivity.this,
						DaiZhiFuActivity.class);
				mIntent.putExtra("title", "����������");
				startActivity(mIntent);
			}
			break;
		case R.id.myself_rel_daishouhuo:
			if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
				mIntent = new Intent(MySelfActivity.this, LoginActivity.class);
				startActivityForResult(mIntent, 4444);
			} else {
				mIntent = new Intent(MySelfActivity.this,
						DaiShouHuoActivity.class);
				startActivity(mIntent);
			}
			break;
		case R.id.rel_myalldindan:
			if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
				mIntent = new Intent(MySelfActivity.this, LoginActivity.class);
				startActivityForResult(mIntent, 6666);
			} else {
				mIntent = new Intent(MySelfActivity.this, OrderActivity.class);
				startActivity(mIntent);
			}

			break;

		case R.id.rel_myyouhuiquan:
			if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
				mIntent = new Intent(MySelfActivity.this, LoginActivity.class);
				startActivityForResult(mIntent, 7777);
			} else {
				mIntent = new Intent(MySelfActivity.this, MyQuanActivity.class);
				startActivity(mIntent);
			}

			break;
		case R.id.rel_myshouhuodizhi:
			if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
				mIntent = new Intent(MySelfActivity.this, LoginActivity.class);
				startActivityForResult(mIntent, 8888);
			} else {
				mIntent = new Intent(MySelfActivity.this,
						AddressListActivity.class);
				mIntent.putExtra("flag", "myself");
				startActivity(mIntent);
			}
			break;
		case R.id.rel_mysave:
			if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
				mIntent = new Intent(MySelfActivity.this, LoginActivity.class);
				startActivityForResult(mIntent, 9999);
			} else {
				mIntent = new Intent(MySelfActivity.this, SaveActivity.class);
				startActivity(mIntent);
			}
			break;
		case R.id.img_setting:// ����
		case R.id.ll_seting:
			mIntent = new Intent(MySelfActivity.this, SettingActivity.class);
			startActivity(mIntent);
			break;
		case R.id.rel_aboutus:// ��������
			mIntent = new Intent(MySelfActivity.this, AboutUsActivity.class);
			startActivity(mIntent);
			break;
		case R.id.rel_kefu:// ���߿ͷ�
			if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
				mIntent = new Intent(MySelfActivity.this, LoginActivity.class);
				startActivityForResult(mIntent, 5555);
			} else {
				dialog.loading();
				// ��ʼ����
				RongIM.init(MySelfActivity.this, AllStaticMessage.KeFu_key,
						R.drawable.icon);
				// �����û���Ϣ�ṩ�ߡ�
				RongIM.setGetUserInfoProvider(new RongIM.GetUserInfoProvider() {
					// App ����ָ�����û���Ϣ�� IMKit ���������
					@Override
					public RongIMClient.UserInfo getUserInfo(String userId) {
						// ԭ���� App
						// Ӧ�ý��û���Ϣ��ͷ�����ƶ��豸�Ͻ��л��棬ÿ�λ�ȡ�û���Ϣ��ʱ�򣬾Ͳ�����ͨ�������ȡ����߼����ٶȣ������û����顣���Ǻ������ṩ�û���Ϣ���湦�ܣ�������������
						return getUserInfoFromLocalCache(userId); // �ɿ������ṩ�˷�����
					}

				}, false);

				getTaken();
			}

			break;
		case R.id.setting_rel_kefudianhua:// �ͷ��绰
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_CALL); // ֱ�Ӳ���ACTION_CALL
													// ���벦�Ž���ACTION_DIAL
			intent.setData(Uri.parse("tel:4009696876"));
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void getTaken() {
		String url = AllStaticMessage.URL_KeFu + AllStaticMessage.User_Id;
		HttpUtil.get(url, MySelfActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@SuppressLint("ShowToast")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							AllStaticMessage.KeFu_Token = response.getString(
									"token").toString();
							if (!AllStaticMessage.KeFu_Token.equals("")) {
								// �������Ʒ�������
								try {
									RongIM.connect(AllStaticMessage.KeFu_Token,
											new RongIMClient.ConnectCallback() {

												@Override
												public void onSuccess(String s) {
													// �˴��������ӳɹ���
													// Log.d("Connect:",
													// "Login successfully.");
													if (dialog != null) {
														dialog.stop();
													}
													RongIM.getInstance()
															.startCustomerServiceChat(
																	MySelfActivity.this,
																	AllStaticMessage.KeFu_seviceId,
																	"���߿ͷ�");
												}

												@Override
												public void onError(
														ErrorCode errorCode) {
													// �˴��������Ӵ���
													// Log.d("Connect:",
													// "Login failed.");
													if (dialog != null) {
														dialog.stop();
													}
												}
											});
								} catch (Exception e) {
									e.printStackTrace();
									if (dialog != null) {
										dialog.stop();
									}
								}
							} else {
								Toast.makeText(MySelfActivity.this, "�ͷ���æ,���Ժ�",
										500).show();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (AllStaticMessage.User_NickName == null
					|| AllStaticMessage.User_NickName.equals("")) {
				mText_NickName.setText(AllStaticMessage.User_Name);
			} else {
				mText_NickName.setText(AllStaticMessage.User_NickName);
			}
			mText_Jifen.setText("����:" + AllStaticMessage.User_JiFen + "��");

			switch (requestCode) {
			case 0x00:
				img_touxiang.setImageBitmap(bit);
				break;
			case 1111:// ��������
				mTextFlag.setVisibility(View.GONE);
				mLayout_Show.setVisibility(View.VISIBLE);
				if (!AllStaticMessage.userImage.equals("")) {
					dialog.loading();
					ImageLoader.getInstance().loadImage(
							AllStaticMessage.userImage,
							new ImageLoadingListener() {

								@Override
								public void onLoadingStarted(String arg0,
										View arg1) {

								}

								@Override
								public void onLoadingFailed(String arg0,
										View arg1, FailReason arg2) {

								}

								@Override
								public void onLoadingComplete(String arg0,
										View arg1, Bitmap arg2) {
									img_touxiang.setImageBitmap(arg2);
									dialog.stop();
								}

								@Override
								public void onLoadingCancelled(String arg0,
										View arg1) {

								}
							});
				}
				break;
			case 2222:// ������
				mTextFlag.setVisibility(View.GONE);
				mLayout_Show.setVisibility(View.VISIBLE);
				mIntent = new Intent(MySelfActivity.this,
						DaiZhiFuActivity.class);
				mIntent.putExtra("title", "�������");
				startActivity(mIntent);
				break;
			case 3333:// ������
				mTextFlag.setVisibility(View.GONE);
				mLayout_Show.setVisibility(View.VISIBLE);
				mIntent = new Intent(MySelfActivity.this,
						DaiZhiFuActivity.class);
				mIntent.putExtra("title", "����������");
				startActivity(mIntent);
				break;
			case 4444:// ���ջ�
				mTextFlag.setVisibility(View.GONE);
				mLayout_Show.setVisibility(View.VISIBLE);
				mIntent = new Intent(MySelfActivity.this,
						DaiShouHuoActivity.class);
				startActivity(mIntent);
				break;
			case 5555:
				dialog.loading();
				// ��ʼ����
				RongIM.init(this, AllStaticMessage.KeFu_key, R.drawable.icon);
				getTaken();
				break;
			case 6666:// ���ж���
				mTextFlag.setVisibility(View.GONE);
				mLayout_Show.setVisibility(View.VISIBLE);
				mIntent = new Intent(MySelfActivity.this, OrderActivity.class);
				startActivity(mIntent);
				break;
			case 7777:// �Ż�ȯ
				mTextFlag.setVisibility(View.GONE);
				mLayout_Show.setVisibility(View.VISIBLE);
				mIntent = new Intent(MySelfActivity.this, MyQuanActivity.class);
				startActivity(mIntent);
				break;
			case 8888:// �ջ���ַ
				mTextFlag.setVisibility(View.GONE);
				mLayout_Show.setVisibility(View.VISIBLE);
				mIntent = new Intent(MySelfActivity.this,
						AddressListActivity.class);
				mIntent.putExtra("flag", "myself");
				startActivity(mIntent);
				break;
			case 9999:// �ҵ��ղ�
				mTextFlag.setVisibility(View.GONE);
				mLayout_Show.setVisibility(View.VISIBLE);
				mIntent = new Intent(MySelfActivity.this, SaveActivity.class);
				startActivity(mIntent);
				break;
			case 0x110:
				dialog.loading();
				final String path = data.getStringExtra("path");
				Bitmap bmp = BitmapFactory.decodeFile(path);
				Bitmap bit = Bitmap.createScaledBitmap(bmp, 100, 100, false);
				final File photoFile = PathManager.getCropPhotoPath();
				boolean successful = BitmapUtil.saveBitmap2file(bit, photoFile,
						Bitmap.CompressFormat.JPEG, 100);
				while (!successful) {
					successful = BitmapUtil.saveBitmap2file(bit, photoFile,
							Bitmap.CompressFormat.JPEG, 100);
				}

				new Thread(new Runnable() {

					@Override
					public void run() {
						str = FileImageUpload.upUserBitmap(
								AllStaticMessage.URL_UpUserPhoto,
								photoFile.getAbsolutePath(),
								AllStaticMessage.User_Id);
						handler.sendEmptyMessage(0000);
					}
				}).start();
				break;
			default:
				break;
			}
		}
	}

	private UserInfo getUserInfoFromLocalCache(String userId) {
		RongIMClient.UserInfo info = new UserInfo(AllStaticMessage.User_Id,
				AllStaticMessage.User_Name, "");
		return info;
	}

	/*
	 * ˫���˳�
	 */
	private long mExitTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "�ٰ�һ���˳�������", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				this.getApplication().onTerminate();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
		// mIntent = null;
		// mText_NickName = null;
		// mText_Jifen = null;
		// mLayout_Show = null;// ��¼����ʾ����
		// mTextFlag = null;
		// mIntent = null;
		// setContentView(R.layout.view_null);
		// System.gc();
	}

	public void onResume() {// ����
		super.onResume();
		dialog.loading();
		if (AllStaticMessage.Login_Flag.equals("")) {
			mLayout_Show.setVisibility(View.GONE);
			mTextFlag.setVisibility(View.VISIBLE);
		} else {
			mTextFlag.setVisibility(View.GONE);
			mLayout_Show.setVisibility(View.VISIBLE);
			if (AllStaticMessage.User_NickName == null
					|| AllStaticMessage.User_NickName.equals("")) {
				mText_NickName.setText(AllStaticMessage.User_Name);
			} else {
				mText_NickName.setText(AllStaticMessage.User_NickName);
			}
			mText_Jifen.setText("����:" + AllStaticMessage.User_JiFen + "��");
		}
		getData1();
		getData2();
		getData3();
		getQuan();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void getData1() {
		String url = AllStaticMessage.URL_Order_List + AllStaticMessage.User_Id
				+ "&orderState=" + "1" + "&page=" + "1";
		HttpUtil.get(url, MySelfActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								JSONArray array = response
										.getJSONArray("Results");
								badgeView1.setText(array.length() + "");
								badgeView1.show();

							} else if (badgeView1 != null) {
								badgeView1.hide();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						// if (dialog != null) {
						// dialog.stop();
						// }

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

	private void getData2() {
		String url = AllStaticMessage.URL_Order_List + AllStaticMessage.User_Id
				+ "&orderState=" + "2" + "&page=" + "1";
		HttpUtil.get(url, MySelfActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								JSONArray array = response
										.getJSONArray("Results");
								badgeView2.setText(array.length() + "");
								badgeView2.show();

							} else if (badgeView2 != null) {
								badgeView2.hide();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						// if (dialog != null) {
						// dialog.stop();
						// }

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

	private void getData3() {
		String url = AllStaticMessage.URL_Order_List + AllStaticMessage.User_Id
				+ "&orderState=3" + "&page=" + "1";
		HttpUtil.get(url, MySelfActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@SuppressLint("ShowToast")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							if (response.getString("Status").equals("true")) {
								JSONArray array = response
										.getJSONArray("Results");
								badgeView3.setText(array.length() + "");
								badgeView3.show();

							} else if (badgeView3 != null) {
								badgeView3.hide();
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

	/**
	 * ��ȡ���õ��Ż�ȯ
	 */
	private void getQuan() {
		// String url = AllStaticMessage.URL_Quan_list +
		// AllStaticMessage.User_Id
		// + "&type=" + type;
		String url = AllStaticMessage.URL_Quan_list_New + "0" + "&UserId="
				+ AllStaticMessage.User_Id;

		HttpUtil.get(url, MySelfActivity.this, null,
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
								if (array.length() > 0) {
									tv_number.setVisibility(View.VISIBLE);
									tv_number.setText(array.length() + "�ſ���");
								}

							} else {
								tv_number.setVisibility(View.GONE);
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
						// ���󷵻�JSONObject
					}
				});
	}
}
