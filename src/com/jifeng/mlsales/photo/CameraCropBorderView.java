package com.jifeng.mlsales.photo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class CameraCropBorderView extends View {
	/**
	 * ���Ƶľ��εĿ��
	 */
	private int mWidth = 640;
	/**
	 * ���Ƶľ��εĸ߶�
	 */
	private int mHeight = 640;
	/**
	 * �߿����ɫ��Ĭ��Ϊ��ɫ
	 */
	private int mBorderColor = Color.parseColor("#FFFFFF");
	/**
	 * �߿��������ɫ
	 */
	private int mFillColor = Color.parseColor("#d6000000");
	/**
	 * �߿�Ŀ�� ��λdp
	 */
	private int mBorderWidth = 1;
	/**
	 * border�Ļ���
	 */
	private Paint mPaint;
	/**
	 * fill�Ļ���
	 */
	private Paint mPaintFill;
	/**
	 * ���Ƶľ��εķ�Χ
	 */
	private Rect rect = new Rect();
	/**
	 * ��һ�λ���ʱȡ��rect�ķ�Χ
	 */
	private boolean isFirst = true;

	public CameraCropBorderView(Context context) {
		this(context, null);
	}

	public CameraCropBorderView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CameraCropBorderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mBorderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources().getDisplayMetrics());
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(mBorderColor);
		mPaint.setStrokeWidth(mBorderWidth);
		mPaint.setStyle(Style.STROKE);

		mPaintFill = new Paint();
		mPaintFill.setAntiAlias(true);
		mPaintFill.setColor(mFillColor);
		mPaintFill.setStyle(Style.FILL);
	}

	public Rect getRect() {
		return rect;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (isFirst) {
			int srceenW = getWidth();
			int screenH = getHeight();
			mWidth = mHeight = srceenW > screenH ? screenH : srceenW;
			int left = (srceenW - mWidth) / 2;
			int top = (screenH - mHeight) / 2;
			int right = left + mWidth;
			int bottom = top + mHeight;
			rect.set(left, top, right, bottom);
			isFirst = false;
		}
		// ������߿�
		canvas.drawRect(rect, mPaint);
		//�����ϱ�
		canvas.drawRect(0, 0, getWidth(), rect.top, mPaintFill);
		//�����±�
		canvas.drawRect(0, rect.bottom, getWidth(), getHeight(), mPaintFill);
		//�������
		canvas.drawRect(0, rect.top, rect.left, rect.bottom, mPaintFill);
		//�����ұ�
		canvas.drawRect(rect.right, rect.top, getWidth(), rect.bottom, mPaintFill);
	}
}
