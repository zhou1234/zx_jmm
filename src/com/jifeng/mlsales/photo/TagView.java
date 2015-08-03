package com.jifeng.mlsales.photo;

import com.jifeng.mlsales.R;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TagView extends RelativeLayout implements View.OnClickListener {
	private Animation blackAnimation1;
	private Animation blackAnimation2;
	private Animation whiteAnimation;

	public TextView textview;// ����������ʾView
	public ImageView blackIcon1;// ��ɫԲȦView
	public ImageView blackIcon2;// ��ɫԲȦView
	protected ImageView brandIcon;// ��ɫԲȦView
	protected ImageView geoIcon;// ��ɫ��λԲȦView
	public ImageView viewPointer;// ָ��brandIcon����geoIcon���������õ����͵Ĳ�ͬ

	public boolean isShow = false;
	private TagViewListener listener;
	private Handler handler = new Handler();

	public interface TagViewListener {
		public void onTagViewClicked(View view, TagInfo info);
	}

	public TagView(Context context) {
		this(context, null);
	}

	public TagView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.blackAnimation1 = AnimationUtils.loadAnimation(context,
				R.anim.black_anim);
		this.blackAnimation2 = AnimationUtils.loadAnimation(context,
				R.anim.black_anim);
		this.whiteAnimation = AnimationUtils.loadAnimation(context,
				R.anim.white_anim);
		this.setOnClickListener(this);
	}

	private final void clearAnim() {
		this.blackIcon1.clearAnimation();
		this.blackIcon2.clearAnimation();
		this.viewPointer.clearAnimation();
		this.isShow = false;
	}

	private final void startBlackAnimation1(final ImageView imageView) {
		blackAnimation1.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (!isShow) {
					return;
				}
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						imageView.clearAnimation();
						blackAnimation1.reset();
						startBlackAnimation2(blackIcon2);
					}
				}, 10);
			}
		});
		imageView.clearAnimation();
		imageView.startAnimation(blackAnimation1);
	}

	private final void startBlackAnimation2(final ImageView imageView) {
		blackAnimation2.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (!isShow) {
					return;
				}
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						imageView.clearAnimation();
						blackAnimation2.reset();
						startWhiteAnimation(viewPointer);
					}
				}, 10);
			}
		});
		imageView.clearAnimation();
		imageView.startAnimation(blackAnimation2);
	}

	private final void startWhiteAnimation(final ImageView imageView) {
		whiteAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (!isShow) {
					return;
				}
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						imageView.clearAnimation();
						whiteAnimation.reset();
						startBlackAnimation1(blackIcon1);
					}
				}, 10);
			}
		});
		imageView.clearAnimation();
		imageView.startAnimation(whiteAnimation);
	}

	protected final void setVisible() {
		if ((textview != null) && (taginfo != null)) {
			if (TagInfo.Type.CustomPoint != taginfo.type
					&& TagInfo.Type.OfficalPoint != taginfo.type) {
				this.viewPointer = this.brandIcon;
				this.geoIcon.setVisibility(View.VISIBLE);
				this.brandIcon.setVisibility(View.GONE);
			} else {
				this.viewPointer = this.geoIcon;
				this.geoIcon.setVisibility(View.GONE);
				this.brandIcon.setVisibility(View.VISIBLE);
			}
			textview.setText(taginfo.bname);
			textview.setVisibility(View.VISIBLE);
		}
		clearAnim();
		show();
	}

	protected final void setVisibleNew() {
		this.viewPointer = this.geoIcon;
		this.geoIcon.setVisibility(View.GONE);
		this.brandIcon.setVisibility(View.VISIBLE);
		textview.setVisibility(View.GONE);
		clearAnim();
		show();
	}

	private void show() {
		if (this.isShow) {
			return;
		}
		this.isShow = true;
		startWhiteAnimation(viewPointer);
	}

	private TagInfo taginfo;

	public void setData(TagInfo mTagInfo) {
		this.taginfo = mTagInfo;
		setVisible();
	}

	public TagInfo getData() {
		return taginfo;
	}

	public void setTagViewListener(TagViewListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(View view) {
		if (null != listener) {
			listener.onTagViewClicked(view, taginfo);
		}
	}
}