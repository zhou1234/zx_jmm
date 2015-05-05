package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.jifeng.adapter.ClassItemListViewAdapter;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ClassItemActivity extends Activity implements
		OnHeaderRefreshListener, OnFooterLoadListener, OnClickListener {

	private AbPullToRefreshView mAbPullToRefreshView = null;
	private ListView mListView;
	private LoadingDialog dialog;
	private List<JSONObject> listData;
	private ClassItemListViewAdapter adapter;

	private LinearLayout ll_price, ll_zheKou, ll_moRen;
	private ImageView iv_jiaGe, iv_zheKou;
	private TextView tv_title, tv_moRen, tv_jiaGe, tv_zheKou;
	private boolean price_flag, zheKou_flag;
	private String id;
	private int num = 1;

	private String sortCol, sortType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_item);
		dialog = new LoadingDialog(this);
		dialog.loading();
		listData = new ArrayList<JSONObject>();
		init();
		Intent intent = getIntent();
		if (intent != null) {
			id = intent.getStringExtra("id");
			String name = intent.getStringExtra("name");
			tv_title.setText(name);
			getData(id, "", "", "");
		}
	}

	private void init() {
		mAbPullToRefreshView = (AbPullToRefreshView) findViewById(R.id.mPullRefreshView);
		mListView = (ListView) findViewById(R.id.classItem_listView);
		tv_title = (TextView) findViewById(R.id.tv_title);

		ll_price = (LinearLayout) findViewById(R.id.ll_price);
		ll_zheKou = (LinearLayout) findViewById(R.id.ll_zheKou);
		ll_moRen = (LinearLayout) findViewById(R.id.ll_moRen);
		iv_jiaGe = (ImageView) findViewById(R.id.iv_jiaGe);
		iv_zheKou = (ImageView) findViewById(R.id.iv_zheKou);
		tv_moRen = (TextView) findViewById(R.id.tv_moRen);
		tv_zheKou = (TextView) findViewById(R.id.tv_zheKou);
		tv_jiaGe = (TextView) findViewById(R.id.tv_jiaGe);

		ll_price.setOnClickListener(this);
		ll_zheKou.setOnClickListener(this);
		ll_moRen.setOnClickListener(this);

		mAbPullToRefreshView.setOnHeaderRefreshListener(this);
		mAbPullToRefreshView.setOnFooterLoadListener(this);

		findViewById(R.id.fenlei_back).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						ClassItemActivity.this.finish();
					}
				});
	}

	private void getData(String Id, String str, String sortCol, String sortType) {
		if (!str.equals("")) {
			dialog.loading();
		}
		String url = AllStaticMessage.class_url_item + Id + "&sortCol="
				+ sortCol + "&sortType=" + sortType;
		HttpUtil.get(url, ClassItemActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@SuppressLint("ShowToast")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								WindowManager manager = getWindowManager();
								@SuppressWarnings("deprecation")
								int width = manager.getDefaultDisplay()
										.getWidth();
								@SuppressWarnings("deprecation")
								int height = manager.getDefaultDisplay()
										.getHeight();
								JSONArray array = response
										.getJSONArray("Results");
								if (listData != null) {
									listData.clear();
								}
								if (array != null && array.length() > 0) {
									for (int i = 0; i < array.length(); i++) {
										listData.add(array.getJSONObject(i));
									}
									adapter = new ClassItemListViewAdapter(
											listData, ClassItemActivity.this,
											width, height);
									mListView.setAdapter(adapter);
									mAbPullToRefreshView
											.onHeaderRefreshFinish();
								}
							} else {
								mAbPullToRefreshView.onHeaderRefreshFinish();
								Toast.makeText(
										ClassItemActivity.this,
										"暂无商品", 500).show();
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

	/**
	 * 上拉更多
	 * 
	 * @param Id
	 */
	private void getData1(String Id, String num, String sortCol, String sortType) {
		String url = AllStaticMessage.class_url_item + Id + "&sortCol="
				+ sortCol + "&sortType=" + sortType + "&pageNum=" + num;
		HttpUtil.get(url, ClassItemActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@SuppressLint("ShowToast")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								JSONArray array = response
										.getJSONArray("Results");
								if (array != null && array.length() > 0) {
									for (int i = 0; i < array.length(); i++) {
										listData.add(array.getJSONObject(i));
									}
									if (adapter != null) {
										adapter.notifyDataSetChanged();
									}
									mAbPullToRefreshView.onFooterLoadFinish();
									// adapter = new ClassItemListViewAdapter(
									// listData, ClassItemActivity.this,
									// width, height);
									// mListView.setAdapter(adapter);
								}
							} else {
								mAbPullToRefreshView.onFooterLoadFinish();
								Toast.makeText(
										ClassItemActivity.this,
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

	/**
	 * 上拉更多
	 */
	@Override
	public void onFooterLoad(AbPullToRefreshView view) {
		new Handler().postDelayed(new Runnable() {
			@SuppressLint("ShowToast")
			@Override
			public void run() {
				num++;
				getData1(id, num + "", sortCol, sortType);
			}
		}, 1000);
	}

	/**
	 * 下拉刷新
	 */
	@SuppressLint("ShowToast")
	@Override
	public void onHeaderRefresh(AbPullToRefreshView view) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				num = 1;
				price_flag = false;
				zheKou_flag = false;
				iv_jiaGe.setImageResource(R.drawable.goods_list_down_up);
				iv_zheKou.setImageResource(R.drawable.goods_list_down_up);
				tv_moRen.setTextColor(getResources().getColor(
						R.color.tab_select));
				tv_jiaGe.setTextColor(getResources().getColor(
						R.color.text_color));
				tv_zheKou.setTextColor(getResources().getColor(
						R.color.text_color));
				getData(id, "", "", "");
			}
		}, 1000);
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ll_price:
			price_flag = !price_flag;
			iv_zheKou.setImageResource(R.drawable.goods_list_down_up);
			tv_jiaGe.setTextColor(getResources().getColor(R.color.tab_select));
			tv_zheKou.setTextColor(getResources().getColor(R.color.text_color));
			tv_moRen.setTextColor(getResources().getColor(R.color.text_color));
			if (price_flag) {
				getData(id, "moRen", "Price", "asc");
				sortCol = "Price";
				sortType = "asc";
				iv_jiaGe.setImageResource(R.drawable.goods_list_down_up1);
			} else {
				getData(id, "moRen", "price", "desc");
				sortCol = "Price";
				sortType = "desc";
				iv_jiaGe.setImageResource(R.drawable.goods_list_down_up2);
			}
			break;
		case R.id.ll_zheKou:
			zheKou_flag = !zheKou_flag;
			iv_jiaGe.setImageResource(R.drawable.goods_list_down_up);
			tv_jiaGe.setTextColor(getResources().getColor(R.color.text_color));
			tv_zheKou.setTextColor(getResources().getColor(R.color.tab_select));
			tv_moRen.setTextColor(getResources().getColor(R.color.text_color));
			if (zheKou_flag) {
				getData(id, "moRen", "DiscountDes", "asc");
				sortCol = "DiscountDes";
				sortType = "asc";
				iv_zheKou.setImageResource(R.drawable.goods_list_down_up1);
			} else {
				getData(id, "moRen", "DiscountDes", "desc");
				sortCol = "DiscountDes";
				sortType = "desc";
				iv_zheKou.setImageResource(R.drawable.goods_list_down_up2);
			}
			break;
		case R.id.ll_moRen:
			price_flag = false;
			zheKou_flag = false;
			iv_jiaGe.setImageResource(R.drawable.goods_list_down_up);
			iv_zheKou.setImageResource(R.drawable.goods_list_down_up);
			tv_moRen.setTextColor(getResources().getColor(R.color.tab_select));
			tv_jiaGe.setTextColor(getResources().getColor(R.color.text_color));
			tv_zheKou.setTextColor(getResources().getColor(R.color.text_color));
			getData(id, "moRen", "", "");
			break;
		}
	}
}
