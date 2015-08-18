package com.jifeng.mlsales.jumeimiao;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jifeng.mlsales.R;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

	private String BaskOrderId;// 晒单id

	private List<JSONObject> mData;
	private DisplayImageOptions options;
	private InputMethodManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		dialog = new LoadingDialog(this);
		options = MyTools.createOptions(R.drawable.my_icon);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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

		listView_comment.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					if (getCurrentFocus() != null
							&& getCurrentFocus().getWindowToken() != null) {
						manager.hideSoftInputFromWindow(getCurrentFocus()
								.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
				}
				return onTouchEvent(arg1);
			}
		});
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

	private class myListViewAdapter extends BaseAdapter {

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
				String imageUrl = mData.get(arg0).getString("Photo");
				if (!imageUrl.equals("") && imageUrl != null) {
					ImageLoader.getInstance().displayImage(imageUrl,
							holder.iv_user, options);
				}
				holder.tv_name.setText(mData.get(arg0).getString("NickName"));
				holder.tv_time.setText(mData.get(arg0).getString("ReviewTime"));
				// String comment = null;
				// try {
				// comment = unicodeToString(mData.get(arg0)
				// .getString("Content").toString().replace(" ", "")
				// .trim());
				// } catch (Exception e) {
				// comment = mData.get(arg0).getString("Content").toString()
				// .replace(" ", "").trim();
				// }
				holder.tv_commmet.setText(mData.get(arg0).getString("Content")
						.toString().replace(" ", "").trim());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return arg1;
		}
	}

	private class Holder {
		private ImageView iv_user;
		private TextView tv_name;
		private TextView tv_time;
		private TextView tv_commmet;
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_back:
			finish();
			if (getCurrentFocus() != null
					&& getCurrentFocus().getWindowToken() != null) {
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
			break;
		case R.id.tv_send:
			if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
				Intent mIntent = new Intent(CommentActivity.this,
						LoginActivity.class);
				startActivity(mIntent);
			} else {
				String content = et_comment.getText().toString().trim();
				// String str = null;
				if (!content.equals("") && content != null) {
					// try {
					// str = strToUnicode(content);
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					sendComment(content);
				} else {
					Toast.makeText(CommentActivity.this, "忘记写评论了哦!",
							Toast.LENGTH_SHORT).show();
				}
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(et_comment, InputMethodManager.SHOW_FORCED);
				imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0); // 强制隐藏键
				et_comment.setText("");
			}
			break;
		default:
			break;
		}
	}

	/**
	 * String的字符串转换成unicode的String
	 * 
	 * @param String
	 *            strText 全角字符串
	 * @return String 每个unicode之间无分隔符
	 * @throws Exception
	 */
	private static String strToUnicode(String strText) throws Exception {
		char c;
		StringBuilder str = new StringBuilder();
		int intAsc;
		String strHex;
		for (int i = 0; i < strText.length(); i++) {
			c = strText.charAt(i);
			intAsc = (int) c;
			strHex = Integer.toHexString(intAsc);
			if (intAsc > 128)
				str.append("\\u" + strHex);
			else
				// 低位在前面补00
				str.append("\\u00" + strHex);
		}
		return str.toString().replace("\\", "E");
	}

	/**
	 * unicode的String转换成String的字符串
	 * 
	 * @param String
	 *            hex 16进制值字符串 （一个unicode为2byte）
	 * @return String 全角字符串
	 */
	private static String unicodeToString(String h) {
		StringBuilder str = new StringBuilder();
		if (!h.contains("E")) {
			str.append(h);
		} else {
			String string = h.replace("E", "\\");
			int t = string.length() / 6;
			for (int i = 0; i < t; i++) {
				String s = string.substring(i * 6, (i + 1) * 6);
				// 高位需要补上00再转
				String s1 = s.substring(2, 4) + "00";
				// 低位直接转
				String s2 = s.substring(4);
				// 将16进制的string转为int
				int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
				// 将int转换为字符
				char[] chars = Character.toChars(n);
				str.append(new String(chars));
			}
		}
		return str.toString();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null
					&& getCurrentFocus().getWindowToken() != null) {
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			/* 隐藏软键盘 */
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (inputMethodManager.isActive()) {
				inputMethodManager.hideSoftInputFromWindow(CommentActivity.this
						.getCurrentFocus().getWindowToken(), 0);
			}

			return true;
		}
		return super.dispatchKeyEvent(event);
	}
}
