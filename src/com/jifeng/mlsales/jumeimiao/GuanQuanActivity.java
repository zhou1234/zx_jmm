package com.jifeng.mlsales.jumeimiao;

import com.jifeng.mlsales.R;
import com.jifeng.mlsales.jumeimiao.MyQuanActivity.AppItem;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GuanQuanActivity extends Activity {
	private ListView mListView;
	private LoadingDialog dialog;
	private JSONArray array;
	private MyListViewAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guan_quan);
		dialog = new LoadingDialog(this);
		dialog.loading();
		initData();
		findView();
		register();
		getData(getIntent().getStringExtra("value").toString());
	}

	// 查找控件
	private void findView() {
		mListView = (ListView) findViewById(R.id.guan_list_quan);
	}

	// 注册事件
	private void register() {

	}

	/*
	 * 初始化数据
	 */
	private void initData() {

	}

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.guan_quan_back:// 返回
			finish();
			break;
		default:
			break;
		}
	}

	private void getData(String value) {
		String url = AllStaticMessage.URL_Guan_Quan + AllStaticMessage.User_Id
				+ "&amount=" + value;
		HttpUtil.get(url, GuanQuanActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								array = response.getJSONArray("Results");
								if (array.length() > 0) {
									mListView.setVisibility(View.VISIBLE);
									mAdapter = new MyListViewAdapter(array);
									mListView.setAdapter(mAdapter);
									mAdapter.notifyDataSetChanged();
								}
							} else {
								Toast.makeText(
										GuanQuanActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				View v = LayoutInflater.from(GuanQuanActivity.this).inflate(
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
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						GuanQuanActivity.this.setResult(RESULT_OK);
						AllStaticMessage.guanquanid = mArray.getJSONObject(
								position).getString("UserCouponId");
						AllStaticMessage.guanquan_name = mArray.getJSONObject(
								position).getString("CouponName");
						AllStaticMessage.guanquan_value = mArray.getJSONObject(
								position).getString("CouponMoney");
						GuanQuanActivity.this.finish();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

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
