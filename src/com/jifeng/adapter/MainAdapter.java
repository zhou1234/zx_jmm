package com.jifeng.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.jifeng.mlsales.R;
import com.jifeng.mlsales.jumeimiao.GoodsListActivity;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainAdapter extends BaseAdapter {
	private Context mContext;
	private MainAppItem appItem;
	private List<JSONObject> mListData;
	private int height, width;
	private DisplayImageOptions options;

	public MainAdapter(Context context, int height, int width,
			List<JSONObject> listData, ListView listView) {
		this.mContext = context;
		this.height = height;
		this.width = width;
		mListData = new ArrayList<JSONObject>();
		this.mListData = listData;
		options = MyTools.createOptions(R.drawable.loading_01);
	}

	@Override
	public int getCount() {
		if (mListData.size() == 0) {
			return 1;
		} else {
			return mListData.size();
		}

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
					R.layout.item_main_listview, null);
			appItem = new MainAppItem();
			appItem.AppBtn_time = (Button) v.findViewById(R.id.btn_time);
			appItem.AppBtn_youhuiquan = (TextView) v
					.findViewById(R.id.btn_youhuiquan);
			appItem.AppBtn_name = (Button) v
					.findViewById(R.id.btn_activity_name);
			appItem.AppImg = (ImageView) v.findViewById(R.id.img_main_tou);
			appItem.AppText_dazhe = (TextView) v.findViewById(R.id.text_dazhe);
			appItem.AppmLayout = (RelativeLayout) v
					.findViewById(R.id.save_gridview_liner);
			appItem.AppmLayout_Quan = (RelativeLayout) v
					.findViewById(R.id.rel_quan);
			v.setTag(appItem);
			convertView = v;
		} else {
			appItem = (MainAppItem) convertView.getTag();
		}

		MyTools.getHight(appItem.AppmLayout, width, height, mContext);
		try {
			setTime(mListData.get(position).getString("EndTime").toString(),
					appItem.AppBtn_time);

			if (mListData.get(position).getString("DesGuide").toString()
					.equals("")) {
				appItem.AppmLayout_Quan.setVisibility(View.GONE);
			} else {
				appItem.AppmLayout_Quan.setVisibility(View.VISIBLE);
				appItem.AppBtn_youhuiquan.setText(mListData.get(position)
						.getString("DesGuide").toString());
			}
			appItem.AppBtn_name.setText(mListData.get(position)
					.getString("ActiveName").toString());
			appItem.AppText_dazhe.setText(mListData.get(position)
					.getString("DiscountDes").toString());
			String imgurl = AllStaticMessage.URL_GBase
					+ mListData.get(position).getString("ActivityPic")
							.toString();

			if (imgurl != null) {
				ImageLoader.getInstance().displayImage(imgurl, appItem.AppImg,
				options);
//				ImageLoader.getInstance().displayImage(imgurl, appItem.AppImg,
//						options, ImageLoadingListenerImpl);
			}

			appItem.AppImg.setOnClickListener(new onMainItemClick(appItem,
					mListData.get(position), mContext));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}

	// 监听图片异步加载
	public static class ImageLoadingListenerImpl extends
			SimpleImageLoadingListener {

		public static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
			if (bitmap != null) {
				ImageView imageView = (ImageView) view;
				boolean isFirstDisplay = !displayedImages.contains(imageUri);
				if (isFirstDisplay) {
					// 图片的淡入效果
					FadeInBitmapDisplayer.animate(imageView, 1 * 1000);
					displayedImages.add(imageUri);
				}
			}
		}  
	}

	private void setTime(String endtime, Button btn) {
		int date = MyTools.creayTime(endtime, MyTools.getTime());
		if (date > 0) {
			btn.setText("仅剩" + String.valueOf(date) + "天");
		} else {
			int hour = returnHour(endtime, MyTools.getTime(), "hour");
			if (hour > 0) {
				btn.setText("仅剩" + String.valueOf(hour) + "小时");
			} else {
				int fenzhong = returnHour(endtime, MyTools.getTime(), "");
				if (fenzhong > 0) {
					btn.setText("仅剩" + String.valueOf(fenzhong) + "分钟");
				} else {
					btn.setText("活动已结束");
				}
			}
		}
	}

	@SuppressLint("SimpleDateFormat")
	private int returnHour(String endtime, String currenttime, String flag) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		int between = 0;
		try {
			Date end = formatter.parse(endtime);
			Date curret = formatter.parse(currenttime);
			if (flag.equals("hour")) {
				between = (int) ((end.getTime() - curret.getTime()) / (1000 * 60 * 60));
			} else {
				between = (int) ((end.getTime() - curret.getTime()) / (1000 * 60));
			}
			return between;
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return between;
	}

	class MainAppItem {
		ImageView AppImg;
		Button AppBtn_time;
		TextView AppBtn_youhuiquan;
		Button AppBtn_name;
		TextView AppText_dazhe;
		RelativeLayout AppmLayout;// 控制图片显示高度
		RelativeLayout AppmLayout_Quan;
	}

	class onMainItemClick implements OnClickListener {
		MainAppItem appItem;
		JSONObject jsonObject;
		Intent mIntent;
		Context mContext;

		public onMainItemClick(MainAppItem appIte, JSONObject paywa,
				Context context) {
			this.appItem = appIte;
			this.jsonObject = paywa;
			this.mContext = context;
		}

		@Override
		public void onClick(View v) {
			try {
				mIntent = new Intent(mContext, GoodsListActivity.class);
				mIntent.putExtra("id", jsonObject.getString("Id").toString());
				mIntent.putExtra("youhui", jsonObject.getString("DesGuide")
						.toString());
				int data = MyTools.creayTime(jsonObject.getString("EndTime")
						.toString(), MyTools.getTime());
				if (data < 0) {
					mIntent.putExtra("time", "已结束");
				} else {
					mIntent.putExtra("time", "仅剩" + String.valueOf(data) + "天");
				}
				mIntent.putExtra("text", jsonObject.getString("MeimiaoSpeak")
						.toString());
				mIntent.putExtra("imgurl", AllStaticMessage.URL_GBase
						+ jsonObject.getString("ActivityPic").toString());
				mIntent.putExtra("active", "0");
				mContext.startActivity(mIntent);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	public ImageView getItemView() {
		return appItem.AppImg;
	}

}