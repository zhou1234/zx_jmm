package com.jifeng.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jifeng.mlsales.R;
import com.jifeng.mlsales.jumeimiao.GoodsDetailActivity;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ClassItemListViewAdapter extends BaseAdapter {
	private ClassItemListViewAppItem appItem;
	private List<JSONObject> mListData;
	private Context mContext;
	private int width, hight;
	private DisplayImageOptions options;

	public ClassItemListViewAdapter(List<JSONObject> listData, Context context,
			int width, int hight) {
		this.mContext = context;
		mListData = new ArrayList<JSONObject>();
		this.mListData = listData;
		this.width = width;
		this.hight = hight;
		options = MyTools.createOptions(R.drawable.img);
	}

	@Override
	public int getCount() {
		return mListData.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			View v = LayoutInflater.from(mContext).inflate(
					R.layout.item_goodslist_gridview1, null);
			appItem = new ClassItemListViewAppItem();
			appItem.AppText_name = (TextView) v.findViewById(R.id.goods_name);
			appItem.AppText_price_now = (TextView) v
					.findViewById(R.id.item_save_price_now);
			appItem.AppText_price_old = (TextView) v
					.findViewById(R.id.item_save_price_old);
			appItem.AppBtn_dazhe = (Button) v.findViewById(R.id.goods_dazhe);
			appItem.AppImg = (ImageView) v.findViewById(R.id.goodslist_imgbig);
			appItem.AppImg_qiangguang = (ImageView) v
					.findViewById(R.id.goodslist_item_qiangguang);
			v.setTag(appItem);
			convertView = v;
		} else {
			appItem = (ClassItemListViewAppItem) convertView.getTag();
		}
		try {
			if (mListData.get(position).getString("Stock").toString()
					.equals("0")) {
				appItem.AppImg_qiangguang.setVisibility(View.VISIBLE);
			} else {
				appItem.AppImg_qiangguang.setVisibility(View.GONE);
			}
			appItem.AppText_name.setText(mListData.get(position)
					.getString("PName").toString());
			if (!mListData.get(position).getString("ActiveName").toString()
					.equals("")) {
				// 特卖价
				appItem.AppText_price_now.setText("￥"
						+ mListData.get(position).getString("SellPrice")
								.toString());
			} else {
				// 普通价
				appItem.AppText_price_now
						.setText("￥"
								+ mListData.get(position).getString("Price")
										.toString());
			}
			appItem.AppText_price_old.setText("￥"
					+ mListData.get(position).getString("MarketPrice")
							.toString());
			appItem.AppText_price_old.getPaint().setFlags(
					Paint.STRIKE_THRU_TEXT_FLAG);
			// if (!num.equals("")) {
			// Double p_1 = Double.valueOf(num);
			// Double p_2 = Double.valueOf(mListData.get(position)
			// .getString("MarketPrice").toString());
			// Double result = (p_1 / p_2) * 10;
			// String price = String.valueOf(result).substring(0, 3);
			// if(p_1.equals(p_2)){
			// price="1.0";
			// }
			// appItem.AppBtn_dazhe.setText(price + "折");
			// }
			appItem.AppBtn_dazhe.setText(mListData.get(position)
					.getString("DiscountDes").toString()
					+ "折");
			String imgurl = AllStaticMessage.URL_GBase + "/UsersData/"
					+ mListData.get(position).getString("Account").toString()
					+ "/"
					+ mListData.get(position).getString("SkuNo").toString()
					+ "/5.jpg".trim();
			ImageLoader.getInstance().displayImage(imgurl, appItem.AppImg,
					options);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Intent mIntent = new Intent(mContext,
							GoodsDetailActivity.class);
					// if (mContext.getClass() == FenLeiActivity.class) {
					mIntent.putExtra("active", "0");
					mIntent.putExtra("pid",
							mListData.get(position).getString("ActiveId")
									.toString());// 活动id
					// } else {
					// mIntent.putExtra("pid", pid);// 活动id
					// }

					mIntent.putExtra("guigeid", mListData.get(position)
							.getString("Id").toString());// 规格
					mIntent.putExtra("goodsid", mListData.get(position)
							.getString("Pid").toString());// 商品id
					// Log.i("11111",
					// jsonArray.getJSONObject(position).getString("Id").toString());
					mIntent.putExtra("imgurl", AllStaticMessage.URL_GBase
							+ "/UsersData/"
							+ mListData.get(position).getString("Account")
									.toString()
							+ "/"
							+ mListData.get(position).getString("SkuNo")
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

class ClassItemListViewAppItem {
	TextView AppText_name;
	TextView AppText_price_now;
	TextView AppText_price_old;
	Button AppBtn_dazhe;
	ImageView AppImg, AppImg_qiangguang;
}