package com.jifeng.mlsales.wxapi;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.mlsales.jumeimiao.CheckOrderActivity;
import com.jifeng.mlsales.jumeimiao.TabHostActivity;
import com.jifeng.tools.TasckActivity;
import com.jifeng.url.AllStaticMessage;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	private IWXAPI api;
	private TasckActivity tasckActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wxpay_result);
		((FBApplication) getApplication()).addActivity(this);
		api = WXAPIFactory.createWXAPI(this, AllStaticMessage.APP_ID);
		api.handleIntent(getIntent(), this);
		tasckActivity = new TasckActivity();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {

	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			int code = resp.errCode;
			if (code == 0) {
				AllStaticMessage.Back_to_ZhangHu = true;
				Intent mIntent = new Intent(WXPayEntryActivity.this,
						TabHostActivity.class);
				startActivity(mIntent);
				this.getApplication().onTerminate();
			} else {
				Intent mIntent = new Intent(WXPayEntryActivity.this,
						CheckOrderActivity.class);
				mIntent.putExtra("orderNum", AllStaticMessage.WxOrder);
				startActivity(mIntent);
				this.finish();
			}
		}

	}
}