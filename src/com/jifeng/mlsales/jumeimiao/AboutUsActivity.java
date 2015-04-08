package com.jifeng.mlsales.jumeimiao;
 
    
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage; 
import com.umeng.analytics.MobclickAgent;

import android.app.Activity; 
import android.app.Dialog; 
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
		dialog=new LoadingDialog(this);
		findView();
		register(); 
		if(MyTools.checkNetWorkStatus(this)){
			mWebView.loadUrl(AllStaticMessage.URL_AboutUs);
		}else{
			mWebView.loadUrl("file:///android_asset/index.html");
		} 
	}
	//查找控件
	private void findView() {
		mWebView=(WebView) findViewById(R.id.aboutus_web);
	}
	//注册事件
	private void register() {
		MyTools.webviewSetting(mWebView);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				 dialog.stop();
				super.onPageFinished(view, url);
			}

			@Override 
			public void onPageStarted(WebView view, String url,
					Bitmap favicon) {
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
				 view.loadUrl(url);
	 				  
				return true;
			}
		});
	}
 
//	//xml注册点击事件的实现
	public void doclick(View view){
		switch (view.getId()) {
		case R.id.aboutUs_back://返回
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
	    mWebView=null;
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
