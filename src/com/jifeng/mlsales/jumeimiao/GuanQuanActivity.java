package com.jifeng.mlsales.jumeimiao;

import java.util.HashMap;
import java.util.Map;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GuanQuanActivity extends Activity implements OnClickListener {
	private ListView mListView;
	private LoadingDialog dialog;
	private JSONArray array;
	private MyListViewAdapter mAdapter;
	private Button bt_ok;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guan_quan);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		dialog.loading();
		findView();
		getData(getIntent().getStringExtra("value").toString());
	}

	// 查找控件
	private void findView() {
		mListView = (ListView) findViewById(R.id.guan_list_quan);
		bt_ok = (Button) findViewById(R.id.bt_ok);
		bt_ok.setOnClickListener(this);
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
		// String url = AllStaticMessage.URL_Guan_Quan +
		// AllStaticMessage.User_Id
		// + "&amount=" + value;
		String url = AllStaticMessage.URL_Guan_Quan_New
				+ AllStaticMessage.User_Id + "&amount=" + value;

		HttpUtil.get(url, GuanQuanActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@SuppressLint("UseSparseArrays")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								array = response.getJSONArray("Results");
								if (array.length() > 0) {
									bt_ok.setVisibility(View.VISIBLE);
									mListView.setVisibility(View.VISIBLE);
									Map<Integer, Boolean> map = new HashMap<Integer, Boolean>(
											array.length());
									for (int i = 0; i < array.length(); i++) {
										map.put(i, false);
									}
									int index = getIntent().getIntExtra(
											"index", -1);
									if (index != 0x132) {
										map.put(index, true);
									}
									mAdapter = new MyListViewAdapter(array, map);
									mListView.setAdapter(mAdapter);
									mAdapter.notifyDataSetChanged();
								}
							} else {
								bt_ok.setVisibility(View.GONE);

								String results = response.getString("Results")
										.toString();
								if (results == null || results.equals("")) {
									results = "暂无可用优惠券";
								}
								Toast.makeText(GuanQuanActivity.this, results,
										500).show();
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
					}
				});
	}

	private class MyListViewAdapter extends BaseAdapter {
		AppItem appItem;
		JSONArray mArray;
		Map<Integer, Boolean> map;

		public MyListViewAdapter(JSONArray array, Map<Integer, Boolean> map) {
			this.mArray = array;
			this.map = map;

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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				View v = LayoutInflater.from(GuanQuanActivity.this).inflate(
						R.layout.quan_list_item, null);
				appItem = new AppItem();
				// appItem.AppText_name = (TextView)
				// v.findViewById(R.id.youhuiquan_name);
				appItem.AppText_time = (TextView) v
						.findViewById(R.id.tv_endTime);
				appItem.AppText_money = (TextView) v
						.findViewById(R.id.tv_price);
				appItem.AppText_classsion = (TextView) v
						.findViewById(R.id.tv_fanWei);
				appItem.AppText_rule = (TextView) v.findViewById(R.id.tv_rule);

				// appItem.AppText_pinpai = (TextView) v
				// .findViewById(R.id.youhuiquan_huodong_name);
				// appItem.AppImg = (ImageView) v.findViewById(R.id.img_all);
				appItem.AppCheck = (CheckBox) v.findViewById(R.id.checkBox);
				v.setTag(appItem);
				convertView = v;
			} else {
				appItem = (AppItem) convertView.getTag();
			}
			try {
				// if (mArray.getJSONObject(position).getString("IsSys")
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
						.getString("CouponMoney").toString()
						+ "元");
				appItem.AppText_classsion.setText(mArray
						.getJSONObject(position).getString("UseRule")
						.toString());

				appItem.AppText_rule.setText(mArray.getJSONObject(position)
						.getString("CouponName"));

				appItem.AppCheck.setChecked(map.get(position));

			} catch (JSONException e) {
				e.printStackTrace();
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						AllStaticMessage.guanquanid = mArray.getJSONObject(
								position).getString("Id");
						AllStaticMessage.guanquan_name = mArray.getJSONObject(
								position).getString("CouponName");
						AllStaticMessage.guanquan_value = mArray.getJSONObject(
								position).getString("CouponMoney");
						// GuanQuanActivity.this.finish();
						CheckBox check = (CheckBox) v
								.findViewById(R.id.checkBox);
						boolean flag = check.isChecked();
						for (int i = 0; i < map.size(); i++) {
							if (i == position && flag == true) {
								map.put(i, false);
							}
							if (i == position && flag == false) {
								map.put(i, true);
							} else {
								map.put(i, false);
							}
						}
						boolean isCheck = false;
						Intent intent = new Intent();
						for (int j = 0; j < map.size(); j++) {
							if (map.get(j)) {
								isCheck = true;
								intent.putExtra("index", j);
								intent.putExtra("flag", isCheck);
							}

						}
						if (isCheck == false) {
							intent.putExtra("flag", isCheck);
						}
						GuanQuanActivity.this.setResult(RESULT_OK, intent);
						notifyDataSetChanged();

					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			});

			return convertView;
		}
	}

	private class AppItem {
		// TextView AppText_name;
		private TextView AppText_time;
		private TextView AppText_money;
		private TextView AppText_classsion;
		private TextView AppText_rule;
		// TextView AppText_pinpai;
		// ImageView AppImg;
		private CheckBox AppCheck;
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

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.bt_ok:
			finish();
			break;

		default:
			break;
		}

	}
}
