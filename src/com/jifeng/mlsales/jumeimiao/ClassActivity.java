package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jifeng.adapter.ClassMyGridViewAdapter;
import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.mlsales.model.ClassModel;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.myview.My_GridView;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ClassActivity extends Activity {
	private ListView listView_class;
	private TextView tv_classTitle;
	private LoadingDialog dialog;
	private List<ClassModel> list_title;
	private List<ClassModel> list_calss;
	private Map<String, List<ClassModel>> map_calss;

	private ImageView goodslist_zhiding;
	private ClassMyGridViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		dialog.loading();
		init();
		list_title = new ArrayList<ClassModel>();
		list_calss = new ArrayList<ClassModel>();
		map_calss = new HashMap<String, List<ClassModel>>();
		getData();
	}

	/**
	 * 初始化数据
	 */
	private void init() {
		listView_class = (ListView) findViewById(R.id.listView_class);
		tv_classTitle = (TextView) findViewById(R.id.tv_classTitle);
		goodslist_zhiding = (ImageView) findViewById(R.id.goodslist_zhiding);

		findViewById(R.id.fenlei_back).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						ClassActivity.this.finish();
					}
				});

		goodslist_zhiding.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				listView_class.setSelection(0);
			}
		});
	}

	private void getData() {
		String url = AllStaticMessage.class_url;
		HttpUtil.get(url, ClassActivity.this, dialog,
				new JsonHttpResponseHandler() {
					@SuppressLint("ShowToast") @Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						// 成功返回JSONObject
						try {
							if (response.getString("Status").toString()
									.equals("true")) {
								JSONArray mArray = response
										.getJSONArray("Results");
								if (mArray.length() > 0 && mArray != null) {
									for (int i = 0; i < mArray.length(); i++) {
										String level = mArray.getJSONObject(i)
												.getString("Level").trim()
												.toString();
										ClassModel model = null;
										if (level.equals("1")) {
											model = new ClassModel();
											model.setName(mArray
													.getJSONObject(i)
													.getString("CategoryName")
													.trim().toString());
											model.setId(mArray.getJSONObject(i)
													.getString("Id").trim()
													.toString());
											list_title.add(model);
										}
									}
									for (int i = 0; i < mArray.length(); i++) {
										String level = mArray.getJSONObject(i)
												.getString("Level").trim()
												.toString();
										ClassModel model = null;
										if (level.equals("2")) {
											model = new ClassModel();
											model.setName(mArray
													.getJSONObject(i)
													.getString("CategoryName")
													.trim().toString());
											model.setParentId(mArray
													.getJSONObject(i)
													.getString("ParentId")
													.trim().toString());
											model.setId(mArray.getJSONObject(i)
													.getString("Id").trim()
													.toString());
											model.setImage_url(mArray
													.getJSONObject(i)
													.getString("CategoryIcon")
													.trim().toString());
											list_calss.add(model);
										}
									}
									List<ClassModel> models = null;
									for (int i = 0; i < list_title.size(); i++) {
										String title_id = list_title.get(i)
												.getId();
										models = new ArrayList<ClassModel>();
										for (int j = 0; j < list_calss.size(); j++) {
											String id = list_calss.get(j)
													.getParentId();
											if (title_id.equals(id)) {
												models.add(list_calss.get(j));
											}
										}
										map_calss.put(title_id, models);
									}

								} else {
									Toast.makeText(
											ClassActivity.this,
											response.getString("Results")
													.toString(), 500).show();
								}
								listView_class.setAdapter(new MyAdapter());

								listView_class
										.setOnScrollListener(new AbsListView.OnScrollListener() {

											@Override
											public void onScrollStateChanged(
													AbsListView arg0, int arg1) {

											}

											@Override
											public void onScroll(
													AbsListView arg0,
													final int arg1, int arg2,
													int arg3) {
												new Handler().postDelayed(
														new Runnable() {
															@Override
															public void run() {
																tv_classTitle
																		.setText(list_title
																				.get(arg1)
																				.getName());
																if (arg1 > 2) {
																	goodslist_zhiding
																			.setVisibility(View.VISIBLE);
																} else {
																	goodslist_zhiding
																			.setVisibility(View.GONE);
																}
															}
														}, 0);

											}
										});
							} else {
								Toast.makeText(
										ClassActivity.this,
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

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list_title.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list_title.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = arg1;
			Holder holder = null;
			if (view == null) {
				view = getLayoutInflater().inflate(
						R.layout.activity_class_listview_item, arg2, false);
				holder = new Holder();
				holder.tv_myGridViewTitle = (TextView) view
						.findViewById(R.id.tv_myGridViewTitle);
				holder.my_gridView = (My_GridView) view
						.findViewById(R.id.my_gridView);
				holder.my_gridView.setSelector(new ColorDrawable(
						Color.TRANSPARENT));
				view.setTag(holder);
			} else {
				holder = (Holder) view.getTag();
			}
			if (arg0 == 0) {
				holder.tv_myGridViewTitle.setVisibility(View.GONE);
				tv_classTitle.setText(list_title.get(0).getName());
			} else {
				holder.tv_myGridViewTitle.setVisibility(View.VISIBLE);
			}
			if (arg0 < list_title.size() - 1) {
				holder.tv_myGridViewTitle.setVisibility(View.VISIBLE);
				holder.tv_myGridViewTitle.setText(list_title.get(arg0 + 1)
						.getName());
			} else {
				holder.tv_myGridViewTitle.setVisibility(View.GONE);
			}

			adapter = new ClassMyGridViewAdapter(ClassActivity.this,
					map_calss.get(list_title.get(arg0).getId()));
			// holder.my_gridView.setAdapter(adapter);
			setAdapter(adapter, holder.my_gridView);
			return view;
		}

	}

	private void setAdapter(final ClassMyGridViewAdapter adapter,
			final My_GridView my_gridView) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				my_gridView.setAdapter(adapter);
				if (dialog != null) {
					dialog.stop();
				}
			}
		}, 0);

	}

	private class Holder {
		private TextView tv_myGridViewTitle;
		private My_GridView my_gridView;

	}
}
