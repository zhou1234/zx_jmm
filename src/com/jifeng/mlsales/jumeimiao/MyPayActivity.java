package com.jifeng.mlsales.jumeimiao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.alipay.sdk.app.PayTask;
import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		findView();
		aboutWX();
		AllPrice = getIntent().getStringExtra("allprice");
		payway = getIntent().getStringExtra("payway");
		orderId = getIntent().getStringExtra("orderid");
		mTextView.setText("֧�����:��" + AllPrice);
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
		super.onDestroy();
//		dialog = null;
//		setContentView(R.layout.view_null);
//		mImage_zhifubao = null;
//		mImage_weixin = null;
//
//		mTextView = null;
//		AllPrice = null;
//		payway = null;
//		orderId = null;
//		mLayout_Faile = null;
//		// mLayout_Success = null;
//		mLayout_second = null;
//		mBtn_zhifu = null;
//
//		this.finish();
//		System.gc();
	}

	// ���ҿؼ�
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

	// //xmlע�����¼���ʵ��
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.mypay_back:// ����
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
				tijiao(orderId, AllPrice);// ����֧����֧��
			} else if (payway.equals("wx")) {
				AllStaticMessage.orderDetailFlag = true;
				AllStaticMessage.OrderFormFlag = true;
				boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
				if (isPaySupported) {
					// ���в���
					weixinPay(AllPrice, orderId);// ����΢��֧��
				} else {
					Toast.makeText(MyPayActivity.this, "�Բ���,����΢�Ű汾����,��֧��΢�Ÿ���",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(MyPayActivity.this, "��ѡ��֧����ʽ",
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
	 * ֧����
	 **/
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				Result resultObj = new Result((String) msg.obj);
				String resultStatus = resultObj.resultStatus;
				MyPayActivity.this.finish();
				// �ж�resultStatus Ϊ��9000�������֧���ɹ�������״̬�������ɲο��ӿ��ĵ�
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(MyPayActivity.this, "֧���ɹ�",
							Toast.LENGTH_SHORT).show();
				} else {
					// �ж�resultStatus Ϊ�ǡ�9000����������֧��ʧ��
					// ��8000��
					// ����֧�������Ϊ֧������ԭ�����ϵͳԭ���ڵȴ�֧�����ȷ�ϣ����ս����Ƿ�ɹ��Է�����첽֪ͨΪ׼��С����״̬��
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(MyPayActivity.this, "֧�����ȷ����",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(MyPayActivity.this, "֧��ʧ��",
								Toast.LENGTH_SHORT).show();
					}

				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(MyPayActivity.this, "�����Ϊ��" + msg.obj,
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
	private void tijiao(final String orderNum, String allPrice) {
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
				PayTask alipay = new PayTask(MyPayActivity.this);
				// ����֧���ӿ�
				String result = alipay.pay(payInfo);

				// Intent intent = new Intent(MyPayActivity.this,
				// CheckOrderActivity.class);
				// intent.putExtra("orderNum", orderNum);
				// // Log.i("111111", orderNum);
				// // startActivityForResult(intent, 0x01);
				// startActivity(intent);
				// MyPayActivity.this.finish();
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	private String getOrderInfo(String orderNum, String allPrice) {

		// ���������ID
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
		orderInfo += "&it_b_pay=\"5m\"";

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
	private String sign(String content) {
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
				+ "&out_trade_no=" + order_no + "&total_fee=" + order_price;
		// Log.i("11111", token_url);
		// http://www.mdeqi.com/WXPay/pay.aspx?body=������΢��֧��&out_trade_no=20150313180265511&total_fee=203
		// Toast.makeText(MyPayActivity.this, "��ȡ������...", 200).show();

		HttpUtil.get(token_url, MyPayActivity.this, dialog,
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
							MyPayActivity.this.finish();
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
