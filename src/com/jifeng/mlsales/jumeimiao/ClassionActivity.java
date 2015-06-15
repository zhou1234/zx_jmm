package com.jifeng.mlsales.jumeimiao;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.framework.ShareSDK;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class ClassionActivity extends Activity {
	private MyGridViewAdapter mAdapter;
	private GridView mGridView;
	private EditText mEditText;
	private ImageView mImageView;
	private TextView mTextView;
	private LoadingDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_classion);
		((FBApplication) getApplication()).addActivity(this);
		dialog = new LoadingDialog(this);
		findView();
		register();
		getData();
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		setContentView(R.layout.view_null);
		super.onDestroy();
		dialog = null;
		mEditText = null;
		mAdapter = null;
		mGridView = null;
		mImageView = null;
		mTextView = null;
		this.finish();
		System.gc();
	}

	// 查找控件
	private void findView() {
		mGridView = (GridView) findViewById(R.id.classion_gridview);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));// 设置点击是背景透明
		mEditText = (EditText) findViewById(R.id.classion_search_edt);
		mImageView = (ImageView) findViewById(R.id.classion_img_input);
		mTextView = (TextView) findViewById(R.id.classion_title);
	}

	// 注册事件
	private void register() {
		// 监控小键盘搜索按钮
		mEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@SuppressLint("ShowToast") @Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					if (!mEditText.getText().toString().equals("")) {
						String str = mEditText.getText().toString()
								.replace("'", "");
						Intent intent = new Intent(ClassionActivity.this,
								SearchActivity.class);
						intent.putExtra("id", str);
						intent.putExtra("pid", "");
						intent.putExtra("title", "搜索结果");
						intent.putExtra("search", "search");
						startActivity(intent);

					} else {
						Toast.makeText(ClassionActivity.this, "请输入搜索内容", 500)
								.show();
					}
					return true;
				}
				return false;
			}
		});
	}

	// //xml注册点击事件的实现
	@SuppressLint("ShowToast") public void doclick(View view) {
		switch (view.getId()) {
		case R.id.classion_search:
			if (mEditText.getVisibility() == View.INVISIBLE) {
				mEditText.setVisibility(View.VISIBLE);
				mImageView.setVisibility(View.VISIBLE);
				mTextView.setVisibility(View.GONE);
			} else {
				if (mEditText.getText().toString().trim().length() > 0) {
					Intent intent = new Intent(ClassionActivity.this,
							SearchActivity.class);
					intent.putExtra("id", mEditText.getText().toString().trim());
					intent.putExtra("pid", "");
					intent.putExtra("title", "搜索结果");
					intent.putExtra("search", "search");
					startActivity(intent);
				} else {
					Toast.makeText(ClassionActivity.this, "请输入搜索内容", 500)
							.show();
				}
			}

			break;

		default:
			break;
		}
	}

	private void getData() {
		dialog.loading();
		String url = AllStaticMessage.URL_FenLei;
		HttpUtil.get(url, ClassionActivity.this, dialog,
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
								// Log.i("11111", mArray.toString());
								if (mArray.length() > 0) {
									mAdapter = new MyGridViewAdapter(mArray);
									mGridView.setAdapter(mAdapter);
								} else {
									Toast.makeText(
											ClassionActivity.this,
											response.getString("Results")
													.toString(), 500).show();
								}

							} else {
								Toast.makeText(
										ClassionActivity.this,
										response.getString("Results")
												.toString(), 500).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (dialog != null) {
							dialog.stop();
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

	// ---------------------------------------------------------------
	private class MyGridViewAdapter extends BaseAdapter {
		AppItem appItem;
		JSONArray jsonArray;
		private DisplayImageOptions options;

		public MyGridViewAdapter(JSONArray array) {
			this.jsonArray = array;
			options = MyTools.createOptionsOther(R.drawable.img);
		}

		@Override
		public int getCount() {
			return jsonArray.length();
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				View v = LayoutInflater.from(ClassionActivity.this).inflate(
						R.layout.item_classion_gridview, null);
				appItem = new AppItem();
				appItem.AppImg = (ImageView) v.findViewById(R.id.imgbig);
				appItem.AppText = (TextView) v.findViewById(R.id.imgtitle);
				v.setTag(appItem);
				convertView = v;
			} else {
				appItem = (AppItem) convertView.getTag();
			}
			try {
				appItem.AppText.setText(jsonArray.getJSONObject(position)
						.getString("CateName").toString());
				String urlImg = AllStaticMessage.URL_GBase
						+ jsonArray.getJSONObject(position).getString("Logo")
								.toString();
				if (urlImg != null) {
					ImageLoader.getInstance().displayImage(urlImg,
							appItem.AppImg, options);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						Intent intent = new Intent(ClassionActivity.this,
								FenLeiActivity.class);
						intent.putExtra("id", jsonArray.getJSONObject(position)
								.getString("NO").toString());
						intent.putExtra("pid", jsonArray
								.getJSONObject(position).getString("Id")
								.toString());
						intent.putExtra(
								"title",
								jsonArray.getJSONObject(position)
										.getString("CateName").toString());
						intent.putExtra("search", "");
						startActivity(intent);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
			return convertView;
		}

	}

	class AppItem {
		ImageView AppImg;
		TextView AppText;
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
				this.finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
