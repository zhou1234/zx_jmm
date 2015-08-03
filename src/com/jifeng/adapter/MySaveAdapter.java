package com.jifeng.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header; 
import org.json.JSONException;
import org.json.JSONObject;

//import com.jifeng.image.ImageLoader; 
import com.jifeng.mlsales.R;
import com.jifeng.mlsales.jumeimiao.PinPaiZhuangChangActivity;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Dialog; 
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView; 
import android.widget.LinearLayout;
import android.widget.RelativeLayout; 
import android.widget.Toast;

public class MySaveAdapter extends BaseAdapter {
	private Context mContext;
	private AppItem appItem;
	private int height, width;
	private Intent mIntent;
	private List<JSONObject> mJsonArray=new ArrayList<JSONObject>(); 
	private LoadingDialog dialog;
	// 用来控制蒙版的选中状况
	private HashMap<Integer, Boolean> isSelected; 
	private Handler handler;
	public int potion;
	private DisplayImageOptions options;
 
	@SuppressLint("UseSparseArrays") public MySaveAdapter(Context context, int height, int width,List<JSONObject> array,Handler handler) {
		this.mContext = context;
		this.height = height;
		this.width = width;
		this.mJsonArray = array; 
		isSelected = new HashMap<Integer, Boolean>();
		this.handler=handler; 
		//MyTools.initImageLoader(mContext);
		options=MyTools.createOptions(R.drawable.loading_01);
		dialog=new LoadingDialog(mContext);
	}
	// 初始化isSelected的数据
	public void initDate(List<JSONObject> mList,boolean falg) {
				for (int i = 0; i < mList.size(); i++) {
					getIsSelected().put(i, falg);
				}
			}
	@Override
	public int getCount() {
		return mJsonArray.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			View v = LayoutInflater.from(mContext).inflate(
					R.layout.item_save_gridview_2, null);
			appItem = new AppItem();
			appItem.AppText_name = (Button) v.findViewById(R.id.mysave_btn_name);
			appItem.AppmLayout = (RelativeLayout) v.findViewById(R.id.save_gridview_liner);
			appItem.mAppImg = (ImageView) v.findViewById(R.id.mysave_img);
			appItem.mAppLayout_Meng = (RelativeLayout) v.findViewById(R.id.rel_meng);
			appItem.AppBtn_Delete = (Button) v.findViewById(R.id.delete);
			v.setTag(appItem);
			convertView = v;
		} else {
			appItem = (AppItem) convertView.getTag();
		}
		if(getIsSelected().get(position)){
			appItem.mAppLayout_Meng.setVisibility(View.VISIBLE);
		}else{
			appItem.mAppLayout_Meng.setVisibility(View.GONE);
		}
		appItem.AppBtn_Delete.setOnClickListener(new ItemClick(appItem, mJsonArray.get(position), position));
		 
		getHigh(appItem.AppmLayout);
		getHigh(appItem.mAppLayout_Meng);
		try {
			appItem.AppText_name.setText(mJsonArray.get(position).getString("BrandName").toString());
			//加载图片
			String imgUrl=AllStaticMessage.URL_GBase+mJsonArray.get(position).getString("AccountLogo").toString();//AllStaticMessage.URL_GBase+
			ImageLoader.getInstance().displayImage(imgUrl,appItem.mAppImg,options);
	 
		} catch (JSONException e) {
			e.printStackTrace();
		}
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					mIntent=new Intent(mContext,PinPaiZhuangChangActivity.class);
					mIntent.putExtra("id", mJsonArray.get(position).getString("BrandId").toString());
					mContext.startActivity(mIntent);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		});
		return convertView;
	}
	private void getHigh(RelativeLayout AppmLayout){
		RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) AppmLayout
				.getLayoutParams(); // 取控件mLayout当前的布局参数
		
		if (width == 800 && height == 1280) {
			linearParams.height = MyTools.dip2px(mContext, 145);// 手机
			AppmLayout.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件 
		} else if (width == 480 && height == 800) {
			linearParams.height = MyTools.dip2px(mContext, 115);
			AppmLayout.setLayoutParams(linearParams);
		}else if(width == 480 && height == 854){
			linearParams.height = MyTools.dip2px(mContext, 125);
			AppmLayout.setLayoutParams(linearParams);
		} else if (width == 800 && height == 1232) {
			linearParams.height = MyTools.dip2px(mContext, 275);// 平板
			AppmLayout.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件
		} else if (width == 720 && height == 1280) {
			linearParams.height = MyTools.dip2px(mContext, 130);
			AppmLayout.setLayoutParams(linearParams);
		} else if (width == 640 && height == 960) {
			linearParams.height = MyTools.dip2px(mContext, 115);
			AppmLayout.setLayoutParams(linearParams);
		} else if (width == 1600 && height == 2560) {
			linearParams.height = MyTools.dip2px(mContext, 260);
			AppmLayout.setLayoutParams(linearParams);
		}else if(width == 1080 && height == 1812){
			linearParams.height = MyTools.dip2px(mContext, 130);
			AppmLayout.setLayoutParams(linearParams);
		}else if (width == 1080 && height == 1920) {
			linearParams.height = MyTools.dip2px(mContext, 136);
			AppmLayout.setLayoutParams(linearParams);
		}else  if(width == 1440 && height == 2392){
			linearParams.height = MyTools.dip2px(mContext, 155);
			AppmLayout.setLayoutParams(linearParams);
		}
	}
	public HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(HashMap<Integer, Boolean> isSelected2) {
		isSelected = isSelected2;
	}

	
	private class ItemClick  implements OnClickListener{
		JSONObject object;  
		private int position;
		private ItemClick(AppItem appItem,JSONObject object,int position){ 
			this.object=object;
			this.position=position;
		}
		@Override
		public void onClick(View view) {
			try {
				delete(object.get("fid").toString(),position);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} 
	}
	private void delete(final String fid, final int position) {
		Builder builder = new Builder(mContext);
		builder.setTitle("确定移除嘛？"); 
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialo, int which) {
				dialog.loading();
				deleteData(fid,position);
			}
		});

		builder.setNegativeButton("取消", null);
		builder.create().show();
	}
	private void deleteData(String id,final int position){ 
		String url = AllStaticMessage.URL_Delete_Save+AllStaticMessage.User_Id+"&favriteId="+id;
		HttpUtil.get(url, mContext,dialog,new JsonHttpResponseHandler() {  
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				//成功返回JSONObject
				try {
					if(response.getString("Status").equals("true")){   
						potion=position;
						handler.sendEmptyMessage(0x02);
						Toast.makeText(mContext, response.getString("Results").toString(), 500).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} 
				if(dialog!=null){
					dialog.stop();
				}
				
			} 
			  
			@Override
			public void onStart() {
				super.onStart();
				 //请求开始
			} 
			@Override
			public void onFinish() {
				super.onFinish();
				//请求结束
			} 
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				// TODO Auto-generated method stub
				super.onFailure(statusCode, headers, throwable, errorResponse);
				//错误返回JSONObject 
				if(dialog!=null){
					dialog.stop();
				}
			}
		});
	}
}

class AppItem {
	Button AppText_name,AppBtn_Delete;
	ImageView mAppImg;
	RelativeLayout AppmLayout;
	RelativeLayout mAppLayout_Meng;
}

