package com.jifeng.mlsales.photo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.jifeng.mlsales.R;

public class TagViewLeftNew extends TagView {
	public TagViewLeftNew(Context paramContext) {
		this(paramContext, null);
	}

	public TagViewLeftNew(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		LayoutInflater.from(paramContext).inflate(R.layout.view_tag_left, this);
		this.textview = ((TextView) findViewById(R.id.text));
		this.textview.getBackground().setAlpha(178);
		this.blackIcon1 = ((ImageView) findViewById(R.id.blackIcon1));
		this.blackIcon2 = ((ImageView) findViewById(R.id.blackIcon2));
		this.brandIcon = ((ImageView) findViewById(R.id.brandIcon));
		this.geoIcon = ((ImageView) findViewById(R.id.geoIcon));
		this.viewPointer = brandIcon;
		setVisibleNew();
	}
}
