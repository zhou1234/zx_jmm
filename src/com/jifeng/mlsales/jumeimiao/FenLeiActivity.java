package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jifeng.adapter.MyGoodsListAdapter;
import com.jifeng.adapter.MyGoodsListAdapter1;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.myview.PullToRefreshGridView;
import com.jifeng.pulltorefresh.PullToRefreshBase;
import com.jifeng.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.jifeng.tools.MyTools;
import com.jifeng.tools.TasckActivity;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class FenLeiActivity extends Activity {
	private PullToRefreshGridView pullToRefreshGridView;
	private ImageView mDongHua;
	LinearLayout mLayout;
	GridView gridView;
	LoadingDialog dialog;
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0x01:
				// Log.i("11111", "1111111111111111111");
				mDongHua.setVisibility(View.VISIBLE);
				Animation hyperspaceJumpAnimation = AnimationUtils
						.loadAnimation(FenLeiActivity.this, R.anim.puch_up_in);
				mDongHua.startAnimation(hyperspaceJumpAnimation);
				break;
			case 0x02:
				mDongHua.setVisibility(View.INVISIBLE);
				mLayout.setVisibility(View.INVISIBLE);
				break;
			case 0x03:
				mLayout.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}
	};
	String Id, Pid;
	private MyGoodsListAdapter mAdapter;
	private TextView txt_Title;
	int pageNo = 1;
	private List<JSONObject> listData;
	private TasckActivity tasckActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fenlei);
		dialog = new LoadingDialog(this);
		dialog.loading();
		listData = new ArrayList<JSONObject>();
		findView();
		initData();
		register();
		Id = getIntent().getStringExtra("id");
		Pid = getIntent().getStringExtra("pid");
		txt_Title.setText(getIntent().getStringExtra("title"));

		getData("1");
		tasckActivity = new TasckActivity();
		tasckActivity.pushActivity(FenLeiActivity.this);
	}

	// 查找控件
	private void findView() {
		mDongHua = (ImageView) findViewById(R.id.fenlei_img_wenzi);
		mLayout = (LinearLayout) findViewById(R.id.fenlei_main_rel);
		txt_Title = (TextView) findViewById(R.id.fenlei_textview_title);
	}

	// 注册事件
	private void register() {

	}

	/*
	 * 初始化数据
	 */
	private void initData() {
		pullToRefreshGridView = new PullToRefreshGridView(this, handler);
		LinearLayout layout = (LinearLayout) findViewById(R.id.fenlei_list);
		@SuppressWarnings("deprecation")
		LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		layout.addView(pullToRefreshGridView, linearParams);
		pullToRefreshGridView
				.setOnRefreshListener(new OnRefreshListener<GridView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<GridView> refreshView) {
						// 下拉刷新
						pageNo = 1;
						if (listData != null) {
							listData.clear();
						}
						if (mAdapter != null) {
							mAdapter.notifyDataSetChanged();
						}
						getData("1");

					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<GridView> refreshView) {
						// TODO Auto-generated method stub
						// 上拉加载更多
						pageNo++;
						// if (pageNo < Integer.parseInt("1")) {// AllPage
						getData(String.valueOf(pageNo));
						// } else {
						// pullToRefreshGridView.onPullUpRefreshComplete();
						// }

					}
				});
		pullToRefreshGridView.setPullLoadEnabled(true);
		pullToRefreshGridView.setScrollLoadEnabled(true);
		gridView = pullToRefreshGridView.getRefreshableView();
		gridView.setNumColumns(2);
		gridView.setVerticalSpacing(8);
		gridView.setHorizontalSpacing(8);
	}

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.fenlei_back:// 返回
			//tasckActivity.popActivity(FenLeiActivity.this);
			finish();
			break;
		default:
			break;
		}
	}

	private void getData(String pageNo) {
		String url = "";
		if (getIntent().getStringExtra("search").equals("")) {
			url = AllStaticMessage.URL_GetList_FenLei + pageNo + "&id=" + Id;// "23401"
		} else {
			url = AllStaticMessage.URL_Search + Id + "&pageNum=" + pageNo;// "23401"
		}

		HttpUtil.get(url, FenLeiActivity.this, dialog,
				new JsonHttpResponseHandler() {
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
								response.getString("totalPage").toString();
								JSONArray array = response
										.getJSONArray("Results");
								if (array != null && array.length() > 0) {
									for (int i = 0; i < array.length(); i++) {
										listData.add(array.getJSONObject(i));
									}
									if (mAdapter == null) {
										mAdapter = new MyGoodsListAdapter(
												listData, FenLeiActivity.this,
												Pid, width, height);
										gridView.setAdapter(mAdapter);
									} else if(mAdapter!=null){
										mAdapter.notifyDataSetChanged();
									}

								}
							} else {
								Toast.makeText(
										FenLeiActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						pullToRefreshGridView.onPullDownRefreshComplete();
						pullToRefreshGridView.onPullUpRefreshComplete();
						mDongHua.setVisibility(View.INVISIBLE);
						mLayout.setVisibility(View.INVISIBLE);
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

	private void relise() {
		setContentView(R.layout.view_null);
		pullToRefreshGridView = null;
		mDongHua = null;
		mLayout = null;
		gridView = null;
		dialog = null;
		handler = null;
		Id = null;
		Pid = null;
		mAdapter = null;
		txt_Title = null;
		pageNo = 1;
		listData = null;
		tasckActivity = null;
		System.gc();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//relise();
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
