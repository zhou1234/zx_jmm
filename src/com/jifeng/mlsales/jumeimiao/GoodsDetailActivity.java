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
	private String activityId, id, spid;// 活动id 商品id 规格id
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
					activityId = strings[2];// 活动id
					id = strings[0];// 商品id
					spid = strings[1];// 规格
				}
			} else {
				activityId = intent.getStringExtra("pid").toString();// 活动id
				id = intent.getStringExtra("goodsid").toString();// 商品id
				spid = intent.getStringExtra("guigeid").toString();// 规格
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

	// 查找控件
	private void findView() {
		mWebView = (WebView) findViewById(R.id.goodslist_webview);
		mText_title = (AlwaysMarqueeTextView) findViewById(R.id.textview_title);
		// mImage_Car = (ImageView) findViewById(R.id.shopping_img_car);
		btn_input_car = (Button) findViewById(R.id.btn_inputcar_num);
		// iv_shopping = (ImageView) findViewById(R.id.iv_shopping);
		// rl_shoppingcar = (RelativeLayout) findViewById(R.id.rl_shoppingcar);

	}

	// 注册事件
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
				// 忽略SSL证书错误，继续加载页面。
				handler.proceed();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d("loadurl", "loadUrl ,url = " + url);
				if (url.contains("zoomPhoto")) { // 大图展示
					url = url.replace(AllStaticMessage.URL_GBase
							+ "/wap/zoomPhoto,", "");
					mIntent = new Intent(GoodsDetailActivity.this,
							PicShowActivity.class);
					mIntent.putExtra("title", mText_title.getText().toString());
					mIntent.putExtra("url", url);
					startActivity(mIntent);
				} else if (url.contains("pageover")) { // 取消loading
					dialog.stop();
				} else if (url.contains("addlove")) { // 添加收藏
					if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
						mIntent = new Intent(GoodsDetailActivity.this,
								LoginActivity.class);
						startActivity(mIntent);
					} else {
						AddSaveOrGetQuan(id, "收藏", "");
					}
				} else if (url.contains("getCoupon")) { // 获取优惠券
					// if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
					// mIntent = new
					// Intent(GoodsDetailActivity.this,LoginActivity.class);
					// startActivity(mIntent);
					// } else {
					// String cid = url.replace(AllStaticMessage.URL_GBase+
					// "/wap/getCoupon", "");
					// AddSaveOrGetQuan(activityId, "领取优惠券", cid);
					// }
				} else if (url.contains("?pid")) {// 选择规格，刷新页面
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
		// 设置应用程序的标题title
		public void onReceivedTitle(WebView view, String title) {
			mText_title.setText(title);
			shareTitle = title;
			super.onReceivedTitle(view, title);
		}

	}

	// 获取购物车商品数量
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
						// 成功返回JSONObject
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

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.goodslist_back:// 返回
			finish();
			break;
		case R.id.goodslist_share:// 分享
			share(shareTitle, "这个不错哦!快来看看吧", shareUrl, shareImg);
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
		// spid = null;// 活动id 商品id 规格id
		// // this.finish();
		// System.gc();
	}

	private void share(String title, String content, String url, String imgurl) {
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.icon, getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(title);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(url);// "http://www.gfeng.com.cn"
		// text是分享文本，所有平台都需要这个字段
		oks.setText(content + url);

		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// File file = new File(
		// android.os.Environment.getExternalStorageDirectory()
		// + "/JuMeiMiao/pic/Myshare.jpg");
		// if (file.exists()) {
		// oks.setImagePath(android.os.Environment
		// .getExternalStorageDirectory()
		// + "/JuMeiMiao/pic/Myshare.jpg");// 确保SDcard下面存在此张图片
		// }
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(url);
		oks.setImageUrl(imgurl);
		// oks.setFilePath(android.os.Environment.getExternalStorageDirectory()
		// + "/JuMeiMiao/pic/Myshare.jpg");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		// oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(url);// "http://www.gfeng.com.cn"

		// 启动分享GUI
		oks.show(GoodsDetailActivity.this);
		// Toast.makeText(getApplicationContext(), "打开url:" + url,
		// Toast.LENGTH_SHORT).show();
	}

	// 添加收藏
	private void AddSaveOrGetQuan(String id, String type, String quanId) {
		dialog.loading();
		String url = "";
		if (type.equals("收藏")) {
			// http://www.jumeimiao.com/api/o.ashx?m=favrite&UserId=41&goodsId=&type=1
			url = url + AllStaticMessage.URL_AddTo_Save
					+ AllStaticMessage.User_Id + "&goodsId=" + id + "&brandId="
					+ "&type=1";
		} else if (type.equals("领取优惠券")) {
			url = url + AllStaticMessage.URL_GetQuan + id + "&userId="
					+ AllStaticMessage.User_Id + "&cid=" + quanId;
		}

		HttpUtil.get(url, GoodsDetailActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
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
						dialog.stop();
					}
				});
	}

	// 添加购物车
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
						// 成功返回JSONObject
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
						// 请求结束
						if (dialog != null) {
							dialog.stop();
						}
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
						Toast.makeText(GoodsDetailActivity.this,
								"添加购物车异常,请稍后重试", 500).show();
					}
				});
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
