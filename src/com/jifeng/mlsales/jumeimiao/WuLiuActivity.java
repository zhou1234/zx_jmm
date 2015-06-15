package com.jifeng.mlsales.jumeimiao;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WuLiuActivity extends Activity {

	private WebView mWebView;
	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wuliu);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);  

		findView();
		register();
		if (MyTools.checkNetWorkStatus(this)) {
			String url = getIntent().getStringExtra("wuliuurl").toString();
			mWebView.loadUrl(url);
		} else {
			mWebView.loadUrl("file:///android_asset/index.html");
		}
	}

	// 查找控件
	private void findView() {
		mWebView = (WebView) findViewById(R.id.wuliu_web);
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
				if (url.contains(AllStaticMessage.URL_GBase)) {
					WuLiuActivity.this.finish();
				} else {
					view.loadUrl(url);
				}

				return true;
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
