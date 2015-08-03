package com.jifeng.mlsales.jumeimiao;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.ECLAIR_MR1)
public class FindActivity extends Activity {
	private LoadingDialog dialog;
	private Intent mIntent;
	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find);
		((FBApplication) getApplication()).addActivity(this);
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

	// ���ҿؼ�
	private void findView() {
		mWebView = (WebView) findViewById(R.id.find_pager);
	}

	private void init() {
		// ����֧��JavaScript�ű�
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// ֻ���ڻ���/�����û���
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);// LOAD_CACHE_ONLY,LOAD_NO_CACHE
		// ���ÿ��Է����ļ�
		webSettings.setAllowFileAccess(true);
		// �Զ���Ӧ��Ļ
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
				// ����SSL֤����󣬼�������ҳ�档
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
					url = url.replace("&activeId=", "$");
					String goodsID = url.substring(url.lastIndexOf("#") + 1,
							url.lastIndexOf("*"));
					// String guigeid = url.substring(url.lastIndexOf("*") + 1,
					// url.length());
					String guigeid = url.substring(url.lastIndexOf("*") + 1,
							url.lastIndexOf("$"));
					String activityId = url.substring(url.lastIndexOf("$") + 1,
							url.length());
					mIntent = new Intent(FindActivity.this,
							GoodsDetailActivity.class);
					mIntent.putExtra("active", "0");
					mIntent.putExtra("pid", activityId);
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

	// //xmlע�����¼���ʵ��
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.find_back:// ����
			finish();
			break;
		default:
			break;
		}
	}

	/*
	 * ˫���˳�
	 */
	private long mExitTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "�ٰ�һ���˳�������", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				this.getApplication().onTerminate();
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
