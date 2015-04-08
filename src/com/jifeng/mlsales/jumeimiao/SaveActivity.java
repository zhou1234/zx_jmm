package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.adapter.MySaveAdapter;
//import com.jifeng.image.ImageLoader;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.AutoLoadListener;
import com.jifeng.tools.MyTools;
import com.jifeng.tools.AutoLoadListener.AutoLoadCallBack;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SaveActivity extends Activity {
	private GridView mGridView_1, mGridView_2;
	private MyGridViewAdapter mAdapter;
	private Button mButton_1, mButton_2;
	private boolean showFlag = false;
	// 屏幕的高度
	int height, width;
	public List<JSONObject> mListData_1, mListData_2;
	private LoadingDialog dialog;
	private boolean firstFlag = true;
	public MySaveAdapter adapter;
	private LinearLayout mLayout;
	private int pagegoods = 1, pagebrand = 1;
	private String AllPage_goods = "1", allPage_brand = "1";
	private boolean goodsOrbrand = true;
	Button mBtn_Bianji;

	private ImageView iv_no;
	private TextView tv_no;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0x01:
				getGoddsSave("1", String.valueOf(pagegoods));
				break;
			case 0x02:
				mListData_2.remove(adapter.potion);
				adapter.notifyDataSetChanged();
				if (mListData_2.size() == 0) {
					mGridView_2.setAdapter(adapter);
					mGridView_2.setVisibility(View.GONE);
				}
				getGoddsSave("2", String.valueOf(pagebrand));
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save);
		dialog = new LoadingDialog(this);
		dialog.loading();
		mListData_1 = new ArrayList<JSONObject>();
		mListData_2 = new ArrayList<JSONObject>();
		initData();
		findView();
		handler.sendEmptyMessage(0x01);
		register();
		WindowManager manager = getWindowManager();
		height = manager.getDefaultDisplay().getHeight();
		width = manager.getDefaultDisplay().getWidth();
	}

	// 查找控件
	private void findView() {
		mGridView_1 = (GridView) findViewById(R.id.save_gridview_1);
		mGridView_1.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mGridView_2 = (GridView) findViewById(R.id.save_gridview_2);
		mGridView_2.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mButton_1 = (Button) findViewById(R.id.save_btn_1);
		mButton_2 = (Button) findViewById(R.id.save_btn_2);
		mLayout = (LinearLayout) findViewById(R.id.layout_loading);
		mBtn_Bianji = (Button) findViewById(R.id.save_bianji);

		iv_no = (ImageView) findViewById(R.id.iv_no);
		tv_no = (TextView) findViewById(R.id.tv_no);
	}

	// 注册事件
	private void register() {

	}

	/*
	 * 初始化数据
	 */
	private void initData() {

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		ShareSDK.stopSDK(this);
		setContentView(R.layout.view_null);
		super.onDestroy();
		mGridView_1 = null;
		mGridView_2 = null;
		mAdapter = null;
		mButton_1 = null;
		mButton_2 = null;
		dialog = null;
		adapter = null;
		mLayout = null;
		AllPage_goods = null;
		allPage_brand = null;
		;
		mBtn_Bianji = null;
		handler = null;
		this.finish();
		System.gc();
	}

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.save_back:// 返回
			finish();
			break;
		case R.id.save_btn_1:
			dialog = new LoadingDialog(this);
			dialog.loading();
			if (mBtn_Bianji.getText().toString().equals("完成")) {
				Toast.makeText(SaveActivity.this, "请先完成编辑", 500).show();
				return;
			}
			showFlag = false;
			// mGridView_1.setVisibility(View.VISIBLE);
			// mGridView_2.setVisibility(View.GONE);
			setView(1);
			Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
					this, R.anim.in_leftright);
			mGridView_1.startAnimation(hyperspaceJumpAnimation);
			Animation hyperspaceJumpAnimation_1 = AnimationUtils.loadAnimation(
					this, R.anim.out_leftright);
			mGridView_2.startAnimation(hyperspaceJumpAnimation_1);
			goodsOrbrand = true;
			break;
		case R.id.save_btn_2:
			dialog = new LoadingDialog(this);
			dialog.loading();
			if (mBtn_Bianji.getText().toString().equals("完成")) {
				Toast.makeText(SaveActivity.this, "请先完成编辑", 500).show();
				return;
			}
			// mGridView_1.setVisibility(View.GONE);
			// mGridView_2.setVisibility(View.VISIBLE);
			setView(2);
			Animation hyperspaceJumpAnimation_2 = AnimationUtils.loadAnimation(
					this, R.anim.in_rightleft);
			mGridView_2.startAnimation(hyperspaceJumpAnimation_2);
			Animation hyperspaceJumpAnimatio_2 = AnimationUtils.loadAnimation(
					this, R.anim.out_rightleft);
			mGridView_1.startAnimation(hyperspaceJumpAnimatio_2);

			goodsOrbrand = false;
			break;
		case R.id.save_bianji:
			if (mBtn_Bianji.getText().toString().equals("编辑")) {
				if (goodsOrbrand) {
					if (mListData_1 != null && mAdapter != null) {
						mBtn_Bianji.setText("完成");
						mAdapter.initDate(mListData_1, true);
						mAdapter.notifyDataSetChanged();
					}
				} else {
					if (mListData_2 != null && adapter != null) {
						mBtn_Bianji.setText("完成");
						adapter.initDate(mListData_2, true);
						adapter.notifyDataSetChanged();
					}

				}
			} else {
				if (goodsOrbrand) {
					if (mListData_1 != null && mAdapter != null) {
						mBtn_Bianji.setText("编辑");
						mAdapter.initDate(mListData_1, false);
						mAdapter.notifyDataSetChanged();
					}

				} else {
					if (mListData_2 != null && adapter != null) {
						mBtn_Bianji.setText("编辑");
						adapter.initDate(mListData_2, false);
						adapter.notifyDataSetChanged();
					}
				}
			}
			break;
		default:
			break;
		}
	}

	private void setView(int i) {
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.transparent);
		mButton_1.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
				drawable);
		mButton_2.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
				drawable);
		mButton_1.setTextColor(getResources().getColor(R.color.text_color));
		mButton_2.setTextColor(getResources().getColor(R.color.text_color));
		Drawable mdrawable = this.getResources().getDrawable(
				R.drawable.save_xian_2);
		switch (i) {
		case 1:
			getGoddsSave("1", String.valueOf(pagegoods));
			mButton_1.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					mdrawable);
			mButton_1.setTextColor(getResources().getColor(R.color.tab_select));
			break;
		case 2:
			getGoddsSave("2", String.valueOf(pagebrand));
			mButton_2.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					mdrawable);
			mButton_2.setTextColor(getResources().getColor(R.color.tab_select));
			break;
		default:
			break;
		}
	}

	private void getGoddsSave(final String type, String page) {
		String url = AllStaticMessage.URL_Save_List + AllStaticMessage.User_Id
				+ "&type=" + type + "&page=" + page;

		HttpUtil.get(url, SaveActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								if (type.equals("1")) {
									mGridView_1.setVisibility(View.VISIBLE);
									mGridView_2.setVisibility(View.GONE);
									iv_no.setVisibility(View.GONE);
									tv_no.setVisibility(View.GONE);
									JSONArray mArray_Goods = response
											.getJSONArray("Results");
									mListData_1.clear();
									// Log.i("11111", mArray_Goods.toString());
									for (int i = 0; i < mArray_Goods.length(); i++) {
										mListData_1.add(mArray_Goods
												.getJSONObject(i));
									}
									if (mAdapter == null) {
										// AutoLoadListener autoLoadListener =
										// new AutoLoadListener(
										// callBack_goods);
										// if (mListData_1.size() > 0) {
										// mGridView_1
										// .setOnScrollListener(autoLoadListener);
										// }
										mAdapter = new MyGridViewAdapter(
												mListData_1);
										mAdapter.initDate(mListData_1, false);
										mBtn_Bianji.setText("编辑");
										mGridView_1.setAdapter(mAdapter);
									} else {
										mAdapter.initDate(mListData_1, false);
										mBtn_Bianji.setText("编辑");
										mAdapter.notifyDataSetChanged();
										mLayout.setVisibility(View.GONE);
									}
								} else {
									JSONArray mArray_Brand = response
											.getJSONArray("Results");
									mGridView_1.setVisibility(View.GONE);
									mGridView_2.setVisibility(View.VISIBLE);
									iv_no.setVisibility(View.GONE);
									tv_no.setVisibility(View.GONE);
									mListData_2.clear();
									// Log.i("11111", mArray_Brand.toString());
									for (int i = 0; i < mArray_Brand.length(); i++) {
										mListData_2.add(mArray_Brand
												.getJSONObject(i));
									}
									if (adapter == null) {
										// AutoLoadListener autoLoadListener =
										// new AutoLoadListener(
										// callBack_brand);
										// if (mListData_2.size() > 0) {
										// mGridView_2
										// .setOnScrollListener(autoLoadListener);
										// }
										adapter = new MySaveAdapter(
												SaveActivity.this, height,
												width, mListData_2, handler);
										adapter.initDate(mListData_2, false);
										mBtn_Bianji.setText("编辑");
										mGridView_2.setAdapter(adapter);

									} else {
										adapter.initDate(mListData_2, false);
										mBtn_Bianji.setText("编辑");
										adapter.notifyDataSetChanged();
										mLayout.setVisibility(View.GONE);
									}

								}

							} else {
								mGridView_1.setVisibility(View.GONE);
								mGridView_2.setVisibility(View.GONE);
								tv_no.setText("暂无收藏");
								iv_no.setVisibility(View.VISIBLE);
								tv_no.setVisibility(View.VISIBLE);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						mLayout.setVisibility(View.GONE);
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
						mLayout.setVisibility(View.GONE);
						dialog.stop();
					}
				});

	}

	// AutoLoadCallBack callBack_goods = new AutoLoadCallBack() {
	// public void execute(String url) {
	// if (mLayout.getVisibility() == View.GONE) {
	// if (pagegoods < Integer.parseInt(AllPage_goods)) {
	// pagegoods++;
	// mLayout.setVisibility(View.VISIBLE);
	// new Handler().postDelayed(new Runnable() {
	// @Override
	// public void run() {
	// getGoddsSave("1", String.valueOf(pagegoods));
	// mAdapter.notifyDataSetChanged();
	// }
	// }, 1500);
	// } else {
	// Toast.makeText(SaveActivity.this, "内容全部加载完毕", 100).show();
	// }
	// }
	//
	// }
	// };
	// AutoLoadCallBack callBack_brand = new AutoLoadCallBack() {
	// public void execute(String url) {
	// if (mLayout.getVisibility() == View.GONE) {
	// if (pagebrand < Integer.parseInt(allPage_brand)) {
	// pagebrand++;
	// mLayout.setVisibility(View.VISIBLE);
	// new Handler().postDelayed(new Runnable() {
	// @Override
	// public void run() {
	// getGoddsSave("2", String.valueOf(pagebrand));
	// adapter.notifyDataSetChanged();
	// }
	// }, 1500);
	//
	// } else {
	// Toast.makeText(SaveActivity.this, "内容全部加载完毕", 100).show();
	// }
	// }
	//
	// }
	// };

	private class MyGridViewAdapter extends BaseAdapter {
		AppItem appItem;
		List<JSONObject> mArray = new ArrayList<JSONObject>();
		// ImageLoader imageLoader;
		// 用来控制蒙版的选中状况
		private HashMap<Integer, Boolean> isSelected;
		DisplayImageOptions options;

		public MyGridViewAdapter(List<JSONObject> array) {
			this.mArray = array;
			// imageLoader = new ImageLoader(SaveActivity.this,"");
			isSelected = new HashMap<Integer, Boolean>();
			// MyTools.initImageLoader(SaveActivity.this);
			options = MyTools.createOptions(R.drawable.img);
		}

		// 初始化isSelected的数据
		private void initDate(List<JSONObject> mList, boolean flag) {
			for (int i = 0; i < mList.size(); i++) {
				getIsSelected().put(i, flag);
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mArray.size();
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
				View v = LayoutInflater.from(SaveActivity.this).inflate(
						R.layout.item_save_gridview, null);
				appItem = new AppItem();
				appItem.AppText_name = (TextView) v
						.findViewById(R.id.save_goods_name);
				appItem.AppText_price_now = (TextView) v
						.findViewById(R.id.item_save_price_now);
				appItem.AppText_price_old = (TextView) v
						.findViewById(R.id.item_save_price_old);
				appItem.mBtn_time = (Button) v
						.findViewById(R.id.goods_btn_shengyu_time);
				appItem.mBtn_zhekou = (Button) v
						.findViewById(R.id.save_goods_dazhe);
				appItem.mImage = (ImageView) v
						.findViewById(R.id.save_goods_img);
				appItem.mAppLayout = (RelativeLayout) v
						.findViewById(R.id.rel_meng);
				appItem.mAppLayout_Tou = (RelativeLayout) v
						.findViewById(R.id.save_rel);
				appItem.mBtn_Delete = (Button) v.findViewById(R.id.delete);
				v.setTag(appItem);
				convertView = v;
			} else {
				appItem = (AppItem) convertView.getTag();
			}

			MyTools.setHight(appItem.mAppLayout, width, height,
					SaveActivity.this);
			MyTools.setHight(appItem.mAppLayout_Tou, width, height,
					SaveActivity.this);

			try {
				if (getIsSelected().get(position)) {
					appItem.mAppLayout.setVisibility(View.VISIBLE);
				} else {
					appItem.mAppLayout.setVisibility(View.GONE);
				}

				appItem.AppText_name.setText(mArray.get(position)
						.getString("PName").toString());
				appItem.AppText_price_now.setText("￥"
						+ mArray.get(position).getString("Price").toString());
				appItem.AppText_price_old.setText("￥"
						+ mArray.get(position).getString("MarketPrice")
								.toString());
				// appItem.mBtn_time.setText(mArray.getJSONObject(position).getString("PName").toString());
				appItem.mBtn_zhekou.setVisibility(View.INVISIBLE);
				if (!mArray
						.get(position)
						.getString("Price")
						.toString()
						.equals(mArray.get(position).getString("MarketPrice")
								.toString())) {
					appItem.mBtn_zhekou.setVisibility(View.VISIBLE);
					Double p_1 = Double.valueOf(mArray.get(position)
							.getString("Price").toString());
					Double p_2 = Double.valueOf(mArray.get(position)
							.getString("MarketPrice").toString());
					Double result = (p_1 / p_2) * 10;
					String price = String.valueOf(result).substring(0, 3);
					appItem.mBtn_zhekou.setText(price + "折");
				}
				// appItem.mBtn_zhekou.setText(mArray.get(position).getString("Description").toString());
				appItem.AppText_price_old.getPaint().setFlags(
						Paint.STRIKE_THRU_TEXT_FLAG);
				String imgUrl = AllStaticMessage.URL_GBase + "/UsersData/"
						+ mArray.get(position).getString("Account").toString()
						+ "/"
						+ mArray.get(position).getString("SkuNo").toString()
						+ "/5.jpg";
				// imageLoader.DisplayImage(imgUrl, appItem.mImage);
				// 加载图片
				ImageLoader.getInstance().displayImage(imgUrl, appItem.mImage,
						options);
				appItem.mBtn_Delete.setOnClickListener(new ItemClick(appItem,
						mArray.get(position), position));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						Intent mIntent = new Intent(SaveActivity.this,
								GoodsDetailActivity.class);
						mIntent.putExtra("pid",
								mArray.get(position).getString("activeId")
										.toString());// 活动id
						mIntent.putExtra("guigeid", mArray.get(position)
								.getString("specId").toString());// 规格
						mIntent.putExtra("goodsid", mArray.get(position)
								.getString("goodsId").toString());// 商品id
						// Log.i("11111",
						// jsonArray.getJSONObject(position).getString("Id").toString());
						mIntent.putExtra("imgurl", AllStaticMessage.URL_GBase
								+ "/UsersData/"
								+ mArray.get(position).getString("Account")
										.toString()
								+ "/"
								+ mArray.get(position).getString("SkuNo")
										.toString() + "/5.jpg".trim());
						startActivity(mIntent);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			return convertView;
		}

		public HashMap<Integer, Boolean> getIsSelected() {
			return isSelected;
		}

		public void setIsSelected(HashMap<Integer, Boolean> isSelected2) {
			isSelected = isSelected2;
		}
	}

	class AppItem {
		TextView AppText_name;
		TextView AppText_price_now;
		TextView AppText_price_old;
		Button mBtn_time, mBtn_zhekou, mBtn_Delete;
		ImageView mImage;
		RelativeLayout mAppLayout, mAppLayout_Tou;
	}

	class ItemClick implements OnClickListener {
		AppItem appItem;
		JSONObject object;
		int position;

		public ItemClick(AppItem appItem, JSONObject object, int position) {
			this.appItem = appItem;
			this.object = object;
			this.position = position;
		}

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			try {
				delete(object.get("fid").toString(), position);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void delete(final String fid, final int position) {
		Builder builder = new Builder(SaveActivity.this);
		builder.setTitle("确定移除嘛？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialo, int which) {
				dialog.loading();
				deleteData(fid, position);
			}
		});

		builder.setNegativeButton("取消", null);
		builder.create().show();
	}

	private void deleteData(String id, final int position) {
		String url = AllStaticMessage.URL_Delete_Save
				+ AllStaticMessage.User_Id + "&favriteId=" + id;
		HttpUtil.get(url, SaveActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								mListData_1.remove(position);
								mAdapter.notifyDataSetChanged();
								if (mListData_1.size() == 0) {
									mGridView_1.setAdapter(mAdapter);
									mGridView_1.setVisibility(View.GONE);
								}
								getGoddsSave("1", String.valueOf(pagegoods));
								Toast.makeText(
										SaveActivity.this,
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
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// 错误返回JSONObject
						dialog.stop();
					}
				});
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
