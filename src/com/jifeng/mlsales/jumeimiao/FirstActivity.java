package com.jifeng.mlsales.jumeimiao;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jifeng.adapter.MainAdapter;
import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.mlsales.fragment.NextFragment;
import com.jifeng.mlsales.fragment.OneFragment;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.myview.My_ListView;
import com.jifeng.myview.PullToRefreshScrollView;
import com.jifeng.myview.SyncHorizontalScrollView;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class FirstActivity extends FragmentActivity {

	public static final String ARGUMENTS_TITLE = "title";
	public static final String ARGUMENTS_ID = "id";
	// private RelativeLayout rl_nav;
	private SyncHorizontalScrollView mHsv;
	private RadioGroup rg_nav_content;
	private ImageView iv_nav_indicator;
	// private ImageView iv_nav_left;
	// private ImageView iv_nav_right;
	private ViewPager mViewPager;
	private int indicatorWidth;
	public static String[] tabTitle; // 标题
	private LayoutInflater mInflater;
	private TabFragmentPagerAdapter mAdapter;
	private int currentIndicatorLeft = 0;

	private LoadingDialog dialog;
	private static JSONArray mArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		dialog.show();
		findViewById();
		getData();
		setListener();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	private void setListener() {

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {

				if (rg_nav_content != null
						&& rg_nav_content.getChildCount() > position) {
					((RadioButton) rg_nav_content.getChildAt(position))
							.performClick();
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		rg_nav_content
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {

						if (rg_nav_content.getChildAt(checkedId) != null) {

							TranslateAnimation animation = new TranslateAnimation(
									currentIndicatorLeft,
									((RadioButton) rg_nav_content
											.getChildAt(checkedId)).getLeft(),
									0f, 0f);
							animation.setInterpolator(new LinearInterpolator());
							animation.setDuration(100);
							animation.setFillAfter(true);

							// 执行位移动画
							iv_nav_indicator.startAnimation(animation);

							mViewPager.setCurrentItem(checkedId); // ViewPager
																	// 跟随一起 切换

							// // 记录当前 下标的距最左侧的 距离
							currentIndicatorLeft = ((RadioButton) rg_nav_content
									.getChildAt(checkedId)).getLeft();
							if (tabTitle.length > 4) {
								mHsv.smoothScrollTo(
										(checkedId > 1 ? ((RadioButton) rg_nav_content
												.getChildAt(checkedId))
												.getLeft() : 0)
												- ((RadioButton) rg_nav_content
														.getChildAt(2))
														.getLeft(), 0);
							}
						}
					}
				});
	}

	private void initNavigationHSV() {

		rg_nav_content.removeAllViews();

		for (int i = 0; i < tabTitle.length; i++) {

			RadioButton rb = (RadioButton) mInflater.inflate(
					R.layout.nav_radiogroup_item, null);
			rb.setId(i);
			rb.setText(tabTitle[i]);
			rb.setLayoutParams(new LayoutParams(indicatorWidth,
					LayoutParams.MATCH_PARENT));

			rg_nav_content.addView(rb);
		}

	}

	private void findViewById() {

		// rl_nav = (RelativeLayout) findViewById(R.id.rl_nav);

		mHsv = (SyncHorizontalScrollView) findViewById(R.id.mHsv);

		rg_nav_content = (RadioGroup) findViewById(R.id.rg_nav_content);

		iv_nav_indicator = (ImageView) findViewById(R.id.iv_nav_indicator);
		// iv_nav_left = (ImageView) findViewById(R.id.iv_nav_left);
		// iv_nav_right = (ImageView) findViewById(R.id.iv_nav_right);

		mViewPager = (ViewPager) findViewById(R.id.mViewPager);

		mViewPager.setOffscreenPageLimit(3);

		findViewById(R.id.iv_class).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						startActivity(new Intent(FirstActivity.this,
								ClassActivity.class));
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public static class TabFragmentPagerAdapter extends FragmentPagerAdapter {

		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			Fragment ft = null;
			switch (arg0) {
			case 0:
				ft = new OneFragment();
				break;

			default:
				ft = new NextFragment();
				try {
					Bundle args = new Bundle();
					args.putString(ARGUMENTS_TITLE, tabTitle[arg0]);
					args.putString(ARGUMENTS_ID, mArray.getJSONObject(arg0 - 1)
							.getString("Id").toString());
					ft.setArguments(args);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;
			}
			return ft;
		}

		@Override
		public int getCount() {
			return tabTitle.length;
		}

	}

	// 获取头部分组信息
	private void getData() {
		String url_1 = AllStaticMessage.URL_Shouye_fenlei;
		HttpUtil.get(url_1, FirstActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							if (response.getString("Status").equals("true")) {
								mArray = response.getJSONArray("Results");
								tabTitle = new String[mArray.length() + 1];
								tabTitle[0] = "今日特卖";
								for (int i = 0; i < mArray.length(); i++) {
									tabTitle[i + 1] = mArray.getJSONObject(i)
											.getString("CateName");
								}
								DisplayMetrics dm = new DisplayMetrics();
								getWindowManager().getDefaultDisplay()
										.getMetrics(dm);
								if (tabTitle.length < 4) {
									indicatorWidth = dm.widthPixels
											/ (tabTitle.length);
								} else {
									indicatorWidth = dm.widthPixels / 4;
								}
								LayoutParams cursor_Params = iv_nav_indicator
										.getLayoutParams();
								cursor_Params.width = indicatorWidth;// 初始化滑动下标的宽
								iv_nav_indicator.setLayoutParams(cursor_Params);

								// mHsv.setSomeParam(rl_nav, iv_nav_left,
								// iv_nav_right, this);

								// 获取布局填充器
								mInflater = (LayoutInflater) FirstActivity.this
										.getSystemService(LAYOUT_INFLATER_SERVICE);

								// 另一种方式获取
								// LayoutInflater mInflater =
								// LayoutInflater.from(this);

								initNavigationHSV();

								mAdapter = new TabFragmentPagerAdapter(
										getSupportFragmentManager());
								mViewPager.setAdapter(mAdapter);
								rg_nav_content.check(0);
								dialog.stop();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							dialog.stop();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						if (dialog.isShowing()) {
							dialog.stop();
						}
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

	/*
	 * 双击退出
	 */
	private long mExitTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出居美喵", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
//				android.os.Process.killProcess(android.os.Process.myPid());
//				this.finish();
//				System.exit(0);
				this.getApplication().onTerminate();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageLoader.getInstance().clearDiskCache();
		ImageLoader.getInstance().clearMemoryCache();
	}
}
