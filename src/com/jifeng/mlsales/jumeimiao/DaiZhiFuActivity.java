package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.FBApplication;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DaiZhiFuActivity extends Activity {
	private Intent mIntent;
	private GridView mGridView;
	private MyGridViewAdapter mAdapter;
	private TextView mText_Title;
	private String orderState;// 1 ������ 2������
	private List<JSONObject> mData;
	private LoadingDialog dialog;

	private LinearLayout ll_no;
	private MyGalleryAdapter gAdapter;
	private List<JSONObject> mListData;
	private Button bt_stroll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daizhifu);
		((FBApplication) getApplication()).addActivity(this);
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

	// ���ҿؼ�
	private void findView() {
		mGridView = (GridView) findViewById(R.id.daizhifu_gridview);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));// ���õ���Ǳ���͸��
		mText_Title = (TextView) findViewById(R.id.textview_title);

		ll_no = (LinearLayout) findViewById(R.id.ll_no);
		bt_stroll = (Button) findViewById(R.id.bt_stroll);
		bt_stroll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(DaiZhiFuActivity.this,
						TabHostActivity.class));
				finish();
			}
		});

		mText_Title.setText(getIntent().getStringExtra("title").toString());
		if (getIntent().getStringExtra("title").toString().equals("�������")) {
			orderState = "1";
		} else {
			orderState = "2";
		}
	}

	// //xmlע�����¼���ʵ��
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.daizhifu_back:// ����
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
						// �ɹ�����JSONObject
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

									mGridView.setVisibility(View.GONE);
									ll_no.setVisibility(View.VISIBLE);
								}
							} else {
								mGridView.setVisibility(View.GONE);
								ll_no.setVisibility(View.VISIBLE);
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
						// ����ʼ
					}

					@Override
					public void onFinish() {
						super.onFinish();
						// �������
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// ���󷵻�JSONObject
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
				// // �½�һ��GridView
				// appItem.my_GridView = new My_GridView(DaiZhiFuActivity.this);
				// // �����ڲ�����Ŀ�Ŀ��
				// appItem.my_GridView.setColumnWidth(itemWidth);
				// // �����ڲ�����Ŀ����Ϊ�Զ���Ӧ
				// //appItem.my_GridView.setNumColumns(GridView.AUTO_FIT);
				// // ����GravityΪCenter
				// // ����SelectorΪ͸��
				// appItem.my_GridView.setSelector(new ColorDrawable(
				// Color.TRANSPARENT));
				// appItem.my_GridView.setStretchMode(GridView.NO_STRETCH);
				// //appItem.my_GridView.setNumColumns(100);
				//
				// appItem.my_GridView.setHorizontalSpacing(10);
				// LayoutParams layoutParams = new LayoutParams(
				// gridviewWidth, LayoutParams.WRAP_CONTENT);
				// // ����GridView��LayoutParamsΪ����Ŀ�Ŀ�ȳ�����Ŀ����
				// appItem.my_GridView.setLayoutParams(layoutParams);
				//
				// LinearLayout categoryLayout = (LinearLayout)
				// v.findViewById(R.id.category_layout);
				// // ���½���GridView��ӵ�������
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
				appItem.AppText_price.setText("��"
						+ mListData.get(position).get("total").toString());
				if (orderState.equals("1")) {
					appItem.AppText_status.setText("������");
					appItem.AppmLayout.setVisibility(View.VISIBLE);
					appItem.AppImg_zhifu.setOnClickListener(new onItemClick(
							appItem, mListData.get(position)));
				} else {
					appItem.AppText_status.setText("������");
					appItem.AppmLayout.setVisibility(View.GONE);
				}
				// ����ͼƬ
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

	private class AppItem {
		private RelativeLayout AppmLayout;
		private TextView AppText_time;
		private TextView AppText_status;
		private TextView AppText_price;
		private ImageView AppImg, AppImg_zhifu;
		private My_GridView my_GridView;
	}

	private class onItemClick implements OnClickListener {
		JSONObject jsonObject;

		private onItemClick(AppItem appIte, JSONObject paywa) {
			this.jsonObject = paywa;
		}

		@Override
		public void onClick(View v) {
			// ����֧��
			try {
				mIntent = new Intent(DaiZhiFuActivity.this, MyPayActivity.class);
				mIntent.putExtra("allprice", jsonObject.getString("total"));
				if (jsonObject.getString("PayWay").contains("֧����")) {
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

	private class MyGalleryAdapter extends BaseAdapter {
		private DisplayImageOptions options;
		private List<JSONObject> mListData;

		private MyGalleryAdapter(List<JSONObject> mListData) {
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
}
