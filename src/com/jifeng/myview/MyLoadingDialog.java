package com.jifeng.myview; 

import com.jifeng.mlsales.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MyLoadingDialog extends Dialog{
	ImageView iv;
	TextView tv;
	String mes = "ÕÊ√¸º”‘ÿ÷–..."; 
	public MyLoadingDialog(Context context, String mes) {
		this(context, R.style.CustomDialog);
		this.mes = mes; 
	}

	public MyLoadingDialog(Context context) {
		this(context, R.style.CustomDialog);
		// TODO Auto-generated constructor stub
	}

	public MyLoadingDialog(Context context, int theme) {
		super(context, R.style.CustomDialog);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_dialog);
		iv = (ImageView) findViewById(R.id.loadingImageView);
		tv = (TextView) findViewById(R.id.id_tv_loadingmsg);
		tv.setText(mes);
		iv.setBackgroundResource(R.drawable.progress_round);
		 AnimationDrawable animationDrawable = (AnimationDrawable)iv.getBackground();
		 animationDrawable.start();
	}

	/**
	 * @hide
	 */
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}

	/** º”‘ÿ */
	public void loading(String mes) {
		if (this == null)
			return;
		if (!this.isShowing())
			this.show();
		if (mes != null)
			tv.setText(mes);
	}

	public void loading() {
		if (this != null)
			this.loading(null);
	}

	public void stop() {
		// TODO Auto-generated method stub
		if (this != null && this.isShowing()) {
			this.dismiss();
		}
	}
}
