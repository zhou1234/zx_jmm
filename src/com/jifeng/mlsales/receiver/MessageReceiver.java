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

	@SuppressWarnings("null")
	@Override
	public void onNotifactionClickedResult(Context arg0,
			XGPushClickedResult arg1) {
		if (arg0 == null || arg1 == null) {
			return;
		}
		if (arg1.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
			// ֪ͨ��֪ͨ�������������������
			// APP�Լ�����������ض���
			String customContent = arg1.getCustomContent();
			if (customContent != null && customContent.length() != 0) {
				try {
					JSONObject obj = new JSONObject(customContent);
					if (!obj.isNull("activyId")) {
						String activyId = obj.getString("activyId");
						if (activyId != null || !activyId.equals("")) {
							Intent intent = new Intent(arg0,
									GoodsListActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("active", "1");
							intent.putExtra("activeId", activyId);
							arg0.startActivity(intent);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else if (arg1.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
			// ֪ͨ���������������
			// APP�Լ�����֪ͨ����������ض���
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
