package com.jifeng.mlsales.jumeimiao;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import cn.sharesdk.framework.ShareSDK;

import com.alipay.sdk.app.PayTask;
import com.jifeng.mlsales.R;
import com.jifeng.mlsales.wxapi.MD5;
import com.jifeng.mlsales.wxapi.Util;
import com.jifeng.mlsales.wxapi.WXPayEntryActivity;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.tools.TasckActivity;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.jifeng.zfb.Keys;
import com.jifeng.zfb.Result;
import com.jifeng.zfb.SignUtils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyPayActivity extends Activity {
	private LoadingDialog dialog;
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

	private ImageView mImage_zhifubao, mImage_weixin;

	private TextView mTextView;
	private String AllPrice, payway, orderId;
	private RelativeLayout mLayout_Faile;// , mLayout_Success
	private LinearLayout mLayout_second;
	private Button mBtn_zhifu;

	private TasckActivity tasckActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mypay);
		dialog = new LoadingDialog(this);
		findView();
		aboutWX();
		AllPrice = getIntent().getStringExtra("allprice");
		payway = getIntent().getStringExtra("payway");
		orderId = getIntent().getStringExtra("orderid");
		mTextView.setText("支付金额:￥" + AllPrice);
		if (payway.equals("zfb")) {
			mImage_zhifubao.setImageDrawable(getResources().getDrawable(
					R.drawable.register_select_2));
			mImage_weixin.setImageDrawable(getResources().getDrawable(
					R.drawable.register_select_1));
		} else {
			mImage_zhifubao.setImageDrawable(getResources().getDrawable(
					R.drawable.register_select_1));
			mImage_weixin.setImageDrawable(getResources().getDrawable(
					R.drawable.register_select_2));
		}
		tasckActivity = new TasckActivity();
		// tasckActivity.pushActivity(MyPayActivity.this);
		if (AllStaticMessage.MyPayBack) {
			AllStaticMessage.MyPayBack = false;
			tasckActivity.popAllActivityExceptOne(TabHostActivity.class);
		}
		if (!AllStaticMessage.OrderId.equals("")) {
			if (AllStaticMessage.OrderStatus.equals("0")) {
				AllStaticMessage.OrderId = "";
				AllStaticMessage.OrderPrice = "";
				AllStaticMessage.OrderStatus = "";
				mLayout_Faile.setVisibility(View.VISIBLE);
			} else {
				AllStaticMessage.OrderId = "";
				AllStaticMessage.OrderPrice = "";
				AllStaticMessage.OrderStatus = "";
				if (AllStaticMessage.MyPayBack) {
					AllStaticMessage.MyPayBack = false;
					tasckActivity
							.popAllActivityExceptOne(TabHostActivity.class);
				}
				finish();
			}
		}

	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		setContentView(R.layout.view_null);
		super.onDestroy();
		dialog = null;

		mImage_zhifubao = null;
		mImage_weixin = null;

		mTextView = null;
		AllPrice = null;
		payway = null;
		orderId = null;
		mLayout_Faile = null;
		// mLayout_Success = null;
		mLayout_second = null;
		mBtn_zhifu = null;

		this.finish();
		System.gc();
	}

	// 查找控件
	private void findView() {
		mImage_zhifubao = (ImageView) findViewById(R.id.mypay_zhifubao_select);
		mImage_weixin = (ImageView) findViewById(R.id.mypay_weixin_select);
		mTextView = (TextView) findViewById(R.id.price);
		mLayout_Faile = (RelativeLayout) findViewById(R.id.mypay_faile);
		mLayout_second = (LinearLayout) findViewById(R.id.mypay_second);
		mBtn_zhifu = (Button) findViewById(R.id.mypay_btn_ok);
		// mLayout_Success = (RelativeLayout) findViewById(R.id.mypay_success);
	}

	private void aboutWX() {
		api = WXAPIFactory.createWXAPI(MyPayActivity.this,
				AllStaticMessage.APP_ID); // App_Id

		// sb=new StringBuffer();
	}

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.mypay_back:// 返回
			if (AllStaticMessage.MyPayBack) {
				AllStaticMessage.MyPayBack = false;
				tasckActivity.popAllActivityExceptOne(TabHostActivity.class);
			}

			finish();
			break;
		case R.id.mypay_zhifubao_select:
			payway = "zfb";
			mImage_zhifubao.setImageDrawable(getResources().getDrawable(
					R.drawable.register_select_2));
			mImage_weixin.setImageDrawable(getResources().getDrawable(
					R.drawable.register_select_1));
			break;
		case R.id.mypay_weixin_select:
			payway = "wx";
			mImage_zhifubao.setImageDrawable(getResources().getDrawable(
					R.drawable.register_select_1));
			mImage_weixin.setImageDrawable(getResources().getDrawable(
					R.drawable.register_select_2));
			break;
		case R.id.mypay_btn_ok:
			if (payway.equals("zfb")) {
				AllStaticMessage.orderDetailFlag = true;
				AllStaticMessage.OrderFormFlag = true;
				tijiao(orderId, AllPrice);// 调用支付宝支付
			} else if (payway.equals("wx")) {
				AllStaticMessage.orderDetailFlag = true;
				AllStaticMessage.OrderFormFlag = true;
				boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
				if (isPaySupported) {
					// 进行操作
					weixinPay(AllPrice, orderId);// 调用微信支付
				} else {
					Toast.makeText(MyPayActivity.this, "对不起,您的微信版本过低,不支持微信付款",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(MyPayActivity.this, "请选择支付方式",
						Toast.LENGTH_SHORT).show();
			}

			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (AllStaticMessage.MyPayBack) {
				AllStaticMessage.MyPayBack = false;
				tasckActivity.popAllActivityExceptOne(TabHostActivity.class);
			}
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// ////////////////////////////////////////////////
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
					Toast.makeText(MyPayActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”
					// 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(MyPayActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(MyPayActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();
					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(MyPayActivity.this, "检查结果为：" + msg.obj,
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
				PayTask alipay = new PayTask(MyPayActivity.this);
				// 调用支付接口
				String result = alipay.pay(payInfo);

				Intent intent = new Intent(MyPayActivity.this,
						CheckOrderActivity.class);
				intent.putExtra("orderNum", orderNum);
				// Log.i("111111", orderNum);
				// startActivityForResult(intent, 0x01);
				startActivity(intent);
				MyPayActivity.this.finish();
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
				PayTask payTask = new PayTask(MyPayActivity.this);
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
		orderInfo += "&it_b_pay=\"5m\"";

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
				+ "&out_trade_no=" + order_no + "&total_fee=" + order_price;
		// Log.i("11111", token_url);
		// http://www.mdeqi.com/WXPay/pay.aspx?body=居美喵微信支付&out_trade_no=20150313180265511&total_fee=203
		// Toast.makeText(MyPayActivity.this, "获取订单中...", 200).show();

		HttpUtil.get(token_url, MyPayActivity.this, dialog,
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
							MyPayActivity.this.finish();
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
					}
				});
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
