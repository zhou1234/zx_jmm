package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.jifeng.adapter.MyGoodsListAdapter2;
import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

public class SearchActivity extends Activity implements
		OnHeaderRefreshListener, OnFooterLoadListener {
	private LoadingDialog dialog;

	private MyGoodsListAdapter2 mAdapter;
	private AbPullToRefreshView pullToRefreshView;
	private ListView lv_search;
	private int pageNo = 1;
	private List<JSONObject> listData;

	private String content = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fenlei);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		dialog.loading();
		listData = new ArrayList<JSONObject>();
		findView();
		Intent intent = getIntent();
		if (intent != null) {
			content = intent.getStringExtra("content");
		}
		getData("1");
	}

	// 查找控件
	private void findView() {
		pullToRefreshView = (AbPullToRefreshView) findViewById(R.id.mPullRefreshView);
		lv_search = (ListView) findViewById(R.id.lv_search);

		pullToRefreshView.setOnHeaderRefreshListener(this);
		pullToRefreshView.setOnFooterLoadListener(this);

		lv_search.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {

			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				if ((arg1 + arg2) == listData.size() -2) {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							pageNo++;
							getAddData(String.valueOf(pageNo));

						}
					}, 0);

				}
			}
		});
	}

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.fenlei_back:// 返回
			finish();
			break;
		default:
			break;
		}
	}

	private void getData(String pageNo) {
		String url = AllStaticMessage.URL_Search + content + "&pageNum="
				+ pageNo+"&pageSize=20";// "23401"

		HttpUtil.get(url, SearchActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@SuppressWarnings("deprecation")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								WindowManager manager = getWindowManager();

								int width = manager.getDefaultDisplay()
										.getWidth();
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

									mAdapter = new MyGoodsListAdapter2(
											listData, SearchActivity.this, "",
											width, height);
									lv_search.setAdapter(mAdapter);
								}
							} else {
								Toast.makeText(
										SearchActivity.this,
										response.getString("Results")
												.toString(), 0).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						pullToRefreshView.onHeaderRefreshFinish();
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

	private void getAddData(String pageNo) {
		String url = AllStaticMessage.URL_Search + content + "&pageNum="
				+ pageNo + "&pageSize=10";// "23401"

		HttpUtil.get(url, SearchActivity.this, dialog,
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

								if (array != null && array.length() > 0) {
									for (int i = 0; i < array.length(); i++) {
										listData.add(array.getJSONObject(i));
									}
									if (mAdapter != null) {
										mAdapter.notifyDataSetChanged();
									}
								}
							} else {
								// Toast.makeText(
								// SearchActivity.this,
								// response.getString("Results")
								// .toString(), 0).show();
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
	protected void onDestroy() {
		super.onDestroy();
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
	public void onFooterLoad(AbPullToRefreshView view) {
		pullToRefreshView.onFooterLoadFinish();

	}

	@Override
	public void onHeaderRefresh(AbPullToRefreshView view) {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// 下拉刷新
				pageNo = 1;
				getData("1");

			}
		}, 200);

	}
}
