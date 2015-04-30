package com.jifeng.adapter;

import java.util.List;

import com.jifeng.mlsales.R;
import com.jifeng.mlsales.jumeimiao.ClassItemActivity;
import com.jifeng.mlsales.model.ClassModel;
import com.jifeng.tools.MyTools;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ClassMyGridViewAdapter extends BaseAdapter {
	private Context context;
	private List<ClassModel> classModels;
	private DisplayImageOptions options= MyTools.createOptions(R.drawable.img);

	public ClassMyGridViewAdapter(Context context, List<ClassModel> classModels) {
		this.context = context;
		this.classModels = classModels;
	}

	@Override
	public int getCount() {
		return classModels.size();
	}

	@Override
	public Object getItem(int arg0) {
		return classModels.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int pos, View arg1, ViewGroup arg2) {
		View view = arg1;
		Holder holder = null;
		try {
			if (view == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.activity_class_gridview_item, arg2, false);
				holder = new Holder();
				holder.iv_class = (ImageView) view.findViewById(R.id.iv_class);
				holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
				view.setTag(holder);
			} else {
				holder = (Holder) view.getTag();
			}
			holder.tv_name.setText(classModels.get(pos).getName());
			String imageUrl = classModels.get(pos).getImage_url();
			if (imageUrl != null) {
				ImageLoader.getInstance().displayImage(imageUrl,
						holder.iv_class, options);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(context, ClassItemActivity.class);
				intent.putExtra("id", classModels.get(pos).getId());
				intent.putExtra("name", classModels.get(pos).getName());
				context.startActivity(intent);
			}
		});
		return view;
	}

	class Holder {
		ImageView iv_class;
		TextView tv_name;

	}
}
