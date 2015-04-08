package com.jifeng.mlsales.jumeimiao;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyQuanActivity extends Activity {
	private ListView mListView;
	private MyListViewAdapter mAdapter;
	private LoadingDialog dialog;
	private Button mBtn_wei, mBtn_yi, mBtn_guoqi;
	JSONArray array;
	private ImageView iv_no;
	private TextView tv_no;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myquan);
		dialog = new LoadingDialog(this);
		initData();
		findView();
		register();
		getData("0");
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
	}

	// 注册事件
	private void register() {

	}

	// 其他实现
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
		case 1:
			mBtn_wei.setTextColor(getResources().getColor(
					R.color.main_no_select));
			mBtn_wei.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.fadain_zuo));
			break;
		case 2:
			mBtn_yi.setTextColor(getResources()
					.getColor(R.color.main_no_select));
			mBtn_yi.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.fadain_middle));
			break;
		case 3:
			mBtn_guoqi.setTextColor(getResources().getColor(
					R.color.main_no_select));
			mBtn_guoqi.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.fadain_you));
			break;
		default:
			break;
		}
	}

	/*
	 * 初始化数据
	 */
	private void initData() {

	}

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.quan_back:// 返回
			finish();
			break;
		case R.id.rel_quan_no:
			setView(1);
			array = null;
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
			getData("0");

			break;
		case R.id.rel_quan_yes:
			setView(2);
			array = null;
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
			getData("1");
			break;
		case R.id.rel_quan_last:
			setView(3);
			array = null;
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
			getData("2");
			break;
		default:
			break;
		}
	}

	private void getData(String type) {
		dialog.loading();
		String url = AllStaticMessage.URL_Quan_list + AllStaticMessage.User_Id
				+ "&type=" + type;
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
								if (mAdapter != null) {
									mAdapter.notifyDataSetChanged();
								}
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
						// TODO Auto-generated method stub
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
			// TODO Auto-generated method stub
			return mArray.length();// mArray.length()
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				View v = LayoutInflater.from(MyQuanActivity.this).inflate(
						R.layout.item_quan_list, null);
				appItem = new AppItem();
				// appItem.AppText_name = (TextView)
				// v.findViewById(R.id.youhuiquan_name);
				appItem.AppText_time = (TextView) v
						.findViewById(R.id.youhuiquan_time);
				appItem.AppText_money = (TextView) v
						.findViewById(R.id.youhuiquan_money);
				appItem.AppText_classsion = (TextView) v
						.findViewById(R.id.text_classion);
				appItem.AppRel_Bg = (RelativeLayout) v
						.findViewById(R.id.youhuiquan_bg);
				appItem.AppText_pinpai = (TextView) v
						.findViewById(R.id.youhuiquan_huodong_name);
				appItem.AppImg = (ImageView) v.findViewById(R.id.img_all);
				v.setTag(appItem);
				convertView = v;
			} else {
				appItem = (AppItem) convertView.getTag();
			}
			try {

				if (mArray.getJSONObject(position).getString("IsSys")
						.toString().equals("0")) {// 品牌
					appItem.AppImg.setVisibility(View.GONE);
					// appItem.AppText_name.setText("消费满1000送200");
					appItem.AppText_pinpai.setVisibility(View.VISIBLE);
					appItem.AppText_pinpai.setText(mArray
							.getJSONObject(position).getString("BrandName")
							.toString());
					appItem.AppRel_Bg.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.youhuiquan_bg_2));
				} else if (mArray.getJSONObject(position).getString("IsSys")
						.toString().equals("1")) {// 系统
					appItem.AppImg.setVisibility(View.VISIBLE);
					appItem.AppText_pinpai.setVisibility(View.GONE);
					appItem.AppRel_Bg.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.youhuiquan_bg));
				}

				String startime = mArray.getJSONObject(position)
						.getString("StartTime").toString();
				String endtime = mArray.getJSONObject(position)
						.getString("EndTime").toString();
				appItem.AppText_time
						.setText(startime.substring(0,
								startime.lastIndexOf(' '))
								+ "~"
								+ endtime.substring(0,
										startime.lastIndexOf(' ')));

				appItem.AppText_money.setText("￥"
						+ mArray.getJSONObject(position)
								.getString("CouponMoney").toString());
				appItem.AppText_classsion.setText(mArray
						.getJSONObject(position).getString("UseRule")
						.toString());

			} catch (JSONException e) {
				// TODO Auto-generated catch block
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
		TextView AppText_classsion;
		RelativeLayout AppRel_Bg;
		TextView AppText_pinpai;
		ImageView AppImg;
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
