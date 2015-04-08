package com.jifeng.mlsales.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.jifeng.adapter.MainAdapter;
import com.jifeng.adapter.MyGoodsListAdapter;
import com.jifeng.mlsales.R;
import com.jifeng.mlsales.jumeimiao.FenLeiActivity;
import com.jifeng.mlsales.jumeimiao.FirstActivity;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.myview.My_ListView;
import com.jifeng.myview.PullToRefreshGridView;
import com.jifeng.myview.PullToRefreshScrollView;
import com.jifeng.pulltorefresh.PullToRefreshBase;
import com.jifeng.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.jifeng.pulltorefresh.PullToRefreshListView;
import com.jifeng.tools.TasckActivity;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class NextFragment extends BaseFragment implements
		OnHeaderRefreshListener, OnFooterLoadListener {
	private String id;
	/** 标志位，标志已经初始化完成 */
	private boolean isPrepared;
	/** 是否已被加载过一次，第二次就不再去请求数据了 */
	private boolean mHasLoadedOnce;

	private int height, width;
	private AbPullToRefreshView mAbPullToRefreshView = null;

	private ListView mListView;
	private ImageView goodslist_zhiding;
	private ScrollView scrollView;

	String Id, Pid;
	private MainAdapter mAdapter;
	private List<JSONObject> listData;

	private RelativeLayout rl_progress;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		id = getArguments().getString(FirstActivity.ARGUMENTS_ID);
		listData = new ArrayList<JSONObject>();
		WindowManager manager = getActivity().getWindowManager();
		height = manager.getDefaultDisplay().getHeight();
		width = manager.getDefaultDisplay().getWidth();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.next_fragment, container,
				false);
		mListView = (My_ListView) rootView.findViewById(R.id.main_first_list);
		mAbPullToRefreshView = (AbPullToRefreshView) rootView
				.findViewById(R.id.mPullRefreshView);
		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
		rl_progress = (RelativeLayout) rootView.findViewById(R.id.rl_progress);
		mAbPullToRefreshView.setOnHeaderRefreshListener(this);
		mAbPullToRefreshView.setOnFooterLoadListener(this);

		mListView.setVerticalScrollBarEnabled(false);
		mListView.setFooterDividersEnabled(false);
		mListView.setFocusable(false);
		goodslist_zhiding = (ImageView) rootView
				.findViewById(R.id.goodslist_zhiding);
		goodslist_zhiding.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				goodslist_zhiding.setVisibility(View.GONE);
				scrollView.smoothScrollTo(0, 1);

			}
		});
		isPrepared = true;
		lazyLoad();
		// ViewGroup parent = (ViewGroup) rootView.getParent();
		// if (parent != null) {
		// parent.removeView(rootView);
		// }
		return rootView;
	}

	private void getListData(final ListView mListView, String id) {
		String url_1 = AllStaticMessage.URL_Shouye_1 + id;
		HttpUtil.get(url_1, getActivity(), null, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				try {
					if (response.getString("Status").equals("true")) {
						JSONArray mArray = response.getJSONArray("Results");
						if (listData != null) {
							listData.clear();
						}
						for (int i = 0; i < mArray.length(); i++) {
							listData.add(mArray.getJSONObject(i));
						}

						mAdapter = new MainAdapter(getActivity(), height,
								width, listData, mListView);
						mListView.setAdapter(mAdapter);
						mHasLoadedOnce = true;
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
				super.onFailure(statusCode, headers, throwable, errorResponse);
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
				goodslist_zhiding.setVisibility(View.VISIBLE);
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

	@Override
	protected void lazyLoad() {
		if (!isPrepared || !isVisible || mHasLoadedOnce) {
			return;
		} else {
			getListData(mListView, id);
		}
	}
}
