package com.jifeng.mlsales.jumeimiao;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.mlsales.photo.BitmapUtil;
import com.jifeng.mlsales.photo.FixWidthFrameLayout;
import com.jifeng.mlsales.photo.HGAlertDlg;
import com.jifeng.mlsales.photo.HGTagPickerView;
import com.jifeng.mlsales.photo.HGTipsDlg;
import com.jifeng.mlsales.photo.LocalStatic;
import com.jifeng.mlsales.photo.TagInfo;
import com.jifeng.mlsales.photo.TagInfoModel;
import com.jifeng.mlsales.photo.TagView;
import com.jifeng.mlsales.photo.TagViewLeft;
import com.jifeng.mlsales.photo.TagViewLeftNew;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.FileImageUpload;
import com.jifeng.url.AllStaticMessage;

/**
 * Created by sreay on 14-9-22.
 */
public class ActivityAddTags extends Activity implements View.OnClickListener,
		View.OnTouchListener, TagView.TagViewListener,
		HGAlertDlg.HGAlertDlgClickListener,
		HGTagPickerView.HGTagPickerViewListener, TextWatcher {
	private ImageView headLeft;
	private TextView headTitle;
	private TextView headRight;
	private ImageView image;
	private FrameLayout tagsContainer;
	private FixWidthFrameLayout frameLayout;

	private float positionX = 0;
	private float positionY = 0;
	private float X;
	private float Y;

	private int width, w, wid;
	private int height, h;
	private List<TagView> tagViews = new ArrayList<TagView>();
	private List<TagInfo> tagInfos = new ArrayList<TagInfo>();

	private static final String KImagePath = "image_path";
	private String imagePath = "";
	public Bitmap bitmap;

	private HGAlertDlg dlg;
	private HGTagPickerView tagPickerView;
	private HGTipsDlg tipsDlg;
	private int tagsCount = 0;
	private List<String> base = Arrays.asList("时尚流", "小清新", "复古风", "甜美风",
			"中性风", "作死", "这就是我", "清晨的宁静", "下午茶", "后会无期", "no zuo no die",
			"随心所欲");
	private String content = "";
	private EditText et_comment;
	private Button bt_ok;
	private LoadingDialog dialog;
	private LinearLayout ll_text;
	private TagView tag;
	private String str;
	private TagView tagView;
	private TagView moveTagView;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 001:
				try {
					dialog.stop();
					if (!str.equals("")) {
						JSONObject object = new JSONObject(str);
						if (object.getString("Status").toString()
								.equals("true")) {
							AllStaticMessage.Back_to_Classion = true;
							Intent mIntent = new Intent(ActivityAddTags.this,
									TabHostActivity.class);
							AllStaticMessage.isShare = true;

							AllStaticMessage.title = object
									.getJSONObject("Results")
									.getString("Content").toString();
							AllStaticMessage.url = object
									.getJSONObject("Results")
									.getString("ShareUrl").toString();
							AllStaticMessage.imgurl = object
									.getJSONObject("Results")
									.getString("ImageUrl").toString();
							startActivity(mIntent);
							Toast.makeText(ActivityAddTags.this, "发布成功",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(ActivityAddTags.this, "发布失败",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(ActivityAddTags.this, "发布失败",
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;

			default:
				break;
			}

		};
	};

	public static void open(Activity activity, String imagePath) {
		Intent intent = new Intent(activity, ActivityAddTags.class);
		intent.putExtra(KImagePath, imagePath);
		activity.startActivity(intent);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_add_tags);
		((FBApplication) getApplication()).addActivity(this);
		wid = getWindowManager().getDefaultDisplay().getWidth();
		if (getIntent() != null) {
			Intent intent = getIntent();
			imagePath = intent.getStringExtra(KImagePath);
		}
		getViews();
		initViews();
		setListeners();
	}

	protected void getViews() {
		headLeft = (ImageView) findViewById(R.id.tv_head_left);
		headTitle = (TextView) findViewById(R.id.tv_head_title);
		headRight = (TextView) findViewById(R.id.tv_head_right);
		headRight.setVisibility(View.GONE);
		image = (ImageView) findViewById(R.id.image);
		frameLayout=(FixWidthFrameLayout) findViewById(R.id.frameLayout);
		LayoutParams params = frameLayout.getLayoutParams();
		params.width = wid;
		params.height = wid;
		frameLayout.setLayoutParams(params);

		tagsContainer = (FrameLayout) findViewById(R.id.tagsContainer);
		et_comment = (EditText) findViewById(R.id.et_comment);
		bt_ok = (Button) findViewById(R.id.bt_ok);
		ll_text = (LinearLayout) findViewById(R.id.ll_text);
		ll_text.setOnClickListener(this);

		bt_ok.setOnClickListener(this);

		addTagView();
		TranslateAnimation alphaAnimation = new TranslateAnimation(-30f, 30f,
				0, 0);
		alphaAnimation.setDuration(200);
		alphaAnimation.setRepeatCount(4);
		alphaAnimation.setRepeatMode(Animation.REVERSE);
		ll_text.setAnimation(alphaAnimation);
		alphaAnimation.start();
	}

	protected void initViews() {
		dialog = new LoadingDialog(this);
		headTitle.setText("添加标签");
		headRight.setText("完成");
		bitmap = BitmapUtil.loadBitmap(imagePath);
		image.setImageBitmap(bitmap);
	}

	protected void setListeners() {
		headLeft.setOnClickListener(this);
		headRight.setOnClickListener(this);
		image.setOnClickListener(this);
		image.setOnTouchListener(this);
		et_comment.addTextChangedListener(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		this.width = image.getMeasuredWidth();
		this.height = image.getMeasuredHeight();
	}

	@Override
	protected void onDestroy() {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_head_left:
			this.finish();
			break;
		case R.id.tv_head_right:
			goToEditFootprints();
			break;
		case R.id.image: {
			if (tagsCount >= 5) {
				if (!HGTipsDlg.hasDlg(this)) {
					tipsDlg = HGTipsDlg.showDlg("最多添加5个标签~", this);
				}
				return;
			}
			// if (tagPickerView == null) {
			// tagPickerView = HGTagPickerView.showDlg(base, this, this);
			// }
			addLabel();
			Intent intent = new Intent(this, ActivityLabel.class);
			startActivityForResult(intent, 0x11);
		}
			break;
		case R.id.bt_ok:
			if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
				Intent mIntent = new Intent(ActivityAddTags.this,
						LoginActivity.class);
				startActivity(mIntent);
			} else {
				String contentString = et_comment.getText().toString().trim();
				if (contentString.equals("") || contentString == null) {
					Toast.makeText(this, "喵,要评论一下哦", Toast.LENGTH_SHORT).show();
				} else {
					release();
				}
			}
			break;
		}
	}

	@SuppressLint("ShowToast")
	private void release() {
		if (AllStaticMessage.Login_Flag.equals("")) {// LoginFlag
			Intent mIntent = new Intent(ActivityAddTags.this,
					LoginActivity.class);
			startActivity(mIntent);
		} else {
			dialog.loading();
			goToEditFootprints();
			final String contentString = et_comment.getText().toString().trim();
			final JSONArray array = getLabelStr();
			// final String url = "&UserId=" + AllStaticMessage.User_Id +
			// "&ImageUrl="
			// + bs.toString() + "&Content=" + contentString + "&Tag="
			// + array.toString();

			new Thread(new Runnable() {

				@Override
				public void run() {
					str = FileImageUpload.upShaiDanBitmap(
							AllStaticMessage.URL_SaveBaskOrder, imagePath,
							AllStaticMessage.User_Id, contentString,
							array.toString());
					handler.sendEmptyMessage(001);
				}
			}).start();
		}
	}

	private JSONArray getLabelStr() {
		JSONArray array = new JSONArray();
		try {
			JSONObject object;
			for (TagView tagView : tagViews) {
				object = new JSONObject();
				TagInfoModel infoModel = new TagInfoModel();
				infoModel.tag_name = tagView.getData().bname;
				infoModel.x = (tagView.getData().leftMargin * 1.0f)
						/ (width * 1.0f);
				infoModel.y = (tagView.getData().topMargin * 1.0f)
						/ (height * 1.0f);
				object.put("x", infoModel.x + "");
				object.put("y", infoModel.y + "");
				object.put("name", infoModel.tag_name);
				array.put(object);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return array;

	}

	private void goToEditFootprints() {
		List<TagInfoModel> tagInfos = new ArrayList<TagInfoModel>();
		for (TagView tagView : tagViews) {
			TagInfoModel infoModel = new TagInfoModel();
			infoModel.tag_name = tagView.getData().bname;
			infoModel.x = (tagView.getData().leftMargin * 1.0f)
					/ (width * 1.0f);
			infoModel.y = (tagView.getData().topMargin * 1.0f)
					/ (height * 1.0f);
			tagInfos.add(infoModel);
		}
		// 需要将这些tag的信息上传到服务器
		LocalStatic.addTagInfos(tagInfos);
		LocalStatic.path = imagePath;
		// Intent intent = new Intent(this, ActivityTagsShow.class);
		// startActivity(intent);
		// this.finish();
	}

	private void moveTagView() {
		try {
			String tagName = moveTagView.getData().getjson().getString("bname");
			for (int i = 0; i < tagViews.size(); i++) {
				String name = tagViews.get(i).getData().getjson()
						.getString("bname");
				if (name.equals(tagName)) {
					TagInfo tagInfo = new TagInfo();
					tagInfo.bid = 2L;
					tagInfo.bname = tagName;
					tagInfo.direct = TagInfo.Direction.Left;
					tagInfo.pic_x = 50;
					tagInfo.pic_y = 50;
					tagInfo.type = TagInfo.Type.CustomPoint;
					tagInfo.leftMargin = (int) positionX;
					tagInfo.topMargin = (int) positionY;
					moveTagView.setData(tagInfo);
					tagViews.set(i, moveTagView);
					return;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// tagViews.add(tagView);
		// tagsContainer.addView(tagView, params);
	}

	private void addTag() {
		w = image.getWidth();
		h = image.getHeight();
		tagsCount++;
		TagInfo tagInfo = new TagInfo();
		tagInfo.bid = 2L;
		tagInfo.bname = content;
		tagInfo.direct = TagInfo.Direction.Left;
		tagInfo.pic_x = 50;
		tagInfo.pic_y = 50;
		tagInfo.type = TagInfo.Type.CustomPoint;
		tagInfo.leftMargin = (int) positionX;
		tagInfo.topMargin = (int) positionY;
		TagView tagView = new TagViewLeft(this, null);
		tagView.setData(tagInfo);
		tagView.getWidth();
		tagView.setTagViewListener(this);
		// tagView.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View view, MotionEvent motionEvent) {
		// moveTagView = (TagView) view;
		// int action = motionEvent.getAction();
		// switch (action) {
		// case MotionEvent.ACTION_DOWN:
		// X = motionEvent.getRawX();
		// Y = motionEvent.getRawY();
		// break;
		// case MotionEvent.ACTION_MOVE:
		// int dx = (int) (motionEvent.getRawX() - X);
		// int dy = (int) (motionEvent.getRawY() - Y);
		//
		// int left = (int) (view.getLeft() + dx);
		// int top = (int) (view.getTop() + dy);
		// int right = (int) (view.getRight() + dx);
		// int bottom = (int) (view.getBottom() + dy);
		//
		// if (left < 0) {
		// left = 0;
		// right = left + view.getWidth();
		// }
		//
		// if (right > w) {
		// right = w;
		// left = right - view.getWidth();
		// }
		//
		// if (top < 0) {
		// top = 0;
		// bottom = top + view.getHeight();
		// }
		//
		// if (bottom > h) {
		// bottom = h;
		// top = bottom - view.getHeight();
		// }
		//
		// view.layout(left, top, right, bottom);
		//
		// X = motionEvent.getRawX();
		// Y = motionEvent.getRawY();
		//
		// positionX = motionEvent.getX();
		// positionY = motionEvent.getY();
		// break;
		// case MotionEvent.ACTION_UP:
		// moveTagView();
		// break;
		// default:
		// break;
		// }
		//
		// return false;
		// }
		// });

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		params.leftMargin = tagInfo.leftMargin;
		params.topMargin = tagInfo.topMargin;
		tagViews.add(tagView);
		tagsContainer.addView(tagView, params);
	}

	private void addTagView() {
		List<TagView> addTagViews = new ArrayList<TagView>();
		TagInfo tagInfo = new TagInfo();
		tagInfo.bid = 2L;
		tagInfo.bname = "点击图片,自定义标签";
		tagInfo.direct = TagInfo.Direction.Left;
		tagInfo.pic_x = 50;
		tagInfo.pic_y = 50;
		tagInfo.type = TagInfo.Type.CustomPoint;
		tagInfo.leftMargin = (int) positionX;
		tagInfo.topMargin = (int) positionY;
		tag = new TagViewLeft(this, null);
		tag.setData(tagInfo);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		params.leftMargin = tagInfo.leftMargin;
		params.topMargin = tagInfo.topMargin;
		addTagViews.add(tag);
		ll_text.addView(tag, params);

		tag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				positionX = width / 2;
				positionY = height / 2;
				addLabel();
				Intent intent = new Intent(ActivityAddTags.this,
						ActivityLabel.class);
				startActivityForResult(intent, 0x11);
			}
		});
	}

	private void addLabel() {
		List<TagView> tag = new ArrayList<TagView>();
		tagView = new TagViewLeftNew(this, null);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		params.leftMargin = (int) positionX;
		params.topMargin = (int) positionY;
		tag.add(tagView);
		tagsContainer.addView(tagView, params);
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		int action = motionEvent.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			positionX = motionEvent.getX();
			positionY = motionEvent.getY();
			break;

		}

		return false;
	}

	private View destView;

	@Override
	public void onTagViewClicked(View view, TagInfo info) {
		destView = view;
		if (null == dlg) {
			dlg = HGAlertDlg.showDlg("确定删除该标签?", null, this, this);
		}

	}

	@Override
	public void onBackPressed() {
		if (tipsDlg != null) {
			tipsDlg.onBackPressed(this);
			tipsDlg = null;
			return;
		}
		if (dlg != null) {
			dlg.onBackPressed(this);
			dlg = null;
			return;
		}
		if (tagPickerView != null) {
			tagPickerView.onBackPressed(this);
			tagPickerView = null;
			return;
		}
		super.onBackPressed();
	}

	private void removeTag() {
		tagsCount--;
		tagViews.remove(destView);
		tagsContainer.removeView(destView);
	}

	@Override
	public void onAlertDlgClicked(boolean isConfirm) {
		if (isConfirm) {
			removeTag();
		}
		dlg = null;
	}

	@Override
	public void onTagPickerViewClicked(String content, boolean isConfirm) {
		if (isConfirm) {
			this.content = content;
			addTag();
			ll_text.setVisibility(View.GONE);
		}
		tagPickerView = null;
	}

	@Override
	public void afterTextChanged(Editable arg0) {

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		Editable editable = et_comment.getText();
		int len = editable.length();
		if (len > 140) {
			int selEndIndex = Selection.getSelectionEnd(editable);
			String str = editable.toString();
			// 截取新字符串
			String newStr = str.substring(0, 140);
			et_comment.setText(newStr);
			editable = et_comment.getText();

			// 新字符串的长度
			int newLen = editable.length();
			// 旧光标位置超过字符串长度
			if (selEndIndex > newLen) {
				selEndIndex = editable.length();
			}
			// 设置新光标所在的位置
			Selection.setSelection(editable, selEndIndex);
			Toast.makeText(this, "点评字数不能超过140字", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0x11) {
			if (resultCode == 100) {
				tagsContainer.removeView(tagView);
				String string = data.getStringExtra("str");
				if (!string.equals("")) {
					content = string;
					addTag();
					ll_text.setVisibility(View.GONE);
				}

			}
		}
	}
}
