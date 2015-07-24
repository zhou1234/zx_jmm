package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.AlwaysMarqueeTextView;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.touchgallery.GalleryWidget.GalleryViewPager;
import com.jifeng.touchgallery.GalleryWidget.UrlPagerAdapter;
import com.jifeng.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import com.jifeng.url.AllStaticMessage;
import com.umeng.analytics.MobclickAgent;

public class PicShowActivity extends Activity {
	private GalleryViewPager mViewPager;
	private WebView mWebView;
	private LoadingDialog dialog;
	private RelativeLayout mLayout;
	private AlwaysMarqueeTextView mTextView;

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.activity_pic_show);
			((FBApplication) getApplication()).addActivity(this);
		} catch (OutOfMemoryError e) {
			com.nostra13.universalimageloader.core.ImageLoader.getInstance()
					.clearMemoryCache();
		}
		dialog = new LoadingDialog(this);
		mLayout = (RelativeLayout) findViewById(R.id.pic_first);
		mTextView = (AlwaysMarqueeTextView) findViewById(R.id.pic_title);
		mWebView = (WebView) findViewById(R.id.webview);
		mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
		String[] urls = getIntent().getStringExtra("url").split(",");
		if (urls.length > 0) {
			String[] url = new String[urls.length - 1];
			for (int i = 0; i < urls.length - 1; i++) {
				if (urls[i].contains("..")) {
					url[i] = AllStaticMessage.URL_GBase
							+ urls[i].replace("..", "");
				} else {
					url[i] = urls[i];
				}

			}
			if (url.length == 1) {
				if (MyTools.checkNetWorkStatus(PicShowActivity.this)) {
					mLayout.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.t_bg));
					mTextView.setText(getIntent().getStringExtra("title")
							.toString());
					mTextView.setTextColor(Color.parseColor("#000000"));
					mTextView.setVisibility(View.VISIBLE);
					mWebView.setVisibility(View.VISIBLE);
					mViewPager.setVisibility(View.GONE);
					MyTools.webviewSetting(mWebView);
					WebSettings webSettings = mWebView.getSettings();
					webSettings.setBuiltInZoomControls(true);
					mWebView.setWebViewClient(new WebViewClient() {
						@Override
						public void onPageFinished(WebView view, String url) {

							super.onPageFinished(view, url);
							if (dialog != null) {
								dialog.stop();
							}
						}

						@Override
						public void onPageStarted(WebView view, String url,
								Bitmap favicon) {
							if (dialog != null) {
								dialog.loading();
								super.onPageStarted(view, url, favicon);
							}
						}

						@Override
						public void onReceivedSslError(WebView view,
								SslErrorHandler handler, SslError error) {
							// TODO Auto-generated method stub
							// 忽略SSL证书错误，继续加载页面。
							handler.proceed();
						}

						@Override
						public boolean shouldOverrideUrlLoading(WebView view,
								String url) {
							Log.d("loadurl", "loadUrl ,url = " + url);

							view.loadUrl(url);

							return true;
						}
					});
					mWebView.loadUrl(url[0]);
				}
			} else {
				List<String> items = new ArrayList<String>();
				Collections.addAll(items, url);

				UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, items);
				pagerAdapter
						.setOnItemChangeListener(new OnItemChangeListener() {
							@Override
							public void onItemChange(int currentPosition) {
								// Toast.makeText(PicShowActivity.this,
								// "Current item is " +
								// currentPosition, Toast.LENGTH_SHORT).show();
							}
						});

				mViewPager.setOffscreenPageLimit(1);
				mViewPager.setAdapter(pagerAdapter);
				mViewPager.setCurrentItem(Integer
						.parseInt(urls[urls.length - 1]));
			}

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		ShareSDK.stopSDK(this);
		setContentView(R.layout.view_null);
		super.onDestroy();
		mViewPager = null;
		mWebView.removeAllViews();
		mWebView.destroy();
		mWebView = null;
		dialog = null;
		this.finish();
		System.gc();
	}

	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.picshow_back:
			finish();
			setContentView(R.layout.view_null);
			break;

		default:
			break;
		}
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
