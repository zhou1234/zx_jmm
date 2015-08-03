package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.adapter.MainAdapter;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.myview.My_ListView;
import com.jifeng.myview.My_ViewPager;
import com.jifeng.myview.My_ViewPager.OnSingleTouchListener;
import com.jifeng.myview.PullToRefreshScrollView;
import com.jifeng.pulltorefresh.PullToRefreshBase;
import com.jifeng.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.jifeng.tools.BitmapUtil;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

@SuppressWarnings("deprecation")public class MainActivity extends Activity {
	private Button button;// ����
	private ViewPager mViewPager;
	private LoadingDialog dialog;
	private Button[] mBtn;
	private String[] classion;
	private List<View> vList = new ArrayList<View>();
	// ��Ļ�ĸ߶�
	private int height, width;
	private ImageView img_wenzi;
	private LinearLayout mLayout;
	private JSONArray mArray;
	private LinearLayout mLinearLayout;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0x01:
				img_wenzi.setVisibility(View.VISIBLE);
				Animation hyperspaceJumpAnimation = AnimationUtils
						.loadAnimation(MainActivity.this, R.anim.puch_up_in);
				img_wenzi.startAnimation(hyperspaceJumpAnimation);
				break;
			case 0x02:
				mLayout.setVisibility(View.INVISIBLE);
				img_wenzi.setVisibility(View.INVISIBLE);
				break;
			case 0x03:
				mLayout.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}
	};
	private int num = -1;
	private RelativeLayout mRelativeLayout;
	private List<List<JSONObject>> mData;
	private MainAdapter[] adapter;
	private PullToRefreshScrollView[] mPullScrollView;
	private ScrollView[] mScrollView;
	private My_ListView[] mListView;
	private ImageView[] mImageView;
	private ImageView mImg_ZhiDing;
	private int watSelect = 0;
	// String imgurl="";

	private ImageView[] mImageViews;
	// ��СԲ�������
	private ImageView[] imgs;
	private JSONArray mArray_ad;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// setContentView(R.layout.view_null);
		dialog = new LoadingDialog(this);
		mRelativeLayout = (RelativeLayout) findViewById(R.id.wifi_rel_bg);
		if (!MyTools.checkNetWorkStatus(this)) {
			mRelativeLayout.setVisibility(View.VISIBLE);
			num = 0;
			return;
		} else {
			initView();
		}
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		dialog.loading();
		// handler.sendEmptyMessage(0x01);
		findView();
		getImgUrl();

		WindowManager manager = getWindowManager();
		height = manager.getDefaultDisplay().getHeight();
		width = manager.getDefaultDisplay().getWidth();
		mData = new ArrayList<List<JSONObject>>();
	}

	private void findView() {
		button = (Button) findViewById(R.id.radio_button_news);
		img_wenzi = (ImageView) findViewById(R.id.img_wenzi);
		mLayout = (LinearLayout) findViewById(R.id.main_rel);
		mLinearLayout = (LinearLayout) findViewById(R.id.main_radio);
		mImg_ZhiDing = (ImageView) findViewById(R.id.main_zhiding);
	}

	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.img_wifi:
			if (!MyTools.checkNetWorkStatus(this)) {
				return;
			}
			mRelativeLayout.setVisibility(View.GONE);
			if (num == 0) {
				initView();
			} else {
				load_1(num, String.valueOf(num));
			}
			break;
		case R.id.main_zhiding:
			mScrollView[watSelect].smoothScrollTo(0, 20);
			mImg_ZhiDing.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}

	private void init() {
		mViewPager = (ViewPager) findViewById(R.id.my_pager);
		MyPagerAdapter adapter = new MyPagerAdapter();
		mViewPager.setAdapter(adapter);
		mViewPager.setOffscreenPageLimit(0);
		mViewPager.setCurrentItem(0);
		mViewPager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}

		});
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// getImgUrl(mImageView[position]);
				watSelect = position;
				setView(position);
				if (!MyTools.checkNetWorkStatus(MainActivity.this)) {
					mRelativeLayout.setVisibility(View.VISIBLE);
					num = position;
					return;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				mViewPager.getParent().requestDisallowInterceptTouchEvent(true);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	private class ItemClick implements View.OnClickListener {
		private int id;

		private ItemClick(int num) {
			this.id = num;
		}

		@Override
		public void onClick(View v) {
			mViewPager.setCurrentItem(id);
			setView(id);
		}

	}

	private void register() {
		button.setOnClickListener(new ItemClick(0));
		for (int i = 0; i < mBtn.length; i++) {
			mBtn[i].setOnClickListener(new ItemClick(i + 1));
		}
	}

	@SuppressWarnings("deprecation")
	private void setView(int i) {
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.transparent);

		Drawable mdrawable = this.getResources().getDrawable(
				R.drawable.main_ok_select_ok);
		if (i == 0) {
			button.setTextSize(16);
			button.setTextColor(getResources().getColor(R.color.tab_select));
			button.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					mdrawable);
		} else {
			button.setTextColor(getResources().getColor(R.color.text_color));
			button.setTextSize(16);
			button.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					drawable);
		}
		for (int j = 0; j < mBtn.length; j++) {
			if ((i - 1) == j) {
				mBtn[j].setTextSize(16);
				mBtn[j].setTextColor(getResources()
						.getColor(R.color.tab_select));
				mBtn[j].setCompoundDrawablesWithIntrinsicBounds(null, null,
						null, mdrawable);
			} else {
				mBtn[j].setTextColor(getResources()
						.getColor(R.color.text_color));
				mBtn[j].setTextSize(16);
				mBtn[j].setCompoundDrawablesWithIntrinsicBounds(null, null,
						null, drawable);
			}

		}
	}

	// ��ȡͷ��������Ϣ
	private void getData() {
		String url_1 = AllStaticMessage.URL_Shouye_fenlei;
		HttpUtil.get(url_1, MainActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@SuppressWarnings("deprecation")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							if (response.getString("Status").equals("true")) {
								mArray = response.getJSONArray("Results");
								mBtn = new Button[mArray.length()];
								classion = new String[mArray.length()];
								Button button = null;
								for (int i = 0; i < mBtn.length; i++) {
									button = new Button(MainActivity.this);
									button.setText(mArray.getJSONObject(i)
											.getString("CateName").toString());
									button.setBackgroundDrawable(getResources()
											.getDrawable(
													R.drawable.main_btn_touming));
									button.setPadding(15, 0, 0, 0);// �ڱ߾�
									button.setTextColor(getResources()
											.getColor(R.color.text_color));
									button.setTextSize(16);
									button.setGravity(Gravity.CENTER);
									mBtn[i] = button;
									mLinearLayout.addView(mBtn[i]);
									classion[i] = mArray.getJSONObject(i)
											.getString("Id").toString();
								}
								for (int j = 0; j < mBtn.length + 1; j++) {
									List<JSONObject> mListData = new ArrayList<JSONObject>();
									mData.add(mListData);
								}
								adapter = new MainAdapter[mArray.length() + 1];
								mPullScrollView = new PullToRefreshScrollView[mArray
										.length() + 1];
								mScrollView = new ScrollView[mArray.length() + 1];
								mListView = new My_ListView[mArray.length() + 1];
								mImageView = new ImageView[mArray.length() + 1];
								if (mBtn != null && mBtn.length > 0) {
									register();
									initData();
									init();
								}
							} else {
								List<JSONObject> mListData = new ArrayList<JSONObject>();
								mData.add(mListData);
								mBtn = new Button[0];
								adapter = new MainAdapter[1];
								mPullScrollView = new PullToRefreshScrollView[1];
								mScrollView = new ScrollView[1];
								mListView = new My_ListView[1];
								mImageView = new ImageView[1];
								register();
								initData();
								init();
								// Toast.makeText(MainActivity.this,response.getString("Results").toString(),
								// 500).show();
								// if (dialog.isShowing()) {
								// dialog.stop();
								// }
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

	// ��ȡ�б���Ϣ
	private void getListData(final ListView mListView, String id,
			final int position, final PullToRefreshScrollView mPullScrollView) {

		String url_1 = AllStaticMessage.URL_Shouye_1 + id;
		HttpUtil.get(url_1, MainActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							if (response.getString("Status").equals("true")) {
								if (adapter[position] != null) {
									adapter[position].notifyDataSetChanged();
								}

								JSONArray mArray = response
										.getJSONArray("Results");
								if (dialog.isShowing()) {
									dialog.stop();
								}
								for (int i = 0; i < mArray.length(); i++) {
									mData.get(position).add(
											mArray.getJSONObject(i));
								}
								adapter[position] = new MainAdapter(
										MainActivity.this, height, width, mData
												.get(position), mListView);
								// mListView.setLayoutAnimation(MyTools.getAnimationController());
								mListView.setAdapter(adapter[position]);
							} else {
								// Toast.makeText(MainActivity.this,response.getString("Results").toString(),500).show();
							}
							img_wenzi.setVisibility(View.INVISIBLE);
							mLayout.setVisibility(View.INVISIBLE);
							mPullScrollView.onPullDownRefreshComplete();
							mPullScrollView.onPullUpRefreshComplete();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (dialog.isShowing()) {
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

	private void getImgUrl() {
		String url = AllStaticMessage.URL_Shouye_DiBu;
		HttpUtil.get(url, MainActivity.this, dialog,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// �ɹ�����JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
								mArray_ad = response.getJSONArray("Results");
								getData();

							} else {
								Toast.makeText(
										MainActivity.this,
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
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// ���󷵻�JSONObject
						dialog.stop();
					}
				});
	}

	// ����ͼƬ������
	private class MyPagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {

			return vList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {

			return arg0 == arg1;
		}

		// һ��ʼ��ʼ����ҳ���ڶ�ҳ���룬��ʼ������ҳ
		// ȷ����ʾҳ�ͺ�һҳ����ҳ��ҳ�Ĵ���
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			/*
			 * 1.��һ������ViewPager���� 2.
			 */
			load_1(position, String.valueOf(position));
			((ViewPager) container).addView(vList.get(position));
			return vList.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(vList.get(position));
			// super.destroyItem(container, position, object);
		}

	}

	private void inImg() {
		DisplayImageOptions options = MyTools
				.createOptions(R.drawable.loading_01);
		for (int i = 0; i < mArray_ad.length(); i++) {
			// appItem.mAppIcon = (ImageView) v.findViewById(R.id.imgbig);
			ImageView imageView = new ImageView(this);
			imageView.setScaleType(ScaleType.FIT_XY);
			// �첽����ͼƬ
			try {
				String infUrl = AllStaticMessage.URL_GBase
						+ mArray_ad.getJSONObject(i).getString("ImgUrl")
								.toString();
				// final String aid =
				// mArray_ad.getJSONObject(i).getString("Id").toString();
				// DisplayImageOptions
				// options=MyTools.createOptions(R.drawable.loading_01);
				ImageLoader.getInstance().displayImage(infUrl, imageView,
						options);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mImageViews[i] = imageView;
		}
	}

	private class MyPicAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			if (mArray_ad.length() > 0) {
				return mArray_ad.length();
			} else {
				return 0;
			}
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			if (mArray_ad.length() > 3) {
				((ViewPager) container).removeView(mImageViews[position
						% mImageViews.length]);
			}
		}

		/**
		 * ����ͼƬ��ȥ���õ�ǰ��position ���� ͼƬ���鳤��ȡ�����ǹؼ�
		 */
		@Override
		public Object instantiateItem(View container, final int position) {

			try {
				if (mImageViews.length == 1) {
					((ViewPager) container).addView(mImageViews[0], 0);//
				} else {
					((ViewPager) container).addView(mImageViews[position
							% mImageViews.length], 0);//
				}

			} catch (Exception e) {
				// handler something
			}
			// �����ת��һҳ
			int num = position % mImageViews.length;
			// Log.i("11111", String.valueOf(num));
			// mImageViews[position % mImageViews.length].setOnClickListener(new
			// OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			//
			//
			//
			// }
			// });
			if (mImageViews.length == 1) {
				return mImageViews[0];
			} else {
				return mImageViews[position % mImageViews.length];
			}
		}
	}

	@SuppressLint("NewApi")
	private void load_1(final int position, final String id) {
		mPullScrollView[position] = new PullToRefreshScrollView(
				MainActivity.this, handler);
		LinearLayout layout = (LinearLayout) vList.get(position).findViewById(
				R.id.main_item);
		@SuppressWarnings("deprecation")
		LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		layout.addView(mPullScrollView[position], linearParams);

		mPullScrollView[position].setPullLoadEnabled(true);
		mPullScrollView[position].setScrollLoadEnabled(true);
		mScrollView[position] = mPullScrollView[position].getRefreshableView();
		mScrollView[position].setVerticalScrollBarEnabled(false);

		View view = null;
		if (position == 0) {
			view = LayoutInflater.from(MainActivity.this).inflate(
					R.layout.loading_item_second, null);
			RelativeLayout relativeLayout = (RelativeLayout) view
					.findViewById(R.id.liner_second);
			MyTools.getHight(relativeLayout, width, height, MainActivity.this);
			mListView[position] = (My_ListView) view
					.findViewById(R.id.main_first_list);
			final My_ViewPager my_ViewPager = (My_ViewPager) view
					.findViewById(R.id.pic_viewPager);
			my_ViewPager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int arg0) {
					for (int i = 0; i < imgs.length; i++) {
						if (i == arg0 % mImageViews.length) {
							imgs[i].setBackgroundResource(R.drawable.second_view6);
							// imgs[i].setBackground(MyTools.getDrawableImg(MainActivity.this,
							// R.drawable.second_view6));
						} else {
							imgs[i].setBackgroundResource(R.drawable.second_view5);
							// imgs[i].setBackground(MyTools.getDrawableImg(MainActivity.this,
							// R.drawable.second_view5));
						}
					}
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {

				}
			});
			// ���ֵ���¼�
			my_ViewPager.setOnSingleTouchListener(new OnSingleTouchListener() {
				@Override
				public void onSingleTouch() {
					try {
						// Log.i("11111", "9999999999999999999999");
						if (mArray_ad
								.getJSONObject(my_ViewPager.getCurrentItem())
								.getString("LinkUrl").contains("Find")) {
							dialog.loading();
							AllStaticMessage.Back_to_Find = true;
							dialog.stop();
						} else if (mArray_ad
								.getJSONObject(my_ViewPager.getCurrentItem())
								.getString("LinkUrl").contains("Reg")) {
							Intent intent = new Intent(MainActivity.this,
									LoginActivity.class);
							startActivity(intent);
						}
//						else if(mArray_ad
//								.getJSONObject(my_ViewPager.getCurrentItem())
//								.getString("LinkUrl").contains("Active")){
//							
//						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			});
			LinearLayout layout_dian = (LinearLayout) view
					.findViewById(R.id.yuandian);
			imgs = new ImageView[mArray_ad.length()];
			for (int i = 0; i < imgs.length; i++) {
				ImageView imageView = new ImageView(MainActivity.this);// ����ͼƬ��
				// imageView.setLayoutParams(new
				// LayoutParams(20,20));
				// �����·�ͼƬ����
				imageView.setPadding(10, 0, 10, 5);// �ڱ߾�
				if (i == 0) {
					imageView.setBackgroundResource(R.drawable.second_view6);
					// imageView.setBackground(MyTools.getDrawableImg(MainActivity.this,
					// R.drawable.second_view6));
				} else {
					imageView.setBackgroundResource(R.drawable.second_view5);
					// imageView.setBackground(MyTools.getDrawableImg(MainActivity.this,
					// R.drawable.second_view5));
				}
				imgs[i] = imageView;
				layout_dian.addView(imageView);
			}
			mImageViews = new ImageView[imgs.length];
			inImg();
			MyPicAdapter adapter = new MyPicAdapter();
			my_ViewPager.setAdapter(adapter);
		} else {
			view = LayoutInflater.from(MainActivity.this).inflate(
					R.layout.loading_item_thread, null);
			mListView[position] = (My_ListView) view
					.findViewById(R.id.main_other_list);
		}

		mListView[position].setVerticalScrollBarEnabled(false);
		mListView[position].setFooterDividersEnabled(false);
		try {
			mPullScrollView[position]
					.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
						@Override
						public void onPullDownToRefresh(
								PullToRefreshBase<ScrollView> refreshView) {
							// getListData(mListView, id);
							if (mData.get(position) != null) {
								mData.get(position).clear();
							}
							if (id.equals("0")) {
								getListData(mListView[position], id, position,
										mPullScrollView[position]);
							} else {
								getListData(mListView[position],
										classion[position - 1], position,
										mPullScrollView[position]);
							}
						}

						@Override
						public void onPullUpToRefresh(
								PullToRefreshBase<ScrollView> refreshView) {
							// Toast.makeText(MainActivity.this,
							// "1111111111111", 500).show();
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									mImg_ZhiDing.setVisibility(View.VISIBLE);
									mPullScrollView[position]
											.onPullUpRefreshComplete();
								}
							}, 1200);

							// getListData(mListView[position],
							// id,position,mPullScrollView[position]);
						}
					});
			if (id.equals("0")) {
				getListData(mListView[position], id, position,
						mPullScrollView[position]);
			} else {
				getListData(mListView[position], classion[position - 1],
						position, mPullScrollView[position]);
			}

			mScrollView[position].smoothScrollTo(0, 20);
			mScrollView[position].addView(view);
			mListView[position].setFocusable(false);
		} catch (IllegalStateException e) {

		}
	}

	/*
	 * ��ʼ������
	 */
	private void initData() {
		LayoutInflater my_inflater = getLayoutInflater();
		for (int i = 0; i < mBtn.length + 1; i++) {
			vList.add(my_inflater.inflate(R.layout.loading_item_first, null));
		}
	}

	/*
	 * ˫���˳�
	 */
	private long mExitTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "�ٰ�һ���˳�������", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				this.finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		setContentView(R.layout.view_null);
		super.onDestroy();
		if (dialog != null)
			dialog.stop();

		for (int i = 0; i < adapter.length; i++) {
			ImageView imageView = adapter[0].getItemView();
			BitmapUtil.releaseImageViewResouce(imageView);
		}

		mLayout = null;
		button = null;
		mViewPager = null;
		mBtn = null;
		vList = null;
		img_wenzi = null;
		mLayout = null;
		mArray = null;
		mLinearLayout = null;
		mRelativeLayout = null;
		mData = null;
		adapter = null;
		mPullScrollView = null;
		mListView = null;
		mImageView = null;
		System.gc();
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
