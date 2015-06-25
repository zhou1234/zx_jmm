package com.jifeng.mlsales;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jifeng.mlsales.jumeimiao.TabHostActivity;
import com.jifeng.tools.DataCleanManager;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class FBApplication extends Application {
	protected static FBApplication instance;
	private List<Activity> activities = new ArrayList<Activity>();

	public void addActivity(Activity activity) {
		activities.add(activity);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();

		for (Activity activity : activities) {
			activity.finish();
		}
		DataCleanManager.clearCache(getCacheDir(), this);
		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public void onCreate() {
		super.onCreate();

		instance = this;

		Thread.setDefaultUncaughtExceptionHandler(restartHandler);

		initImageLoader(getApplicationContext());
	}

	private UncaughtExceptionHandler restartHandler = new UncaughtExceptionHandler() {
		public void uncaughtException(Thread thread, Throwable ex) {
			Log.d("Throwable", ex.toString());
			restartApp();
		}
	};

	public void restartApp() {
		Intent intent = new Intent(instance, TabHostActivity.class); // //跳转到的页面，可以是登陆页或主页

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		instance.startActivity(intent);
		instance.onTerminate();
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
		config.memoryCacheSize(2 * 1024 * 1024); // 内存缓存的最大值
		config.diskCacheFileCount(300);
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		// config.writeDebugLogs(); // Remove for release app
		// Initialize ImageLoader with configuration.
		config.threadPoolSize(3);// 线程池内加载的数量
		ImageLoader.getInstance().init(config.build());

	}
}