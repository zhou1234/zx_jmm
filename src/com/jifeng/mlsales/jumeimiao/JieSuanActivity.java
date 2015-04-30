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
	 * 支付宝
	 **/
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;

	/**
	 * 微信
	 */
	// 集成微信支付
	private IWXAPI api;

	// Map<String,String> resultunifiedorder;
	// StringBuffer sb;

	private TasckActivity tasckActivity;

	private String wx_order;
	private String allvalue = "";

	private String zfb_order;

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
//		dialog = null;
//		mIntent = null;
//		mText_Name = null;
//		mText_Phone = null;
//		mText_Address = null;
//		mText_Time = null;
//		mLayout_1 = null;
//		mLayout_2 = null;
//		mImage_zhifubao = null;
//		mImage_weixin = null;
//		// private Button mBtn_BianJi=null;
//		mShrefUtil = null;
//		addressId = null;
//		payWay = null;
//		orderAmount = null;
//		goodsAmount = null;
//		mTextView_GoodsPrice = null;
//		mTextView_YunFei = null;
//		mTextView_AllPrice = null;
//		api = null;
//		tasckActivity.popActivity(JieSuanActivity.this);
//		tasckActivity = null;
//		setContentView(R.layout.view_null);
//		this.finish();
//		System.gc();
	}

	private void aboutWX() {
		api = WXAPIFactory.createWXAPI(JieSuanActivity.this,
				AllStaticMessage.APP_ID); // App_Id

		// sb=new StringBuffer();
	}

	// 查找控件
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
	 * 初始化数据
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
	 * 初始化数据
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
						// 成功返回JSONObject
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
								if (dialog != null) {
									dialog.stop();
								}
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
						if (dialog != null) {
							dialog.stop();
						}
					}
				});
	}

	// //xml注册点击事件的实现
	@SuppressLint("ShowToast")
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.setting_back:// 返回
			finish();
			break;
		case R.id.jiesuan_select_address:
			// mText_guanfang.setText("未使用");
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
			// startActivity(mIntent);
			startActivityForResult(mIntent, 1);
			break;
		case R.id.jiesuan_btn_select_address:
			// mIntent = new Intent(JieSuanActivity.this,
			// AddressListActivity.class);
			// mIntent.putExtra("flag", "jiesuan");
			// startActivity(mIntent);

			mIntent = new Intent(JieSuanActivity.this,
					NewCreateAddressActivity.class);
			mIntent.putExtra("data", "");
			//startActivity(mIntent);
			startActivityForResult(mIntent, 1);
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
				Toast.makeText(JieSuanActivity.this, "暂无可用优惠券", 500).show();
				return;
			}
			mIntent = new Intent(JieSuanActivity.this, GuanQuanActivity.class);
			mIntent.putExtra("value", allvalue);
			startActivityForResult(mIntent, 0x09);

			break;
		case R.id.jiesuan_zhifubao_select:
			payWay = "支付宝支付";
			mImage_zhifubao.setImageDrawable(getResources().getDrawable(
					R.drawable.register_select_2));
			mImage_weixin.setImageDrawable(getResources().getDrawable(
					R.drawable.register_select_1));
			break;
		case R.id.jiesuan_weixin_select:
			aboutWX();
			payWay = "微信支付";
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
				Toast.makeText(JieSuanActivity.this, "请选择收货地址", 500).show();
				return;
			}
			if (payWay == null || payWay.equals("")) {
				Toast.makeText(JieSuanActivity.this, "请选择支付方式", 500).show();
				return;
			}
			orderAmount = mTextView_AllPrice.getText().toString()
					.replace("￥", "");
			goodsAmount = mTextView_GoodsPrice.getText().toString()
					.replace("￥", "");
			tijiaoOrder();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
			case 1:
				initDatas();
				break;

			}
		}

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (AllStaticMessage.JieSuan_Select_Address) {
			AllStaticMessage.guanquanid = "";
			AllStaticMessage.guanquan_name = "";
			AllStaticMessage.guanquan_value = "";

			AllStaticMessage.JieSuan_Select_Address = false;

//			if (AllStaticMessage.mJsonObject_select_address != null) {
//				mLayout_1.setVisibility(View.VISIBLE);
//				mLayout_2.setVisibility(View.GONE);
//				mShrefUtil.write("songhuo_address",
//						AllStaticMessage.mJsonObject_select_address.toString());
//				try {
//					MyTools.setText(mText_Name,
//							AllStaticMessage.mJsonObject_select_address
//									.getString("TrueName"),
//							JieSuanActivity.this);
//					MyTools.setText(mText_Phone,
//							AllStaticMessage.mJsonObject_select_address
//									.getString("PhoneTel"),
//							JieSuanActivity.this);
//					MyTools.setText(
//							mText_Address,
//							AllStaticMessage.mJsonObject_select_address
//									.getString("Province")
//									+ AllStaticMessage.mJsonObject_select_address
//											.getString("City")
//									+ AllStaticMessage.mJsonObject_select_address
//											.getString("Country")
//									+ AllStaticMessage.mJsonObject_select_address
//											.getString("DetailAddress"),
//							JieSuanActivity.this);
//					MyTools.setText(mText_Time,
//							AllStaticMessage.mJsonObject_select_address
//									.getString("GoodsTime"),
//							JieSuanActivity.this);
//					addressId = AllStaticMessage.mJsonObject_select_address
//							.getString("Id");
//					getYunFei(addressId);
//					twoFlag = true;
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			} else {
//				addressId = "";
//				mLayout_1.setVisibility(View.GONE);
//				mLayout_2.setVisibility(View.VISIBLE);
//			}
		}
	}

	// 订单提交
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
						// 成功返回JSONObject
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

								if (payWay.equals("支付宝支付")) {
									// tijiao(response.getJSONArray("Results")
									// .getJSONObject(0)
									// .getString("OrderId"),
									// response.getJSONArray("Results")
									// .getJSONObject(0)
									// .getString("Amount"));
									zfb_order = response
											.getJSONArray("Results")
											.getJSONObject(0)
											.getString("OrderId").toString();
									String zfb_allprice = response
											.getJSONArray("Results")
											.getJSONObject(0)
											.getString("Amount").toString();
									AllStaticMessage.zfb_Order = zfb_order;
									tijiao(zfb_order, zfb_allprice);

								} else {
									boolean isPaySupported = api
											.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
									if (isPaySupported) {
										// 微信支付 进行操作
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
												AllStaticMessage.WxOrder);// 调用微信支付
									} else {
										Toast.makeText(JieSuanActivity.this,
												"对不起,您的微信版本过低,不支持微信付款",
												Toast.LENGTH_SHORT).show();
									}
								}
							} else {
								Toast.makeText(
										JieSuanActivity.this,
										response.getString("Results")
												.toString(), 500).show();
								if (payWay.equals("支付宝支付")) {
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
						if (dialog != null) {
							dialog.stop();
						}
					}
				});
	}

	// 获取运费
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
						// 成功返回JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
								mTextView_GoodsPrice.setText("￥"
										+ response.getString("GoodsMoney")
												.toString());
								mTextView_YunFei.setText("￥"
										+ response.getString("FreightMoney")
												.toString());
								mTextView_AllPrice.setText("￥"
										+ response.getString("TotalMoney")
												.toString());
								allvalue = response.getString("TotalMoney")
										.toString();
								if (twoFlag) {
									mText_guanfang.setText("未使用");
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
						if (dialog != null) {
							dialog.stop();
						}
					}
				});
	}

	/**
	 * 支付宝
	 **/
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				Result resultObj = new Result((String) msg.obj);
				String resultStatus = resultObj.resultStatus;

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Intent mIntent = new Intent(JieSuanActivity.this,
							CheckOrderActivity.class);
					mIntent.putExtra("orderNum", AllStaticMessage.zfb_Order);
					startActivity(mIntent);
					Toast.makeText(JieSuanActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”
					// 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(JieSuanActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();

					} else {
						Toast.makeText(JieSuanActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(JieSuanActivity.this, "检查结果为：" + msg.obj,
						Toast.LENGTH_SHORT).show();
				break;
			}
			default:
				break;
			}
		};
	};

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void tijiao(final String orderNum, String allPrice) {
		String orderInfo = getOrderInfo(orderNum, allPrice);
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(JieSuanActivity.this);
				// 调用支付接口
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
	 * 查询终端设备是否存在支付宝认证账户
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
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	private String getOrderInfo(String orderNum, String allPrice) {

		// 合作者身份ID
		String orderInfo = "partner=" + "\"" + Keys.DEFAULT_PARTNER + "\"";

		// 卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + Keys.DEFAULT_SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + orderNum + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + "订单号:" + orderNum + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + "居美喵" + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + allPrice + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + AllStaticMessage.URL_notify + "\"";

		// 接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"20m\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		// orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值
		// orderInfo += "&paymethod=\"expressGateway\"";
		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 获取外部订单号
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
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, Keys.PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

	/**
	 * 微信操作
	 */
	@SuppressWarnings("unused")
	private void weixinPay(String order_price, String order_no) {
		dialog.loading();
		String product_name = "居美喵居家特卖商品";
		// try{
		// //测试服务器采用GBK编码，否则不需要转码
		// product_name = URLEncoder.encode("测试微信支付(app服务器端签名)","GBK");
		// }catch(Exception e){
		// Log.d("Parse errer","编码转换失败");
		// Toast.makeText(MyPayActivity.this, "编码转换失败",
		// Toast.LENGTH_SHORT).show();
		// }
		// 请求的url
		String token_url = AllStaticMessage.URL_Wx_pay + product_name
				+ "&out_trade_no=" + order_no + "&total_fee=" + order_price;// AllStaticMessage.URL_Wx_qian+order_price+"&order_no="+order_no+"&product_name="+product_name;

		// Toast.makeText(JieSuanActivity.this, "获取订单中...", 200).show();
		HttpUtil.get(token_url, JieSuanActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
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
							// Toast.makeText(JieSuanActivity.this, "正常调起支付",
							// 200).show();
							// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
							api.registerApp(AllStaticMessage.APP_ID);
							api.sendReq(req);
						} catch (JSONException e) {
							e.printStackTrace();
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
		Toast.makeText(JieSuanActivity.this, "异常：" + arg0.openId, 200000)
				.show();
	}

	@Override
	public void onResp(BaseResp arg0) {
		Toast.makeText(JieSuanActivity.this,
				"异常：" + arg0.errCode + arg0.errStr, 200000).show();
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
