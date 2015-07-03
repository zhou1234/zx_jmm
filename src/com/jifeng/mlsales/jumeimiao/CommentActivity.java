package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jifeng.image.ImageLoaderUser;
import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CommentActivity extends Activity implements OnClickListener {
	private ImageView iv_back;
	private ListView listView_comment;
	private TextView tv_wu, tv_send;
	private EditText et_comment;
	private LoadingDialog dialog;
	private ImageLoaderUser imageLoaderUser;

	private String BaskOrderId;// 晒单id

	private List<JSONObject> mData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		dialog = new LoadingDialog(this);
		imageLoaderUser = new ImageLoaderUser(CommentActivity.this, "");
		init();
		mData = new ArrayList<JSONObject>();
		Intent intent = getIntent();
		if (intent != null) {
			BaskOrderId = intent.getStringExtra("id");
			getCommentList();
		}

	}

	private void init() {
		iv_back = (ImageView) findViewById(R.id.iv_back);
		listView_comment = (ListView) findViewById(R.id.listView_comment);
		tv_wu = (TextView) findViewById(R.id.tv_wu);
		tv_send = (TextView) findViewById(R.id.tv_send);
		et_comment = (EditText) findViewById(R.id.et_comment);

		iv_back.setOnClickListener(this);
		tv_send.setOnClickListener(this);
	}

	/**
	 * 评论列表
	 */
	private void getCommentList() {
		dialog.loading();
		String url = AllStaticMessage.URL_ReviewList + "&BaskOrderId="
				+ BaskOrderId + "&UserId=" + AllStaticMessage.User_Id;
		HttpUtil.get(url, CommentActivity.this, null,
				new JsonHttpResponseHandler() {
					@SuppressLint("UseSparseArrays")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							if (response.getString("Status").equals("true")) {
								listView_comment.setVisibility(View.VISIBLE);
								tv_wu.setVisibility(View.GONE);
								JSONArray mArray = response
										.getJSONArray("Results");
								if (mData != null) {
									mData.clear();
								}
								for (int i = 0; i < mArray.length(); i++) {
									mData.add(mArray.getJSONObject(i));
								}

								listView_comment
										.setAdapter(new myListViewAdapter());

							} else {
								listView_comment.setVisibility(View.INVISIBLE);
								tv_wu.setVisibility(View.VISIBLE);
							}

							if (dialog != null) {
								dialog.stop();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						if (dialog != null) {
							dialog.stop();
						}
					}

					@Override
					public void onStart() {
						super.onStart();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						if (dialog != null) {
							dialog.stop();
						}
					}
				});
	}

	/**
	 * 发布评论
	 */
	private void sendComment(String content) {
		dialog.loading();
		String url = AllStaticMessage.URL_CreateReview + "&UserId="
				+ AllStaticMessage.User_Id + "&BaskOrderId=" + BaskOrderId
				+ "&Content=" + content;

		HttpUtil.get(url, CommentActivity.this, null,
				new JsonHttpResponseHandler() {
					@SuppressLint("UseSparseArrays")
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						super.onSuccess(statusCode, headers, response);
						try {
							if (response.getString("Status").equals("true")) {
								getCommentList();
							} else {
								Toast.makeText(CommentActivity.this,
										response.getString("Results"),
										Toast.LENGTH_SHORT).show();
							}
							if (dialog != null) {
								dialog.stop();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						super.onFailure(statusCode, headers, throwable,
								errorResponse);
						if (dialog != null) {
							dialog.stop();
						}
					}

					@Override
					public void onStart() {
						super.onStart();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						if (dialog != null) {
							dialog.stop();
						}
					}
				});
	}

	class myListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mData.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			Holder holder;
			try {
				if (arg1 == null) {
					View view = getLayoutInflater().inflate(
							R.layout.comment_listview_item, arg2, false);
					holder = new Holder();
					holder.iv_user = (ImageView) view
							.findViewById(R.id.iv_user);
					holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
					holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
					holder.tv_commmet = (TextView) view
							.findViewById(R.id.tv_comment);

					Bitmap bitmap = BitmapFactory.decodeResource(
							getResources(), R.drawable.icon);
					Bitmap bitmap1 = MyTools.getRoundedCornerBitmap(bitmap);

					holder.iv_user.setImageBitmap(bitmap1);
					view.setTag(holder);
					arg1 = view;
				} else {
					holder = (Holder) arg1.getTag();
				}

				String imageUrl = AllStaticMessage.URL_GBase+"/"+mData.get(arg0).getString("Photo");
				if (!imageUrl.equals("") && imageUrl != null) {
					imageLoaderUser.DisplayImage(imageUrl, holder.iv_user);
				}

				holder.tv_name.setText(mData.get(arg0).getString("NickName"));
				holder.tv_time.setText(mData.get(arg0).getString("ReviewTime"));
				holder.tv_commmet.setText(mData.get(arg0).getString("Content"));

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return arg1;
		}
	}

	class Holder {
		ImageView iv_user;
		TextView tv_name;
		TextView tv_time;
		TextView tv_commmet;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_send:
			String content = et_comment.getText().toString().trim();
			if (!content.equals("") && content != null) {
				sendComment(content);
			} else {
				Toast.makeText(CommentActivity.this, "忘记写评论了哦!",
						Toast.LENGTH_SHORT).show();
			}

			break;
		default:
			break;
		}

	}
}
