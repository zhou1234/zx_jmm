package com.jifeng.mlsales.jumeimiao;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MyQuanActivity extends Activity implements OnClickListener {
	private ListView mListView;
	private MyListViewAdapter mAdapter;
	private LoadingDialog dialog;
	private Button mBtn_wei, mBtn_yi, mBtn_guoqi;
	JSONArray array;
	private ImageView iv_no;
	private TextView tv_no, tv_addQuan;
	private String statu = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myquan);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		findView();
		// mAdapter = new MyListViewAdapter();
		// mListView.setAdapter(mAdapter);
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		setContentView(R.layout.view_null);
		super.onDestroy();
		dialog = null;

		mListView = null;
		mAdapter = null;
		mBtn_wei = null;
		mBtn_yi = null;
		mBtn_guoqi = null;
		array = null;

		this.finish();
		System.gc();
	}

	// 查找控件
	private void findView() {
		mListView = (ListView) findViewById(R.id.listView_quan);
		mListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mBtn_wei = (Button) findViewById(R.id.rel_quan_no);
		mBtn_yi = (Button) findViewById(R.id.rel_quan_yes);
		mBtn_guoqi = (Button) findViewById(R.id.rel_quan_last);

		iv_no = (ImageView) findViewById(R.id.iv_no);
		tv_no = (TextView) findViewById(R.id.tv_no);
		tv_addQuan = (TextView) findViewById(R.id.tv_addQuan);
		tv_addQuan.setOnClickListener(this);
	}

	// 其他实现
	@SuppressWarnings("deprecation")
	private void setView(int i) {
		mBtn_wei.setTextColor(getResources().getColor(R.color.white));
		mBtn_yi.setTextColor(getResources().getColor(R.color.white));
		mBtn_guoqi.setTextColor(getResources().getColor(R.color.white));
		mBtn_wei.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.fadain_touming));
		mBtn_yi.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.fadain_touming));
		mBtn_guoqi.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.fadain_touming));
		switch (i) {
		case 0:
			mBtn_wei.setTextColor(getResources().getColor(
					R.color.main_no_select));
			mBtn_wei.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.fadain_zuo));
			break;
		case 1:
			mBtn_yi.setTextColor(getResources()
					.getColor(R.color.main_no_select));
			mBtn_yi.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.fadain_middle));
			break;
		case 2:
			mBtn_guoqi.setTextColor(getResources().getColor(
					R.color.main_no_select));
			mBtn_guoqi.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.fadain_you));
			break;
		default:
			break;
		}
	}

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.quan_back:// 返回
			finish();
			break;
		case R.id.rel_quan_no:
			setView(0);
			getData("0");

			break;
		case R.id.rel_quan_yes:
			setView(1);
			getData("1");
			break;
		case R.id.rel_quan_last:
			setView(2);
			getData("2");
			break;
		default:
			break;
		}
	}

	private void getData(String status) {
		dialog.loading();
		// String url = AllStaticMessage.URL_Quan_list +
		// AllStaticMessage.User_Id
		// + "&type=" + type;
		statu = status;
		String url = AllStaticMessage.URL_Quan_list_New
				+ status + "&UserId=" + AllStaticMessage.User_Id;

		HttpUtil.get(url, MyQuanActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
								iv_no.setVisibility(View.GONE);
								tv_no.setVisibility(View.GONE);
								array = response.getJSONArray("Results");
								if (array.length() > 0) {
									mListView.setVisibility(View.VISIBLE);
									mAdapter = new MyListViewAdapter(array);
									mListView.setAdapter(mAdapter);
									mAdapter.notifyDataSetChanged();
								}

							} else {
								tv_no.setText("暂无优惠券");
								iv_no.setVisibility(View.VISIBLE);
								tv_no.setVisibility(View.VISIBLE);
								mListView.setVisibility(View.GONE);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						dialog.stop();
					}

					@Override
					public void onStart() {
						super.onStart();
						// 请求开始
					}

					@Override
					public void onFinish() {
						super.onFinish();
						// 请求结束
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// 错误返回JSONObject
						dialog.stop();
					}
				});
	}

	private class MyListViewAdapter extends BaseAdapter {
		AppItem appItem;
		JSONArray mArray;

		public MyListViewAdapter(JSONArray array) {
			this.mArray = array;

		}

		@Override
		public int getCount() {
			return mArray.length();
		}

		@Override
		public Object getItem(int position) {

			return null;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				View v = LayoutInflater.from(MyQuanActivity.this).inflate(
						R.layout.myquan_listview_item, null);
				appItem = new AppItem();
				// appItem.AppText_name = (TextView)
				// v.findViewById(R.id.youhuiquan_name);
				appItem.AppText_time = (TextView) v
						.findViewById(R.id.tv_endTime);
				appItem.AppText_money = (TextView) v
						.findViewById(R.id.tv_money);
				appItem.AppText_price = (TextView) v
						.findViewById(R.id.tv_price);
				appItem.AppTv_code = (TextView) v.findViewById(R.id.tv_code);
				appItem.AppTv_rule = (TextView) v.findViewById(R.id.tv_rule);
				appItem.AppTv_status = (TextView) v
						.findViewById(R.id.tv_status);
				appItem.Appll_price = (LinearLayout) v
						.findViewById(R.id.ll_price);
				// appItem.AppText_classsion = (TextView) v
				// .findViewById(R.id.text_classion);
				// appItem.AppRel_Bg = (RelativeLayout) v
				// .findViewById(R.id.youhuiquan_bg);
				// appItem.AppText_pinpai = (TextView) v
				// .findViewById(R.id.youhuiquan_huodong_name);
				// appItem.AppImg = (ImageView) v.findViewById(R.id.img_all);
				// appItem.AppCheck = (CheckBox) v.findViewById(R.id.check);
				// appItem.AppCheck.setVisibility(View.GONE);
				v.setTag(appItem);
				convertView = v;
			} else {
				appItem = (AppItem) convertView.getTag();
			}
			try {

				// if (mArray.getJSONObject(position).getString("IsSys")
				// .toString().equals("0")) {// 品牌
				// appItem.AppImg.setVisibility(View.GONE);
				// // appItem.AppText_name.setText("消费满1000送200");
				// appItem.AppText_pinpai.setVisibility(View.VISIBLE);
				// appItem.AppText_pinpai.setText(mArray
				// .getJSONObject(position).getString("BrandName")
				// .toString());
				// appItem.AppRel_Bg.setBackgroundDrawable(getResources()
				// .getDrawable(R.drawable.youhuiquan_bg_2));
				// } else if (mArray.getJSONObject(position).getString("IsSys")
				// .toString().equals("1")) {// 系统
				// appItem.AppImg.setVisibility(View.VISIBLE);
				// appItem.AppText_pinpai.setVisibility(View.GONE);
				// appItem.AppRel_Bg.setBackgroundDrawable(getResources()
				// .getDrawable(R.drawable.youhuiquan_bg));
				// }

				// String startime = mArray.getJSONObject(position)
				// .getString("StartTime").toString();
				String endtime = mArray.getJSONObject(position)
						.getString("EndTime").toString();
				appItem.AppText_time.setText(endtime);

				appItem.AppText_money.setText(mArray.getJSONObject(position)
						.getString("CouponMoney") + "元现金券");
				appItem.AppText_price.setText(mArray.getJSONObject(position)
						.getString("CouponMoney"));
				appItem.AppTv_rule.setText("("
						+ mArray.getJSONObject(position).getString("UseRule")
						+ ")");

				appItem.AppTv_code.setText(mArray.getJSONObject(position)
						.getString("CouponCode"));
				String status;
				if (statu.equals("0")) {
					status = "未使用";
					appItem.Appll_price
							.setBackgroundResource(R.drawable.myquan_bg);
				} else if (statu.equals("1")) {
					status = "已使用";
					appItem.Appll_price
							.setBackgroundResource(R.drawable.myquan_bg_2);
				} else {
					status = "已过期";
					appItem.Appll_price
							.setBackgroundResource(R.drawable.myquan_bg_2);
				}
				appItem.AppTv_status.setText(status);
				// appItem.AppText_classsion.setText(mArray
				// .getJSONObject(position).getString("UseRule")
				// .toString());

			} catch (JSONException e) {
				e.printStackTrace();
			}

			// try {
			// appItem.AppText_name.setText(mArray.getJSONObject(position).getString("CouponName").toString());

			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			return convertView;
		}

	}

	class AppItem {
		// TextView AppText_name;
		TextView AppText_time;
		TextView AppText_money;
		TextView AppText_price;
		TextView AppTv_rule;
		TextView AppTv_code;
		TextView AppTv_status;
		LinearLayout Appll_price;
		// TextView AppText_classsion;
		// RelativeLayout AppRel_Bg;
		// TextView AppText_pinpai;
		// ImageView AppImg;
		// CheckBox AppCheck;
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		getData(statu);
		setView(Integer.parseInt(statu));
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.tv_addQuan:
			startActivity(new Intent(this, AddQuanActivity.class));
			break;

		default:
			break;
		}
	}
}
