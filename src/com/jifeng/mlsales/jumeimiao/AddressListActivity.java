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
import com.jifeng.mlsales.model.CustomerAlertDialog;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.ShrefUtil;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddressListActivity extends Activity {
	private Intent mIntent;
	private MyGridViewAdapter mAdapter;
	private GridView mGridView;
	LoadingDialog dialog;
	private ShrefUtil mShrefUtil;
	private List<JSONObject> mJsonObjects;
	private TextView tv_no;
	private LinearLayout no_ll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_list);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		dialog.loading();
		mShrefUtil = new ShrefUtil(this, "data");
		mJsonObjects = new ArrayList<JSONObject>();
		findView();
		getData();
	}

	// 查找控件
	private void findView() {
		mGridView = (GridView) findViewById(R.id.address_list_gridview);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));// 设置点击是背景透明

		no_ll = (LinearLayout) findViewById(R.id.no_ll);
		tv_no = (TextView) findViewById(R.id.tv_no);
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
		// mIntent = null;
		// dialog = null;
		// mAdapter = null;
		// mGridView = null;
		// mShrefUtil = null;
		// mJsonObjects = null;
		// setContentView(R.layout.view_null);
		// this.finish();
		// System.gc();
	}

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.address_list_back:// 返回
			finish();
			break;
		case R.id.address_list_btn_new:
		case R.id.jiesuan_btn_select_address:
			mIntent = new Intent(AddressListActivity.this,
					NewCreateAddressActivity.class);
			mIntent.putExtra("data", "");
			startActivity(mIntent);
			break;
		default:
			break;
		}
	}

	private void getData() {
		String url = AllStaticMessage.URL_Get_AddressList
				+ AllStaticMessage.User_Id;
		HttpUtil.get(url, AddressListActivity.this, dialog,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								mGridView.setVisibility(View.VISIBLE);
								no_ll.setVisibility(View.GONE);
								JSONArray mArray = response
										.getJSONArray("Results");
								if (mJsonObjects != null) {
									mJsonObjects.clear();
								}
								if (mAdapter != null) {
									mAdapter.notifyDataSetChanged();
								}
								for (int i = 0; i < mArray.length(); i++) {
									mJsonObjects.add(mArray.getJSONObject(i));
								}
								if (mAdapter == null) {
									mAdapter = new MyGridViewAdapter(
											mJsonObjects,
											AddressListActivity.this);
									mGridView.setAdapter(mAdapter);
								} else {
									mAdapter.notifyDataSetChanged();
								}

							} else {
								mGridView.setVisibility(View.GONE);
								if (response.getString("Results").toString()
										.equals("暂无数据")) {
									AllStaticMessage.JieSuan_Select_Address = true;
									AllStaticMessage.mJsonObject_select_address = null;
									mShrefUtil.write("songhuo_address", "");
								}
								tv_no.setText("暂无收货地址");
								no_ll.setVisibility(View.VISIBLE);
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
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// 错误返回JSONObject
						if (dialog != null) {
							dialog.stop();
						}
					}
				});
	}

	private void deleteData(final String id) {
		dialog.loading();
		String url = AllStaticMessage.URL_Delete_Address
				+ AllStaticMessage.User_Id + "&addressId=" + id;// AllStaticMessage.User_Id

		HttpUtil.get(url, AddressListActivity.this, dialog,
				new JsonHttpResponseHandler() {

					@SuppressLint("ShowToast")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								if (!mShrefUtil.readString("songhuo_address")
										.toString().equals("")) {
									AllStaticMessage.mJsonObject_select_address = new JSONObject(
											mShrefUtil.readString(
													"songhuo_address")
													.toString());
									if (AllStaticMessage.mJsonObject_select_address
											.getString("Id").equals(id)) {
										AllStaticMessage.JieSuan_Select_Address = true;
										AllStaticMessage.mJsonObject_select_address = null;
										mShrefUtil.write("songhuo_address", "");
									}
								}
								for (int i = 0; i < mJsonObjects.size(); i++) {
									if (mJsonObjects.get(i).getString("Id")
											.toString().equals(id)) {
										mJsonObjects.remove(i);
									}
								}

								if (mAdapter != null) {
									mAdapter.notifyDataSetChanged();
								}
								Toast.makeText(
										AddressListActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							} else {
								Toast.makeText(
										AddressListActivity.this,
										response.getString("Results")
												.toString(), 500).show();
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
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// 错误返回JSONObject
						if (dialog != null) {
							dialog.stop();
						}
					}
				});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (AllStaticMessage.AddressListFlag) {
			AllStaticMessage.AddressListFlag = false;
			getData();
		}
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private class MyGridViewAdapter extends BaseAdapter {
		AppItem appItem;
		List<JSONObject> mObjects;

		// Context mContext;

		public MyGridViewAdapter(List<JSONObject> array, Context context) {
			mObjects = new ArrayList<JSONObject>();
			this.mObjects = array;
			// this.mContext = context;
		}

		@Override
		public int getCount() {
			return mObjects.size();
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
				View v = LayoutInflater.from(AddressListActivity.this).inflate(
						R.layout.item_address_list, null);

				appItem = new AppItem();
				appItem.AppText_name = (TextView) v
						.findViewById(R.id.item_addresslist_name);
				appItem.AppText_phone = (TextView) v
						.findViewById(R.id.item_addresslist_phone);
				appItem.AppText_address = (TextView) v
						.findViewById(R.id.item_addresslist_address);
				appItem.AppLayout_bianji = (RelativeLayout) v
						.findViewById(R.id.item_addresslist_rel_bianji);
				appItem.AppLayout_delete = (RelativeLayout) v
						.findViewById(R.id.item_addresslist_rel_delete);
				appItem.AppImg_moren = (ImageView) v
						.findViewById(R.id.address_list_moren);
				v.setTag(appItem);
				convertView = v;
			} else {
				appItem = (AppItem) convertView.getTag();
			}
			try {
				if (mShrefUtil.readString("songhuo_address") == null) {

				} else {
					if (!mShrefUtil.readString("songhuo_address").equals("")) {
						AllStaticMessage.mJsonObject_select_address = new JSONObject(
								mShrefUtil.readString("songhuo_address")
										.toString());
						if (AllStaticMessage.mJsonObject_select_address
								.getString("Id").equals(
										mObjects.get(position).getString("Id")
												.toString())) {
							appItem.AppImg_moren.setVisibility(View.VISIBLE);
						} else {
							appItem.AppImg_moren.setVisibility(View.GONE);
						}
					}
				}
				appItem.AppText_name.setText(mObjects.get(position)
						.getString("TrueName").toString());
				appItem.AppText_phone.setText(mObjects.get(position)
						.getString("PhoneTel").toString());
				appItem.AppText_address.setText(mObjects.get(position)
						.getString("Province").toString()
						+ mObjects.get(position).getString("City").toString()
						+ mObjects.get(position).getString("Country")
								.toString()
						+ mObjects.get(position).getString("DetailAddress")
								.toString());
				appItem.AppLayout_bianji.setOnClickListener(new ItemClick(
						appItem, position, "bianji", mObjects.get(position)));
				appItem.AppLayout_delete.setOnClickListener(new ItemClick(
						appItem, position, "delete", mObjects.get(position)));

			} catch (JSONException e) {
				e.printStackTrace();
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = getIntent();
					if (intent != null) {
						if (intent.getStringExtra("flag").equals("jiesuan")) {

							AllStaticMessage.JieSuan_Select_Address = true;
							AllStaticMessage.mJsonObject_select_address = mObjects
									.get(position);
							setResult(RESULT_CANCELED);
							finish();
						}
					}
				}
			});
			return convertView;
		}
	}

	class AppItem {
		TextView AppText_name;
		TextView AppText_phone;
		TextView AppText_address;
		RelativeLayout AppLayout_bianji;
		RelativeLayout AppLayout_delete;
		ImageView AppImg_moren;
	}

	class ItemClick implements OnClickListener {
		AppItem appItem;
		int position;
		String flag;
		JSONObject mArray;

		// .getString("Id").toString()
		public ItemClick(AppItem appItem, int position, String flag,
				JSONObject mArray) {
			this.appItem = appItem;
			this.position = position;
			this.flag = flag;
			this.mArray = mArray;
		}

		@Override
		public void onClick(View v) {
			if (flag.equals("delete")) {
				final CustomerAlertDialog alertDialog = new CustomerAlertDialog(
						AddressListActivity.this, false);
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
						try {
							deleteData(mArray.getString("Id").toString());
							alertDialog.dismiss();
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				});
				// Builder builder = new Builder(AddressListActivity.this);
				// builder.setTitle("确定移除嘛？");
				// builder.setPositiveButton("确定",
				// new DialogInterface.OnClickListener() {
				// @Override
				// public void onClick(DialogInterface dialog,
				// int which) {
				// try {
				// deleteData(mArray.getString("Id")
				// .toString());
				// } catch (JSONException e) {
				// e.printStackTrace();
				// }
				// }
				// });
				// builder.setNegativeButton("取消", null);
				// builder.create().show();

			} else if (flag.equals("bianji")) {
				mIntent = new Intent(AddressListActivity.this,
						NewCreateAddressActivity.class);
				mIntent.putExtra("data", mArray.toString());
				startActivity(mIntent);
			}

		}
	}

}
