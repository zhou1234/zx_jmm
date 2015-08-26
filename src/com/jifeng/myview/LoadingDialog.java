package com.jifeng.myview;

import com.jifeng.mlsales.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
 

public class LoadingDialog extends Dialog{

	ImageView iv;
	TextView tv;
	String mes = "����������...";

	public LoadingDialog(Context context, String mes) {
		this(context, R.style.CustomDialog);
		this.mes = mes;
	}

	public LoadingDialog(Context context) {
		this(context, R.style.CustomDialog);
	}

	public LoadingDialog(Context context, int theme) {
		super(context, R.style.CustomDialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_dialog);
		iv = (ImageView) findViewById(R.id.loadingImageView);
		tv = (TextView) findViewById(R.id.id_tv_loadingmsg);
		tv.setText(mes);
	}

	/**
	 * @hide
	 */
	@Override
	public void show() {
		super.show();
	}

	/** ���� */
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
		if (this != null && this.isShowing()) {
			this.dismiss();
		}
	}

}
