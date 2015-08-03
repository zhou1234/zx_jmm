package com.jifeng.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

//import com.jifeng.image.ImageLoader;
import com.jifeng.mlsales.R;
import com.jifeng.mlsales.jumeimiao.GoodsDetailActivity;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyGoodsListAdapter extends BaseAdapter {
	private GoodsListAppItem appItem;
	private List<JSONObject> mListData;
	// ImageLoader imageLoader;
	private Context mContext;
	private String pid;
	private int width, hight;
	private DisplayImageOptions options;
	private Map<Integer, Boolean> map;

	public MyGoodsListAdapter(List<JSONObject> listData, Context context,
			String id, int width, int hight) {// , ImageLoader imageLoader
		this.mContext = context;
		// this.imageLoader = imageLoader;
		mListData = new ArrayList<JSONObject>();
		this.mListData = listData;
		this.pid = id;
		this.width = width;
		this.hight = hight;
		// MyTools.initImageLoader(mContext);
		options = MyTools.createOptions(R.drawable.img);
	}

	public void setIndex(Map<Integer, Boolean> map) {
		this.map = map;
	}

	@Override
	public int getCount() {
		if (mListData.size() % 2 == 0) {
			return mListData.size() / 2;
		}

		return mListData.size() / 2 + 1;
	}

	@Override
	public Object getItem(int position) {
		return mListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			View v = LayoutInflater.from(mContext).inflate(
					R.layout.activity_goods_list_view_item, null);
			appItem = new GoodsListAppItem();
			appItem.ll_left = (LinearLayout) v.findViewById(R.id.ll_left);
			appItem.ll_right = (LinearLayout) v.findViewById(R.id.ll_right);

			appItem.AppText_name = (TextView) v.findViewById(R.id.goods_name);
			appItem.AppText_name1 = (TextView) v.findViewById(R.id.goods_name1);
			appItem.AppText_price_now = (TextView) v
					.findViewById(R.id.item_save_price_now);
			appItem.AppText_price_now1 = (TextView) v
					.findViewById(R.id.item_save_price_now1);
			appItem.AppText_price_old = (TextView) v
					.findViewById(R.id.item_save_price_old);
			appItem.AppText_price_old1 = (TextView) v
					.findViewById(R.id.item_save_price_old1);
			// appItem.AppBtn_shengyu_time =
			// (Button)v.findViewById(R.id.goods_btn_shengyu_time);
			appItem.AppBtn_dazhe = (Button) v.findViewById(R.id.goods_dazhe);
			appItem.AppImg = (ImageView) v.findViewById(R.id.goodslist_imgbig);
			appItem.AppBtn_dazhe1 = (Button) v.findViewById(R.id.goods_dazhe1);
			appItem.AppImg1 = (ImageView) v
					.findViewById(R.id.goodslist_imgbig1);
			LayoutParams para = appItem.AppImg.getLayoutParams();
			para.height = (width - 20) / 2;
			para.width = (width - 20) / 2;
			appItem.AppImg.setLayoutParams(para);
			appItem.AppImg_qiangguang = (ImageView) v
					.findViewById(R.id.goodslist_item_qiangguang);
			LayoutParams para1 = appItem.AppImg1.getLayoutParams();
			para1.height = (width - 20) / 2;
			para1.width = (width - 20) / 2;
			appItem.AppImg1.setLayoutParams(para1);
			appItem.AppImg_qiangguang1 = (ImageView) v
					.findViewById(R.id.goodslist_item_qiangguang1);
			v.setTag(appItem);
			convertView = v;
		} else {
			appItem = (GoodsListAppItem) convertView.getTag();
		}
		// MyTools.setHight(appItem.AppmLayout, width, hight, mContext);
		// appItem.AppBtn_shengyu_time.setVisibility(View.INVISIBLE);
		final int left = position * 2;
		final int right = position * 2 + 1;
		try {
			if (mListData.get(left).getString("Stock").toString().equals("0")) {
				appItem.AppImg_qiangguang.setVisibility(View.VISIBLE);
			} else {
				appItem.AppImg_qiangguang.setVisibility(View.GONE);
			}
			appItem.AppText_name.setText(mListData.get(left).getString("PName")
					.toString());
			String num = "";
			if (!mListData.get(left).getString("ActiveName").toString()
					.equals("")) {
				// 特卖价
				appItem.AppText_price_now
						.setText("￥"
								+ mListData.get(left).getString("SellPrice")
										.toString());
				num = mListData.get(left).getString("SellPrice").toString();
			} else {
				// 普通价
				appItem.AppText_price_now.setText("￥"
						+ mListData.get(left).getString("Price").toString());
				num = mListData.get(left).getString("Price").toString();
			}
			appItem.AppText_price_old.setText("￥"
					+ mListData.get(left).getString("MarketPrice").toString());
			appItem.AppText_price_old.getPaint().setFlags(
					Paint.STRIKE_THRU_TEXT_FLAG);
			// setTime(jsonArray.getJSONObject(position).getString("StartTime").toString(),jsonArray.getJSONObject(position).getString("EndTime").toString()
			// ,appItem.AppBtn_shengyu_time);
			if (!num.equals("")) {
				Double p_1 = Double.valueOf(num);
				Double p_2 = Double.valueOf(mListData.get(left)
						.getString("MarketPrice").toString());
				Double result = (p_1 / p_2) * 10;
				String price = String.valueOf(result).substring(0, 3);
				appItem.AppBtn_dazhe.setText(price + "折");
			}
			final String imgurl = AllStaticMessage.URL_GBase + "/UsersData/"
					+ mListData.get(left).getString("Account").toString() + "/"
					+ mListData.get(left).getString("SkuNo").toString()
					+ "/5.jpg".trim();

			ImageLoader.getInstance().displayImage(imgurl, appItem.AppImg,
					options);

			if (right < mListData.size()) {
				appItem.ll_right.setVisibility(View.VISIBLE);
				if (mListData.get(right).getString("Stock").toString()
						.equals("0")) {
					appItem.AppImg_qiangguang1.setVisibility(View.VISIBLE);
				} else {
					appItem.AppImg_qiangguang1.setVisibility(View.GONE);
				}
				appItem.AppText_name1.setText(mListData.get(right)
						.getString("PName").toString());
				String num1 = "";
				if (!mListData.get(right).getString("ActiveName").toString()
						.equals("")) {
					// 特卖价
					appItem.AppText_price_now1.setText("￥"
							+ mListData.get(right).getString("SellPrice")
									.toString());
					num1 = mListData.get(right).getString("SellPrice")
							.toString();
				} else {
					// 普通价
					appItem.AppText_price_now1.setText("￥"
							+ mListData.get(right).getString("Price")
									.toString());
					num1 = mListData.get(right).getString("Price").toString();
				}
				appItem.AppText_price_old1.setText("￥"
						+ mListData.get(right).getString("MarketPrice")
								.toString());
				appItem.AppText_price_old1.getPaint().setFlags(
						Paint.STRIKE_THRU_TEXT_FLAG);
				// setTime(jsonArray.getJSONObject(position).getString("StartTime").toString(),jsonArray.getJSONObject(position).getString("EndTime").toString()
				// ,appItem.AppBtn_shengyu_time);
				if (!num1.equals("")) {
					Double p_1 = Double.valueOf(num1);
					Double p_2 = Double.valueOf(mListData.get(right)
							.getString("MarketPrice").toString());
					Double result = (p_1 / p_2) * 10;
					String price = String.valueOf(result).substring(0, 3);
					appItem.AppBtn_dazhe1.setText(price + "折");
				}
				final String imgurl1 = AllStaticMessage.URL_GBase
						+ "/UsersData/"
						+ mListData.get(right).getString("Account").toString()
						+ "/"
						+ mListData.get(right).getString("SkuNo").toString()
						+ "/5.jpg".trim();

				ImageLoader.getInstance().displayImage(imgurl1,
						appItem.AppImg1, options);
			}
			if (right == mListData.size()) {
				if (map.get(right)) {
					appItem.ll_right.setVisibility(View.INVISIBLE);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		appItem.ll_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent mIntent = new Intent(mContext,
							GoodsDetailActivity.class);
					// if (mContext.getClass() == FenLeiActivity.class) {
					// mIntent.putExtra("pid", mListData.get(position)
					// .getString("ActiveId").toString());// 活动id
					// } else {
					mIntent.putExtra("pid", pid);// 活动id
					// }
					mIntent.putExtra("active", "0");
					mIntent.putExtra("guigeid",
							mListData.get(left).getString("Id").toString());// 规格
					mIntent.putExtra("goodsid",
							mListData.get(left).getString("Pid").toString());// 商品id
					// Log.i("11111",
					// jsonArray.getJSONObject(position).getString("Id").toString());
					mIntent.putExtra(
							"imgurl",
							AllStaticMessage.URL_GBase
									+ "/UsersData/"
									+ mListData.get(left).getString("Account")
											.toString()
									+ "/"
									+ mListData.get(left).getString("SkuNo")
											.toString() + "/5.jpg".trim());
					mContext.startActivity(mIntent);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		appItem.ll_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent mIntent = new Intent(mContext,
							GoodsDetailActivity.class);
					// if (mContext.getClass() == FenLeiActivity.class) {
					// mIntent.putExtra("pid", mListData.get(position)
					// .getString("ActiveId").toString());// 活动id
					// } else {
					mIntent.putExtra("pid", pid);// 活动id
					// }
					mIntent.putExtra("active", "0");
					mIntent.putExtra("guigeid",
							mListData.get(right).getString("Id").toString());// 规格
					mIntent.putExtra("goodsid",
							mListData.get(right).getString("Pid").toString());// 商品id
					// Log.i("11111",
					// jsonArray.getJSONObject(position).getString("Id").toString());
					mIntent.putExtra(
							"imgurl",
							AllStaticMessage.URL_GBase
									+ "/UsersData/"
									+ mListData.get(right).getString("Account")
											.toString()
									+ "/"
									+ mListData.get(right).getString("SkuNo")
											.toString() + "/5.jpg".trim());
					mContext.startActivity(mIntent);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		return convertView;
	}
}

class GoodsListAppItem {
	TextView AppText_name;
	TextView AppText_price_now;
	TextView AppText_price_old;
	// Button AppBtn_shengyu_time;
	Button AppBtn_dazhe;
	ImageView AppImg, AppImg_qiangguang;
	LinearLayout ll_left;

	TextView AppText_name1;
	TextView AppText_price_now1;
	TextView AppText_price_old1;
	// Button AppBtn_shengyu_time;
	Button AppBtn_dazhe1;
	ImageView AppImg1, AppImg_qiangguang1;
	LinearLayout ll_right;
}