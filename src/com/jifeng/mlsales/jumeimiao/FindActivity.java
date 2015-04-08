package com.jifeng.mlsales.jumeimiao;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.umeng.analytics.MobclickAgent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.ECLAIR_MR1)
public class FindActivity extends Activity {
	private LoadingDialog dialog;
	private Intent mIntent;
	WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find);
		dialog = new LoadingDialog(this);
		findView();
		init();
		if (MyTools.checkNetWorkStatus(this)) {
			mWebView.loadUrl(AllStaticMessage.URL_Find);
		} else {
			mWebView.loadUrl("file:///android_asset/index.html");
		}
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
		dialog = null;
		mIntent = null;
		mWebView.removeAllViews();
		mWebView.destroy();
		mWebView = null;
		setContentView(R.layout.view_null);
		System.gc();
	}

	// 查找控件
	private void findView() {
		mWebView = (WebView) findViewById(R.id.find_pager);
	}

	private void init() {
		// 设置支持JavaScript脚本
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// 只用于缓存/不适用缓存
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);// LOAD_CACHE_ONLY,LOAD_NO_CACHE
		// 设置可以访问文件
		webSettings.setAllowFileAccess(true);
		// 自动适应屏幕
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (dialog != null) {
					dialog.stop();
				}
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {

				dialog.loading();

				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				// 忽略SSL证书错误，继续加载页面。
				handler.proceed();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d("loadurl", "loadUrl ,url = " + url);
				if (url.contains("pageover")) {
					if (dialog != null) {
						dialog.stop();
					}
				} else if (url.contains("pid=")) {
					url = url.replace("pid=", "#");
					url = url.replace("&id=", "*");
					String goodsID = url.substring(url.lastIndexOf("#") + 1,
							url.lastIndexOf("*"));
					String guigeid = url.substring(url.lastIndexOf("*") + 1,
							url.length());
					mIntent = new Intent(FindActivity.this,
							GoodsDetailActivity.class);
					mIntent.putExtra("pid", "");
					mIntent.putExtra("goodsid", goodsID);
					mIntent.putExtra("guigeid", guigeid);
					mIntent.putExtra("imgurl", "");
					startActivity(mIntent);
				} else {
					// view.loadUrl(url);
				}
				return true;
			}
		});

	}

	/*
	 * 双击退出
	 */
	private long mExitTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出居美喵", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				this.finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
