package com.jifeng.mlsales.jumeimiao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jifeng.adapter.MyGoodsListAdapter;
import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.AlwaysMarqueeTextView;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.myview.My_GridView;
import com.jifeng.myview.PullToRefreshScrollView;
import com.jifeng.pulltorefresh.PullToRefreshBase;
import com.jifeng.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.jifeng.tools.MyTools;
import com.jifeng.tools.TasckActivity;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class GoodsListActivity extends Activity {
	private Intent mIntent;
	private AlwaysMarqueeTextView mText_title;
	private LoadingDialog dialog;
	private TasckActivity tasckActivity;
	private Button btn_moren, btn_news, btn_rexiao, btn_price;
	private ImageView mImage_price;
	private PullToRefreshScrollView mPullScrollView;

	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");
	private MyGoodsListAdapter mAdapter;
	private My_GridView mGridView;
	private ImageView mImageView;
	private Button mBtn_YouHui;
	private TextView mText_Time, mText_MeiMiaoShuo, mText_miao_say;
	private ImageView mDongHua;
	private LinearLayout mLayout;
	private boolean oneFlag = true;
	private boolean flag_meimiaoshuo;
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0x01:
				mDongHua.setVisibility(View.VISIBLE);
				Animation hyperspaceJumpAnimation = AnimationUtils
						.loadAnimation(GoodsListActivity.this,
								R.anim.puch_up_in);
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

	private List<JSONObject> mListData;
	int width, height;
	private RelativeLayout mRelativeLayout_say;
	private String loadFlag = "0";
	private int pageNum = 1;
	private String pinpaiId = "";
	private DisplayImageOptions options;
	private ScrollView mScrollView;
	private ImageView mImg_Zhiding;

	private String id = "", youhui = "", time = "", text = "", imgurl = "";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_list);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		dialog.loading();
		options = MyTools.createOptions(R.drawable.loading_01);

		WindowManager manager = getWindowManager();
		width = manager.getDefaultDisplay().getWidth();
		height = manager.getDefaultDisplay().getHeight();
		mListData = new ArrayList<JSONObject>();
		Intent intent = getIntent();
		if (intent != null) {
			String active = intent.getStringExtra("active");
			if (active.equals("0")) {
				id = intent.getStringExtra("id").toString();
				youhui = intent.getStringExtra("youhui").toString();
				time = intent.getStringExtra("time").toString();
				text = intent.getStringExtra("text").toString();
				imgurl = intent.getStringExtra("imgurl").toString();
				findView();
				register();
				getData(id, "0", String.valueOf(pageNum));
			} else {
				String activeId = intent.getStringExtra("activeId");
				getActiveDetial(activeId);
			}
		}
		tasckActivity = new TasckActivity();
		tasckActivity.pushActivity(GoodsListActivity.this);
	}

	private void getActiveDetial(String activeId) {
		String url = AllStaticMessage.active_url + activeId;
		HttpUtil.get(url, GoodsListActivity.this, dialog,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {

								JSONObject jsonObject = response
										.getJSONObject("Results");
								id = jsonObject.getString("Id").toString();
								// youhui = jsonObject.getString("DesGuide")
								// .toString();
								int data = MyTools.creayTime(jsonObject
										.getString("EndTime").toString(),
										MyTools.getTime());
								if (data < 0) {
									time = "已结束";
								} else {
									time = "仅剩" + String.valueOf(data) + "天";
								}
								text = jsonObject.getString("MeimiaoSpeak")
										.toString();

								imgurl = AllStaticMessage.URL_GBase
										+ jsonObject.getString("ActivityPic")
												.toString();
								findView();
								register();
								getData(id, "0", String.valueOf(pageNum));

							} else {
								Toast.makeText(
										GoodsListActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}

						} catch (JSONException e) {
							e.printStackTrace();
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

	// 查找控件
	@SuppressLint("NewApi")
	private void findView() {
		mText_title = (AlwaysMarqueeTextView) findViewById(R.id.textview_title);
		btn_moren = (Button) findViewById(R.id.goods_list_btn_moren);
		btn_news = (Button) findViewById(R.id.goods_list_btn_news);
		btn_rexiao = (Button) findViewById(R.id.goods_list_btn_rexiao);
		btn_price = (Button) findViewById(R.id.goods_list_btn_jiage);
		mImage_price = (ImageView) findViewById(R.id.goods_list_img_jiage);
		mDongHua = (ImageView) findViewById(R.id.img_wenzi);
		mLayout = (LinearLayout) findViewById(R.id.main_rel);
		mText_miao_say = (TextView) findViewById(R.id.text_miao_say);
		mText_miao_say.setMovementMethod(ScrollingMovementMethod.getInstance());
		mRelativeLayout_say = (RelativeLayout) findViewById(R.id.rel_say);
		mImg_Zhiding = (ImageView) findViewById(R.id.goodslist_zhiding);
	}

	// 注册事件
	@SuppressLint("NewApi")
	private void register() {

		mPullScrollView = new PullToRefreshScrollView(this, handler);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.goodslist_liner_pullrefresh);
		@SuppressWarnings("deprecation")
		RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		layout.addView(mPullScrollView, linearParams);
		mPullScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						pageNum = 1;
						if (mListData != null) {
							mListData.clear();
						}
						if (mAdapter != null) {
							mAdapter.notifyDataSetChanged();
						}
						getData(id, loadFlag, "1");
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						pageNum++;
						mImg_Zhiding.setVisibility(View.VISIBLE);
						getData(id, loadFlag, String.valueOf(pageNum));

					}
				});
		mPullScrollView.setPullLoadEnabled(true);
		mPullScrollView.setScrollLoadEnabled(true);
		mScrollView = mPullScrollView.getRefreshableView();

		mScrollView.setVerticalScrollBarEnabled(false);

		// View view = LayoutInflater.from(GoodsListActivity.this).inflate(
		// R.layout.item_scrollview_gridview, null);
		// mBtn_YouHui = (Button) view.findViewById(R.id.goodslist_youhui);
		// mText_Time = (TextView) view.findViewById(R.id.item_goodslist_time);
		// mText_MeiMiaoShuo = (TextView) view
		// .findViewById(R.id.textView_meimiaoshuo);
		// RelativeLayout relativeLayout = (RelativeLayout) view
		// .findViewById(R.id.lll);
		// if (youhui.equals("")) {
		// relativeLayout.setVisibility(View.INVISIBLE);
		// } else {
		// mBtn_YouHui.setText(youhui);
		// relativeLayout.setVisibility(View.VISIBLE);
		// }
		//
		// mText_Time.setText(time);
		// mText_MeiMiaoShuo.setText(text);
		// mText_miao_say.setText(text);
		// mText_MeiMiaoShuo.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// mRelativeLayout_say.setVisibility(View.VISIBLE);
		// }
		// });
		// mGridView = (My_GridView) view.findViewById(R.id.item_gridview);
		// mImageView = (ImageView) view.findViewById(R.id.img_goodslist_tou);
		// RelativeLayout mLayout = (RelativeLayout) view
		// .findViewById(R.id.rel_top_2);
		// RelativeLayout mLayout_2 = (RelativeLayout) view
		// .findViewById(R.id.menban_miao);

		View view = LayoutInflater.from(GoodsListActivity.this).inflate(
				R.layout.item_scrollview_gridview_new, null);
		
		final TextView tv_meimiaoshuo = (TextView) view
				.findViewById(R.id.tv_meimiaoshuo);
		final ImageView iv_DownAndUp = (ImageView) view
				.findViewById(R.id.iv_downAndUp);
		mGridView = (My_GridView) view.findViewById(R.id.item_gridview);
		mImageView = (ImageView) view.findViewById(R.id.iv_meimiao);
		MyTools.setWidthAndHeight(mImageView, width);
		
		tv_meimiaoshuo.setText(text);
		view.findViewById(R.id.ll_downAndUp).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						flag_meimiaoshuo = !flag_meimiaoshuo;
						if (flag_meimiaoshuo) {
							iv_DownAndUp.setImageResource(R.drawable.img_up);
							tv_meimiaoshuo.setMaxLines(100);
						} else {
							iv_DownAndUp.setImageResource(R.drawable.img_down);
							tv_meimiaoshuo.setMaxLines(2);
						}
					}
				});

		ImageLoader.getInstance().displayImage(imgurl, mImageView,options);
		try {
			mScrollView.smoothScrollTo(0, 20);
			mScrollView.addView(view);
			mGridView.setFocusable(false);
		} catch (IllegalStateException e) {

		}
	}

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.goodslist_back:// 返回
			ImageLoader.getInstance().clearMemoryCache();
			ImageLoader.getInstance().clearDiskCache();
			finish();
			break;
		case R.id.goodslist_save:// 收藏品牌
			if (AllStaticMessage.Login_Flag.equals("")) {
				mIntent = new Intent(GoodsListActivity.this,
						LoginActivity.class);
				startActivity(mIntent);
			} else {
				AddSave(pinpaiId);
			}

			break;
		case R.id.goodslist_zhiding:
			if (mAdapter != null) {
				mImg_Zhiding.setVisibility(View.GONE);
				mScrollView.smoothScrollTo(0, 1);
			}
			break;
		case R.id.goods_list_btn_moren:// 默认
			sort(1);
			break;
		case R.id.goods_list_btn_news:// 最新
			sort(2);
			break;
		case R.id.goods_list_btn_rexiao:// 热销
			sort(3);
			break;
		case R.id.goods_list_btn_jiage:// 价格
			dialog.loading();
			setView(4);
			if (mListData != null) {
				mListData.clear();
			}
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
			if (oneFlag) {
				getData(id, "3", "1");
				loadFlag = "3";
				oneFlag = false;
			} else {
				oneFlag = true;
				getData(id, "4", "1");
				loadFlag = "4";
			}
			break;
		case R.id.rel_say:
			mRelativeLayout_say.setVisibility(View.GONE);
			break;
		case R.id.text_miao_say:
			mRelativeLayout_say.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	private void sort(int num) {
		dialog.loading();
		pageNum = 1;
		setView(num);
		if (mListData != null) {
			mListData.clear();
		}
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		getData(id, String.valueOf(num - 1), "1");
		loadFlag = String.valueOf(num - 1);
	}

	// 添加收藏
	@SuppressLint("ShowToast")
	private void AddSave(String goodsId) {
		dialog.loading();
		String url = AllStaticMessage.URL_AddTo_Save + AllStaticMessage.User_Id
				+ "&goodsId=" + "&brandId=" + goodsId + "&type=2";
		HttpUtil.get(url, GoodsListActivity.this, dialog,
				new JsonHttpResponseHandler() {

					@SuppressLint("ShowToast")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								Toast.makeText(
										GoodsListActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							} else {
								Toast.makeText(
										GoodsListActivity.this,
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

	//
	private void setView(int num) {
		btn_moren.setTextColor(getResources().getColor(R.color.black));
		btn_news.setTextColor(getResources().getColor(R.color.black));
		btn_rexiao.setTextColor(getResources().getColor(R.color.black));
		btn_price.setTextColor(getResources().getColor(R.color.black));
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.transparent);
		btn_moren.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
				drawable);
		btn_news.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
				drawable);
		btn_rexiao.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
				drawable);
		btn_price.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
				drawable);
		Drawable mDrawable = this.getResources().getDrawable(
				R.drawable.goods_list_xian);
		mImage_price.setImageDrawable(getResources().getDrawable(
				R.drawable.goods_list_down_up));
		switch (num) {
		case 1:
			btn_moren.setTextColor(getResources().getColor(R.color.tab_select));
			btn_moren.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					mDrawable);
			break;
		case 2:
			btn_news.setTextColor(getResources().getColor(R.color.tab_select));
			btn_news.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					mDrawable);
			break;
		case 3:
			btn_rexiao
					.setTextColor(getResources().getColor(R.color.tab_select));
			btn_rexiao.setCompoundDrawablesWithIntrinsicBounds(null, null,
					null, mDrawable);
			break;
		case 4:
			btn_price.setTextColor(getResources().getColor(R.color.tab_select));
			btn_price.setCompoundDrawablesWithIntrinsicBounds(null, null, null,
					mDrawable);
			mImage_price.setImageDrawable(getResources().getDrawable(
					R.drawable.goods_list_down_up_2));

			break;

		default:
			break;
		}
	}

	private void getData(String id, String type, String page) {
		String url = AllStaticMessage.URL_Goods_List + id + "&sort=" + type
				+ "&pageNum=" + page;
		// Log.i("11111", url);
		HttpUtil.get(url, GoodsListActivity.this, dialog,
				new JsonHttpResponseHandler() {

					@SuppressLint("ShowToast")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
								// MyTools.downImg(AllStaticMessage.URL_GBase+response.getString("Picture").toString(),mImageView);
								pinpaiId = response.getJSONArray("Results")
										.getJSONObject(0).getString("Account")
										.toString();
								JSONArray mArray = response
										.getJSONArray("Results");
								if (mArray != null && mArray.length() > 0) {
									for (int i = 0; i < mArray.length(); i++) {
										mListData.add(mArray.getJSONObject(i));
									}
									mText_title.setText(response.getString(
											"Title").toString());
									if (mAdapter == null) {
										mAdapter = new MyGoodsListAdapter(
												mListData,
												GoodsListActivity.this,
												GoodsListActivity.this.id,
												width, height);
										mGridView.setAdapter(mAdapter);
									} else if (mAdapter != null) {
										mAdapter.notifyDataSetChanged();
									}

								}

							} else {
								Toast.makeText(
										GoodsListActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}
							mPullScrollView.onPullDownRefreshComplete();
							mPullScrollView.onPullUpRefreshComplete();
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

		// setContentView(R.layout.view_null);

		// ImageLoader.getInstance().clearMemoryCache();
		// ImageLoader.getInstance().clearDiskCache();

		// if (handler != null) {
		// handler.removeMessages(0x01);
		// handler.removeMessages(0x02);
		// handler.removeMessages(0x03);
		// }
		//
		// handler = null;
		//
		// if (dialog != null)
		// dialog.stop();

		// mDateFormat = null;
		// tasckActivity.popActivity(GoodsListActivity.this);
		// tasckActivity = null;
		//
		// mIntent = null;
		// mText_title = null;
		// dialog = null;
		// btn_moren = null;
		// btn_news = null;
		// btn_rexiao = null;
		// btn_price = null;
		// mImage_price = null;
		// mPullScrollView = null;
		//
		// mDateFormat = null;
		// mAdapter = null;
		//
		// mGridView = null;
		// mImageView = null;
		// mBtn_YouHui = null;
		// mText_Time = null;
		// mText_MeiMiaoShuo = null;
		// mText_miao_say = null;
		// mDongHua = null;
		// mLayout = null;
		// mListData.clear();
		// mListData = null;
		// mRelativeLayout_say = null;
		// this.finish();
		//
		// System.gc();
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
