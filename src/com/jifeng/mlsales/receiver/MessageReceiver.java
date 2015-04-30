package com.jifeng.mlsales.receiver;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.jifeng.mlsales.jumeimiao.GoodsListActivity;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

public class MessageReceiver extends XGPushBaseReceiver {

	@Override
	public void onDeleteTagResult(Context arg0, int arg1, String arg2) {

	}

	@Override
	public void onNotifactionClickedResult(Context arg0,
			XGPushClickedResult arg1) {
		if (arg0 == null || arg1 == null) {
			return;
		}
		if (arg1.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
			// 通知在通知栏被点击啦。。。。。
			// APP自己处理点击的相关动作
			String customContent = arg1.getCustomContent();
			if (customContent != null && customContent.length() != 0) {
				try {
					JSONObject obj = new JSONObject(customContent);
					if (!obj.isNull("activyId")) {
						String activyId = obj.getString("activyId");
						Intent intent = new Intent(arg0,
								GoodsListActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("active", "1");
						intent.putExtra("activeId", activyId);
						arg0.startActivity(intent);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else if (arg1.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
			// 通知被清除啦。。。。
			// APP自己处理通知被清除后的相关动作
		}

	}

	@Override
	public void onNotifactionShowedResult(Context arg0, XGPushShowedResult arg1) {

	}

	@Override
	public void onRegisterResult(Context arg0, int arg1,
			XGPushRegisterResult arg2) {

	}

	@Override
	public void onSetTagResult(Context arg0, int arg1, String arg2) {

	}

	@Override
	public void onTextMessage(Context arg0, XGPushTextMessage arg1) {

	}

	@Override
	public void onUnregisterResult(Context arg0, int arg1) {

	}

}
