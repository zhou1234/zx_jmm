package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.myview.My_GridView;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DaiZhiFuActivity extends Activity {
	private Intent mIntent;
	private GridView mGridView;
	private MyGridViewAdapter mAdapter;
	private TextView mText_Title;
	String orderState;// 1 待付款 2待发货
	private List<JSONObject> mData;
	private LoadingDialog dialog;

	private String no = "";
	private ImageView iv_no;
	private TextView tv_no;
	private MyGalleryAdapter gAdapter;
	private List<JSONObject> mListData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daizhifu);
		dialog = new LoadingDialog(this);
		mListData = new ArrayList<JSONObject>();
		dialog.loading();
		mData = new ArrayList<JSONObject>();
		findView();

	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
		// setContentView(R.layout.view_null);
		// dialog = null;
		// mText_Title = null;
		// mIntent = null;
		// mGridView = null;
		// mAdapter = null;
		// mData = null;
		// orderState = null;
		// this.finish();
		// System.gc();
	}

	// 查找控件
	private void findView() {
		mGridView = (GridView) findViewById(R.id.daizhifu_gridview);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));//设置点击是背景透明
		mText_Title = (TextView) findViewById(R.id.textview_title);
		iv_no = (ImageView) findViewById(R.id.iv_no);
		tv_no = (TextView) findViewById(R.id.tv_no);

		mText_Title.setText(getIntent().getStringExtra("title").toString());
		if (getIntent().getStringExtra("title").toString().equals("待付款订单")) {
			orderState = "1";
			no = "暂无待付款订单";
		} else {
			orderState = "2";
			no = "暂无待发货订单";
		}
	}


	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.daizhifu_back:// 返回
			finish();
			break;
		default:
			break;
		}
	}

	private void getData(String page) {
		String url = AllStaticMessage.URL_Order_List + AllStaticMessage.User_Id
				+ "&orderState=" + orderState + "&page=" + page;
		HttpUtil.get(url, DaiZhiFuActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								JSONArray array = response
										.getJSONArray("Results");
								// Log.i("11111", array.toString());
								if (mData != null) {
									mData.clear();
								} else {
									mData = new ArrayList<JSONObject>();
								}
								if (array.length() > 0) {
									for (int i = 0; i < array.length(); i++) {
										mData.add(array.getJSONObject(i));
									}
									mAdapter = new MyGridViewAdapter(mData);
									mGridView.setAdapter(mAdapter);
								} else {
									tv_no.setText(no);
									mGridView.setVisibility(View.GONE);
									iv_no.setVisibility(View.VISIBLE);
									tv_no.setVisibility(View.VISIBLE);
								}
							} else {
								tv_no.setText(no);
								mGridView.setVisibility(View.GONE);
								iv_no.setVisibility(View.VISIBLE);
								tv_no.setVisibility(View.VISIBLE);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (dialog != null) {
							dialog.stop();
						}

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
						if (dialog != null) {
							dialog.stop();
						}
					}
				});
	}

	private class MyGridViewAdapter extends BaseAdapter {
		AppItem appItem;
		List<JSONObject> mListData;

		DisplayImageOptions options;

		public MyGridViewAdapter(List<JSONObject> listData) {
			mListData = new ArrayList<JSONObject>();
			this.mListData = listData;
			options = MyTools.createOptions(R.drawable.img);
		}

		@Override
		public int getCount() {
			return mListData.size();
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
				View v = LayoutInflater.from(DaiZhiFuActivity.this).inflate(
						R.layout.item_daizhifu_gridview, null);

				appItem = new AppItem();

				// DisplayMetrics dm = new DisplayMetrics();
				// getWindowManager().getDefaultDisplay().getMetrics(dm);
				// float density = dm.density;
				// int gridviewWidth = (int) (1* (110)* density);
				// int itemWidth = (int) (100 * density);
				//
				// // 新建一个GridView
				// appItem.my_GridView = new My_GridView(DaiZhiFuActivity.this);
				// // 设置内部子栏目的宽度
				// appItem.my_GridView.setColumnWidth(itemWidth);
				// // 设置内部子栏目个数为自动适应
				// //appItem.my_GridView.setNumColumns(GridView.AUTO_FIT);
				// // 设置Gravity为Center
				// // 设置Selector为透明
				// appItem.my_GridView.setSelector(new ColorDrawable(
				// Color.TRANSPARENT));
				// appItem.my_GridView.setStretchMode(GridView.NO_STRETCH);
				// //appItem.my_GridView.setNumColumns(100);
				//
				// appItem.my_GridView.setHorizontalSpacing(10);
				// LayoutParams layoutParams = new LayoutParams(
				// gridviewWidth, LayoutParams.WRAP_CONTENT);
				// // 设置GridView的LayoutParams为子栏目的宽度乘以栏目个数
				// appItem.my_GridView.setLayoutParams(layoutParams);
				//
				// LinearLayout categoryLayout = (LinearLayout)
				// v.findViewById(R.id.category_layout);
				// // 将新建的GridView添加到布局中
				// categoryLayout.addView(appItem.my_GridView);
				appItem.AppText_time = (TextView) v
						.findViewById(R.id.item_order_id);
				appItem.AppText_status = (TextView) v
						.findViewById(R.id.item_order_status);
				appItem.AppText_price = (TextView) v
						.findViewById(R.id.item_order_price);
				appItem.AppImg = (ImageView) v
						.findViewById(R.id.item_order_img);
				appItem.AppImg_zhifu = (ImageView) v
						.findViewById(R.id.img_zhifu);
				appItem.AppmLayout = (RelativeLayout) v
						.findViewById(R.id.rel_time_zhifu);
				v.setTag(appItem);
				convertView = v;
			} else {
				appItem = (AppItem) convertView.getTag();
			}
			// appItem.mAppIcon.setImageDrawable(getResources().getDrawable(img[position]));
			// appItem.AppImg.setImageResource(imgId[position]);
			// appItem.AppText.setText(titles[position]);
			// convertView.setBackgroundResource(R.drawable.doclick);
			try {
				appItem.AppText_time.setText(mListData.get(position)
						.get("AddTime").toString());
				appItem.AppText_price.setText("￥"
						+ mListData.get(position).get("total").toString());
				if (orderState.equals("1")) {
					appItem.AppText_status.setText("待付款");
					appItem.AppmLayout.setVisibility(View.VISIBLE);
					appItem.AppImg_zhifu.setOnClickListener(new onItemClick(
							appItem, mListData.get(position)));
				} else {
					appItem.AppText_status.setText("待发货");
					appItem.AppmLayout.setVisibility(View.GONE);
				}
				// 加载图片
				String imgUrl = AllStaticMessage.URL_GBase + "/UsersData/"
						+ mData.get(position).getString("Account").toString()
						+ "/" + mData.get(position).getString("Img").toString()
						+ "/5.jpg";
				// getDataImage(mData.get(position).getString("OrderId")
				// .toString(), appItem);
				ImageLoader.getInstance().displayImage(imgUrl, appItem.AppImg,
						options);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						mIntent = new Intent(DaiZhiFuActivity.this,
								OrderDetailActivity.class);
						mIntent.putExtra("id",
								mData.get(position).getString("OrderId")
										.toString());
						startActivity(mIntent);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			// appItem.my_GridView
			// .setOnItemClickListener(new AdapterView.OnItemClickListener() {
			//
			// @Override
			// public void onItemClick(AdapterView<?> arg0, View arg1,
			// int arg2, long arg3) {
			// try {
			// mIntent = new Intent(DaiZhiFuActivity.this,
			// OrderDetailActivity.class);
			// mIntent.putExtra("id", mData.get(position)
			// .getString("OrderId").toString());
			// startActivity(mIntent);
			// } catch (JSONException e) {
			// e.printStackTrace();
			// }
			//
			// }
			// });
			return convertView;
		}
	}

	class AppItem {
		RelativeLayout AppmLayout;
		TextView AppText_time;
		TextView AppText_status;
		TextView AppText_price;
		ImageView AppImg, AppImg_zhifu;
		My_GridView my_GridView;
	}

	class onItemClick implements OnClickListener {
		AppItem appItem;
		JSONObject jsonObject;

		public onItemClick(AppItem appIte, JSONObject paywa) {
			this.appItem = appIte;
			this.jsonObject = paywa;
		}

		@Override
		public void onClick(View v) {
			// 立即支付
			try {
				mIntent = new Intent(DaiZhiFuActivity.this, MyPayActivity.class);
				mIntent.putExtra("allprice", jsonObject.getString("total"));
				if (jsonObject.getString("PayWay").contains("支付宝")) {
					mIntent.putExtra("payway", "zfb");
				} else {
					mIntent.putExtra("payway", "wx");
				}
				mIntent.putExtra("orderid", jsonObject.getString("OrderId")
						.toString());
				startActivity(mIntent);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	class MyGalleryAdapter extends BaseAdapter {
		DisplayImageOptions options;
		List<JSONObject> mListData;

		public MyGalleryAdapter(List<JSONObject> mListData) {
			options = MyTools.createOptionsOther(R.drawable.img);
			this.mListData = mListData;
		}

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = getLayoutInflater().inflate(
					R.layout.item_daizhifu_gridview_new_gallery, arg2, false);
			ImageView v = (ImageView) view.findViewById(R.id.v);
			try {
				String imgUrl = AllStaticMessage.URL_GBase + "/UsersData/"
						+ mListData.get(arg0).getString("Account").toString()
						+ "/" + mListData.get(arg0).getString("Img").toString()
						+ "/5.jpg";
				ImageLoader.getInstance().displayImage(imgUrl, v, options);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return view;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		getData("1");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void getDataImage(String orderId, final AppItem appItem) {
		String url = AllStaticMessage.URL_Order_Deatil
				+ AllStaticMessage.User_Id + "&orderId=" + orderId;
		HttpUtil.get(url, DaiZhiFuActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								JSONArray mArray = response
										.getJSONArray("Results");
								if (mListData != null) {
									mListData.clear();
								}
								if (mArray.length() > 0) {
									for (int i = 0; i < mArray.length(); i++) {
										mListData.add(mArray.getJSONObject(i));
									}
									appItem.my_GridView.setNumColumns(mListData
											.size());
									// appItem.my_GridView
									// .setSelector(new ColorDrawable(
									// Color.TRANSPARENT));
									gAdapter = new MyGalleryAdapter(mListData);
									appItem.my_GridView.setAdapter(gAdapter);

								} else {
									Toast.makeText(
											DaiZhiFuActivity.this,
											response.getString("Results")
													.toString(), 500).show();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

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
						if (dialog != null) {
							dialog.stop();
						}
					}
				});
	}
}
