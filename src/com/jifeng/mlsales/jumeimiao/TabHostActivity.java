package com.jifeng.mlsales.jumeimiao;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import com.jifeng.mlsales.R;
import com.jifeng.myview.BadgeView;
import com.jifeng.myview.LoadingDialog;
import com.jifeng.tools.MyTools;
import com.jifeng.tools.TasckActivity;
import com.jifeng.url.AllStaticMessage;
import com.jifeng.url.HttpUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;

//@TargetApi(Build.VERSION_CODES.GINGERBREAD)
//@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class TabHostActivity extends TabActivity implements
		OnCheckedChangeListener {

	public TabHost mTabHost;
	private Intent mAIntent;
	private Intent mBIntent;
	private Intent mCIntent;
	private Intent mDIntent;
	private Intent mEIntent;
	// private Intent mIntent;
	RadioButton button;
	RadioButton button2;
	RadioButton button3;
	RadioButton button4;
	RadioButton button5;
	private TasckActivity tasckActivity;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectDiskReads().detectDiskWrites().detectNetwork() //
		// 这里或者用.detectAll()方法
		// .penaltyLog().build());
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		// .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
		// .build());
		setContentView(R.layout.maintabs);
		button = (RadioButton) findViewById(R.id.radio_button_xianshi);
		button2 = (RadioButton) findViewById(R.id.radio_button_fenlei);
		button3 = (RadioButton) findViewById(R.id.radio_button_meirifaxian);
		button4 = (RadioButton) findViewById(R.id.radio_button_shoppingcar);
		button5 = (RadioButton) findViewById(R.id.radio_button_zhanghu);

		init();
		mTabHost.setCurrentTab(0);
		button.setBackgroundResource(R.drawable.tab_dibu_select_bg_1);
		tasckActivity = new TasckActivity();
		tasckActivity.pushActivity(TabHostActivity.this);

		MobclickAgent.updateOnlineConfig(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (AllStaticMessage.Back_to_XianShiTm) {
			AllStaticMessage.Back_to_XianShiTm = false;
			nextView(mTabHost.getCurrentTab(), 0);
		} else if (AllStaticMessage.Back_to_ShoppingCar) {
			AllStaticMessage.Back_to_ShoppingCar = false;
			nextView(mTabHost.getCurrentTab(), 3);
		} else if (AllStaticMessage.Back_to_Classion) {
			AllStaticMessage.Back_to_Classion = false;
			nextView(mTabHost.getCurrentTab(), 1);
		} else if (AllStaticMessage.Back_to_Find) {
			AllStaticMessage.Back_to_Find = false;
			nextView(mTabHost.getCurrentTab(), 2);
		} else if (AllStaticMessage.Back_to_ZhangHu) {
			AllStaticMessage.Back_to_ZhangHu = false;
			nextView(mTabHost.getCurrentTab(), 4);
		}
	}

	private void nextView(int before, int after) {
		switch (before) {
		case 0:
			button.setChecked(true);
			button.setTextColor(getResources().getColor(R.color.tab_select));
			Drawable drawable = this.getResources().getDrawable(
					R.drawable.tab_first_2);
			button.setCompoundDrawablesWithIntrinsicBounds(null, drawable,
					null, null);

			break;
		case 1:
			button2.setChecked(true);
			button2.setTextColor(getResources().getColor(R.color.tab_select));
			Drawable drawable1 = this.getResources().getDrawable(
					R.drawable.tab_second_2);
			button2.setCompoundDrawablesWithIntrinsicBounds(null, drawable1,
					null, null);

			break;
		case 2:
			button3.setChecked(true);
			button3.setTextColor(getResources().getColor(R.color.tab_select));
			Drawable drawable2 = this.getResources().getDrawable(
					R.drawable.tab_thread_2);
			button3.setCompoundDrawablesWithIntrinsicBounds(null, drawable2,
					null, null);

			break;
		case 3:
			button4.setChecked(true);
			button4.setTextColor(getResources().getColor(R.color.tab_select));
			Drawable drawable3 = null;
			if (AllStaticMessage.ShoppingCar) {
				drawable3 = this.getResources().getDrawable(
						R.drawable.tab_forth_2_2);
			} else {
				drawable3 = this.getResources().getDrawable(
						R.drawable.tab_forth_2);
			}
			button4.setCompoundDrawablesWithIntrinsicBounds(null, drawable3,
					null, null);
			// if (AllStaticMessage.ShoppingCar) {
			// drawable3 = this.getResources().getDrawable(
			// R.drawable.tab_forth_2_2);
			// } else {
			// }
			break;
		case 4:
			button5.setChecked(true);
			button5.setTextColor(getResources().getColor(R.color.tab_select));
			Drawable drawable4 = this.getResources().getDrawable(
					R.drawable.tab_fifth_2);
			button5.setCompoundDrawablesWithIntrinsicBounds(null, drawable4,
					null, null);
			break;
		default:
			break;
		}
		mTabHost.setCurrentTab(after);
		setView(after);
	}

	private void setView(int num) {
		switch (num) {
		case 0:
			button.setChecked(true);
			button.setBackgroundResource(R.drawable.tab_dibu_select_bg_2);
			break;
		case 1:
			button2.setChecked(true);
			break;
		case 2:
			button3.setChecked(true);
			break;
		case 3:
			button4.setChecked(true);
			break;
		case 4:
			button5.setChecked(true);
			break;
		default:
			break;   
		}
	}

	private void init() {
		this.mAIntent = new Intent(this, FirstActivity.class);
		this.mBIntent = new Intent(this, ClassionActivity.class);

		this.mCIntent = new Intent(this, FindActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		this.mDIntent = new Intent(this, ShoppingCarActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 清除顶层,实现页面刷新
		this.mDIntent.putExtra("showflag", "no");
		this.mEIntent = new Intent(this, MySelfActivity.class);// 清除顶层,实现页面刷新

		button.setOnCheckedChangeListener(this);
		button2.setOnCheckedChangeListener(this);
		button3.setOnCheckedChangeListener(this);
		button4.setOnCheckedChangeListener(this);
		button5.setOnCheckedChangeListener(this);
		setupIntent();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		if (isChecked) {
			int currentTab = this.mTabHost.getCurrentTab();
			// 恢复默认
			reBack();
			switch (buttonView.getId()) {
			case R.id.radio_button_xianshi:
				button.setBackgroundResource(R.drawable.tab_dibu_select_bg_1);
				button.setTextColor(getResources().getColor(R.color.tab_select));
				Drawable drawable = this.getResources().getDrawable(
						R.drawable.tab_first_2);
				button.setCompoundDrawablesWithIntrinsicBounds(null, drawable,
						null, null);
				setCurrentTabWithAnim(currentTab, 0, "A_TAB");
				// this.mTabHost.setCurrentTabByTag("A_TAB");
				break;
			case R.id.radio_button_fenlei:
				button2.setTextColor(getResources()
						.getColor(R.color.tab_select));
				Drawable drawable2_1 = this.getResources().getDrawable(
						R.drawable.tab_second_2);
				button2.setCompoundDrawablesWithIntrinsicBounds(null,
						drawable2_1, null, null);
				setCurrentTabWithAnim(currentTab, 1, "B_TAB");
				// this.mTabHost.setCurrentTabByTag("B_TAB");
				break;
			case R.id.radio_button_meirifaxian:
				button3.setTextColor(getResources()
						.getColor(R.color.tab_select));
				Drawable drawable3_11 = this.getResources().getDrawable(
						R.drawable.tab_thread_2);
				button3.setCompoundDrawablesWithIntrinsicBounds(null,
						drawable3_11, null, null);
				setCurrentTabWithAnim(currentTab, 2, "C_TAB");
				// this.mTabHost.setCurrentTabByTag("C_TAB");
				break;
			case R.id.radio_button_shoppingcar:
				button4.setTextColor(getResources()
						.getColor(R.color.tab_select));
				Drawable drawable4_111 = null;
				if (AllStaticMessage.ShoppingCar) {
					drawable4_111 = this.getResources().getDrawable(
							R.drawable.tab_forth_2_2);
				} else {
					drawable4_111 = this.getResources().getDrawable(
							R.drawable.tab_forth_2);

				}
				button4.setCompoundDrawablesWithIntrinsicBounds(null,
						drawable4_111, null, null);
				setCurrentTabWithAnim(currentTab, 3, "D_TAB");
				// this.mTabHost.setCurrentTabByTag("D_TAB");
				break;
			case R.id.radio_button_zhanghu:
				button5.setTextColor(getResources()
						.getColor(R.color.tab_select));
				Drawable drawable5_1111 = this.getResources().getDrawable(
						R.drawable.tab_fifth_2);
				button5.setCompoundDrawablesWithIntrinsicBounds(null,
						drawable5_1111, null, null);
				setCurrentTabWithAnim(currentTab, 4, "MORE_TAB");
				// this.mTabHost.setCurrentTabByTag("MORE_TAB");
				break;
			}
		}
	}

	private void reBack() {
		button.setBackgroundResource(R.drawable.tab_dibu_select_bg_2);
		button.setTextColor(getResources().getColor(R.color.text_color));
		Drawable drawable_1 = this.getResources().getDrawable(
				R.drawable.tab_first_1);
		button.setCompoundDrawablesWithIntrinsicBounds(null, drawable_1, null,
				null);

		button2.setTextColor(getResources().getColor(R.color.text_color));
		Drawable drawable2 = this.getResources().getDrawable(
				R.drawable.tab_second_1);
		button2.setCompoundDrawablesWithIntrinsicBounds(null, drawable2, null,
				null);

		button3.setTextColor(getResources().getColor(R.color.text_color));
		Drawable drawable3 = this.getResources().getDrawable(
				R.drawable.tab_thread_1);
		button3.setCompoundDrawablesWithIntrinsicBounds(null, drawable3, null,
				null);

		button4.setTextColor(getResources().getColor(R.color.text_color));
		Drawable drawable4 = null;
		if (AllStaticMessage.ShoppingCar) {
			drawable4 = this.getResources().getDrawable(
					R.drawable.tab_forth_1_1);
		} else {
			drawable4 = this.getResources().getDrawable(R.drawable.tab_forth_1);
		}
		button4.setCompoundDrawablesWithIntrinsicBounds(null, drawable4, null,
				null);

		button5.setTextColor(getResources().getColor(R.color.text_color));
		Drawable drawable5 = this.getResources().getDrawable(
				R.drawable.tab_fifth_1);
		button5.setCompoundDrawablesWithIntrinsicBounds(null, drawable5, null,
				null);
	}

	private void setupIntent() {
		this.mTabHost = getTabHost();
		TabHost localTabHost = this.mTabHost;

		localTabHost.addTab(buildTabSpec("A_TAB", "限时特卖",
				R.drawable.tab_fifth_2, this.mAIntent));

		localTabHost.addTab(buildTabSpec("B_TAB", "分类",
				R.drawable.tab_second_1, this.mBIntent));

		localTabHost.addTab(buildTabSpec("C_TAB", "每日发现",
				R.drawable.tab_thread_1, this.mCIntent));

		localTabHost.addTab(buildTabSpec("D_TAB", "购物车",
				R.drawable.tab_forth_1, this.mDIntent));

		localTabHost.addTab(buildTabSpec("MORE_TAB", "我",
				R.drawable.tab_fifth_1, this.mEIntent));
	}

	private TabHost.TabSpec buildTabSpec(String tag, String resLabel,
			int resIcon, final Intent content) {
		return this.mTabHost.newTabSpec(tag)
				.setIndicator(resLabel, getResources().getDrawable(resIcon))
				.setContent(content);
	}

	// 标签添加动画
	private void setCurrentTabWithAnim(int now, int next, String tag) {
		// 这个方法是关键，用来判断动画滑动的方向
		if (now > next) {
			mTabHost.getCurrentView().startAnimation(
					AnimationUtils.loadAnimation(this, R.anim.out_leftright));
			mTabHost.setCurrentTabByTag(tag);
			mTabHost.getCurrentView().startAnimation(
					AnimationUtils.loadAnimation(this, R.anim.in_leftright));
		} else {
			mTabHost.getCurrentView().startAnimation(
					AnimationUtils.loadAnimation(this, R.anim.out_rightleft));
			mTabHost.setCurrentTabByTag(tag);
			mTabHost.getCurrentView().startAnimation(
					AnimationUtils.loadAnimation(this, R.anim.in_rightleft));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}
