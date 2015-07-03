package com.jifeng.tools;

import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.jifeng.url.AllStaticMessage;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.Log;

public class OtherLogin {
	public static Context mContext;
	private static SharedPreferences sp;

	@SuppressWarnings("static-access")
	public OtherLogin(Context context) {
		// ShareSDK.initSDK(context);
		this.mContext = context;
		sp = this.mContext.getSharedPreferences(AllStaticMessage.SPNE, 0);
	}

	public static void QQ_login(final Handler handler, final int num) {
		final Platform plat_qq = ShareSDK.getPlatform(mContext, QQ.NAME);
		// ��ȡ����
		plat_qq.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onError(Platform plat, int action, Throwable arg2) {
				System.out.println("action" + action);
				// Log.i("11111", "123456789");
			}

			@Override
			public void onCancel(Platform plat, int action) {
				handler.sendEmptyMessage(0x05);// ȡ��dialog
				// Log.i("11111", "11111111111111111111111111");
			}

			@SuppressLint("CommitPrefEdits")
			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> data) {
				if (plat_qq.isValid()) {
					String qq_openId = plat_qq.getDb().getUserId();
					AllStaticMessage.OpenId = qq_openId;
					// Log.i("11111", qq_openId);
				}
				try {
					String str = new Gson().toJson(data);
					JSONObject jsonObject = new JSONObject(str);
					// Log.i("11111", jsonObject.toString());
					AllStaticMessage.NickName = jsonObject
							.getString("nickname").toString();
					AllStaticMessage.Address = jsonObject.getString("province")
							.toString();
					String string = jsonObject.getString("figureurl_qq_2")
							.toString();

					Editor editor = sp.edit();
					editor.remove(AllStaticMessage.USER_PATH);
					editor.commit();

					editor.putString(AllStaticMessage.USER_PATH, string);
					editor.commit();
					AllStaticMessage.userImage = string;

					if (jsonObject.getString("gender").toString().equals("Ů")) {
						AllStaticMessage.Gender = "0";
					} else {
						AllStaticMessage.Gender = "1";
					}
					handler.sendEmptyMessage(num);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				plat_qq.removeAccount();
			}
		});
		plat_qq.showUser(null);
	}

	public static void Sina_login(final Handler handler, final int num) {
		final Platform plat_sina = ShareSDK.getPlatform(mContext,
				SinaWeibo.NAME);
		plat_sina.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onError(Platform plat, int action, Throwable arg2) {
				System.out.println("action" + action);
				plat_sina.removeAccount();
			}

			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> data) {
				// System.out.println(arg2);
				if (plat_sina.isValid()) {
					String Sina_openId = plat_sina.getDb().getUserId();
					AllStaticMessage.OpenId = Sina_openId;
					// Log.i("11111", Sina_openId);
				}
				try {
					String str = new Gson().toJson(data);
					JSONObject jsonObject = new JSONObject(str);
					// Log.i("11111", jsonObject.toString());
					AllStaticMessage.NickName = jsonObject.getString("name")
							.toString();
					AllStaticMessage.Address = jsonObject.getString("location")
							.toString();

					String url = jsonObject.getString("avatar_large").toString();
					AllStaticMessage.userImage = url;
					Editor editor = sp.edit();
					editor.remove(AllStaticMessage.USER_PATH);
					editor.commit();

					editor.putString(AllStaticMessage.USER_PATH, url);
					editor.commit();

					if (jsonObject.getString("gender").toString().equals("m")) {
						AllStaticMessage.Gender = "1";
					} else {
						AllStaticMessage.Gender = "0";
					}
					handler.sendEmptyMessage(num);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				plat_sina.removeAccount();
				// result_sian(arg2);
			}

			@Override
			public void onCancel(Platform plat, int action) {
				handler.sendEmptyMessage(0x05);// ȡ��dialog
			}
		});
		plat_sina.SSOSetting(true);
		plat_sina.showUser(null);
	}

	public static void WeiXin_login(final Handler handler, final int num) {
		final Platform plat_weixin = ShareSDK
				.getPlatform(mContext, Wechat.NAME);// new Wechat(mContext) ;//
		plat_weixin.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onError(Platform plat, int action, Throwable arg2) {
				System.out.println("action" + action);

			}

			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> data) {
				System.out.println(data);
				// Log.i("11111", data.toString());
				if (plat_weixin.isValid()) {
					String qq_openId = plat_weixin.getDb().getUserId();
					AllStaticMessage.OpenId = qq_openId;

				}
				try {
					String str = new Gson().toJson(data);
					JSONObject jsonObject = new JSONObject(str);
					// Log.i("11111", jsonObject.toString());
					AllStaticMessage.NickName = jsonObject
							.getString("nickname").toString();
					AllStaticMessage.Address = jsonObject.getString("province")
							.toString();

					String url = jsonObject.getString("headimgurl").toString();
					AllStaticMessage.userImage = url;
					Editor editor = sp.edit();
					editor.remove(AllStaticMessage.USER_PATH);
					editor.commit();

					editor.putString(AllStaticMessage.USER_PATH, url);
					editor.commit();

					AllStaticMessage.Gender = jsonObject.getString("sex")
							.toString();

					handler.sendEmptyMessage(num);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				plat_weixin.removeAccount();
				// result_sian(arg2);
			}

			@Override
			public void onCancel(Platform plat, int action) {
				handler.sendEmptyMessage(0x05);// ȡ��dialog
			}
		});
		plat_weixin.SSOSetting(true);
		plat_weixin.showUser(null);
	}
}
