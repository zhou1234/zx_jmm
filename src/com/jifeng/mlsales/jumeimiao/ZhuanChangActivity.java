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
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.jifeng.adapter.MainAdapter;
import com.jifeng.mlsales.R;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class ZhuanChangActivity extends Activity implements
		OnHeaderRefreshListener, OnFooterLoadListener {

	private String id;
	private String title;
	private int height, width;
	private AbPullToRefreshView mAbPullToRefreshView = null;

	private ListView mListView;
	private ImageView goodslist_zhiding;

	private MainAdapter mAdapter;
	private List<JSONObject> listData;

	private RelativeLayout rl_progress;
	private RelativeLayout rl_zhiding;
	private TextView tv_number, tv_cont,tv_title;

	private int cont;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.next_fragment);
		listData = new ArrayList<JSONObject>();
		WindowManager manager = getWindowManager();
		height = manager.getDefaultDisplay().getHeight();
		width = manager.getDefaultDisplay().getWidth();
		init();
		Intent intent = getIntent();
		if (intent != null) {
			id = intent.getStringExtra("id");
			title=intent.getStringExtra("title");
			tv_title.setText(title);
			getListData(mListView, id);
		}
	}

	private void init() {
		tv_title=(TextView) findViewById(R.id.tv_title);
		mListView = (ListView) findViewById(R.id.main_first_list);

		mAbPullToRefreshView = (AbPullToRefreshView) findViewById(R.id.mPullRefreshView);
		rl_progress = (RelativeLayout) findViewById(R.id.rl_progress);
		goodslist_zhiding = (ImageView) findViewById(R.id.goodslist_zhiding);
		rl_zhiding = (RelativeLayout) findViewById(R.id.rl_zhiding);
		tv_number = (TextView) findViewById(R.id.tv_number);
		tv_cont = (TextView) findViewById(R.id.tv_cont);

		mAbPullToRefreshView.setOnHeaderRefreshListener(this);
		mAbPullToRefreshView.setOnFooterLoadListener(this);

		mListView.setVerticalScrollBarEnabled(false);
		mListView.setFooterDividersEnabled(false);
		mListView.setDividerHeight(0);

		mListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader
				.getInstance(), true, true));
		mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				switch (arg1) {
				// 当屏幕停止滑动
				case SCROLL_STATE_IDLE:
					if (cont >= 10) {
						rl_zhiding.setVisibility(View.GONE);
						goodslist_zhiding.setVisibility(View.VISIBLE);
					} else {
						goodslist_zhiding.setVisibility(View.GONE);
						rl_zhiding.setVisibility(View.GONE);
					}
					break;
				// 当屏幕滚动且用户使用的触碰或手指还在屏幕上时为SCROLL_STATE_TOUCH_SCROLL = 1；
				// 由于用户的操作，屏幕产生惯性滑动时为SCROLL_STATE_FLING = 2
				case SCROLL_STATE_TOUCH_SCROLL:
				case SCROLL_STATE_FLING:
					if (cont >= 10) {
						rl_zhiding.setVisibility(View.VISIBLE);
						goodslist_zhiding.setVisibility(View.GONE);
					} else {
						goodslist_zhiding.setVisibility(View.GONE);
						rl_zhiding.setVisibility(View.GONE);
					}
					break;
				}
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				tv_number.setText(arg1 + arg2 + "");
				tv_cont.setText(arg3 + "");
				cont = arg1 + arg2;
			}
		});

		goodslist_zhiding.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				mListView.setSelection(0);
				mListView.smoothScrollBy(0, 1);
				goodslist_zhiding.setVisibility(View.GONE);
				rl_zhiding.setVisibility(View.GONE);

			}
		});

	}

	private void getListData(final ListView mListView, String id) {
		String url_1 = AllStaticMessage.URL_Shouye_1 + id;
		HttpUtil.get(url_1, ZhuanChangActivity.this, null,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							if (response.getString("Status").equals("true")) {
								JSONArray mArray = response
										.getJSONArray("Results");
								if (listData != null) {
									listData.clear();
								}
								for (int i = 0; i < mArray.length(); i++) {
									listData.add(mArray.getJSONObject(i));
								}
								mAdapter = new MainAdapter(
										ZhuanChangActivity.this, height, width,
										listData, mListView);
								mListView.setAdapter(mAdapter);
								mAbPullToRefreshView.onHeaderRefreshFinish();
								rl_progress.setVisibility(View.GONE);
							} else {
								mAbPullToRefreshView.onHeaderRefreshFinish();
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
					}

					@Override
					public void onStart() {
						super.onStart();
					}

					@Override
					public void onFinish() {
						super.onFinish();
					}
				});
	}

	@Override
	public void onFooterLoad(AbPullToRefreshView view) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mAbPullToRefreshView.onFooterLoadFinish();
				Toast.makeText(ZhuanChangActivity.this, "没有更多了", 0).show();
			}
		}, 1200);
	}

	@Override
	public void onHeaderRefresh(AbPullToRefreshView view) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				getListData(mListView, id);
			}
		}, 1200);

	}

	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.iv_back:// 返回
			finish();
			break;

		default:
			break;
		}
	}

}
