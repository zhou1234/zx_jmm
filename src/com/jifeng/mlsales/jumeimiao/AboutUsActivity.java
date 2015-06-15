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
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AboutUsActivity extends Activity {

	private WebView mWebView;
	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aboutus);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		findView();
		register();
		if (MyTools.checkNetWorkStatus(this)) {
			mWebView.loadUrl(AllStaticMessage.URL_AboutUs);
		} else {
			mWebView.loadUrl("file:///android_asset/index.html");
		}
	}

	// ���ҿؼ�
	private void findView() {
		mWebView = (WebView) findViewById(R.id.aboutus_web);
	}

	// ע���¼�
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
				// ����SSL֤����󣬼�������ҳ�档
				handler.proceed();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d("loadurl", "loadUrl ,url = " + url);
				view.loadUrl(url);

				return true;
			}
		});
	}

	// //xmlע�����¼���ʵ��
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.aboutUs_back:// ����
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWebView.removeAllViews();
		mWebView.destroy();
		mWebView = null;
		System.gc();
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
