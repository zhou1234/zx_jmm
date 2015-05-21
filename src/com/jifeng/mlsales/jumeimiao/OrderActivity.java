package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.ShareSDK;

import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class OrderActivity extends Activity implements OnHeaderRefreshListener,
		OnFooterLoadListener {
	private Intent mIntent;
	private GridView mGridView;
	private MyGridViewAdapter mAdapter;
	private List<JSONObject> mListData;
	private LoadingDialog dialog;
	private LinearLayout mLayout;// 加载更多
//	private int pageno = 1;
//	private String AllPage = "1";

	private AbPullToRefreshView mAbPullToRefreshView = null;
	private int num = 1;

	private ImageView iv_no;
	private TextView tv_no;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.activity_order);
		} catch (OutOfMemoryError e) {
			ImageLoader.getInstance().clearMemoryCache();
		}
		dialog = new LoadingDialog(this);
		mListData = new ArrayList<JSONObject>();
		findView();
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
		// setContentView(R.layout.view_null);
		// mIntent = null;
		// dialog = null;
		// mGridView = null;
		// mAdapter = null;
		// mListData = null;
		// mLayout = null;
		// AllPage = null;
		// this.finish();
		// System.gc();
	}

	// 查找控件
	private void findView() {
		mGridView = (GridView) findViewById(R.id.order_gridview);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));// 设置点击是背景透明
		mLayout = (LinearLayout) findViewById(R.id.layout_loading);

		iv_no = (ImageView) findViewById(R.id.iv_no);
		tv_no = (TextView) findViewById(R.id.tv_no);

		mAbPullToRefreshView = (AbPullToRefreshView) findViewById(R.id.mPullRefreshView);
		mAbPullToRefreshView.setOnHeaderRefreshListener(this);
		mAbPullToRefreshView.setOnFooterLoadListener(this);
	}

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.order_back:// 返回
			finish();
			break;
		default:
			break;
		}
	}

	// @Override
	// public void onWindowFocusChanged(boolean hasFocus) {
	// super.onWindowFocusChanged(hasFocus);
	//
	// }
	@Override
	protected void onResume() {
		super.onResume();
		// if (AllStaticMessage.OrderFormFlag) {
		// AllStaticMessage.OrderFormFlag = false;
		// if (mListData != null) {
		// mListData.clear();
		// }
		// if (mAdapter != null) {
		// mAdapter.notifyDataSetChanged();
		// }
		// pageno = 1;
		// getData();
		// }
		getData();
		MobclickAgent.onResume(this);
	}

	// 获取订单列表
	private void getData() {
		if (mLayout.getVisibility() == View.GONE) {
			dialog.loading();
		}
		num = 1;
		// String url = AllStaticMessage.URL_Order_List +
		// AllStaticMessage.User_Id
		// + "&orderState=0" + "&page=" + page;
		String url = AllStaticMessage.URL_Order + AllStaticMessage.User_Id
				+ "&orderState=0" + "&pageNum=1";
		HttpUtil.get(url, OrderActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@SuppressLint("ShowToast") @Override
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
									if (mAdapter == null) {
										mAdapter = new MyGridViewAdapter(
												mListData);
										mGridView.setVisibility(View.VISIBLE);
										mGridView.setAdapter(mAdapter);
									} else if (mAdapter != null) {
										mAdapter.notifyDataSetChanged();
										mLayout.setVisibility(View.GONE);
									}
								} else {
									tv_no.setText("暂无订单");
									mGridView.setVisibility(View.GONE);
									iv_no.setVisibility(View.VISIBLE);
									tv_no.setVisibility(View.VISIBLE);
								}
							} else {
								tv_no.setText("暂无订单");
								mGridView.setVisibility(View.GONE);
								iv_no.setVisibility(View.VISIBLE);
								tv_no.setVisibility(View.VISIBLE);
								Toast.makeText(
										OrderActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						mLayout.setVisibility(View.GONE);
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
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// 错误返回JSONObject
						if (mLayout != null) {
							mLayout.setVisibility(View.GONE);
						}
						if (dialog != null) {
							dialog.stop();
						}
					}
				});
	}

	// 获取订单列表--上拉更多
	private void getData(String num) {

		String url = AllStaticMessage.URL_Order + AllStaticMessage.User_Id
				+ "&orderState=0" + "&pageNum=" + num;
		HttpUtil.get(url, OrderActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@SuppressLint("ShowToast") @Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								JSONArray mArray = response
										.getJSONArray("Results");

								if (mArray.length() > 0) {
									for (int i = 0; i < mArray.length(); i++) {
										mListData.add(mArray.getJSONObject(i));
									}

									if (mAdapter != null) {
										mAdapter.notifyDataSetChanged();
									}
								}
							} else {
								Toast.makeText(
										OrderActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						mAbPullToRefreshView.onFooterLoadFinish();
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

	// AutoLoadCallBack callBack = new AutoLoadCallBack() {
	// public void execute(String url) {
	// if (mLayout.getVisibility() == View.GONE) {
	// if (pageno < Integer.parseInt(AllPage)) {
	// pageno++;
	// mLayout.setVisibility(View.VISIBLE);
	// new Handler().postDelayed(new Runnable() {
	// @Override
	// public void run() {
	// // getData(String.valueOf(pageno));
	// mAdapter.notifyDataSetChanged();
	// }
	// }, 1500);
	//
	// } else {
	// Toast.makeText(OrderActivity.this, "内容全部加载完毕", 100).show();
	// }
	// }
	//
	// }
	// };

	private class MyGridViewAdapter extends BaseAdapter {
		AppItem appItem;
		List<JSONObject> mData = new ArrayList<JSONObject>();
		DisplayImageOptions options;

		public MyGridViewAdapter(List<JSONObject> array) {
			this.mData = array;
			options = MyTools.createOptions(R.drawable.img);
		}

		@Override
		public int getCount() {
			return mData.size();
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
				View v = LayoutInflater.from(OrderActivity.this).inflate(
						R.layout.item_order_gridview, null);

				appItem = new AppItem();
				appItem.AppText_time = (TextView) v
						.findViewById(R.id.item_order_time);
				appItem.AppText_id = (TextView) v
						.findViewById(R.id.item_order_id);
				appItem.AppText_status = (TextView) v
						.findViewById(R.id.item_order_status);
				appItem.AppText_price = (TextView) v
						.findViewById(R.id.item_order_price);
				appItem.AppImg = (ImageView) v
						.findViewById(R.id.item_order_img);
				appItem.AppLayout = (LinearLayout) v
						.findViewById(R.id.liner_order_list);
				// appItem.AppBtn_ok=(Button) v.findViewById(R.id.btn_shouhuo);
				// appItem.AppBtn_wuliu=(Button) v.findViewById(R.id.btn_wuliu);
				appItem.AppBtn_lijizhifu = (Button) v
						.findViewById(R.id.btn_lijizhifu);
				v.setTag(appItem);
				convertView = v;
			} else {
				appItem = (AppItem) convertView.getTag();
			}
			try {
				appItem.AppText_time.setText(mData.get(position)
						.getString("AddTime").toString());
				appItem.AppText_id.setText(mData.get(position)
						.getString("OrderId").toString());
				appItem.AppLayout.setVisibility(View.VISIBLE);
				// appItem.AppBtn_ok.setVisibility(View.GONE);
				// appItem.AppBtn_wuliu.setVisibility(View.GONE);
				appItem.AppBtn_lijizhifu.setVisibility(View.GONE);

				switch (Integer.parseInt(mData.get(position)
						.getString("Status").toString())) {
				case 0:
					appItem.AppBtn_lijizhifu.setVisibility(View.VISIBLE);
					appItem.AppBtn_lijizhifu
							.setOnClickListener(new onItemClick(appItem, mData
									.get(position)));
					break;
				case 1:

				case 2:
					// appItem.AppBtn_ok.setVisibility(View.VISIBLE);
					// appItem.AppBtn_wuliu.setVisibility(View.VISIBLE);
					break;
				case 3:
				case 4:
				case 5:
					appItem.AppLayout.setVisibility(View.GONE);
					break;
				default:
					break;
				}
				if (mData.get(position).getString("Status").toString()
						.equals("1")) {
					appItem.AppText_status.setText("支付"
							+ AllStaticMessage.zhifu[Integer.parseInt(mData
									.get(position).getString("Status")
									.toString())]);
				} else {
					appItem.AppText_status
							.setText(AllStaticMessage.zhifu[Integer
									.parseInt(mData.get(position)
											.getString("Status").toString())]);
				}

				appItem.AppText_price.setText("￥"
						+ mData.get(position).getString("total").toString());
				String imgUrl = AllStaticMessage.URL_GBase + "/UsersData/"
						+ mData.get(position).getString("Account").toString()
						+ "/" + mData.get(position).getString("Img").toString()
						+ "/5.jpg";
				// imageLoader.DisplayImage(imgUrl, appItem.AppImg);
				ImageLoader.getInstance().displayImage(imgUrl, appItem.AppImg,
						options);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						mIntent = new Intent(OrderActivity.this,
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
			return convertView;
		}

	}

	class AppItem {
		TextView AppText_time;
		TextView AppText_id;
		TextView AppText_status;
		TextView AppText_price;
		ImageView AppImg;
		LinearLayout AppLayout;
		Button AppBtn_lijizhifu;// AppBtn_ok,AppBtn_wuliu,
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
				mIntent = new Intent(OrderActivity.this, MyPayActivity.class);
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

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 上拉更多
	 */
	@Override
	public void onFooterLoad(AbPullToRefreshView view) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				num++;
				getData(num + "");
			}
		}, 500);
	}

	/**
	 * 下拉刷新
	 */
	@Override
	public void onHeaderRefresh(AbPullToRefreshView view) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				getData();
				mAbPullToRefreshView.onHeaderRefreshFinish();
			}
		}, 500);
	}
}
