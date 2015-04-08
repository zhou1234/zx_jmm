package com.jifeng.image;

import java.lang.ref.SoftReference;
import java.util.HashMap; 

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class AsyncImageLoader {

	// SoftReference是软引用，是为了更好的为了系统回收变量
	private static HashMap<String, SoftReference<Drawable>> imageCache;

	static {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	public AsyncImageLoader() {

	}

	// 获取带有ProgressBar 的图片
	public Drawable loadDrawable(final String imageUrl,
			final ImageView imageView, final ProgressBar pgBar,
			final ImagePgCallback imageCallback) {
		if (imageCache.containsKey(imageUrl)) {
			// 从缓存中获取
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageView,
						pgBar, imageUrl);
			}
		};
		// 建立新一个新的线程下载图片
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = null;
				try {
					drawable = ImageUtil.getDrawableFromUrl(imageUrl);
				} catch (Exception e) {
					e.printStackTrace();
				}
				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}

	public Drawable loadDrawable(final String imageUrl,
			final ImageView imageView, final ImageCallback imageCallback) {
		if (imageCache.containsKey(imageUrl)) {
			// 从缓存中获取
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageView,
						imageUrl);
			}
		};
		// 建立新一个新的线程下载图片
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = null;
				try {
					drawable = ImageUtil.getDrawableFromUrl(imageUrl);
				} catch (Exception e) {
					e.printStackTrace();
				}
				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}

	public Drawable loadDrawable(final String imageUrl,
			final ImageView imageView, final GalleryImageCallback imageCallback) {
		if (imageCache.containsKey(imageUrl)) {
			// 从缓存中获取
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageView,
						imageUrl);
			}
		};
		// 建立新一个新的线程下载图片
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = null;
				try {
					drawable = ImageUtil.getRoundDrawableFromUrl(imageUrl, 20);
				} catch (Exception e) {
					e.printStackTrace();
				}
				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}

	// 回调接口 带有progressBar的
	public interface ImagePgCallback {
		public void imageLoaded(Drawable imageDrawable, ImageView imageView,
				ProgressBar pgBar, String imageUrl);
	}

	// 回调接口普通图片的下载
	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, ImageView imageView,
				String imageUrl);
	}

	// 回调接口  Gallery
	public interface GalleryImageCallback {
		public void imageLoaded(Drawable imageDrawable, ImageView imageView,
				String imageUrl);
	}
}