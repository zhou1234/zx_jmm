package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.adapter.MyGoodsListAdapter;
import com.jifeng.image.ImageLoader;
import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
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
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class PinPaiZhuangChangActivity extends Activity {
	private Intent mIntent;
	private TextView mText_title;
	private LoadingDialog dialog;
	private TasckActivity tasckActivity;
	private Button btn_moren, btn_news, btn_rexiao, btn_price;
	private ImageView mImage_price;
	private PullToRefreshScrollView mPullScrollView;

	private MyGoodsListAdapter mAdapter;
	private My_GridView mGridView;
	private ImageView mImageView;
	// private Button mBtn_YouHui;
	private TextView mText_MeiMiaoShuo, mText_miao_say;// mText_Time,
	ImageLoader imageLoader, mILoader;
	private ImageView mDongHua;
	LinearLayout mLayout;
	private boolean oneFlag = true;
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0x01:
				mDongHua.setVisibility(View.VISIBLE);
				Animation hyperspaceJumpAnimation = AnimationUtils
						.loadAnimation(PinPaiZhuangChangActivity.this,
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
	private int pageNum = 1, TotailPage = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_list);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		dialog.loading();
		mILoader = new ImageLoader(this, "");
		WindowManager manager = getWindowManager();
		width = manager.getDefaultDisplay().getWidth();
		height = manager.getDefaultDisplay().getHeight();
		mListData = new ArrayList<JSONObject>();
		initData();
		findView();
		register();
		getData(getIntent().getStringExtra("id").toString(), "0",
				String.valueOf(pageNum));
		tasckActivity = new TasckActivity();
		tasckActivity.pushActivity(PinPaiZhuangChangActivity.this);
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
//		imageLoader.clearCache();
//		setContentView(R.layout.view_null);
//		mText_title = null;
//		dialog = null;
//		tasckActivity.popActivity(PinPaiZhuangChangActivity.this);
//		tasckActivity = null;
//		btn_moren = null;
//		btn_news = null;
//		btn_rexiao = null;
//		btn_price = null;
//		mImage_price = null;
//		mPullScrollView = null;
//
//		mAdapter = null;
//		mGridView = null;
//		mImageView = null;
//		mText_MeiMiaoShuo = null;
//		mText_miao_say = null;
//		imageLoader = null;
//		mILoader = null;
//		mDongHua = null;
//		mLayout = null;
//		mIntent = null;
//		handler = null;
//		this.finish();
//		System.gc();
	}

	// 注册事件

	// 查找控件
	private void findView() {
		mText_title = (TextView) findViewById(R.id.textview_title);
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
	}

	// 注册事件
	private void register() {
		mPullScrollView = new PullToRefreshScrollView(this, handler);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.goodslist_liner_pullrefresh);
		RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		layout.addView(mPullScrollView, linearParams);
		mPullScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						// TODO Auto-generated method stub
						pageNum = 1;
						if (mListData != null) {
							mListData.clear();
						}
						if (mAdapter != null) {
							mAdapter.notifyDataSetChanged();
						}
						getData(getIntent().getStringExtra("id").toString(),
								loadFlag, "1");
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						pageNum++;
						if (pageNum < TotailPage) {
							getData(getIntent().getStringExtra("id").toString(),
									loadFlag, String.valueOf(pageNum));
						} else {
							Toast.makeText(PinPaiZhuangChangActivity.this,
									"内容已加载完毕", 500).show();
							mPullScrollView.onPullUpRefreshComplete();
						}

					}
				});
		mPullScrollView.setPullLoadEnabled(true);
		mPullScrollView.setScrollLoadEnabled(true);
		ScrollView mScrollView = mPullScrollView.getRefreshableView();

		View view = LayoutInflater.from(PinPaiZhuangChangActivity.this)
				.inflate(R.layout.item_scrollview_gridview, null);
		LinearLayout linearLayout = (LinearLayout) view
				.findViewById(R.id.goodslist_youhui_time);
		linearLayout.setVisibility(View.GONE);
		mText_MeiMiaoShuo = (TextView) view
				.findViewById(R.id.textView_meimiaoshuo);
		RelativeLayout relativeLayout = (RelativeLayout) view
				.findViewById(R.id.lll);

		mText_MeiMiaoShuo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mRelativeLayout_say.setVisibility(View.VISIBLE);
			}
		});
		mGridView = (My_GridView) view.findViewById(R.id.item_gridview);
		mImageView = (ImageView) view.findViewById(R.id.img_goodslist_tou);
		RelativeLayout mLayout = (RelativeLayout) view
				.findViewById(R.id.rel_top_2);
		MyTools.getHight(mLayout, width, height, PinPaiZhuangChangActivity.this);
		imageLoader = new ImageLoader(this, "长");
		imageLoader.DisplayImage("", mImageView);
		try {

			mScrollView.addView(view);
			mScrollView.smoothScrollTo(0, 20);
			mGridView.setFocusable(false);
		} catch (IllegalStateException e) {

		}
	}

	/*
	 * 初始化数据
	 */
	private void initData() {

	}

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.goodslist_back:// 返回
			finish();
			break;
		case R.id.goodslist_save:// 收藏品牌
			if (AllStaticMessage.Login_Flag.equals("")) {
				mIntent = new Intent(PinPaiZhuangChangActivity.this,
						LoginActivity.class);
				startActivity(mIntent);
			} else {
				AddSave(getIntent().getStringExtra("id").toString());
			}

			break;
		// case R.id.goodslist_share:// 分享
		//
		// break;
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
				getData(getIntent().getStringExtra("id").toString(), "3", "1");
				loadFlag = "3";
				oneFlag = false;
			} else {
				oneFlag = true;
				getData(getIntent().getStringExtra("id").toString(), "4", "1");
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
		getData(getIntent().getStringExtra("id").toString(),
				String.valueOf(num - 1), "1");
		loadFlag = String.valueOf(num - 1);
	}

	// 添加收藏
	private void AddSave(String goodsId) {
		dialog.loading();
		String url = AllStaticMessage.URL_AddTo_Save + AllStaticMessage.User_Id
				+ "&goodsId=" + "&brandId=" + goodsId + "&type=2";
		HttpUtil.get(url, PinPaiZhuangChangActivity.this, dialog,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").equals("true")) {
								Toast.makeText(
										PinPaiZhuangChangActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							} else {
								Toast.makeText(
										PinPaiZhuangChangActivity.this,
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
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// 错误返回JSONObject
						dialog.stop();
					}
				});
	}

	//
	private void setView(int num) {
		btn_moren.setTextColor(getResources().getColor(R.color.text_color));
		btn_news.setTextColor(getResources().getColor(R.color.text_color));
		btn_rexiao.setTextColor(getResources().getColor(R.color.text_color));
		btn_price.setTextColor(getResources().getColor(R.color.text_color));
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
		String url = AllStaticMessage.URL_PinPai + id + "&sort=" + type
				+ "&pageNum=" + page;
		// Log.i("11111", url);
		HttpUtil.get(url, PinPaiZhuangChangActivity.this, dialog,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
								// MyTools.downImg(AllStaticMessage.URL_GBase+response.getString("Picture").toString(),mImageView);

								JSONArray mArray = response
										.getJSONArray("Results");
								if (mArray.length() > 0) {
									for (int i = 0; i < mArray.length(); i++) {
										mListData.add(mArray.getJSONObject(i));
									}
									if (mAdapter == null) {
										TotailPage = Integer.parseInt(response
												.getString("totalPage")
												.toString());
										mText_title.setText(response.getString(
												"BrandName").toString());
										mText_MeiMiaoShuo.setText(response
												.getString("MeimiaoSpeak")
												.toString());
										mText_miao_say.setText(response
												.getString("MeimiaoSpeak")
												.toString());
										mAdapter = new MyGoodsListAdapter(
												mListData,
												PinPaiZhuangChangActivity.this,
												getIntent()
														.getStringExtra("id")
														.toString(), width,
												height);// ,mILoader
										mGridView.setAdapter(mAdapter);
									} else {
										mAdapter.notifyDataSetChanged();
									}

								}
							} else {
								Toast.makeText(
										PinPaiZhuangChangActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mPullScrollView.onPullDownRefreshComplete();
						mPullScrollView.onPullUpRefreshComplete();
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
						// TODO Auto-generated method stub
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						// 错误返回JSONObject
						mPullScrollView.onPullDownRefreshComplete();
						mPullScrollView.onPullUpRefreshComplete();
						if (dialog != null) {
							dialog.stop();
						}
					}
				});
	}

	// private void setTime(String starttime, String endtime,Button btn) {
	// int data = MyTools.creayTime(endtime, MyTools.getTime());
	// if(data<0){
	// btn.setText("活动已结束");
	// }else if(MyTools.creayTime(MyTools.getTime(),starttime)<0){
	// btn.setText("活动还未开始");
	// }else{
	// btn.setText("还剩"+String.valueOf(data)+"天");
	// }
	// }
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
