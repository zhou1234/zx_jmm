package com.jifeng.mlsales.jumeimiao;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.jifeng.mlsales.FBApplication;
import com.jifeng.mlsales.R;
import com.jifeng.mlsales.photo.BitmapUtil;
import com.jifeng.mlsales.photo.LocalStatic;
import com.jifeng.mlsales.photo.TagsView;

/**
 * Created by sreay on 14-11-25.
 */
public class ActivityTagsShow extends Activity {
	private TagsView tagsView;
	public Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_tags_show);
		((FBApplication) getApplication()).addActivity(this);
		tagsView = (TagsView) findViewById(R.id.tagsView);
		bitmap = BitmapUtil.loadBitmap(LocalStatic.path);
		tagsView.setImage(bitmap);
		tagsView.setTagInfoModels(LocalStatic.tagInfoModels);
	}

	@Override
	public void onBackPressed() {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}

		super.onBackPressed();
	}
}
