package com.jifeng.mlsales;

import java.lang.Thread.UncaughtExceptionHandler;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jifeng.mlsales.jumeimiao.TabHostActivity;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class FBApplication extends Application {
	protected static FBApplication instance;

	@Override
	public void onCreate() {

		super.onCreate();

		instance = this;

		Thread.setDefaultUncaughtExceptionHandler(restartHandler);

		initImageLoader(getApplicationContext());
	}

	private UncaughtExceptionHandler restartHandler = new UncaughtExceptionHandler() {
		public void uncaughtException(Thread thread, Throwable ex) {
			restartApp();
		}
	};

	public void restartApp() {
		Intent intent = new Intent(instance, TabHostActivity.class); // //跳转到的页面，可以是登陆页或主页

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		instance.startActivity(intent);

		android.os.Process.killProcess(android.os.Process.myPid());

	}

	@SuppressLint("NewApi")
	public static void initImageLoader(Context context) {
		// File cacheDir = StorageUtils.getCacheDirectory(context);
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
				context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		// config.denyCacheImageMultipleSizesInMemory();
		// config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheFileNameGenerator(new HashCodeFileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.diskCacheFileCount(300);
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		// config.writeDebugLogs(); // Remove for release app
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());

	}
}