package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.annotation.SuppressLint;
import android.app.Activity;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnFooterLoadListener;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.mlsales.photo.TagInfoModel;
import com.jifeng.mlsales.photo.TagsView;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.myview.My_ViewPager;
import com.jifeng.myview.My_ViewPager.OnSingleTouchListener;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("HandlerLeak")
public class MeiMiaoQuanActivity extends Activity implements OnClickListener {
	private ImageView iv_photo;
	private LinearLayout ll_tuiJian, ll_guanZhu;
	private TextView tv_tuiJian, tv_guanZhu;
	private ImageView iv_tuiJian, iv_guanZhu;
	private int width, height;
	private ViewPager viewPager;
	private List<View> list;

	private ListView listView_tuiJian, listView_guanZhu;
	private ImageView[] imgs;
	private ImageView[] mImageViews;
	private JSONArray mArray_ad;

	private boolean flag_guanZhu, flag_zan, flag_guanZhu_guanZhu,
			flag_guanzhu_zan;

	private boolean flag_first, flag_tuijian;
	private List<JSONObject> mData, mDataGanZhu;
	private LoadingDialog dialog;

	private Map<Integer, Boolean> map;
	private Map<Integer, Boolean> map_guanzhu;

	private Map<Integer, Boolean> map_guanzhu_zan;
	private Map<Integer, Boolean> map_guanzhu_guanzhu;

	private AbPullToRefreshView mPullRefreshView = null;
	private AbPullToRefreshView mPullRefreshView_guanzhu = null;

	private TextView tv_wuGuanzhu;

	private LinearLayout layout_dian;
	private My_ViewPager my_ViewPager;

	private DisplayImageOptions options;
	private DisplayImageOptions option;

	private View heardView;

	private MyListViewGuanZhuAdapter guanZhuAdapter;
	private MyListViewAdapter tuijianadapter;
	private int tuijian_num = 1, guanzhu_num = 1;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meimiaoquan_activity);
		ShareSDK.initSDK(MeiMiaoQuanActivity.this);
		((FBApplication) getApplication()).addActivity(this);
		width = getWindowManager().getDefaultDisplay().getWidth();
		height = getWindowManager().getDefaultDisplay().getHeight();
		dialog = new LoadingDialog(this);
		dialog.loading();
		tuijianadapter = new MyListViewAdapter();
		guanZhuAdapter = new MyListViewGuanZhuAdapter();
		options = MyTools.createOptions(R.drawable.my_icon);
		option = MyTools.createOptions(R.drawable.img);
		init();

	}

	private void init() {
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		iv_photo.setOnClickListener(this);

		ll_tuiJian = (LinearLayout) findViewById(R.id.ll_tuiJian);
		ll_guanZhu = (LinearLayout) findViewById(R.id.ll_guanZhu);

		ll_tuiJian.setOnClickListener(this);
		ll_guanZhu.setOnClickListener(this);

		tv_tuiJian = (TextView) findViewById(R.id.tv_tuiJian);
		tv_guanZhu = (TextView) findViewById(R.id.tv_guanZhu);

		iv_tuiJian = (ImageView) findViewById(R.id.iv_tuiJian);
		iv_guanZhu = (ImageView) findViewById(R.id.iv_guanZhu);

		viewPager = (ViewPager) findViewById(R.id.viewPager);

		mData = new ArrayList<JSONObject>();
		mDataGanZhu = new ArrayList<JSONObject>();
		list = new ArrayList<View>();

		View tuijianView = getLayoutInflater().inflate(R.layout.tui_jian, null);
		listView_tuiJian = (ListView) tuijianView
				.findViewById(R.id.listView_tuiJian);

		listView_tuiJian.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {

			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				if ((arg1 + arg2) == mData.size() - 2) {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							tuijian_num++;
							getListTuiJianDataNum(listView_tuiJian, tuijian_num
									+ "");
						}
					}, 0);
				}

			}
		});

		mPullRefreshView = (AbPullToRefreshView) tuijianView
				.findViewById(R.id.mPullRefreshView);
		mPullRefreshView.setOnFooterLoadListener(new OnFooterLoadListener() {

			@Override
			public void onFooterLoad(AbPullToRefreshView view) {
				mPullRefreshView.onFooterLoadFinish();
			}
		});

		mPullRefreshView
				.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

					@Override
					public void onHeaderRefresh(AbPullToRefreshView view) {
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								tuijian_num = 1;
								getListTuiJianData(listView_tuiJian);
							}
						}, 1000);
					}
				});

		addHeardView();

		View guanzhuView = getLayoutInflater().inflate(R.layout.guan_zhu, null);
		listView_guanZhu = (ListView) guanzhuView
				.findViewById(R.id.listView_guanZhu);
		listView_guanZhu.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {

			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				if ((arg1 + arg2) == mDataGanZhu.size() - 2) {
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							guanzhu_num++;
							getListGuanZhuDataNum(listView_tuiJian, guanzhu_num
									+ "");
						}
					}, 0);

				}
			}
		});
		tv_wuGuanzhu = (TextView) guanzhuView.findViewById(R.id.tv_wuGuanzhu);
		mPullRefreshView_guanzhu = (AbPullToRefreshView) guanzhuView
				.findViewById(R.id.mPullRefreshView_guanzhu);

		mPullRefreshView_guanzhu
				.setOnFooterLoadListener(new OnFooterLoadListener() {

					@Override
					public void onFooterLoad(AbPullToRefreshView view) {
						mPullRefreshView_guanzhu.onFooterLoadFinish();
					}
				});
		mPullRefreshView_guanzhu
				.setOnHeaderRefreshListener(new OnHeaderRefreshListener() {

					@Override
					public void onHeaderRefresh(AbPullToRefreshView view) {
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								guanzhu_num = 1;
								getListGuanZhuData(listView_guanZhu);
							}
						}, 1000);
					}
				});

		list.add(tuijianView);
		list.add(guanzhuView);

		viewPager.setAdapter(new MyAdapter());
		viewPager.setOnPageChangeListener(new MyListener());
		if (AllStaticMessage.isShare) {
			viewPager.setCurrentItem(1);
			addListViewHeardView();
			flag_first = false;

		} else {
			if (!flag_tuijian) {
				dialog.loading();
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						getImgUrl(layout_dian, my_ViewPager);
						getListTuiJianData(listView_tuiJian);
						flag_tuijian = true;
					}
				}, 300);
			}
		}
	}

	private void addListViewHeardView() {
		heardView = getLayoutInflater().inflate(
				R.layout.item_guanzhu_heard_view, null);
		heardView.findViewById(R.id.iv_finish).setOnClickListener(
				new MyOnClickListener());
		heardView.findViewById(R.id.iv_weixin).setOnClickListener(
				new MyOnClickListener());
		heardView.findViewById(R.id.iv_weixin_p).setOnClickListener(
				new MyOnClickListener());
		heardView.findViewById(R.id.iv_sina).setOnClickListener(
				new MyOnClickListener());

		listView_guanZhu.addHeaderView(heardView);
	}

	private class MyOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.iv_finish:
				listView_guanZhu.removeHeaderView(heardView);
				break;
			case R.id.iv_weixin:
				share(Wechat.NAME);
				break;
			case R.id.iv_weixin_p:
				share(WechatMoments.NAME);
				break;
			case R.id.iv_sina:
				shareSina();
				break;
			default:
				break;
			}

		}

	}

	private void share(String name) {
		dialog.loading();
		ShareParams sp = new ShareParams();
		sp.setShareType(Platform.SHARE_WEBPAGE);
		sp.setTitle(AllStaticMessage.title);
		// sp.setTitleUrl(AllStaticMessage.url); // 标题的超链接
		sp.setUrl(AllStaticMessage.url);
		sp.setText(AllStaticMessage.title + AllStaticMessage.url);
		sp.setImageUrl(AllStaticMessage.imgurl);
		// sp.setSite(getString(R.string.app_name));
		// sp.setSiteUrl(AllStaticMessage.url);

		Platform platform = ShareSDK.getPlatform(name);
		platform.setPlatformActionListener(new PlatformActionListener() {

			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				if (dialog != null) {
					dialog.stop();
				}
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> arg2) {
				if (dialog != null) {
					dialog.stop();
				}
				handler.sendEmptyMessage(2);
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				if (dialog != null) {
					dialog.stop();
				}
				handler.sendEmptyMessage(3);
			}
		}); // 设置分享事件回调

		// 执行分享
		platform.share(sp);

	}

	private void shareSina() {
		dialog.loading();
		ShareParams sp = new ShareParams();
		sp.setText(AllStaticMessage.title + AllStaticMessage.url);
		sp.setImageUrl(AllStaticMessage.imgurl);

		Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
		// 设置分享监听
		weibo.setPlatformActionListener(new PlatformActionListener() {

			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				if (dialog != null) {
					dialog.stop();
				}
				handler.sendEmptyMessage(1);
			}

			@Override
			public void onComplete(Platform arg0, int arg1,
					HashMap<String, Object> arg2) {
				if (dialog != null) {
					dialog.stop();
				}
				handler.sendEmptyMessage(2);
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				if (dialog != null) {
					dialog.stop();
				}
				handler.sendEmptyMessage(3);
			}
		});
		// 执行分享
		weibo.share(sp);
	}

	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(MeiMiaoQuanActivity.this, "分享失败", 0).show();
				break;

			case 2:
				Toast.makeText(MeiMiaoQuanActivity.this, "分享成功", 0).show();
				listView_guanZhu.removeHeaderView(heardView);
				guanZhuAdapter.notifyDataSetChanged();
				break;

			case 3:
				Toast.makeText(MeiMiaoQuanActivity.this, "取消分享", 0).show();
				break;
			}

		};
	};

	private class MyListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mData.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			final int cont = arg0;
			try {
				final Holder holder;
				if (arg1 == null) {
					View view = getLayoutInflater().inflate(
							R.layout.listview_tuijian_item, arg2, false);
					holder = new Holder();
					holder.tv_content = (TextView) view
							.findViewById(R.id.tv_content);
					holder.iv_name = (ImageView) view
							.findViewById(R.id.iv_name);
					holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
					holder.bt_guanZhu = (Button) view
							.findViewById(R.id.bt_guanZhu);
					holder.tagsView = (TagsView) view
							.findViewById(R.id.tagsView1);
					holder.iv_zan = (ImageView) view.findViewById(R.id.iv_zan);
					holder.tv_zan = (TextView) view.findViewById(R.id.tv_zan);
					holder.iv_pingLun = (ImageView) view
							.findViewById(R.id.iv_pingLun);
					holder.tv_pingLun = (TextView) view
							.findViewById(R.id.tv_pingLun);
					holder.iv_fengXiang = (ImageView) view
							.findViewById(R.id.iv_fengXiang);
					Bitmap bitmap = BitmapFactory.decodeResource(
							getResources(), R.drawable.icon);
					Bitmap bitmap1 = MyTools.getRoundedCornerBitmap(bitmap);

					Bitmap bitmap2 = BitmapFactory.decodeResource(
							getResources(), R.drawable.img);
					holder.iv_name.setImageBitmap(bitmap1);
					holder.tagsView.setImage(bitmap2);
					view.setTag(holder);
					arg1 = view;
				} else {
					holder = (Holder) arg1.getTag();
				}

				String userImage = mData.get(arg0).getString("Photo");

				if (!userImage.equals("") && userImage != null) {
					ImageLoader.getInstance().displayImage(userImage,
							holder.iv_name, options);
				}
				holder.tv_name.setText(mData.get(arg0).getString("NickName"));
				holder.tv_content.setText(mData.get(arg0).getString("Content"));
				holder.tv_zan.setText(mData.get(arg0).getString(
						"SomePraiseCount")
						+ "赞");
				holder.tv_pingLun.setText(mData.get(arg0).getString(
						"ReviewCount"));
				String flag_zanString = mData.get(arg0).getString("ZanStatus");
				if (flag_zanString.equals("1")) {
					map.put(arg0, true);
				} else {
					map.put(arg0, false);
				}
				if (map.get(arg0)) {
					holder.iv_zan.setImageResource(R.drawable.img_zuan);
				} else {
					holder.iv_zan.setImageResource(R.drawable.img_zuan1);
				}
				String flag_guanzhuString = mData.get(arg0).getString(
						"ConcernStatus");
				if (flag_guanzhuString.equals("1")) {
					map_guanzhu.put(arg0, true);
				} else {
					map_guanzhu.put(arg0, false);
				}

				if (map_guanzhu.get(arg0)) {
					holder.bt_guanZhu.setBackground(getResources().getDrawable(
							R.drawable.img_guanzhu1));
					holder.bt_guanZhu.setText("已关注");
					holder.bt_guanZhu.setTextColor(getResources().getColor(
							R.color.text_color));
				} else {
					holder.bt_guanZhu.setBackground(getResources().getDrawable(
							R.drawable.img_guanzhu));
					holder.bt_guanZhu.setText("关注");
					holder.bt_guanZhu.setTextColor(getResources().getColor(
							R.color.tab_select));
				}

				final ArrayList<TagInfoModel> tagInfoModels = new ArrayList<TagInfoModel>();
				JSONArray jsonArray = mData.get(arg0).getJSONArray("UserTag");
				if (jsonArray.length() > 0) {

					List<TagInfoModel> tagInfos = new ArrayList<TagInfoModel>();
					for (int i = 0; i < jsonArray.length(); i++) {
						TagInfoModel infoModel = new TagInfoModel();
						infoModel.tag_name = jsonArray.getJSONObject(i)
								.getString("Content");
						float PositionX = Float.parseFloat(jsonArray
								.getJSONObject(i).getString("PositionX"));
						float PositionY = Float.parseFloat(jsonArray
								.getJSONObject(i).getString("PositionY"));
						infoModel.activityId = jsonArray.getJSONObject(i)
								.getString("ActivityId");
						infoModel.x = PositionX;
						infoModel.y = PositionY;
						tagInfos.add(infoModel);
					}
					tagInfoModels.addAll(tagInfos);
				}
				String tageViewUrl = mData.get(arg0).getString("ImageUrl");

				holder.tagsView.setTagInfoModels(tagInfoModels);
				if (!tageViewUrl.equals("") && tageViewUrl != null) {
					ImageLoader.getInstance().displayImage(tageViewUrl,
							holder.tagsView.getImageView(), option);
				}

				holder.bt_guanZhu.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg) {
						if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
							Intent mIntent = new Intent(
									MeiMiaoQuanActivity.this,
									LoginActivity.class);
							startActivityForResult(mIntent, 0x001);
						} else {
							try {
								if (map_guanzhu.get(cont)) {
									flag_guanZhu = true;
								} else {
									flag_guanZhu = false;
								}
								if (!flag_guanZhu) {
									GuanZhu(mData.get(cont).getString("UserId"));

									holder.bt_guanZhu
											.setBackground(getResources()
													.getDrawable(
															R.drawable.img_guanzhu1));
									holder.bt_guanZhu.setText("已关注");
									holder.bt_guanZhu
											.setTextColor(getResources()
													.getColor(
															R.color.text_color));
									JSONObject jsonObject = new JSONObject();
									jsonObject = mData.get(cont);

									jsonObject.put("ConcernStatus", 1);
									mData.set(cont, jsonObject);
									flag_guanZhu = true;
									map_guanzhu.put(cont, true);
								} else {
									Toast.makeText(MeiMiaoQuanActivity.this,
											"已关注过了哦", Toast.LENGTH_SHORT)
											.show();
									// holder.bt_guanZhu.setBackground(getResources()
									// .getDrawable(R.drawable.img_guanzhu));
									// holder.bt_guanZhu.setText("关注");
									// holder.bt_guanZhu.setTextColor(getResources()
									// .getColor(R.color.tab_select));
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
				holder.tagsView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						try {
							Intent intent = new Intent(
									MeiMiaoQuanActivity.this,
									GoodsDetailActivity.class);
							intent.putExtra("active", "1");
							intent.putExtra("shareUrl", mData.get(cont)
									.getString("ActivityShare"));
							intent.putExtra("detailUrl", mData.get(cont)
									.getString("IdGroup"));
							intent.putExtra("imgurl", mData.get(cont)
									.getString("ImageUrl"));
							startActivity(intent);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				});

				holder.iv_zan.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
							Intent mIntent = new Intent(
									MeiMiaoQuanActivity.this,
									LoginActivity.class);
							startActivity(mIntent);
						} else {
							try {
								int i = 0;
								int j = 0;
								if (map.get(cont)) {
									flag_zan = true;
								} else {
									flag_zan = false;
								}

								if (!flag_zan) {
									dianZan(mData.get(cont).getString("Id"));

									holder.iv_zan
											.setImageResource(R.drawable.img_zuan);
									JSONObject jsonObject = new JSONObject();
									jsonObject = mData.get(cont);
									i = Integer.parseInt(mData.get(cont)
											.getString("SomePraiseCount"));
									i++;
									jsonObject.put("SomePraiseCount", i + "");
									jsonObject.put("ZanStatus", 1);
									mData.set(cont, jsonObject);
									// flag_zan = true;
									holder.tv_zan.setText(i + "赞");
									map.put(cont, true);
								} else {
									CancelDianZan(mData.get(cont).getString(
											"Id"));
									holder.iv_zan
											.setImageResource(R.drawable.img_zuan1);
									JSONObject jsonObject = new JSONObject();
									jsonObject = mData.get(cont);
									j = Integer.parseInt(mData.get(cont)
											.getString("SomePraiseCount"));
									j--;
									jsonObject.put("SomePraiseCount", j + "");
									jsonObject.put("ZanStatus", 0);
									mData.set(cont, jsonObject);
									// flag_zan = true;
									holder.tv_zan.setText(j + "赞");
									map.put(cont, false);
								}

							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
				holder.iv_pingLun.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
							Intent mIntent = new Intent(
									MeiMiaoQuanActivity.this,
									LoginActivity.class);
							startActivity(mIntent);
						} else {
							try {
								Intent intent = new Intent(
										MeiMiaoQuanActivity.this,
										CommentActivity.class);
								intent.putExtra("id", mData.get(cont)
										.getString("Id"));
								startActivity(intent);
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

					}
				});
				holder.iv_fengXiang.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						try {
							share(mData.get(cont).getString("Content"),
									"",
									mData.get(cont).getString("ShareUrl")
											.toString(),
									AllStaticMessage.URL_GBase
											+ "/"
											+ mData.get(cont)
													.getString("ImageUrl")
													.toString());
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return arg1;
		}
	}

	/**
	 * 点赞
	 * 
	 * @param BaskOrderId
	 */
	private void dianZan(String BaskOrderId) {
		String url = AllStaticMessage.URL_Zan + "&BaskOrderId=" + BaskOrderId
				+ "&UserId=" + AllStaticMessage.User_Id;

		HttpUtil.get(url, MeiMiaoQuanActivity.this, null,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							if (response.getString("Status").equals("true")) {
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 取消点赞
	 * 
	 * @param BaskOrderId
	 */
	private void CancelDianZan(String BaskOrderId) {
		String url = AllStaticMessage.URL_CancelZan + "&BaskOrderId="
				+ BaskOrderId + "&UserId=" + AllStaticMessage.User_Id;

		HttpUtil.get(url, MeiMiaoQuanActivity.this, null,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							if (response.getString("Status").equals("true")) {
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 关注
	 * 
	 * @param ToCustomerId
	 */
	private void GuanZhu(String ToCustomerId) {
		String url = AllStaticMessage.URL_GuanZhu + "&UserId="
				+ AllStaticMessage.User_Id + "&ToCustomerId=" + ToCustomerId;

		HttpUtil.get(url, MeiMiaoQuanActivity.this, null,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							if (response.getString("Status").equals("true")) {
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	private class MyListViewGuanZhuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mDataGanZhu.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mDataGanZhu.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			final int cont = arg0;
			try {
				final Holder holder;
				if (arg1 == null) {
					View view = getLayoutInflater().inflate(
							R.layout.listview_tuijian_item, arg2, false);
					holder = new Holder();
					holder.tv_content = (TextView) view
							.findViewById(R.id.tv_content);
					holder.iv_name = (ImageView) view
							.findViewById(R.id.iv_name);
					holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
					holder.bt_guanZhu = (Button) view
							.findViewById(R.id.bt_guanZhu);
					holder.bt_guanZhu.setVisibility(View.GONE);
					holder.tagsView = (TagsView) view
							.findViewById(R.id.tagsView1);
					holder.iv_zan = (ImageView) view.findViewById(R.id.iv_zan);
					holder.tv_zan = (TextView) view.findViewById(R.id.tv_zan);
					holder.iv_pingLun = (ImageView) view
							.findViewById(R.id.iv_pingLun);
					holder.tv_pingLun = (TextView) view
							.findViewById(R.id.tv_pingLun);
					holder.iv_fengXiang = (ImageView) view
							.findViewById(R.id.iv_fengXiang);
					Bitmap bitmap = BitmapFactory.decodeResource(
							getResources(), R.drawable.icon);
					Bitmap bitmap1 = MyTools.getRoundedCornerBitmap(bitmap);

					Bitmap bitmap2 = BitmapFactory.decodeResource(
							getResources(), R.drawable.img);
					holder.iv_name.setImageBitmap(bitmap1);
					holder.tagsView.setImage(bitmap2);
					view.setTag(holder);
					arg1 = view;
				} else {
					holder = (Holder) arg1.getTag();
				}

				String userImage = mDataGanZhu.get(arg0).getString("Photo");

				if (!userImage.equals("") && userImage != null) {
					ImageLoader.getInstance().displayImage(userImage,
							holder.iv_name, options);
				}
				holder.tv_name.setText(mDataGanZhu.get(arg0).getString(
						"NickName"));
				holder.tv_content.setText(mDataGanZhu.get(arg0).getString(
						"Content"));
				holder.tv_zan.setText(mDataGanZhu.get(arg0).getString(
						"SomePraiseCount")
						+ "赞");
				holder.tv_pingLun.setText(mDataGanZhu.get(arg0).getString(
						"ReviewCount"));
				String flag_zanString = mDataGanZhu.get(arg0).getString(
						"ZanStatus");
				if (flag_zanString.equals("1")) {
					map_guanzhu_zan.put(arg0, true);
				} else {
					map_guanzhu_zan.put(arg0, false);
				}
				if (map_guanzhu_zan.get(arg0)) {
					holder.iv_zan.setImageResource(R.drawable.img_zuan);
				} else {
					holder.iv_zan.setImageResource(R.drawable.img_zuan1);
				}
				// String flag_guanzhuString = mData.get(arg0).getString(
				// "ConcernStatus");
				// if (flag_guanzhuString.equals("1")) {
				// map_guanzhu_guanzhu.put(arg0, true);
				// } else {
				// map_guanzhu_guanzhu.put(arg0, false);
				// }
				// if (map_guanzhu_guanzhu.get(arg0)) {
				// holder.bt_guanZhu.setBackground(getResources().getDrawable(
				// R.drawable.img_guanzhu1));
				// holder.bt_guanZhu.setText("已关注");
				// holder.bt_guanZhu.setTextColor(getResources().getColor(
				// R.color.text_color));
				// } else {
				// holder.bt_guanZhu.setBackground(getResources().getDrawable(
				// R.drawable.img_guanzhu));
				// holder.bt_guanZhu.setText("关注");
				// holder.bt_guanZhu.setTextColor(getResources().getColor(
				// R.color.tab_select));
				// }

				final ArrayList<TagInfoModel> tagInfoModels = new ArrayList<TagInfoModel>();
				JSONArray jsonArray = mDataGanZhu.get(arg0).getJSONArray(
						"UserTag");
				if (jsonArray.length() > 0) {
					List<TagInfoModel> tagInfos = new ArrayList<TagInfoModel>();
					for (int i = 0; i < jsonArray.length(); i++) {
						TagInfoModel infoModel = new TagInfoModel();
						infoModel.tag_name = jsonArray.getJSONObject(i)
								.getString("Content");
						float PositionX = Float.parseFloat(jsonArray
								.getJSONObject(i).getString("PositionX"));
						float PositionY = Float.parseFloat(jsonArray
								.getJSONObject(i).getString("PositionY"));
						infoModel.activityId = jsonArray.getJSONObject(i)
								.getString("ActivityId");
						infoModel.x = PositionX;
						infoModel.y = PositionY;
						tagInfos.add(infoModel);
					}
					tagInfoModels.addAll(tagInfos);
				}
				String tageViewUrl = mDataGanZhu.get(arg0)
						.getString("ImageUrl");

				holder.tagsView.setTagInfoModels(tagInfoModels);
				if (!tageViewUrl.equals("") && tageViewUrl != null) {
					ImageLoader.getInstance().displayImage(tageViewUrl,
							holder.tagsView.getImageView(), option);
				}

				holder.bt_guanZhu.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg) {
						if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
							Intent mIntent = new Intent(
									MeiMiaoQuanActivity.this,
									LoginActivity.class);
							startActivity(mIntent);
						} else {
							try {
								if (map_guanzhu_guanzhu.get(cont)) {
									flag_guanZhu_guanZhu = true;
								} else {
									flag_guanZhu_guanZhu = false;
								}
								if (!flag_guanZhu_guanZhu) {
									GuanZhu(mDataGanZhu.get(cont).getString(
											"UserId"));

									holder.bt_guanZhu
											.setBackground(getResources()
													.getDrawable(
															R.drawable.img_guanzhu1));
									holder.bt_guanZhu.setText("已关注");
									holder.bt_guanZhu
											.setTextColor(getResources()
													.getColor(
															R.color.text_color));
									flag_guanZhu_guanZhu = true;
									map_guanzhu_guanzhu.put(cont, true);
								} else {
									Toast.makeText(MeiMiaoQuanActivity.this,
											"已关注过了哦", Toast.LENGTH_SHORT)
											.show();
									// holder.bt_guanZhu.setBackground(getResources()
									// .getDrawable(R.drawable.img_guanzhu));
									// holder.bt_guanZhu.setText("关注");
									// holder.bt_guanZhu.setTextColor(getResources()
									// .getColor(R.color.tab_select));
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
				holder.tagsView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						try {
							Intent intent = new Intent(
									MeiMiaoQuanActivity.this,
									GoodsDetailActivity.class);
							intent.putExtra("active", "1");
							intent.putExtra("shareUrl", mDataGanZhu.get(cont)
									.getString("ActivityShare"));
							intent.putExtra("detailUrl", mDataGanZhu.get(cont)
									.getString("IdGroup"));
							intent.putExtra("imgurl", mDataGanZhu.get(cont)
									.getString("ImageUrl"));
							startActivity(intent);
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

				});
				holder.iv_zan.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
							Intent mIntent = new Intent(
									MeiMiaoQuanActivity.this,
									LoginActivity.class);
							startActivity(mIntent);
						} else {
							try {
								int i = 0;
								int j = 0;
								if (map_guanzhu_zan.get(cont)) {
									flag_guanzhu_zan = true;
								} else {
									flag_guanzhu_zan = false;
								}
								if (!flag_guanzhu_zan) {
									dianZan(mDataGanZhu.get(cont).getString(
											"Id"));
									holder.iv_zan
											.setImageResource(R.drawable.img_zuan);
									JSONObject jsonObject = new JSONObject();
									jsonObject = mDataGanZhu.get(cont);
									i = Integer.parseInt(mDataGanZhu.get(cont)
											.getString("SomePraiseCount"));
									i++;
									jsonObject.put("SomePraiseCount", i + "");
									jsonObject.put("ZanStatus", 1);
									mDataGanZhu.set(cont, jsonObject);
									// flag_guanzhu_zan = true;
									holder.tv_zan.setText(i + "赞");
									map_guanzhu_zan.put(cont, true);
								} else {
									CancelDianZan(mDataGanZhu.get(cont)
											.getString("Id"));
									holder.iv_zan
											.setImageResource(R.drawable.img_zuan1);
									JSONObject jsonObject = new JSONObject();
									jsonObject = mDataGanZhu.get(cont);
									j = Integer.parseInt(mDataGanZhu.get(cont)
											.getString("SomePraiseCount"));
									j--;
									jsonObject.put("SomePraiseCount", j + "");
									jsonObject.put("ZanStatus", 0);
									mDataGanZhu.set(cont, jsonObject);
									// flag_guanzhu_zan = true;
									holder.tv_zan.setText(j + "赞");
									map_guanzhu_zan.put(cont, false);
								}

							} catch (NumberFormatException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
				holder.iv_pingLun.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
							Intent mIntent = new Intent(
									MeiMiaoQuanActivity.this,
									LoginActivity.class);
							startActivity(mIntent);
						} else {
							try {
								Intent intent = new Intent(
										MeiMiaoQuanActivity.this,
										CommentActivity.class);
								intent.putExtra("id", mDataGanZhu.get(cont)
										.getString("Id"));
								startActivity(intent);
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

					}
				});
				holder.iv_fengXiang.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						try {
							share(mDataGanZhu.get(cont).getString("Content"),
									"",
									mDataGanZhu.get(cont).getString("ShareUrl")
											.toString(),
									AllStaticMessage.URL_GBase
											+ "/"
											+ mDataGanZhu.get(cont).getString(
													"ImageUrl"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return arg1;
		}
	}

	private class Holder {
		private ImageView iv_name, iv_zan, iv_pingLun, iv_fengXiang;
		private TextView tv_name, tv_content, tv_zan, tv_pingLun;
		private Button bt_guanZhu;
		private TagsView tagsView;

	}

	private void addHeardView() {
		View headerView = getLayoutInflater().inflate(
				R.layout.activity_meimiaoquan_viewpager, null);

		RelativeLayout relativeLayout = (RelativeLayout) headerView
				.findViewById(R.id.liner_second);
		MyTools.setWidthAndHeight(relativeLayout, width);

		listView_tuiJian.addHeaderView(headerView);

		my_ViewPager = (My_ViewPager) headerView
				.findViewById(R.id.pic_viewPager);
		layout_dian = (LinearLayout) headerView.findViewById(R.id.yuandian);

		my_ViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				for (int i = 0; i < imgs.length; i++) {
					if (i == arg0 % mImageViews.length) {
						imgs[i].setBackgroundResource(R.drawable.second_view6);
					} else {
						imgs[i].setBackgroundResource(R.drawable.second_view5);
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
		// 单手点击事件
		my_ViewPager.setOnSingleTouchListener(new OnSingleTouchListener() {
			@Override
			public void onSingleTouch() {

			}
		});
	}

	/**
	 * 晒单列表
	 * 
	 * @param listView
	 */
	private void getListTuiJianData(final ListView listView) {
		String url = AllStaticMessage.URL_BaskOrderList + "&UserId="
				+ AllStaticMessage.User_Id+"&pageSize=10";

		HttpUtil.get(url, MeiMiaoQuanActivity.this, null,
				new JsonHttpResponseHandler() {
					@SuppressLint("UseSparseArrays")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							if (response.getString("Status").equals("true")) {
								JSONArray mArray = response
										.getJSONArray("Results");
								if (mData != null) {
									mData.clear();
								}
								for (int i = 0; i < mArray.length(); i++) {
									mData.add(mArray.getJSONObject(i));
								}

								listView.setAdapter(tuijianadapter);
								map_guanzhu = new HashMap<Integer, Boolean>();
								map = new HashMap<Integer, Boolean>();
								for (int j = 0; j < mData.size(); j++) {
									map.put(j, false);
									map_guanzhu.put(j, false);
								}
							}
							mPullRefreshView.onHeaderRefreshFinish();
							if (dialog != null) {
								dialog.stop();
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

	/**
	 * 晒单列表
	 * 
	 * @param listView
	 */
	private void getListTuiJianDataNum(final ListView listView, String num) {
		String url = AllStaticMessage.URL_BaskOrderList + "&UserId="
				+ AllStaticMessage.User_Id + "&pageNum=" + num+"&pageSize=5";

		HttpUtil.get(url, MeiMiaoQuanActivity.this, null,
				new JsonHttpResponseHandler() {
					@SuppressLint({ "UseSparseArrays", "ShowToast" })
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							if (response.getString("Status").equals("true")) {
								JSONArray mArray = response
										.getJSONArray("Results");
								for (int i = 0; i < mArray.length(); i++) {
									mData.add(mArray.getJSONObject(i));
								}
								tuijianadapter.notifyDataSetChanged();
								map_guanzhu = new HashMap<Integer, Boolean>();
								map = new HashMap<Integer, Boolean>();
								for (int j = 0; j < mData.size(); j++) {
									map.put(j, false);
									map_guanzhu.put(j, false);
								}
							} else {
//								Toast.makeText(
//										MeiMiaoQuanActivity.this,
//										response.getString("Results")
//												.toString(), 0).show();
							}
							if (dialog != null) {
								dialog.stop();
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

	/**
	 * 关注列表
	 * 
	 * @param listView
	 */
	private void getListGuanZhuData(final ListView listView) {
		String url = AllStaticMessage.URL_GuanZhuList + "&UserId="
				+ AllStaticMessage.User_Id+"&pageSize=10";

		HttpUtil.get(url, MeiMiaoQuanActivity.this, null,
				new JsonHttpResponseHandler() {
					@SuppressLint("UseSparseArrays")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							if (response.getString("Status").equals("true")) {
								tv_wuGuanzhu.setVisibility(View.GONE);
								mPullRefreshView_guanzhu
										.setVisibility(View.VISIBLE);
								JSONArray mArray = response
										.getJSONArray("Results");
								if (mDataGanZhu != null) {
									mDataGanZhu.clear();
								}
								for (int i = 0; i < mArray.length(); i++) {
									mDataGanZhu.add(mArray.getJSONObject(i));
								}

								listView.setAdapter(guanZhuAdapter);
								map_guanzhu_guanzhu = new HashMap<Integer, Boolean>();
								map_guanzhu_zan = new HashMap<Integer, Boolean>();
								for (int j = 0; j < mDataGanZhu.size(); j++) {
									map_guanzhu_zan.put(j, false);
									map_guanzhu_guanzhu.put(j, false);
								}
							} else {
								tv_wuGuanzhu.setVisibility(View.VISIBLE);
								mPullRefreshView_guanzhu
										.setVisibility(View.GONE);
								flag_first = false;
							}
							mPullRefreshView_guanzhu.onHeaderRefreshFinish();
							if (AllStaticMessage.isShare) {

								// View mView = getLayoutInflater().inflate(
								// R.layout.share, null);
								// mView.setOnClickListener(new
								// OnClickListener() {
								//
								// @Override
								// public void onClick(View arg0) {
								// popupWindow.dismiss();
								//
								// }
								// });
								// popupWindow = new PopupWindow(mView);
								// popupWindow.setWidth(LayoutParams.MATCH_PARENT);
								// popupWindow
								// .setHeight(LayoutParams.MATCH_PARENT);
								// popupWindow
								// .setAnimationStyle(R.style.AnimBottomPopup);
								// ColorDrawable dw = new
								// ColorDrawable(0xb0000000);
								// popupWindow.setBackgroundDrawable(dw);
								// popupWindow.setOutsideTouchable(true);
								//
								// popupWindow.showAtLocation(
								// findViewById(R.id.share),
								// Gravity.BOTTOM, 0, 0);
								AllStaticMessage.isShare = false;
							}
							if (dialog != null) {
								dialog.stop();
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

	/**
	 * 关注列表
	 * 
	 * @param listView
	 * @param num
	 */
	private void getListGuanZhuDataNum(final ListView listView, String num) {
		String url = AllStaticMessage.URL_GuanZhuList + "&UserId="
				+ AllStaticMessage.User_Id + "&pageNum=" + num+"&pageSize=5";

		HttpUtil.get(url, MeiMiaoQuanActivity.this, null,
				new JsonHttpResponseHandler() {
					@SuppressLint("UseSparseArrays")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							if (response.getString("Status").equals("true")) {
								tv_wuGuanzhu.setVisibility(View.GONE);
								mPullRefreshView_guanzhu
										.setVisibility(View.VISIBLE);
								JSONArray mArray = response
										.getJSONArray("Results");
								for (int i = 0; i < mArray.length(); i++) {
									mDataGanZhu.add(mArray.getJSONObject(i));
								}

								guanZhuAdapter.notifyDataSetChanged();
								map_guanzhu_guanzhu = new HashMap<Integer, Boolean>();
								map_guanzhu_zan = new HashMap<Integer, Boolean>();
								for (int j = 0; j < mDataGanZhu.size(); j++) {
									map_guanzhu_zan.put(j, false);
									map_guanzhu_guanzhu.put(j, false);
								}
							} else {
//								Toast.makeText(
//										MeiMiaoQuanActivity.this,
//										response.getString("Results")
//												.toString(), 0).show();
							}
							if (dialog != null) {
								dialog.stop();
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

	private void getImgUrl(final LinearLayout layout_dian,
			final My_ViewPager my_ViewPager) {
		dialog.loading();
		String url = AllStaticMessage.URL_BannerList;
		HttpUtil.get(url, MeiMiaoQuanActivity.this, null,
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
								mArray_ad = new JSONArray();
								mArray_ad = response.getJSONArray("Results");
								imgs = new ImageView[mArray_ad.length()];
								for (int i = 0; i < imgs.length; i++) {
									ImageView imageView = new ImageView(
											MeiMiaoQuanActivity.this);// 创建图片框
									imageView.setLayoutParams(new LayoutParams(
											30, 30));
									// 设置下方图片宽，高
									imageView.setPadding(10, 0, 10, 5);// 内边距
									if (i == 0) {
										imageView
												.setBackgroundResource(R.drawable.second_view6);
										// imageView.setBackground(MyTools.getDrawableImg(MainActivity.this,
										// R.drawable.second_view6));
									} else {
										imageView
												.setBackgroundResource(R.drawable.second_view5);
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
								Toast.makeText(
										MeiMiaoQuanActivity.this,
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
					}
				});
	}

	private void inImg() {
		DisplayImageOptions options = MyTools
				.createOptions(R.drawable.loading_01);
		for (int i = 0; i < mArray_ad.length(); i++) {
			ImageView imageView = new ImageView(MeiMiaoQuanActivity.this);
			imageView.setScaleType(ScaleType.FIT_XY);
			// 异步加载图片
			try {
				String infUrl = mArray_ad.getJSONObject(i)
						.getString("BannerPic").toString();
				ImageLoader.getInstance().displayImage(infUrl, imageView,
						options);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mImageViews[i] = imageView;
		}
	}

	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(list.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {

			((ViewPager) arg0).addView((View) list.get(arg1));

			return list.get(arg1);

		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

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
		 * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
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
			}
			// 点击跳转下一页
			int num = position % mImageViews.length;
			if (mImageViews.length == 1) {
				return mImageViews[0];
			} else {
				return mImageViews[position % mImageViews.length];
			}
		}
	}

	private class MyListener implements OnPageChangeListener {

		// 当滑动状态改变时调用
		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		// 当当前页面被滑动时调用
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		// 当新的页面被选中时调用
		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				tv_tuiJian.setTextColor(getResources().getColor(
						R.color.tab_select));
				iv_tuiJian.setVisibility(View.VISIBLE);
				tv_guanZhu.setTextColor(getResources().getColor(
						R.color.text_color));
				iv_guanZhu.setVisibility(View.INVISIBLE);
				if (!flag_tuijian) {
					dialog.loading();
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							getImgUrl(layout_dian, my_ViewPager);
							getListTuiJianData(listView_tuiJian);
							flag_tuijian = true;
						}
					}, 300);
				}
				break;

			case 1:
				if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
					Intent mIntent = new Intent(MeiMiaoQuanActivity.this,
							LoginActivity.class);
					startActivityForResult(mIntent, 0x001);
				} else {
					tv_tuiJian.setTextColor(getResources().getColor(
							R.color.text_color));
					iv_tuiJian.setVisibility(View.INVISIBLE);
					tv_guanZhu.setTextColor(getResources().getColor(
							R.color.tab_select));
					iv_guanZhu.setVisibility(View.VISIBLE);
					if (!flag_first) {
						dialog.loading();
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								getListGuanZhuData(listView_guanZhu);
								flag_first = true;
							}
						}, 400);
					}
					break;
				}
			default:
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 0x001:
				AllStaticMessage.Back_to_Classion = true;
				startActivity(new Intent(MeiMiaoQuanActivity.this,
						TabHostActivity.class));
				break;

			}
		}
		if (resultCode == 8080) {
			switch (requestCode) {
			case 0x001:
				AllStaticMessage.Back_to_Classion = true;
				startActivity(new Intent(MeiMiaoQuanActivity.this,
						TabHostActivity.class));
				break;

			}
		}
	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.iv_photo:
			AllStaticMessage.user_flag = false;
			startActivity(new Intent(this, ActivityCapture.class));
			break;
		case R.id.ll_tuiJian:
			viewPager.setCurrentItem(0);
			// tv_tuiJian
			// .setTextColor(getResources().getColor(R.color.tab_select));
			// iv_tuiJian.setVisibility(View.VISIBLE);
			// tv_guanZhu
			// .setTextColor(getResources().getColor(R.color.text_color));
			// iv_guanZhu.setVisibility(View.INVISIBLE);
			break;
		case R.id.ll_guanZhu:
			viewPager.setCurrentItem(1);
			// tv_tuiJian
			// .setTextColor(getResources().getColor(R.color.text_color));
			// iv_tuiJian.setVisibility(View.INVISIBLE);
			// tv_guanZhu
			// .setTextColor(getResources().getColor(R.color.tab_select));
			// iv_guanZhu.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
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
		oks.setText(title + url);

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
		oks.show(MeiMiaoQuanActivity.this);
		// Toast.makeText(getApplicationContext(), "打开url:" + url,
		// Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
				this.getApplication().onTerminate();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
