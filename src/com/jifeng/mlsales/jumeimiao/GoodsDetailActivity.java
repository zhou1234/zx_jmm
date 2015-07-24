package com.jifeng.mlsales.jumeimiao;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.onekeyshare.OnekeyShare;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.AlwaysMarqueeTextView;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.DownPic;
import com.jifeng.tools.MyTools;
import com.jifeng.tools.TasckActivity;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class GoodsDetailActivity extends Activity {
	private Intent mIntent;
	private WebView mWebView;
	private AlwaysMarqueeTextView mText_title;
	private LoadingDialog dialog;
	private TasckActivity tasckActivity;
	// private ImageView mImage_Car;
	// private ImageView iv_shopping;
	// private RelativeLayout rl_shoppingcar;
	// private Animation translateAnimation;
	// private Animation scaleAnimation;
	private Button btn_input_car;
	private String shareTitle, shareContent, shareUrl, detailUrl, shareImg;
	private String activityId, id, spid;// �id ��Ʒid ���id
	private boolean diaFlag = false;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0x01:
				addShoppingCar();
				break;
			case 0x02:
				try {
					DownPic.downloadLy1(shareImg);
				} catch (Exception e) {
					e.printStackTrace();
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
		setContentView(R.layout.activity_goods_detail);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		findView();
		register();
		Intent intent = getIntent();
		if (intent != null) {
			String active = intent.getStringExtra("active").toString();
			if (active.equals("1")) {
				String detail = intent.getStringExtra("detailUrl").toString();
				if (!detail.equals("")) {
					String[] strings = detail.split(",");
					activityId = strings[2];// �id
					id = strings[0];// ��Ʒid
					spid = strings[1];// ���
				}
			} else {
				activityId = intent.getStringExtra("pid").toString();// �id
				id = intent.getStringExtra("goodsid").toString();// ��Ʒid
				spid = intent.getStringExtra("guigeid").toString();// ���
			}
			shareUrl = AllStaticMessage.URL_Goods_detail_share + activityId
					+ "&pid=" + id + "&id=" + spid;// +"&spid="+spid
			detailUrl = AllStaticMessage.URL_Goods_detail + activityId
					+ "&pid=" + id + "&id=" + spid + "&userId="
					+ AllStaticMessage.User_Id;

			shareImg = intent.getStringExtra("imgurl").toString();
		}
		tasckActivity = new TasckActivity();
		tasckActivity.pushActivity(GoodsDetailActivity.this);
	}

	// ���ҿؼ�
	private void findView() {
		mWebView = (WebView) findViewById(R.id.goodslist_webview);
		mText_title = (AlwaysMarqueeTextView) findViewById(R.id.textview_title);
		// mImage_Car = (ImageView) findViewById(R.id.shopping_img_car);
		btn_input_car = (Button) findViewById(R.id.btn_inputcar_num);
		// iv_shopping = (ImageView) findViewById(R.id.iv_shopping);
		// rl_shoppingcar = (RelativeLayout) findViewById(R.id.rl_shoppingcar);

	}

	// ע���¼�
	private void register() {
		mWebView.setWebChromeClient(new MyWebChromeClient());
		MyTools.webviewSetting(mWebView);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (diaFlag) {
					diaFlag = false;
					dialog.stop();
				}
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				if (!GoodsDetailActivity.this.isFinishing()) {
					dialog.loading();
				}
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				if (dialog != null) {
					dialog.stop();
				}
				// ����SSL֤����󣬼�������ҳ�档
				handler.proceed();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d("loadurl", "loadUrl ,url = " + url);
				if (url.contains("zoomPhoto")) { // ��ͼչʾ
					url = url.replace(AllStaticMessage.URL_GBase
							+ "/wap/zoomPhoto,", "");
					mIntent = new Intent(GoodsDetailActivity.this,
							PicShowActivity.class);
					mIntent.putExtra("title", mText_title.getText().toString());
					mIntent.putExtra("url", url);
					startActivity(mIntent);
				} else if (url.contains("pageover")) { // ȡ��loading
					dialog.stop();
				} else if (url.contains("addlove")) { // ����ղ�
					if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
						mIntent = new Intent(GoodsDetailActivity.this,
								LoginActivity.class);
						startActivity(mIntent);
					} else {
						AddSaveOrGetQuan(id, "�ղ�", "");
					}
				} else if (url.contains("getCoupon")) { // ��ȡ�Ż�ȯ
					// if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
					// mIntent = new
					// Intent(GoodsDetailActivity.this,LoginActivity.class);
					// startActivity(mIntent);
					// } else {
					// String cid = url.replace(AllStaticMessage.URL_GBase+
					// "/wap/getCoupon", "");
					// AddSaveOrGetQuan(activityId, "��ȡ�Ż�ȯ", cid);
					// }
				} else if (url.contains("?pid")) {// ѡ����ˢ��ҳ��
					spid = url.substring(url.lastIndexOf("=") + 1, url.length());
					view.loadUrl(url);
				} else if (url.contains("UsersData")) {
					url = url + ",0";
					mIntent = new Intent(GoodsDetailActivity.this,
							PicShowActivity.class);
					mIntent.putExtra("title", mText_title.getText().toString());
					mIntent.putExtra("url", url);
					startActivity(mIntent);
					// finish();
				} else if (url.contains("shop?")) {
					// http://www.jumeimiao.com/wap/shop?id=4
					url = url.replace(AllStaticMessage.URL_GBase
							+ "/wap/shop?id=", "");
					mIntent = new Intent(GoodsDetailActivity.this,
							PinPaiZhuangChangActivity.class);
					mIntent.putExtra("id", url);
					mIntent.putExtra("time", "text");
					mIntent.putExtra("text", "text");
					startActivity(mIntent);

				} else {
					view.loadUrl(url);
				}
				return true;
			}
		});
	}

	class MyWebChromeClient extends WebChromeClient {

		@Override
		public void onCloseWindow(WebView window) {
			super.onCloseWindow(window);
		}

		@Override
		// ����Ӧ�ó���ı���title
		public void onReceivedTitle(WebView view, String title) {
			mText_title.setText(title);
			shareTitle = title;
			super.onReceivedTitle(view, title);
		}

	}

	// ��ȡ���ﳵ��Ʒ����
	private void getCarNum() {
		String url = AllStaticMessage.URL_GetShoppingCarNum
				+ AllStaticMessage.User_Id + "&udid="
				+ MyTools.getAndroidID(GoodsDetailActivity.this);
		HttpUtil.get(url, GoodsDetailActivity.this, dialog,
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
								if (AllStaticMessage.Car_num > 0) {
									btn_input_car.setVisibility(View.VISIBLE);
									btn_input_car.setText(String
											.valueOf(AllStaticMessage.Car_num));
									AllStaticMessage.ShoppingCar = true;
								} else {
									btn_input_car.setVisibility(View.INVISIBLE);
									AllStaticMessage.ShoppingCar = false;
								}
							} else {
								btn_input_car.setVisibility(View.INVISIBLE);
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

	@Override
	protected void onResume() {
		super.onResume();
		if (MyTools.checkNetWorkStatus(this)) {
			if (shareImg != null || !shareImg.equals("")) {
				handler.sendEmptyMessage(0x02);
			}
			mWebView.loadUrl(detailUrl);
			getCarNum();
		} else {
			diaFlag = true;
			mWebView.loadUrl("file:///android_asset/index.html");
		}
		MobclickAgent.onResume(this);
	}

	// //xmlע�����¼���ʵ��
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.goodslist_back:// ����
			finish();
			break;
		case R.id.goodslist_share:// ����
			share(shareTitle, "�������Ŷ!����������", shareUrl, shareImg);
			break;
		case R.id.input_shoppingcar:
		case R.id.btn_inputcar_num:
		case R.id.rl_shoppingcar:
			if (AllStaticMessage.Car_num > 0) {
				mIntent = new Intent(GoodsDetailActivity.this,
						ShoppingCarActivity.class);
				mIntent.putExtra("showflag", "yes");
				startActivity(mIntent);

				// AllStaticMessage.Back_to_ShoppingCar = true;
				// tasckActivity.popAllActivityExceptOne(TabHostActivity.class);
			}
			break;
		case R.id.goodsdetail_btn_inputshopping:
			if (AllStaticMessage.Login_Flag.equals("")) {
				mIntent = new Intent(GoodsDetailActivity.this,
						LoginActivity.class);
				startActivityForResult(mIntent, 9999);
			} else {
				dialog.loading();
				handler.sendEmptyMessage(0x01);
			}
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
			case 9999:
				dialog.loading();
				handler.sendEmptyMessage(0x01);
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// setContentView(R.layout.view_null);
		// handler = null;
		// tasckActivity.popActivity(GoodsDetailActivity.this);
		// tasckActivity = null;
		// mIntent = null;
		// mWebView.removeAllViews();
		// mWebView.destroy();
		// mWebView = null;
		// mText_title = null;
		// dialog = null;
		// mImage_Car = null;
		// // translateAnimation = null;
		// // scaleAnimation = null;
		// btn_input_car = null;
		// shareTitle = null;
		// shareContent = null;
		// shareUrl = null;
		// shareImg = null;
		// activityId = null;
		// id = null;
		// spid = null;// �id ��Ʒid ���id
		// // this.finish();
		// System.gc();
	}

	private void share(String title, String content, String url, String imgurl) {
		OnekeyShare oks = new OnekeyShare();
		// �ر�sso��Ȩ
		oks.disableSSOWhenAuthorize();
		// ����ʱNotification��ͼ�������
		oks.setNotification(R.drawable.icon, getString(R.string.app_name));
		// title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
		oks.setTitle(title);
		// titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
		oks.setTitleUrl(url);// "http://www.gfeng.com.cn"
		// text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
		oks.setText(content + url);

		// imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
		// File file = new File(
		// android.os.Environment.getExternalStorageDirectory()
		// + "/JuMeiMiao/pic/Myshare.jpg");
		// if (file.exists()) {
		// oks.setImagePath(android.os.Environment
		// .getExternalStorageDirectory()
		// + "/JuMeiMiao/pic/Myshare.jpg");// ȷ��SDcard������ڴ���ͼƬ
		// }
		// url����΢�ţ��������Ѻ�����Ȧ����ʹ��
		oks.setUrl(url);
		oks.setImageUrl(imgurl);
		// oks.setFilePath(android.os.Environment.getExternalStorageDirectory()
		// + "/JuMeiMiao/pic/Myshare.jpg");
		// comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
		// oks.setComment("���ǲ��������ı�");
		// site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
		oks.setSite(getString(R.string.app_name));
		// siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
		oks.setSiteUrl(url);// "http://www.gfeng.com.cn"

		// ��������GUI
		oks.show(GoodsDetailActivity.this);
		// Toast.makeText(getApplicationContext(), "��url:" + url,
		// Toast.LENGTH_SHORT).show();
	}

	// ����ղ�
	private void AddSaveOrGetQuan(String id, String type, String quanId) {
		dialog.loading();
		String url = "";
		if (type.equals("�ղ�")) {
			// http://www.jumeimiao.com/api/o.ashx?m=favrite&UserId=41&goodsId=&type=1
			url = url + AllStaticMessage.URL_AddTo_Save
					+ AllStaticMessage.User_Id + "&goodsId=" + id + "&brandId="
					+ "&type=1";
		} else if (type.equals("��ȡ�Ż�ȯ")) {
			url = url + AllStaticMessage.URL_GetQuan + id + "&userId="
					+ AllStaticMessage.User_Id + "&cid=" + quanId;
		}

		HttpUtil.get(url, GoodsDetailActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").equals("true")) {

								Toast.makeText(
										GoodsDetailActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							} else {
								Toast.makeText(
										GoodsDetailActivity.this,
										response.getString("Results")
												.toString(), 500).show();
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

	// ��ӹ��ﳵ
	private void addShoppingCar() {
		String url = AllStaticMessage.URL_Add_ShoppingCar + id + "&spid="
				+ spid + "&udid="
				+ MyTools.getAndroidID(GoodsDetailActivity.this) + "&activeId="
				+ activityId + "&UserId=" + AllStaticMessage.User_Id;//
		HttpUtil.get(url, GoodsDetailActivity.this, dialog,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").equals("true")) {

								AllStaticMessage.Car_num++;
								btn_input_car.setVisibility(View.VISIBLE);
								btn_input_car.setText(String
										.valueOf(AllStaticMessage.Car_num));
								AllStaticMessage.ShoppingCar = true;
								// Toast.makeText(GoodsDetailActivity.this,
								// response.getString("Results").toString(),
								// 500)
								// .show();
							} else {
								Toast.makeText(
										GoodsDetailActivity.this,
										response.getString("Results")
												.toString(), 500).show();

							}
						} catch (JSONException e) {
							e.printStackTrace();

						}

					}

					@SuppressLint("ShowToast")
					@Override
					public void onStart() {
						super.onStart();
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
						Toast.makeText(GoodsDetailActivity.this,
								"��ӹ��ﳵ�쳣,���Ժ�����", 500).show();
					}
				});
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
