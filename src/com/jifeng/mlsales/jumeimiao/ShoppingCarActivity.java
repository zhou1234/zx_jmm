package com.jifeng.mlsales.jumeimiao;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.tools.TasckActivity;
import com.jifeng.url.AllStaticMessage;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ShoppingCarActivity extends Activity {
	private WebView mWebView;
	private LoadingDialog dialog;
	private Intent mIntent;
	private RelativeLayout mLayout;
	private ImageView mImageView_back;
	private TasckActivity tasckActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shoppingcar);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		findView();
		register();
		if (MyTools.checkNetWorkStatus(this)) {
			if (AllStaticMessage.Login_Flag.equals("")) {
				mIntent = new Intent(ShoppingCarActivity.this,
						LoginActivity.class);
				startActivityForResult(mIntent, 1111);
			} else {
				mWebView.loadUrl(AllStaticMessage.URL_ShoppingCar + "&UserId="
						+ AllStaticMessage.User_Id + "&udid="
						+ MyTools.getAndroidID(ShoppingCarActivity.this));
			}
		} else {
			mWebView.loadUrl("file:///android_asset/index.html");
		}
	}

	// 查找控件
	private void findView() {
		mWebView = (WebView) findViewById(R.id.shoppingcar_webview);
		mLayout = (RelativeLayout) findViewById(R.id.shoppingcar_no);
		mImageView_back = (ImageView) findViewById(R.id.shoppingcar_back);
		if (getIntent().getStringExtra("showflag").equals("yes")) {
			mImageView_back.setVisibility(View.VISIBLE);
			tasckActivity = new TasckActivity();
			tasckActivity.pushActivity(ShoppingCarActivity.this);
		}
	}

	// 注册事件
	private void register() {
		MyTools.webviewSetting(mWebView);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				dialog.stop();
				super.onPageFinished(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				dialog.loading();
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// 忽略SSL证书错误，继续加载页面。
				handler.proceed();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d("loadurl", "loadUrl ,url = " + url);
				if (url.contains("addorder")) {
					if (AllStaticMessage.Login_Flag.equals("")) {
						mIntent = new Intent(ShoppingCarActivity.this,
								LoginActivity.class);
						startActivityForResult(mIntent, 0x01);
					} else {
						mIntent = new Intent(ShoppingCarActivity.this,
								JieSuanActivity.class);
						if (getIntent().getStringExtra("showflag")
								.equals("yes")) {
							mIntent.putExtra("flag", "yes");
						} else {
							mIntent.putExtra("flag", "no");
						}
						startActivity(mIntent);
					}
				} else if (url.contains("pid=")) {
					url = url.replace("pid=", "#");
					url = url.replace("&id=", "*");
					String goodsID = url.substring(url.lastIndexOf("#") + 1,
							url.lastIndexOf("*"));
					String guigeid = url.substring(url.lastIndexOf("*") + 1,
							url.length());
					mIntent = new Intent(ShoppingCarActivity.this,
							GoodsDetailActivity.class);
					mIntent.putExtra("active", "0");
					mIntent.putExtra("pid", "");
					mIntent.putExtra("goodsid", goodsID);
					mIntent.putExtra("guigeid", guigeid);
					mIntent.putExtra("imgurl", "");
					startActivity(mIntent);
				} else if (url.contains("hide")) {// 购物车清空
					dialog.loading();
					AllStaticMessage.Car_num = 0;
					AllStaticMessage.ShoppingCar = false;
					if (getIntent().getStringExtra("showflag").equals("no")) {
						AllStaticMessage.Back_to_ShoppingCar = true;
					}
					mLayout.setVisibility(View.VISIBLE);
					dialog.stop();
				} else {
					view.loadUrl(url);
				}
				return true;
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0x01:
				// mWebView.loadUrl(AllStaticMessage.URL_ShoppingCar+"&UserId="+AllStaticMessage.User_Id+"&udid="+
				// MyTools.getAndroidID(ShoppingCarActivity.this));
				mIntent = new Intent(ShoppingCarActivity.this,
						JieSuanActivity.class);
				if (getIntent().getStringExtra("showflag").equals("yes")) {
					mIntent.putExtra("flag", "yes");
				} else {
					mIntent.putExtra("flag", "no");
				}
				startActivity(mIntent);
				break;
			case 1111:
				mWebView.loadUrl(AllStaticMessage.URL_ShoppingCar + "&UserId="
						+ AllStaticMessage.User_Id + "&udid="
						+ MyTools.getAndroidID(ShoppingCarActivity.this));
				break;
			default:
				break;
			}
		}
	}

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.shoppingcar_gotobuy:
			if (getIntent().getStringExtra("showflag").equals("yes")) {
				AllStaticMessage.Back_to_XianShiTm = true;
				tasckActivity.popAllActivityExceptOne(TabHostActivity.class);
			} else {
				dialog.loading();
				AllStaticMessage.Back_to_XianShiTm = true;
				dialog.stop();
			}
			break;
		case R.id.shoppingcar_back:
			finish();
			break;
		default:
			break;
		}
	}

	/*
	 * 双击退出
	 */
	private long mExitTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 如果不是来自产品详细，就退出
			if (getIntent().getStringExtra("showflag").equals("yes")) {
				this.finish();
			} else {
				if ((System.currentTimeMillis() - mExitTime) > 2000) {
					Toast.makeText(this, "再按一次退出居美喵", Toast.LENGTH_SHORT)
							.show();
					mExitTime = System.currentTimeMillis();
				} else {
					this.getApplication().onTerminate();
				}
			}

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
		// mWebView.removeAllViews();
		// mWebView.destroy();
		// mWebView = null;
		// setContentView(R.layout.view_null);
		//
		// dialog = null;
		// mLayout = null;
		// mIntent = null;
		// System.gc();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
