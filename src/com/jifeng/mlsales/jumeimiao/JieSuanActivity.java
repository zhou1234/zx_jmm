package com.jifeng.mlsales.jumeimiao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.alipay.sdk.app.PayTask;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.tools.ShrefUtil;
import com.jifeng.tools.TasckActivity;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.jifeng.zfb.Keys;
import com.jifeng.zfb.Result;
import com.jifeng.zfb.SignUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class JieSuanActivity extends Activity implements IWXAPIEventHandler {
	private Intent mIntent;
	private TextView mText_Name, mText_Phone, mText_Address, mText_Time;
	private RelativeLayout mLayout_1, mLayout_2;
	private ImageView mImage_zhifubao, mImage_weixin;
	// private Button mBtn_BianJi;
	private ShrefUtil mShrefUtil;
	private String addressId = null, payWay = null, orderAmount = null,
			goodsAmount = null;
	private TextView mTextView_GoodsPrice, mTextView_YunFei,
			mTextView_AllPrice, mText_guanfang;
	private LoadingDialog dialog;
	private boolean twoFlag = false;
	/**
	 * ֧����
	 **/
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;

	/**
	 * ΢��
	 */
	// ����΢��֧��
	private IWXAPI api;

	// Map<String,String> resultunifiedorder;
	// StringBuffer sb;

	private TasckActivity tasckActivity;

	private String wx_order;
	private String allvalue = "";

	private List<JSONObject> mJsonObjects;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jiesuan);
		mShrefUtil = new ShrefUtil(this, "data");
		dialog = new LoadingDialog(this);
		dialog.loading();
		mJsonObjects = new ArrayList<JSONObject>();
		findView();
		initDatas();
		aboutWX();
		tasckActivity = new TasckActivity();
		tasckActivity.pushActivity(JieSuanActivity.this);
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
		dialog = null;
		mIntent = null;
		mText_Name = null;
		mText_Phone = null;
		mText_Address = null;
		mText_Time = null;
		mLayout_1 = null;
		mLayout_2 = null;
		mImage_zhifubao = null;
		mImage_weixin = null;
		// private Button mBtn_BianJi=null;
		mShrefUtil = null;
		addressId = null;
		payWay = null;
		orderAmount = null;
		goodsAmount = null;
		mTextView_GoodsPrice = null;
		mTextView_YunFei = null;
		mTextView_AllPrice = null;
		api = null;
		tasckActivity.popActivity(JieSuanActivity.this);
		tasckActivity = null;
		setContentView(R.layout.view_null);
		this.finish();
		System.gc();
	}

	private void aboutWX() {
		api = WXAPIFactory.createWXAPI(JieSuanActivity.this,
				AllStaticMessage.APP_ID); // App_Id

		// sb=new StringBuffer();
	}

	// ���ҿؼ�
	private void findView() {
		mText_Name = (TextView) findViewById(R.id.jiesuan_name);
		mText_Phone = (TextView) findViewById(R.id.jiesuan_phone);
		mText_Address = (TextView) findViewById(R.id.jiesuan_address);
		mText_Time = (TextView) findViewById(R.id.jiesuan_time);
		mLayout_1 = (RelativeLayout) findViewById(R.id.jiesuan_select);
		mLayout_2 = (RelativeLayout) findViewById(R.id.jiesuan_select_address);
		mImage_zhifubao = (ImageView) findViewById(R.id.jiesuan_zhifubao_select);
		mImage_weixin = (ImageView) findViewById(R.id.jiesuan_weixin_select);
		mTextView_GoodsPrice = (TextView) findViewById(R.id.jiesuan_goods_price);
		mTextView_YunFei = (TextView) findViewById(R.id.jiesuan_yunfei_price);
		mTextView_AllPrice = (TextView) findViewById(R.id.jiesuan_all_price);
		// / mBtn_BianJi=(Button) findViewById(R.id.jiesuan_bianji);
		mText_guanfang = (TextView) findViewById(R.id.jiesuan_youhuiquan);
	}

	/*
	 * ��ʼ������
	 */
	private void initData() {
		if (AllStaticMessage.mJsonObject_select_address == null) {
			initDatas();
		} else {
			mLayout_1.setVisibility(View.VISIBLE);
			mLayout_2.setVisibility(View.GONE);
			try {

				MyTools.setText(mText_Name,
						AllStaticMessage.mJsonObject_select_address
								.getString("TrueName"), JieSuanActivity.this);
				MyTools.setText(mText_Phone,
						AllStaticMessage.mJsonObject_select_address
								.getString("PhoneTel"), JieSuanActivity.this);
				MyTools.setText(
						mText_Address,
						AllStaticMessage.mJsonObject_select_address
								.getString("Province")
								+ AllStaticMessage.mJsonObject_select_address
										.getString("City")
								+ AllStaticMessage.mJsonObject_select_address
										.getString("Country")
								+ AllStaticMessage.mJsonObject_select_address
										.getString("DetailAddress"),
						JieSuanActivity.this);
				MyTools.setText(mText_Time,
						AllStaticMessage.mJsonObject_select_address
								.getString("GoodsTime"), JieSuanActivity.this);
				addressId = AllStaticMessage.mJsonObject_select_address
						.getString("Id");
				getYunFei(addressId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * ��ʼ������
	 */
	private void initDatas() {
		String url = AllStaticMessage.URL_Get_AddressList
				+ AllStaticMessage.User_Id;// AllStaticMessage.User_Id
		// Log.i("11111", url);
		HttpUtil.get(url, JieSuanActivity.this, dialog,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								mLayout_1.setVisibility(View.VISIBLE);
								mLayout_2.setVisibility(View.GONE);
								JSONArray mArray = response
										.getJSONArray("Results");
								if (mJsonObjects != null) {
									mJsonObjects.clear();
								}

								for (int i = 0; i < mArray.length(); i++) {
									mJsonObjects.add(mArray.getJSONObject(i));
								}
								MyTools.setText(mText_Name, mJsonObjects.get(0)
										.getString("TrueName"),
										JieSuanActivity.this);
								MyTools.setText(mText_Phone, mJsonObjects
										.get(0).getString("PhoneTel"),
										JieSuanActivity.this);
								MyTools.setText(
										mText_Address,
										mJsonObjects.get(0).getString(
												"Province")
												+ mJsonObjects.get(0)
														.getString("City")
												+ mJsonObjects.get(0)
														.getString("Country")
												+ mJsonObjects
														.get(0)
														.getString(
																"DetailAddress"),
										JieSuanActivity.this);
								MyTools.setText(mText_Time, mJsonObjects.get(0)
										.getString("GoodsTime"),
										JieSuanActivity.this);
								addressId = mJsonObjects.get(0).getString("Id");
								getYunFei(addressId);

							} else {
								mLayout_2.setVisibility(View.VISIBLE);
								mLayout_1.setVisibility(View.GONE);
								Toast.makeText(
										JieSuanActivity.this,
										response.getString("Results")
												.toString(), 500).show();
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

	// //xmlע�����¼���ʵ��
	@SuppressLint("ShowToast")
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.setting_back:// ����
			finish();
			break;
		case R.id.jiesuan_select_address:
			// mText_guanfang.setText("δʹ��");
			// AllStaticMessage.guanquan_name="";
			// AllStaticMessage.guanquan_value="";
			// AllStaticMessage.guanquanid="";

			// mIntent = new Intent(JieSuanActivity.this,
			// AddressListActivity.class);
			// mIntent.putExtra("flag", "jiesuan");
			// startActivity(mIntent);

			mIntent = new Intent(JieSuanActivity.this,
					NewCreateAddressActivity.class);
			mIntent.putExtra("data", "");
			startActivity(mIntent);
			break;
		case R.id.jiesuan_btn_select_address:
			// mIntent = new Intent(JieSuanActivity.this,
			// AddressListActivity.class);
			// mIntent.putExtra("flag", "jiesuan");
			// startActivity(mIntent);

			mIntent = new Intent(JieSuanActivity.this,
					NewCreateAddressActivity.class);
			mIntent.putExtra("data", "");
			startActivity(mIntent);
			break;
		case R.id.jiesuan_bianji:
			mIntent = new Intent(JieSuanActivity.this,
					AddressListActivity.class);
			mIntent.putExtra("flag", "jiesuan");
			startActivityForResult(mIntent, 0);
			// startActivity(mIntent);
			break;
		case R.id.jiesuan_guan_quan:
			if (allvalue.equals("")) {
				Toast.makeText(JieSuanActivity.this, "���޿����Ż�ȯ", 500).show();
				return;
			}
			mIntent = new Intent(JieSuanActivity.this, GuanQuanActivity.class);
			mIntent.putExtra("value", allvalue);
			startActivityForResult(mIntent, 0x09);

			break;
		case R.id.jiesuan_zhifubao_select:
			payWay = "֧����֧��";
			mImage_zhifubao.setImageDrawable(getResources().getDrawable(
					R.drawable.register_select_2));
			mImage_weixin.setImageDrawable(getResources().getDrawable(
					R.drawable.register_select_1));
			break;
		case R.id.jiesuan_weixin_select:
			aboutWX();
			payWay = "΢��֧��";
			mImage_zhifubao.setImageDrawable(getResources().getDrawable(
					R.drawable.register_select_1));
			mImage_weixin.setImageDrawable(getResources().getDrawable(
					R.drawable.register_select_2));
			break;
		case R.id.jiesuan_tijiao_dindan:
			// private String addressId,couponId,payWay,orderAmount,goodsAmount;
			// private TextView
			// mTextView_GoodsPrice,mTextView_YunFei,mTextView_AllPrice;
			if (addressId == null || addressId.equals("")) {
				Toast.makeText(JieSuanActivity.this, "��ѡ���ջ���ַ", 500).show();
				return;
			}
			if (payWay == null || payWay.equals("")) {
				Toast.makeText(JieSuanActivity.this, "��ѡ��֧����ʽ", 500).show();
				return;
			}
			orderAmount = mTextView_AllPrice.getText().toString()
					.replace("��", "");
			goodsAmount = mTextView_GoodsPrice.getText().toString()
					.replace("��", "");
			tijiaoOrder();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0x09:
				if (!AllStaticMessage.guanquan_name.equals("")) {
					mText_guanfang.setText(AllStaticMessage.guanquan_name);
					Double mm = Double.valueOf(AllStaticMessage.guanquan_value);
					Double nn = Double.valueOf(allvalue);
					Double value = nn - mm;
					if (value < 0) {
						value = 0.00;
					}
					mTextView_AllPrice.setText(String.valueOf(value));
				}
				break;
			default:
				break;
			}
		}
		if (resultCode == RESULT_CANCELED) {
			switch (requestCode) {
			case 0:
				initData();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (AllStaticMessage.JieSuan_Select_Address) {
			AllStaticMessage.guanquanid = "";
			AllStaticMessage.guanquan_name = "";
			AllStaticMessage.guanquan_value = "";

			AllStaticMessage.JieSuan_Select_Address = false;

			if (AllStaticMessage.mJsonObject_select_address != null) {
				mLayout_1.setVisibility(View.VISIBLE);
				mLayout_2.setVisibility(View.GONE);
				mShrefUtil.write("songhuo_address",
						AllStaticMessage.mJsonObject_select_address.toString());
				try {
					MyTools.setText(mText_Name,
							AllStaticMessage.mJsonObject_select_address
									.getString("TrueName"),
							JieSuanActivity.this);
					MyTools.setText(mText_Phone,
							AllStaticMessage.mJsonObject_select_address
									.getString("PhoneTel"),
							JieSuanActivity.this);
					MyTools.setText(
							mText_Address,
							AllStaticMessage.mJsonObject_select_address
									.getString("Province")
									+ AllStaticMessage.mJsonObject_select_address
											.getString("City")
									+ AllStaticMessage.mJsonObject_select_address
											.getString("Country")
									+ AllStaticMessage.mJsonObject_select_address
											.getString("DetailAddress"),
							JieSuanActivity.this);
					MyTools.setText(mText_Time,
							AllStaticMessage.mJsonObject_select_address
									.getString("GoodsTime"),
							JieSuanActivity.this);
					addressId = AllStaticMessage.mJsonObject_select_address
							.getString("Id");
					getYunFei(addressId);
					twoFlag = true;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				addressId = "";
				mLayout_1.setVisibility(View.GONE);
				mLayout_2.setVisibility(View.VISIBLE);
			}
		}
	}

	// �����ύ
	private void tijiaoOrder() {
		dialog.loading();
		String url = AllStaticMessage.URL_Add_Order + AllStaticMessage.User_Id
				+ "&tel=" + "&addressId=" + addressId + "&payMessage="
				+ "&payWay=" + payWay + "&orderAmount=" + orderAmount
				+ "&goodsAmount=" + goodsAmount + "&couponId="
				+ AllStaticMessage.guanquanid;
		// Log.i("11111", url);
		HttpUtil.get(url, JieSuanActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {

								AllStaticMessage.guanquanid = "";
								AllStaticMessage.guanquan_name = "";
								AllStaticMessage.guanquan_value = "";

								Toast.makeText(
										JieSuanActivity.this,
										response.getJSONArray("Results")
												.getJSONObject(0)
												.getString("Message")
												.toString(), 500).show();
								if (getIntent().getStringExtra("flag").equals(
										"yes")) {
									AllStaticMessage.MyPayBack = true;
								} else {
									AllStaticMessage.MyPayBack = false;
								}
								AllStaticMessage.Back_to_ZhangHu = true;

								if (payWay.equals("֧����֧��")) {
									tijiao(response.getJSONArray("Results")
											.getJSONObject(0)
											.getString("OrderId"),
											response.getJSONArray("Results")
													.getJSONObject(0)
													.getString("Amount"));
								} else {
									boolean isPaySupported = api
											.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
									if (isPaySupported) {
										// ΢��֧�� ���в���
										wx_order = response
												.getJSONArray("Results")
												.getJSONObject(0)
												.getString("OrderId")
												.toString();
										String allprice = response
												.getJSONArray("Results")
												.getJSONObject(0)
												.getString("Amount").toString();
										AllStaticMessage.WxOrder = wx_order;
										weixinPay(allprice,
												AllStaticMessage.WxOrder);// ����΢��֧��
									} else {
										Toast.makeText(JieSuanActivity.this,
												"�Բ���,����΢�Ű汾����,��֧��΢�Ÿ���",
												Toast.LENGTH_SHORT).show();
									}
								}
							} else {
								Toast.makeText(
										JieSuanActivity.this,
										response.getString("Results")
												.toString(), 500).show();
								if (payWay.equals("֧����֧��")) {
									return;
								}
								if (wx_order != null && !wx_order.equals("")) {
									Intent intent = new Intent(
											JieSuanActivity.this,
											CheckOrderActivity.class);
									intent.putExtra("orderNum", wx_order);
									startActivity(intent);
									JieSuanActivity.this.finish();
								}
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

	// ��ȡ�˷�
	private void getYunFei(String addressid) {
		String url = AllStaticMessage.URL_YunFei + AllStaticMessage.User_Id
				+ "&addressId=" + addressid + "&udid="
				+ MyTools.getAndroidID(JieSuanActivity.this);
		HttpUtil.get(url, JieSuanActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
								mTextView_GoodsPrice.setText("��"
										+ response.getString("GoodsMoney")
												.toString());
								mTextView_YunFei.setText("��"
										+ response.getString("FreightMoney")
												.toString());
								mTextView_AllPrice.setText("��"
										+ response.getString("TotalMoney")
												.toString());
								allvalue = response.getString("TotalMoney")
										.toString();
								if (twoFlag) {
									mText_guanfang.setText("δʹ��");
									twoFlag = false;
								}
								// Toast.makeText(JieSuanActivity.this,response.getString("Results").toString(),500).show();
							} else {
								Toast.makeText(
										JieSuanActivity.this,
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

	/**
	 * ֧����
	 **/
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				Result resultObj = new Result((String) msg.obj);
				String resultStatus = resultObj.resultStatus;

				// �ж�resultStatus Ϊ��9000�������֧���ɹ�������״̬���������ɲο��ӿ��ĵ�
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(JieSuanActivity.this, "֧���ɹ�",
							Toast.LENGTH_SHORT).show();
				} else {
					// �ж�resultStatus Ϊ�ǡ�9000�����������֧��ʧ��
					// ��8000��
					// ����֧�������Ϊ֧������ԭ�����ϵͳԭ���ڵȴ�֧�����ȷ�ϣ����ս����Ƿ�ɹ��Է�����첽֪ͨΪ׼��С����״̬��
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(JieSuanActivity.this, "֧�����ȷ����",
								Toast.LENGTH_SHORT).show();

					} else {
						Toast.makeText(JieSuanActivity.this, "֧��ʧ��",
								Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(JieSuanActivity.this, "�����Ϊ��" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};

	/**
	 * call alipay sdk pay. ����SDK֧��
	 * 
	 */
	public void tijiao(final String orderNum, String allPrice) {
		String orderInfo = getOrderInfo(orderNum, allPrice);
		String sign = sign(orderInfo);
		try {
			// �����sign ��URL����
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// ����PayTask ����
				PayTask alipay = new PayTask(JieSuanActivity.this);
				// ����֧���ӿ�
				String result = alipay.pay(payInfo);

				Intent intent = new Intent(JieSuanActivity.this,
						CheckOrderActivity.class);
				intent.putExtra("orderNum", orderNum);
				// Log.i("111111", orderNum);
				// startActivityForResult(intent, 0x01);
				startActivity(intent);
				JieSuanActivity.this.finish();
				// Message msg = new Message();
				// msg.what = SDK_PAY_FLAG;
				// msg.obj = result;
				// mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * ��ѯ�ն��豸�Ƿ����֧������֤�˻�
	 * 
	 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask payTask = new PayTask(JieSuanActivity.this);
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * get the sdk version. ��ȡSDK�汾��
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	private String getOrderInfo(String orderNum, String allPrice) {

		// ����������ID
		String orderInfo = "partner=" + "\"" + Keys.DEFAULT_PARTNER + "\"";

		// ����֧�����˺�
		orderInfo += "&seller_id=" + "\"" + Keys.DEFAULT_SELLER + "\"";

		// �̻���վΨһ������
		orderInfo += "&out_trade_no=" + "\"" + orderNum + "\"";

		// ��Ʒ����
		orderInfo += "&subject=" + "\"" + "������:" + orderNum + "\"";

		// ��Ʒ����
		orderInfo += "&body=" + "\"" + "������" + "\"";

		// ��Ʒ���
		orderInfo += "&total_fee=" + "\"" + allPrice + "\"";

		// �������첽֪ͨҳ��·��
		orderInfo += "&notify_url=" + "\"" + AllStaticMessage.URL_notify + "\"";

		// �ӿ����ƣ� �̶�ֵ
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// ֧�����ͣ� �̶�ֵ
		orderInfo += "&payment_type=\"1\"";

		// �������룬 �̶�ֵ
		orderInfo += "&_input_charset=\"utf-8\"";

		// ����δ����׵ĳ�ʱʱ��
		// Ĭ��30���ӣ�һ����ʱ���ñʽ��׾ͻ��Զ����رա�
		// ȡֵ��Χ��1m��15d��
		// m-���ӣ�h-Сʱ��d-�죬1c-���죨���۽��׺�ʱ����������0��رգ���
		// �ò�����ֵ������С���㣬��1.5h����ת��Ϊ90m��
		orderInfo += "&it_b_pay=\"20m\"";

		// ֧��������������󣬵�ǰҳ����ת���̻�ָ��ҳ���·�����ɿ�
		// orderInfo += "&return_url=\"m.alipay.com\"";

		// �������п�֧���������ô˲���������ǩ���� �̶�ֵ
		// orderInfo += "&paymethod=\"expressGateway\"";
		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. ��ȡ�ⲿ������
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. �Զ�����Ϣ����ǩ��
	 * 
	 * @param content
	 *            ��ǩ��������Ϣ
	 */
	public String sign(String content) {
		return SignUtils.sign(content, Keys.PRIVATE);
	}

	/**
	 * get the sign type we use. ��ȡǩ����ʽ
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

	/**
	 * ΢�Ų���
	 */
	@SuppressWarnings("unused")
	private void weixinPay(String order_price, String order_no) {
		dialog.loading();
		String product_name = "�������Ӽ�������Ʒ";
		// try{
		// //���Է���������GBK���룬������Ҫת��
		// product_name = URLEncoder.encode("����΢��֧��(app��������ǩ��)","GBK");
		// }catch(Exception e){
		// Log.d("Parse errer","����ת��ʧ��");
		// Toast.makeText(MyPayActivity.this, "����ת��ʧ��",
		// Toast.LENGTH_SHORT).show();
		// }
		// �����url
		String token_url = AllStaticMessage.URL_Wx_pay + product_name
				+ "&out_trade_no=" + order_no + "&total_fee=" + order_price;// AllStaticMessage.URL_Wx_qian+order_price+"&order_no="+order_no+"&product_name="+product_name;

		// Toast.makeText(JieSuanActivity.this, "��ȡ������...", 200).show();
		HttpUtil.get(token_url, JieSuanActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						if (dialog != null) {
							dialog.stop();
						}
						try {
							PayReq req = new PayReq();
							req.appId = AllStaticMessage.APP_ID;
							req.partnerId = AllStaticMessage.APP_MCH;
							req.prepayId = response.getString("prepayid");
							req.nonceStr = response.getString("noncestr");
							req.timeStamp = response.getString("timestamp");
							req.packageValue = response.getString("package");
							req.sign = response.getString("sign");
							req.extData = "app data"; // optional
							// Toast.makeText(JieSuanActivity.this, "��������֧��",
							// 200).show();
							// ��֧��֮ǰ�����Ӧ��û��ע�ᵽ΢�ţ�Ӧ���ȵ���IWXMsg.registerApp��Ӧ��ע�ᵽ΢��
							api.registerApp(AllStaticMessage.APP_ID);
							api.sendReq(req);
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
						if (dialog != null) {
							dialog.stop();
						}
						Intent intent = new Intent(JieSuanActivity.this,
								CheckOrderActivity.class);
						intent.putExtra("orderNum", AllStaticMessage.WxOrder);
						startActivity(intent);
						JieSuanActivity.this.finish();
					}
				});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq arg0) {
		Toast.makeText(JieSuanActivity.this, "�쳣��" + arg0.openId, 200000)
				.show();
	}

	@Override
	public void onResp(BaseResp arg0) {
		Toast.makeText(JieSuanActivity.this,
				"�쳣��" + arg0.errCode + arg0.errStr, 200000).show();
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