package com.jifeng.mlsales.jumeimiao;

import java.io.File;

import com.edmodo.cropper.CropImageView;
import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.mlsales.photo.BitmapUtil;
import com.jifeng.mlsales.photo.PathManager;
import com.jifeng.url.AllStaticMessage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ShowCropActivity extends Activity implements OnClickListener {
	private CropImageView cropImageView;
	private ImageView iv_ok, iv_false;
	private LinearLayout ll_false, ll_ok;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crop_show);
		((FBApplication) getApplication()).addActivity(this);
		init();

		Intent intent = getIntent();
		if (intent != null) {
			String path = intent.getStringExtra("img");
			File file = new File(path);
			if (file.exists()) {
				Bitmap bitmap = BitmapFactory.decodeFile(path);
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				cropImageView.setImageBitmap(bitmap);
				if (width < 400 || height < 400) {
					Toast.makeText(this, "Í¼Æ¬³ß´çÌ«Ð¡ÁË", Toast.LENGTH_SHORT).show();
					finish();
				} else {
					int aspectRatio;
					if (width > height) {
						aspectRatio = height;
					} else {
						aspectRatio = width;
					}
					cropImageView.setAspectRatio(aspectRatio, aspectRatio);
					cropImageView.setFixedAspectRatio(true);
				}
			} else {
				Toast.makeText(this, "Í¼Æ¬³ß´çÌ«Ð¡ÁË", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	private void init() {
		cropImageView = (CropImageView) findViewById(R.id.cropImageView);
		iv_ok = (ImageView) findViewById(R.id.iv_ok);
		iv_false = (ImageView) findViewById(R.id.iv_false);
		ll_ok = (LinearLayout) findViewById(R.id.ll_ok);
		ll_false = (LinearLayout) findViewById(R.id.ll_false);

		iv_ok.setOnClickListener(this);
		ll_ok.setOnClickListener(this);
		iv_false.setOnClickListener(this);
		ll_false.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_ok:
		case R.id.ll_ok:
			Bitmap croppedImage = cropImageView.getCroppedImage();
			File photoFile = PathManager.getCropPhotoPath();
			boolean successful = BitmapUtil.saveBitmap2file(croppedImage,
					photoFile, Bitmap.CompressFormat.JPEG, 100);
			while (!successful) {
				successful = BitmapUtil.saveBitmap2file(croppedImage,
						photoFile, Bitmap.CompressFormat.JPEG, 100);
			}
			if (croppedImage != null && !croppedImage.isRecycled()) {
				croppedImage.recycle();
			}
			if (AllStaticMessage.user_flag) {
				Intent intent = new Intent();
				intent.putExtra("img", photoFile.getAbsolutePath());
				setResult(RESULT_OK, intent);
			} else {
				ActivityAddTags.open(this, photoFile.getAbsolutePath());
			}
			finish();
			break;
		case R.id.iv_false:
		case R.id.ll_false:
			finish();
			break;
		default:
			break;
		}

	}

}
