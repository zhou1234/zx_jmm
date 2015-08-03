package com.jifeng.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jifeng.image.AsyncImageLoader;
import com.jifeng.image.AsyncImageLoader.ImageCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.ECLAIR_MR1)
public class MyTools {
	private static AsyncImageLoader image = new AsyncImageLoader();

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static String getRandom() {
		Random random = new Random();

		int str = random.nextInt(9999) % (9999 - 1000 + 1) + 1000;

		return String.valueOf(str);
	}

	/**
	 * 检查是否存在SDCard
	 * 
	 * @return
	 */
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	// 判断文件是否存在
	public static boolean hasFile(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	// 判断手机格式是否正确
	public static boolean isMobileNO(String mobiles) {
		if (mobiles != null && !mobiles.equals("")) {
			Pattern p = Pattern
					.compile("^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$");// |(14[0-9])
			Matcher m = p.matcher(mobiles);

			return m.matches();
		} else {
			return false;
		}
	}

	// 判断有误网络连接
	public static boolean checkNetWorkStatus(Context context) {
		boolean netSataus = false;
		ConnectivityManager cwjManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		cwjManager.getActiveNetworkInfo();
		if (cwjManager.getActiveNetworkInfo() != null) {
			netSataus = cwjManager.getActiveNetworkInfo().isAvailable();
		}
		return netSataus;
	}

	// 设置webview的一些属性
	public static void webviewSetting(WebView mWebView) {
		// 设置支持JavaScript脚本
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// 只用于缓存/不适用缓存
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);// LOAD_CACHE_ONLY,LOAD_NO_CACHE
		// 设置可以访问文件
		webSettings.setAllowFileAccess(true);
		// 自动适应屏幕
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
	}

	// // 根据item设置listview的高度
	// public static void setListViewHeightBasedOnChildren(ListView listView,
	// ListAdapter listAdapter) {
	// if (listAdapter == null) {
	// // pre-condition
	// return;
	// }
	//
	// int totalHeight = 0;
	// for (int i = 0; i < listAdapter.getCount(); i++) {
	// View listItem = listAdapter.getView(i, null, listView);
	// listItem.measure(0, 0);
	// totalHeight += listItem.getMeasuredHeight();
	// }
	// ViewGroup.LayoutParams params = listView.getLayoutParams();
	// params.height = totalHeight
	// + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	// listView.setLayoutParams(params);
	// }

	/**
	 * 设置头像
	 ***/
	private static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	private static String getFileName(String IMAGE_FILE_NAME) {
		String saveDir = Environment.getExternalStorageDirectory()
				+ "/JuMeiMiao/pic";
		File dir = new File(saveDir);
		if (!dir.exists()) {
			dir.mkdirs(); // 创建文件夹
		}
		String fileName = saveDir + "/" + IMAGE_FILE_NAME;
		return fileName;
	}

	// 头像圆形
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		// 保证是方形，并且从中心画
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int w;
		int deltaX = 0;
		int deltaY = 0;
		if (width <= height) {
			w = width;
			deltaY = height - w;
		} else {
			w = height;
			deltaX = width - w;
		}
		final Rect rect = new Rect(deltaX, deltaY, w, w);
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		canvas.drawARGB(0, 0, 0, 0);
		// 圆形，所有只用一个

		int radius = (int) (Math.sqrt(w * w * 2.0d) / 2);
		canvas.drawRoundRect(rectF, radius, radius, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/*
	 * 时间计算(返回天数)
	 */

	public static String getTime() {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy/MM/dd   HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		return str;
	}

	@SuppressLint("SimpleDateFormat")
	public static int creayTime(String endtime, String currenttime) {// String
		// endtime,
		// String
		// currenttime
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		int between = 0;
		try {
			Date end = formatter.parse(endtime);
			Date curret = formatter.parse(currenttime);
			between = (int) ((end.getTime() - curret.getTime()) / (1000 * 60 * 60 * 24));
			return between;
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return between;
	}

	/**
	 * 获取Android 手机唯一标识符
	 */
	public static String getAndroidID(Context context) {
		TelephonyManager TelephonyMgr = (TelephonyManager) context
				.getSystemService(context.TELEPHONY_SERVICE);
		String szImei = TelephonyMgr.getDeviceId();
		return szImei;
	}

	/**
	 * 赋值
	 */
	public static void setText(TextView textView, String value, Context mContext) {
		textView.setText(value);
		// textView.setTextColor(mContext.getResources().getColor(R.color.white));//设置颜色
		// textView.setTextSize(15);//设置字体大小
	}

	

	// 绑定图片
	public static void downImg(String imgurl, ImageView mImageView) {
		Drawable cachedImage = image.loadDrawable(imgurl, mImageView,
				new ImageCallback() {
					@Override
					public void imageLoaded(Drawable imageDrawable,
							ImageView imageView, String imageUrl) {
						if (imageDrawable != null) {
							imageView.setImageDrawable(imageDrawable);
						}
					}
				});
		if (cachedImage != null) {
			mImageView.setImageDrawable(cachedImage);
		}
	}

	//
	// 动态设置控件高度(长)
	public static void getHight(RelativeLayout layout, int width, int height,
			Context mContext) {
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) layout
				.getLayoutParams(); // 取控件mLayout当前的布局参数

		if (width == 800 && height == 1280) {
			linearParams.height = MyTools.dip2px(mContext, 150);// 手机
			layout.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件
		} else if (width == 480 && height == 800) {
			linearParams.height = MyTools.dip2px(mContext, 120);
			layout.setLayoutParams(linearParams);
		} else if (width == 480 && height == 854) {
			linearParams.height = MyTools.dip2px(mContext, 126);
			layout.setLayoutParams(linearParams);
		} else if (width == 800 && height == 1232) {
			linearParams.height = MyTools.dip2px(mContext, 280);// 平板
			layout.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件
		} else if (width == 720 && height == 1280) {
			linearParams.height = MyTools.dip2px(mContext, 135);
			layout.setLayoutParams(linearParams);
		} else if (width == 640 && height == 960) {
			linearParams.height = MyTools.dip2px(mContext, 120);
			layout.setLayoutParams(linearParams);
		} else if (width == 1600 && height == 2560) {
			linearParams.height = MyTools.dip2px(mContext, 265);
			layout.setLayoutParams(linearParams);
		} else if (width == 1080 && height == 1776) {
			linearParams.height = MyTools.dip2px(mContext, 135);
			layout.setLayoutParams(linearParams);
		} else if (width == 1080 && height == 1812) {
			linearParams.height = MyTools.dip2px(mContext, 135);
			layout.setLayoutParams(linearParams);
		} else if (width == 1080 && height == 1920) {
			linearParams.height = MyTools.dip2px(mContext, 136);
			layout.setLayoutParams(linearParams);
		} else if (width == 1440 && height == 2392) {
			linearParams.height = MyTools.dip2px(mContext, 160);
			layout.setLayoutParams(linearParams);
		} else if (width == 768 && height == 1184) {
			linearParams.height = MyTools.dip2px(mContext, 145);
			layout.setLayoutParams(linearParams);
		} else if (width == 1440 && height == 2560) {
			linearParams.height = MyTools.dip2px(mContext, 175);
			layout.setLayoutParams(linearParams);
		} else if (width == 1200 && height == 1824) {
			linearParams.height = MyTools.dip2px(mContext, 175);
			layout.setLayoutParams(linearParams);
		}
	}

	

	

	/**
	 * 得到自定义的progressDialog
	 */
	// public static Dialog createLoadingDialog(Context context, String msg) {
	// LayoutInflater inflater = LayoutInflater.from(context);
	// View v = inflater.inflate(R.layout.all_dialog, null);// 得到加载view
	// RelativeLayout layout = (RelativeLayout) v
	// .findViewById(R.id.dialog_view);// 加载布局
	// // main.xml中的ImageView
	// ImageView spaceshipImage = (ImageView) v
	// .findViewById(R.id.loadingImageView);
	// TextView tipTextView = (TextView)
	// v.findViewById(R.id.id_tv_loadingmsg);// 提示文字
	// // 加载动画显示
	// // Animation hyperspaceJumpAnimation =
	// // AnimationUtils.loadAnimation(context, R.anim.loading_animation);
	// // 使用ImageView显示动画
	// // spaceshipImage.startAnimation(hyperspaceJumpAnimation);
	//
	// // 轮播图片显示
	// spaceshipImage.setBackgroundResource(R.anim.progress_round);
	// AnimationDrawable animationDrawable = (AnimationDrawable) spaceshipImage
	// .getBackground();
	// animationDrawable.start();
	//
	// tipTextView.setText(msg);// 设置加载信息
	//
	// Dialog loadingDialog = new Dialog(context, R.style.CustomDialog);//
	// 创建自定义样式dialog
	//
	// // loadingDialog.setCancelable(false);// 不可以用“返回键”取消
	// loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
	// LinearLayout.LayoutParams.WRAP_CONTENT,
	// LinearLayout.LayoutParams.WRAP_CONTENT));// 设置布局
	// return loadingDialog;
	// }

	// 动态设置控件高度（正）
	public static void setHight(RelativeLayout layout, int width, int height,
			Context mContext) {
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) layout
				.getLayoutParams(); // 取控件mLayout当前的布局参数
		if (width == 800 && height == 1280) {
			linearParams.height = MyTools.dip2px(mContext, 270);// 手机
			layout.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件
		} else if (width == 480 && height == 800) {
			linearParams.height = MyTools.dip2px(mContext, 235);
			layout.setLayoutParams(linearParams);
		} else if (width == 480 && height == 854) {
			linearParams.height = MyTools.dip2px(mContext, 240);
			layout.setLayoutParams(linearParams);
		} else if (width == 800 && height == 1232) {
			linearParams.height = MyTools.dip2px(mContext, 447);// 平板
			layout.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件
		} else if (width == 720 && height == 1280) {
			linearParams.height = MyTools.dip2px(mContext, 250);// 257
			layout.setLayoutParams(linearParams);
		} else if (width == 1080 && height == 1812) {
			linearParams.height = MyTools.dip2px(mContext, 250);
			layout.setLayoutParams(linearParams);
		} else if (width == 1080 && height == 1920) {
			linearParams.height = MyTools.dip2px(mContext, 250);
			layout.setLayoutParams(linearParams);
		} else if (width == 1440 && height == 2392) {
			linearParams.height = MyTools.dip2px(mContext, 276);
			layout.setLayoutParams(linearParams);
		} else if (width == 768 && height == 1184) {
			linearParams.height = MyTools.dip2px(mContext, 256);
			layout.setLayoutParams(linearParams);
		} else if (width == 1440 && height == 2560) {
			linearParams.height = MyTools.dip2px(mContext, 290);
			layout.setLayoutParams(linearParams);
		} else if (width == 1200 && height == 1824) {
			linearParams.height = MyTools.dip2px(mContext, 310);
			layout.setLayoutParams(linearParams);
		}
	}

	public static DisplayImageOptions createOptions(int imgId) {
		@SuppressWarnings("deprecation")
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(imgId).showImageForEmptyUri(imgId)
				.showImageOnFail(imgId).cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
				// .considerExifParams(true) //是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				/*
				 * 设置图片以如何的编码方式显示mageScaleType的选择值: EXACTLY :图像将完全按比例缩小的目标大小
				 * EXACTLY_STRETCHED:图片会缩放到目标大小完全 IN_SAMPLE_INT:图像将被二次采样的整数倍
				 * IN_SAMPLE_POWER_OF_2:图片将降低2倍，直到下一减少步骤，使图像更小的目标大小 NONE:图片不会调整
				 */
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
				.delayBeforeLoading(100)// int delayInMillis为你设置的下载前的延迟时间
				// 对图片进行处理
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				// .displayer(new
				// RoundedBitmapDisplayer(20))//不推荐用！！！！是否设置为圆角，弧度为多少
				// .displayer(new FadeInBitmapDisplayer(1*1000))// 渐显--设置图片渐显的时间
				.build();
		return options;
	}

	public static DisplayImageOptions createOptionsOther(int imgId) {
		@SuppressWarnings("deprecation")
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(imgId).showImageForEmptyUri(imgId)
				.showImageOnFail(imgId).cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
				// .considerExifParams(true) //是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				/*
				 * 设置图片以如何的编码方式显示mageScaleType的选择值: EXACTLY :图像将完全按比例缩小的目标大小
				 * EXACTLY_STRETCHED:图片会缩放到目标大小完全 IN_SAMPLE_INT:图像将被二次采样的整数倍
				 * IN_SAMPLE_POWER_OF_2:图片将降低2倍，直到下一减少步骤，使图像更小的目标大小 NONE:图片不会调整
				 */
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
				.delayBeforeLoading(30)// int delayInMillis为你设置的下载前的延迟时间
				// 对图片进行处理
				.resetViewBeforeLoading(false)// 设置图片在下载前是否重置，复位
				// .displayer(new
				// RoundedBitmapDisplayer(20))//不推荐用！！！！是否设置为圆角，弧度为多少
				// .displayer(new
				// FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间，可能会出现闪动
				.build();
		return options;
	}

	public static String getChannelCode(Context context) {
		String code = getMetaData(context, "CHANNEL");
		if (code != null) {
			return code;
		}
		return "";
	}

	private static String getMetaData(Context context, String key) {
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);

			Object value = ai.metaData.get(key);
			if (value != null) {
				return value.toString();
			}
		} catch (Exception e) {
		}
		return null;
	}

	public static void setWidthAndHeight(ImageView imageView, int width) {
		LayoutParams params = imageView.getLayoutParams();
		float w = (float) 640 / width;
		params.width = width;
		params.height = (int) (251 / w);
		imageView.setLayoutParams(params);
	}

	public static void setWidthAndHeight(RelativeLayout relativeLayout,
			int width) {
		LayoutParams params = relativeLayout.getLayoutParams();
		float w = (float) 640 / width;
		params.width = width;
		params.height = (int) (251 / w);
		relativeLayout.setLayoutParams(params);
	}

	public static void setImageViewWandH(RelativeLayout relativeLayout,
			ImageView imageView, int width) {
		LayoutParams params = imageView.getLayoutParams();
		params.width = width - 80;
		params.height = width - 80;
		imageView.setLayoutParams(params);

		// LayoutParams params1 = relativeLayout.getLayoutParams();
		// params1.width = width - 60;
		// params1.height = width - 60;
		// relativeLayout.setLayoutParams(params1);
	}

	// /**
	// * Layout动画
	// *
	// * @return
	// */
	// public static LayoutAnimationController getAnimationController() {
	// int duration = 300;
	// AnimationSet set = new AnimationSet(true);
	//
	// Animation animation = new AlphaAnimation(0.0f, 1.0f);
	// animation.setDuration(duration);
	// set.addAnimation(animation);
	//
	// animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
	// Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
	// -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
	// animation.setDuration(duration);
	// set.addAnimation(animation);
	//
	// LayoutAnimationController controller = new LayoutAnimationController(
	// set, 0.5f);
	// controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
	// return controller;
	// }
}
