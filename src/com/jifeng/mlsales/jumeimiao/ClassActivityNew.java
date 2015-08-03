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
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ClassActivityNew extends Activity implements OnClickListener {
	private ListView listView_class;
	private GridView class_gridView;
	private LoadingDialog dialog;
	private List<ClassModel> list_title;
	private List<ClassModel> list_calss;
	private Map<String, List<ClassModel>> map_calss;

	private ClassMyGridViewAdapter adapter;
	private MyAdapter myAdapter;

	private ImageView iv_search;
	private EditText et_search;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_new);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		dialog.loading();
		init();
		list_title = new ArrayList<ClassModel>();
		list_calss = new ArrayList<ClassModel>();
		map_calss = new HashMap<String, List<ClassModel>>();
		myAdapter = new MyAdapter();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				getData();
			}
		}, 200);
	}

	private void init() {
		listView_class = (ListView) findViewById(R.id.listView_class);
		class_gridView = (GridView) findViewById(R.id.class_gridView);
		et_search = (EditText) findViewById(R.id.et_search);
		iv_search = (ImageView) findViewById(R.id.iv_search);
		iv_search.setOnClickListener(this);

		listView_class.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				myAdapter.setSelectItem(arg2);
				myAdapter.notifyDataSetChanged();
				adapter = new ClassMyGridViewAdapter(ClassActivityNew.this,
						map_calss.get(list_title.get(arg2).getId()));
				class_gridView.setAdapter(adapter);

			}
		});
	}

	private void getData() {
		String url = AllStaticMessage.class_url;
		HttpUtil.get(url, ClassActivityNew.this, dialog,
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
											ClassActivityNew.this,
											response.getString("Results")
													.toString(), 500).show();
								}
								listView_class.setAdapter(myAdapter);
								adapter = new ClassMyGridViewAdapter(
										ClassActivityNew.this, map_calss
												.get(list_title.get(0).getId()));
								class_gridView.setAdapter(adapter);

							} else {
								Toast.makeText(
										ClassActivityNew.this,
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

	private class MyAdapter extends BaseAdapter {
		private int selectItem = 0;

		public void setSelectItem(int selectItem) {
			this.selectItem = selectItem;
		}

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

		@SuppressLint({ "NewApi", "ResourceAsColor" })
		@Override
		public View getView(final int pos, View arg1, ViewGroup arg2) {
			View view = arg1;
			ListViewHolder holder = null;
			if (view == null) {
				view = getLayoutInflater().inflate(
						R.layout.activity_class_new_listview_item, arg2, false);
				holder = new ListViewHolder();
				holder.tv_name = (TextView) view
						.findViewById(R.id.tv_class_name);
				view.setTag(holder);
			} else {
				holder = (ListViewHolder) view.getTag();
			}

			if (pos == selectItem) {
				view.setBackground(getResources().getDrawable(
						R.drawable.img_class_listview_item_bg));
				holder.tv_name.setTextColor(getResources().getColor(
						R.color.tab_select));
			} else {
				view.setBackground(getResources().getDrawable(
						R.drawable.img_class_listview_item1_bg));
				holder.tv_name.setTextColor(getResources().getColor(
						R.color.black));
			}
			holder.tv_name.setText(list_title.get(pos).getName());
			return view;
		}
	}

	private class ListViewHolder {
		private TextView tv_name;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_search:
			String strSearch = et_search.getText().toString().replace(" ", "")
					.trim();
			if (strSearch.equals("")) {
				strSearch = "清风";
			}
			Intent intent = new Intent(ClassActivityNew.this,
					SearchActivity.class);
			intent.putExtra("content", strSearch);
			startActivity(intent);
			et_search.setText("");
			break;

		default:
			break;
		}
	}
}
