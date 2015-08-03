package com.jifeng.mlsales.model;

import com.jifeng.mlsales.R;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class UpdateDialog {

	private android.app.AlertDialog ad;
	private TextView dialog_title_tv, dialog_cancel_tv, dialog_ok_tv;

	public UpdateDialog(Context context) {
		try {
			ad = new android.app.AlertDialog.Builder(context).create();
			ad.setCancelable(false);
			ad.show();
			// 关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
			Window window = ad.getWindow();
			window.setContentView(R.layout.update_dialog);
			dialog_title_tv = (TextView) window
					.findViewById(R.id.dialog_title_tv);
			dialog_cancel_tv = (TextView) window
					.findViewById(R.id.dialog_cancel_tv);
			dialog_ok_tv = (TextView) window.findViewById(R.id.dialog_ok_tv);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTitle(int resId) {
		if (dialog_title_tv != null) {
			dialog_title_tv.setText(resId);
		}

	}

	public void setTitle(String title) {
		if (dialog_title_tv != null) {
			dialog_title_tv.setText(title);
		}
	}

	public void setPositiveButtonText(String message) {
		dialog_ok_tv.setText(message);
	}

	public void setNegativeButtonText(String message) {
		dialog_cancel_tv.setText(message);
	}

	/**
	 * 设置按钮
	 * 
	 * @param text
	 * @param listener
	 */
	public void setPositiveButton(String text,
			final View.OnClickListener listener) {
		if (dialog_ok_tv != null) {
			dialog_ok_tv.setText(text);
			dialog_ok_tv.setOnClickListener(listener);
		}

	}

	public void setPositiveButton(String text, OnClickListener onClickListener) {
		if (dialog_ok_tv != null) {
			dialog_ok_tv.setText(text);
			dialog_ok_tv
					.setOnClickListener((android.view.View.OnClickListener) onClickListener);
		}

	}

	/**
	 * 设置按钮
	 * 
	 * @param text
	 * @param listener
	 */
	public void setNegativeButton1(String text,
			final View.OnClickListener listener) {
		if (dialog_cancel_tv != null) {
			dialog_cancel_tv.setText(text);
			dialog_cancel_tv.setOnClickListener(listener);
		}

	}

	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		if (ad != null) {
			ad.dismiss();
		}
	}
}
