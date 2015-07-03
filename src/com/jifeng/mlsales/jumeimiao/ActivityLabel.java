package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.List;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityLabel extends Activity {
	private EditText et_label;
	private TextView tv_cancel;
	private ListView listView_label;

	private List<String> listLabel;
	private MyListViewAdapter adapter;
	private Intent intent;
	private String str = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_label);
		((FBApplication) getApplication()).addActivity(this);
		init();
		intent = new Intent();
	}

	private void init() {

		et_label = (EditText) findViewById(R.id.et_label);
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		listView_label = (ListView) findViewById(R.id.listView_label);

		tv_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				intent.putExtra("str", "");
				setResult(100, intent);
				finish();
			}
		});

		listLabel = new ArrayList<String>();
		adapter = new MyListViewAdapter();
		listView_label.setAdapter(adapter);

		listView_label.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				str = listLabel.get(arg2).toString().trim();
				intent.putExtra("str", str);
				setResult(100, intent);
				finish();
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

				} else {
					listView_label.setVisibility(View.VISIBLE);
					listLabel.clear();
					listLabel.add(editable.toString());
					adapter.notifyDataSetChanged();
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

	class MyListViewAdapter extends BaseAdapter {

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
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View view = getLayoutInflater().inflate(
					R.layout.listview_label_item, arg2, false);
			final TextView tv_labelName = (TextView) view
					.findViewById(R.id.tv_labelName);
			// ImageView iv_addLabel = (ImageView) view
			// .findViewById(R.id.iv_addLabel);

			tv_labelName.setText(listLabel.get(arg0));
			return view;
		}

	}
}
