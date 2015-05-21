package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.R;
import com.jifeng.mlsales.model.CustomerAlertDialog;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.myview.My_ListView;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderDetailActivity extends Activity {
	private Intent mIntent;
	private MyListViewAdapter mAdapter;
	private My_ListView mListView;
	private List<JSONObject> mListData;
	private TextView[] mText;
	private int[] mTextId = { R.id.orderDetail_1, R.id.orderDetail_2,
			R.id.orderDetail_3, R.id.orderDetail_4, R.id.orderDetail_5,
			R.id.orderDetail_6, R.id.orderDetail_7, R.id.orderDetail_8,
			R.id.orderDetail_9, R.id.orderDetail_10, R.id.orderDetail_11 };
	private TextView mText_AllPrice, mText_JieYuePrice;
	private LoadingDialog dialog;
	private Button mButton_zhifu, mBtn_QuXiao_Order;
	private boolean tuihuoFlag = false;
	private String payway, allprice, zhifuFlag = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_orderdetail);
		dialog = new LoadingDialog(this);
		mListData = new ArrayList<JSONObject>();
		findView();
		if (getIntent().getStringExtra("id") != null
				|| !getIntent().getStringExtra("id").equals("")) {
			getData(getIntent().getStringExtra("id"));
		}

	}

	// 查找控件
	private void findView() {
		mListView = (My_ListView) findViewById(R.id.orderdetail_list);
		mText = new TextView[mTextId.length];
		for (int i = 0; i < mTextId.length; i++) {
			mText[i] = (TextView) findViewById(mTextId[i]);
		}
		mText_AllPrice = (TextView) findViewById(R.id.orderdetail_all_price);
		mText_JieYuePrice = (TextView) findViewById(R.id.orderdetail_jieyue_price);
		mButton_zhifu = (Button) findViewById(R.id.orderdetail_btn_lijizhifu);
		mBtn_QuXiao_Order = (Button) findViewById(R.id.orderdetail_btn_quxiao_order);
		ScrollView mScrollView = (ScrollView) findViewById(R.id.orderDetail_scrollview);
		mScrollView.smoothScrollTo(0, 20);
		mListView.setFocusable(false);
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
		// setContentView(R.layout.view_null);
		// mIntent = null;
		// dialog = null;
		// mAdapter = null;
		// mListData = null;
		// mAdapter = null;
		// mListView = null;
		// mListData = null;
		// mText = null;
		// mTextId = null;
		// mText_AllPrice = null;
		// mText_JieYuePrice = null;
		// mButton_zhifu = null;
		// mBtn_QuXiao_Order = null;
		// payway = null;
		// allprice = null;
		// this.finish();
		// System.gc();
	}

	// xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.orderdetail_back:// 返回
			finish();
			break;
		case R.id.orderdetail_btn_lijizhifu:
			if (zhifuFlag.equals("0")) {
				mIntent = new Intent(OrderDetailActivity.this,
						MyPayActivity.class);
				mIntent.putExtra("allprice", allprice);
				mIntent.putExtra("payway", payway);
				mIntent.putExtra("orderid", getIntent().getStringExtra("id"));
				startActivity(mIntent);
			} else {
				Toast.makeText(OrderDetailActivity.this, "订单已支付", 500).show();
			}

			break;
		case R.id.orderdetail_btn_quxiao_order:
			// Builder builder = new Builder(OrderDetailActivity.this);
			// builder.setTitle("确定移除嘛？");
			// builder.setPositiveButton("确定",
			// new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// cancelOrder(getIntent().getStringExtra("id"));
			// }
			// });
			// builder.setNegativeButton("取消", null);
			// builder.create().show();

			final CustomerAlertDialog alertDialog = new CustomerAlertDialog(
					OrderDetailActivity.this, false);
			alertDialog.setTitle("确定移除嘛？");
			alertDialog.setPositiveButton("取消", new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					alertDialog.dismiss();
				}
			});
			alertDialog.setNegativeButton1("确定", new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					cancelOrder(getIntent().getStringExtra("id"));
					alertDialog.dismiss();
				}
			});

			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (AllStaticMessage.orderDetailFlag) {
			AllStaticMessage.orderDetailFlag = false;
			if (mListData != null) {
				mListData.clear();
			}
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
			getData(getIntent().getStringExtra("id"));
		}
		MobclickAgent.onResume(this);
	}

	/***
	 * 获取订单详情
	 */
	private void getData(String orderId) {
		dialog.loading();
		String url = AllStaticMessage.URL_Order_Deatil
				+ AllStaticMessage.User_Id + "&orderId=" + orderId;
		HttpUtil.get(url, OrderDetailActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								if (response.getString("OrderState").toString()
										.equals("1")) {
									mText[0].setText("支付"
											+ AllStaticMessage.zhifu[Integer
													.parseInt(response
															.getString(
																	"OrderState")
															.toString())]);
								} else {
									mText[0].setText(AllStaticMessage.zhifu[Integer
											.parseInt(response.getString(
													"OrderState").toString())]);
								}

								zhifuFlag = response.getString("OrderState")
										.toString();
								if (response.getString("OrderState").toString()
										.equals("0")) {
									((RelativeLayout) OrderDetailActivity.this
											.findViewById(R.id.orderdetail_botton))
											.setVisibility(View.VISIBLE);
									mBtn_QuXiao_Order
											.setVisibility(View.VISIBLE);
									mText_AllPrice.setText("￥"
											+ response
													.getJSONObject("PriceInfo")
													.getString("orderTotal")
													.toString());
									mText_JieYuePrice.setText("￥"
											+ response
													.getJSONObject("PriceInfo")
													.getString("SaveMoney")
													.toString());
								} else {
									mBtn_QuXiao_Order
											.setVisibility(View.INVISIBLE);
								}
								mText[1].setText(response.getString("PayWay")
										.toString());
								if (response.getString("PayWay").toString()
										.equals("支付宝支付")) {
									payway = "zfb";
								} else {
									payway = "wx";
								}
								mText[2].setText(response.getString(
										"CreateDate").toString());
								mText[3].setText(response.getString("OrderId")
										.toString());

								mText[4].setText(response
										.getJSONObject("Receiving")
										.getString("userName").toString());
								mText[5].setText(response
										.getJSONObject("Receiving")
										.getString("Tel").toString());
								mText[6].setText(response
										.getJSONObject("Receiving")
										.getString("Address").toString());
								mText[7].setText(response
										.getJSONObject("Receiving")
										.getString("getGoodsTime").toString());

								mText[8].setText("￥"
										+ response.getJSONObject("PriceInfo")
												.getString("Total").toString());

								mText[9].setText("￥"
										+ response.getJSONObject("PriceInfo")
												.getString("Feight").toString());
								mText[10].setText("￥"
										+ response.getJSONObject("PriceInfo")
												.getString("orderTotal")
												.toString());
								allprice = response.getJSONObject("PriceInfo")
										.getString("orderTotal").toString();
								JSONArray mArray = response
										.getJSONArray("Results");
								if (mArray.length() > 0) {
									for (int i = 0; i < mArray.length(); i++) {
										mListData.add(mArray.getJSONObject(i));
									}

									mAdapter = new MyListViewAdapter(mListData);
									mListView.setAdapter(mAdapter);
									// MyTools.setListViewHeightBasedOnChildren(mListView,
									// mAdapter);
								}

							} else {
								Toast.makeText(
										OrderDetailActivity.this,
										response.getString("Results")
												.toString(), 500).show();
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

	/***
	 * 取消订单
	 * 
	 */
	private void cancelOrder(String orderId) {
		dialog.loading();
		String url = AllStaticMessage.URL_QuXiao_Order
				+ AllStaticMessage.User_Id + "&orderId=" + orderId;
		HttpUtil.get(url, OrderDetailActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								AllStaticMessage.OrderFormFlag = true;
								Toast.makeText(
										OrderDetailActivity.this,
										response.getString("Results")
												.toString(), 500).show();
								finish();
							} else {
								Toast.makeText(
										OrderDetailActivity.this,
										response.getString("Results")
												.toString(), 500).show();
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

	/***
	 * 确认收货
	 */
	private void querenOrder(String orderId, String goodsid) {
		dialog.loading();
		String url = AllStaticMessage.URL_ok_Order + AllStaticMessage.User_Id
				+ "&orderId=" + orderId + "&productId=" + goodsid;
		HttpUtil.get(url, OrderDetailActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								AllStaticMessage.OrderFormFlag = true;
								if (mListData != null) {
									mListData.clear();
								}
								if (mAdapter != null) {
									mAdapter.notifyDataSetChanged();
								}
								getData(getIntent().getStringExtra("id"));
								Toast.makeText(
										OrderDetailActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							} else {
								Toast.makeText(
										OrderDetailActivity.this,
										response.getString("Results")
												.toString(), 500).show();
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
		List<JSONObject> mData;
		DisplayImageOptions options;

		private MyListViewAdapter(List<JSONObject> listData) {
			mListData = new ArrayList<JSONObject>();
			this.mData = listData;
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
				View v = LayoutInflater.from(OrderDetailActivity.this).inflate(
						R.layout.item_orderdetail_listview, null);
				appItem = new AppItem();
				appItem.AppText_name = (TextView) v
						.findViewById(R.id.item_detail_name);
				appItem.AppText_comefrom = (TextView) v
						.findViewById(R.id.item_detail_comefrom);
				appItem.AppText_size = (TextView) v
						.findViewById(R.id.item_detail_size);
				appItem.AppText_price = (TextView) v
						.findViewById(R.id.item_detail_price);
				appItem.AppText_num = (TextView) v
						.findViewById(R.id.item_detail_num);
				appItem.AppImg = (ImageView) v
						.findViewById(R.id.item_detail_img);
				appItem.AppBtn_TuiHuo = (Button) v
						.findViewById(R.id.btn_shenqingtuihuo);
				appItem.AppBtn_Wuliu = (Button) v
						.findViewById(R.id.btn_orderdetail_wuliu);
				appItem.AppBtn_ok_shouhuo = (Button) v
						.findViewById(R.id.btn_orderdetail_shouhuo);
				v.setTag(appItem);
				convertView = v;
			} else {
				appItem = (AppItem) convertView.getTag();
			}
			try {
				// appItem.AppBtn_TuiHuo.setVisibility(View.GONE);
				appItem.AppText_name.setText(mData.get(position)
						.getString("PName").toString());
				appItem.AppText_comefrom.setText(mData.get(position)
						.getString("ActiveName").toString());
				appItem.AppText_size.setText(mData.get(position)
						.getString("SpecName").toString());
				appItem.AppText_price.setText("￥"
						+ mData.get(position).getString("Price").toString());
				appItem.AppText_num.setText("x"
						+ mData.get(position).getString("Quantity").toString());
				String imgUrl = AllStaticMessage.URL_GBase + "/UsersData/"
						+ mData.get(position).getString("Account").toString()
						+ "/" + mData.get(position).getString("Img").toString()
						+ "/5.jpg";
				ImageLoader.getInstance().displayImage(imgUrl, appItem.AppImg,
						options);
				int num = Integer.parseInt(mData.get(position)
						.getString("State").toString());
				if (num > 0 && num < 3) {
					appItem.AppBtn_TuiHuo.setVisibility(View.VISIBLE);
					appItem.AppBtn_TuiHuo.setOnClickListener(new onItemClick(
							appItem, mData.get(position), "tuikuang"));
				}
				if (num > 1 && num <= 6) {
					appItem.AppBtn_Wuliu.setVisibility(View.VISIBLE);
					appItem.AppBtn_Wuliu.setOnClickListener(new onItemClick(
							appItem, mData.get(position), "wuliu"));
				}
				switch (num) {
				case 0:

					break;
				case 1:

					break;
				case 2:
					appItem.AppBtn_ok_shouhuo.setVisibility(View.VISIBLE);
					appItem.AppBtn_ok_shouhuo
							.setOnClickListener(new onItemClick(appItem, mData
									.get(position), "shouhuo"));
					break;
				case 3:
					// appItem.AppBtn_Wuliu.setVisibility(View.VISIBLE);
					// appItem.AppBtn_ok_shouhuo.setVisibility(View.VISIBLE);
					break;
				case 4:
					// appItem.AppBtn_Wuliu.setVisibility(View.VISIBLE);
					appItem.AppBtn_TuiHuo.setText("  退款中  ");
					appItem.AppBtn_TuiHuo.setVisibility(View.VISIBLE);
					break;
				case 5:

					break;
				case 6:
					appItem.AppBtn_TuiHuo.setText("退款(货)成功");
					appItem.AppBtn_TuiHuo.setVisibility(View.VISIBLE);
					break;
				default:
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// appItem.AppImg
			convertView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent mIntent = new Intent(OrderDetailActivity.this,
							GoodsDetailActivity.class);

					try {
						mIntent.putExtra("pid",
								mData.get(position).getString("ActiveId")
										.toString());

						mIntent.putExtra("guigeid", mData.get(position)
								.getString("Pid").toString());
						mIntent.putExtra("goodsid", mData.get(position)
								.getString("GoodsId").toString());// 商品id
						mIntent.putExtra("imgurl", AllStaticMessage.URL_GBase
								+ "/UsersData/"
								+ mData.get(position).getString("Account")
										.toString()
								+ "/"
								+ mData.get(position).getString("Img")
										.toString() + "/5.jpg");
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
		TextView AppText_name;
		TextView AppText_comefrom;
		TextView AppText_size;
		TextView AppText_num;
		TextView AppText_price;
		ImageView AppImg;
		Button AppBtn_TuiHuo, AppBtn_Wuliu, AppBtn_ok_shouhuo;
	}

	class onItemClick implements OnClickListener {
		AppItem appItem;
		JSONObject jsonObject;
		String flag;

		public onItemClick(AppItem appIte, JSONObject positio, String flag) {
			this.appItem = appIte;
			this.jsonObject = positio;
			this.flag = flag;
		}

		@Override
		public void onClick(View v) {
			// 申请退货
			try {
				if (flag.equals("tuikuang")) {
					mIntent = new Intent(OrderDetailActivity.this,
							TuiKuangActivity.class);
					mIntent.putExtra("orderId", getIntent()
							.getStringExtra("id"));
					mIntent.putExtra("goodsId",
							jsonObject.getString("ProductId").toString());
					mIntent.putExtra("price", jsonObject.getString("Price")
							.toString());
					startActivity(mIntent);
				} else if (flag.equals("wuliu")) {
					mIntent = new Intent(OrderDetailActivity.this,
							WuLiuActivity.class);
					String url = AllStaticMessage.URL_WuLiu
							+ jsonObject.getString("CourierName").toString()
							+ "&postid="
							+ jsonObject.getString("CourierNo").toString()
							+ "&callbackurl=http://www.jumeimiao.com#result";
					mIntent.putExtra("wuliuurl", url);
					startActivity(mIntent);
				} else {
					querenOrder(getIntent().getStringExtra("id"), jsonObject
							.getString("ProductId").toString());
				}

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
}
