package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.mlsales.model.Tags;
import com.jifeng.url.AllStaticMessage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityLabel extends Activity {
	private EditText et_label;
	private TextView tv_cancel, tv_newTag;
	private ListView listView_label;
	private ListView listView_hot;

	private List<String> listLabel;
	private List<Tags> listHot;
	private MyListViewAdapter adapter;
	private MyListViewHotAdapter hotAdapter;
	private Intent intent;
	private String str = "";
	private InputMethodManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_label);
		((FBApplication) getApplication()).addActivity(this);
		init();
	}

	private void init() {
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		intent = new Intent();
		listLabel = new ArrayList<String>();
		listHot = new ArrayList<Tags>();
		adapter = new MyListViewAdapter();
		hotAdapter = new MyListViewHotAdapter();
		addTagList();

		et_label = (EditText) findViewById(R.id.et_label);
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		listView_label = (ListView) findViewById(R.id.listView_label);
		listView_hot = (ListView) findViewById(R.id.listView_hot);

		View hotHeaderView = getLayoutInflater().inflate(
				R.layout.activity_listview_tag_hot_header, null);
		listView_hot.addHeaderView(hotHeaderView);
		listView_hot.setAdapter(hotAdapter);

		View tagHeaderView = getLayoutInflater().inflate(
				R.layout.listview_label_header, null);
		tv_newTag = (TextView) tagHeaderView.findViewById(R.id.tv_newTag);
		listView_label.addHeaderView(tagHeaderView);
		listView_label.setAdapter(adapter);

		tagHeaderView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				str = tv_newTag.getText().toString().trim();
				finishActivity();
			}
		});
		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finishActivity();
			}
		});
		et_label.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				Editable editable = et_label.getText();
				if (editable.toString().equals("")
						|| editable.toString() == null) {
					listView_label.setVisibility(View.GONE);
					listView_hot.setVisibility(View.VISIBLE);

				} else {
					tv_newTag.setText(editable.toString());
					listView_label.setVisibility(View.VISIBLE);
					listView_hot.setVisibility(View.GONE);
					// listLabel.clear();
					// listLabel.add(editable.toString());
					// adapter.notifyDataSetChanged();

					listLabel.clear();
					if (AllStaticMessage.jsonObjectsTag != null) {
						for (int i = 0; i < AllStaticMessage.jsonObjectsTag
								.size(); i++) {
							try {
								String tagName = AllStaticMessage.jsonObjectsTag
										.get(i).getString("TagName").toString();
								if (tagName.contains(editable.toString())) {
									listLabel.add(tagName);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						adapter.notifyDataSetChanged();
					}

				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
	}

	private void finishActivity() {
		intent.putExtra("str", str);
		setResult(100, intent);
		finish();
		if (getCurrentFocus() != null
				&& getCurrentFocus().getWindowToken() != null) {
			manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private void addTagList() {
		try {
			int cont;
			if (AllStaticMessage.jsonObjectsTag.size() < 10) {
				cont = AllStaticMessage.jsonObjectsTag.size();
			} else {
				cont = 10;
			}
			for (int i = 0; i < cont; i++) {
				Tags tags = new Tags();
				String tagName = AllStaticMessage.jsonObjectsTag.get(i)
						.getString("TagName").toString();
				String tagHot = AllStaticMessage.jsonObjectsTag.get(i)
						.getString("UseCount").toString();
				tags.setTagName(tagName);
				tags.setUserHot(tagHot);
				listHot.add(tags);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private class MyListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listLabel.size();
		}

		@Override
		public Object getItem(int arg0) {
			return listLabel.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int post, View arg1, ViewGroup arg2) {
			View view = getLayoutInflater().inflate(
					R.layout.listview_label_item, arg2, false);
			TextView tv_labelName = (TextView) view
					.findViewById(R.id.tv_labelName);

			tv_labelName.setText(listLabel.get(post));
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					str = listLabel.get(post).toString().trim();
					finishActivity();
				}
			});
			return view;
		}

	}

	private class MyListViewHotAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return listHot.size();
		}

		@Override
		public Object getItem(int arg0) {
			return listHot.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int post, View arg1, ViewGroup arg2) {
			View view = getLayoutInflater().inflate(
					R.layout.activity_listview_tag_item, arg2, false);
			TextView tv_tagName = (TextView) view.findViewById(R.id.tv_tagName);
			TextView tv_tagHot = (TextView) view.findViewById(R.id.tv_tagHot);

			tv_tagName.setText(listHot.get(post).getTagName());
			tv_tagHot.setText(listHot.get(post).getUserHot() + "ÈË²ÎÓë");
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					str = listHot.get(post).getTagName().toString().trim();
					finishActivity();
				}
			});
			return view;
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			/* Òþ²ØÈí¼üÅÌ */
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (inputMethodManager.isActive()) {
				inputMethodManager.hideSoftInputFromWindow(ActivityLabel.this
						.getCurrentFocus().getWindowToken(), 0);
			}

			return true;
		}
		return super.dispatchKeyEvent(event);
	}
}
