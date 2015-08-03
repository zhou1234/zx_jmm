package com.jifeng.mlsales.jumeimiao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.i;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class GoodsListActivity extends Activity implements
		OnHeaderRefreshListener, OnFooterLoadListener {
	private AlwaysMarqueeTextView mText_title;
	private LoadingDialog dialog;
	private TasckActivity tasckActivity;
	private Button btn_moren, btn_news, btn_rexiao, btn_price;
	private ImageView mImage_price;
	private AbPullToRefreshView abPullToRefreshView;
	private ListView lv_goodsList;

	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");
	private MyGoodsListAdapter mAdapter;
	private ImageView mImageView;
	private Button mBtn_YouHui;
	private LinearLayout mLayout;
	private boolean oneFlag = true;
	private boolean flag_meimiaoshuo;

	private String shareTitle = "";
	private String shareUrl = "";
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

		}
	};

	private List<JSONObject> mListData;
	private int width, height;
	private String loadFlag = "0";
	private int pageNum = 1;
	private DisplayImageOptions options;
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
		mImg_Zhiding = (ImageView) findViewById(R.id.goodslist_zhiding);
		lv_goodsList = (ListView) findViewById(R.id.lv_goodsList);
		abPullToRefreshView = (AbPullToRefreshView) findViewById(R.id.abPullToRefreshView);

		abPullToRefreshView.setOnHeaderRefreshListener(this);
		abPullToRefreshView.setOnFooterLoadListener(this);
	}

	// 注册事件
	@SuppressLint("NewApi")
	private void register() {
		View view = LayoutInflater.from(GoodsListActivity.this).inflate(
				R.layout.item_scrollview_gridview_new, null);
		lv_goodsList.addHeaderView(view);
		lv_goodsList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {

			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				if ((arg1 + arg2) == mListData.size() / 2) {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							pageNum++;
							mImg_Zhiding.setVisibility(View.VISIBLE);
							getDataNum(id, loadFlag, String.valueOf(pageNum));
						}
					}, 0);

				}
			}
		});
		final TextView tv_meimiaoshuo = (TextView) view
				.findViewById(R.id.tv_meimiaoshuo);
		final ImageView iv_DownAndUp = (ImageView) view
				.findViewById(R.id.iv_downAndUp);
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

		ImageLoader.getInstance().displayImage(imgurl, mImageView, options);

	}

	// private class TouchListenerImpl implements OnTouchListener {
	// @Override
	// public boolean onTouch(View view, MotionEvent motionEvent) {
	// switch (motionEvent.getAction()) {
	// case MotionEvent.ACTION_DOWN:
	//
	// break;
	// case MotionEvent.ACTION_MOVE:
	// int scrollY = view.getScrollY();
	// int height = view.getHeight();
	// int scrollViewMeasuredHeight = mScrollView.getChildAt(0)
	// .getMeasuredHeight();
	// if (scrollY == 0) {
	// System.out.println("滑动到了顶端 view.getScrollY()=" + scrollY);
	// }
	// if ((scrollY + height)>= (scrollViewMeasuredHeight/2)) {
	// System.out.println("滑动到了底部 scrollY=" + scrollY);
	// System.out.println("滑动到了底部 height=" + height);
	// System.out.println("滑动到了底部 scrollViewMeasuredHeight="
	// + scrollViewMeasuredHeight);
	// }
	// break;
	//
	// default:
	// break;
	// }
	// return false;
	// }
	//
	// };

	// //xml注册点击事件的实现
	public void doclick(View view) {
		switch (view.getId()) {
		case R.id.goodslist_back:// 返回
			ImageLoader.getInstance().clearMemoryCache();
			ImageLoader.getInstance().clearDiskCache();
			finish();
			break;
		case R.id.goodslist_save:// 收藏品牌
			// if (AllStaticMessage.Login_Flag.equals("")) {
			// mIntent = new Intent(GoodsListActivity.this,
			// LoginActivity.class);
			// startActivity(mIntent);
			// } else {
			// AddSave(pinpaiId);
			// }
			share(shareTitle, "美喵家居，中国最大的家居生活特卖商城。正品精选，便宜到落泪，好用到心碎，幸福感加倍",
					shareUrl, imgurl);
			break;
		case R.id.goodslist_zhiding:
			mImg_Zhiding.setVisibility(View.GONE);
			lv_goodsList.setSelection(0);
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
				+ "&pageNum=" + page + "&pageSize=20";
		// Log.i("11111", url);
		HttpUtil.get(url, GoodsListActivity.this, dialog,
				new JsonHttpResponseHandler() {

					@SuppressLint({ "ShowToast", "UseSparseArrays" })
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
								JSONArray mArray = response
										.getJSONArray("Results");
								if (mListData != null) {
									mListData.clear();
								}
								if (mArray != null && mArray.length() > 0) {
									for (int i = 0; i < mArray.length(); i++) {
										mListData.add(mArray.getJSONObject(i));
									}
									shareTitle = response.getString("Title")
											.toString();
									shareUrl = AllStaticMessage.URL_Goods_list_share
											+ GoodsListActivity.this.id
											+ "&activeName=" + shareTitle;
									mText_title.setText(shareTitle);

									mAdapter = new MyGoodsListAdapter(
											mListData, GoodsListActivity.this,
											GoodsListActivity.this.id, width,
											height);
									Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
									if (mListData.size() % 2 == 0) {
										for (int i = 0; i < mListData.size(); i++) {
											map.put(i, false);
										}
									} else {
										for (int i = 0; i < mListData.size() + 1; i++) {
											if (i == mListData.size()) {
												map.put(i, true);
											} else {
												map.put(i, false);
											}
										}
									}
									mAdapter.setIndex(map);
									lv_goodsList.setAdapter(mAdapter);

								}
								abPullToRefreshView.onHeaderRefreshFinish();
							} else {
								Toast.makeText(
										GoodsListActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}
							if (dialog != null) {
								dialog.stop();
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

	private void getDataNum(String id, String type, String page) {
		String url = AllStaticMessage.URL_Goods_List + id + "&sort=" + type
				+ "&pageNum=" + page + "&pageSize=10";
		HttpUtil.get(url, GoodsListActivity.this, dialog,
				new JsonHttpResponseHandler() {

					@SuppressLint({ "ShowToast", "UseSparseArrays" })
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
								JSONArray mArray = response
										.getJSONArray("Results");
								if (mArray != null && mArray.length() > 0) {
									for (int i = 0; i < mArray.length(); i++) {
										mListData.add(mArray.getJSONObject(i));
									}

									if (mAdapter != null) {
										Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
										if (mListData.size() % 2 == 0) {
											for (int i = 0; i < mListData
													.size(); i++) {
												map.put(i, false);
											}
										} else {
											for (int i = 0; i < mListData
													.size() + 1; i++) {
												if (i == mListData.size()) {
													map.put(i, true);
												} else {
													map.put(i, false);
												}
											}
										}
										mAdapter.setIndex(map);
										mAdapter.notifyDataSetChanged();
									}

								}
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
					}
				});
	}

	private void share(String title, String content, String url, String imgurl) {
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字
		oks.setNotification(R.drawable.icon, getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(title);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(url);// "http://www.gfeng.com.cn"
		// text是分享文本，所有平台都需要这个字段
		oks.setText(content + url);

		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// File file = new File(
		// android.os.Environment.getExternalStorageDirectory()
		// + "/JuMeiMiao/pic/Myshare.jpg");
		// if (file.exists()) {
		// oks.setImagePath(android.os.Environment
		// .getExternalStorageDirectory()
		// + "/JuMeiMiao/pic/Myshare.jpg");// 确保SDcard下面存在此张图片
		// }
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(url);
		oks.setImageUrl(imgurl);
		// oks.setFilePath(android.os.Environment.getExternalStorageDirectory()
		// + "/JuMeiMiao/pic/Myshare.jpg");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		// oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(url);// "http://www.gfeng.com.cn"

		// 启动分享GUI
		oks.show(GoodsListActivity.this);
		// Toast.makeText(getApplicationContext(), "打开url:" + url,
		// Toast.LENGTH_SHORT).show();
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
		abPullToRefreshView.onFooterLoadFinish();

	}

	@Override
	public void onHeaderRefresh(AbPullToRefreshView view) {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				pageNum = 1;
				getData(id, loadFlag, "1");
			}
		}, 1000);

	}

}
