package com.jifeng.mlsales.photo;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;

import java.io.*;

/**
 * Created by sreay on 14-10-24.
 */
public class BitmapUtil {
	/**
	 * 从exif信息获取图片旋转角度
	 * 
	 * @param path
	 * @return
	 */
	private static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 对图片进行压缩选择处理
	 * 
	 * @param picPath
	 * @return
	 */
	public static Bitmap compressRotateBitmap(String picPath) {
		Bitmap bitmap = null;
		int degree = readPictureDegree(picPath);
		if (degree == 90) {
			bitmap = featBitmapToSuitable(picPath, 500, 1.8f);
			bitmap = rotate(bitmap, 90);
		} else {
			bitmap = featBitmapToSuitable(picPath, 500, 1.8f);
		}
		return bitmap;
	}

	/**
	 * 转换bitmap为字节数组
	 * 
	 * @param bitmap
	 * @return
	 */
	public static byte[] bitmapToBytes(Bitmap bitmap) {
		final int size = bitmap.getWidth() * bitmap.getHeight() * 4;
		final ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		byte[] image = out.toByteArray();
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return image;

	}

	/**
	 * 获取合适尺寸的图片 图片的长或高中较大的值要 < suitableSize*factor
	 * 
	 * @param path
	 * @param suitableSize
	 * @return
	 */
	public static Bitmap featBitmapToSuitable(String path, int suitableSize,
			float factor) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			options.inJustDecodeBounds = false;
			options.inSampleSize = 1;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			int bitmap_w = options.outWidth;
			int bitmap_h = options.outHeight;
			int max_edge = bitmap_w > bitmap_h ? bitmap_w : bitmap_h;
			while (max_edge / (float) suitableSize > factor) {
				options.inSampleSize <<= 1;
				max_edge >>= 1;
			}
			return BitmapFactory.decodeFile(path, options);
		} catch (Exception e) {
		}
		return bitmap;
	}

	public static Bitmap featBitmap(String path, int width) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);
			options.inJustDecodeBounds = false;
			options.inSampleSize = 1;
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inPurgeable = true;
			options.inInputShareable = true;
			int bitmap_w = options.outWidth;
			while (bitmap_w / (float) width > 2) {
				options.inSampleSize <<= 1;
				bitmap_w >>= 1;
			}
			return BitmapFactory.decodeFile(path, options);
		} catch (Exception e) {
		}
		return bitmap;
	}

	public static Bitmap loadBitmap(String path, int maxSideLen) {
		if (null == path) {
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false;
		options.inSampleSize = Math.max(options.outWidth / maxSideLen,
				options.outHeight / maxSideLen);
		if (options.inSampleSize < 1) {
			options.inSampleSize = 1;
		}
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			if (bitmap != bitmap) {
				bitmap.recycle();
			}
			return bitmap;
		} catch (OutOfMemoryError e) {
			Debug.debug("higo", e.toString());
		}
		return null;
	}

	public static Bitmap loadBitmap(String path) {
		if (null == path) {
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 不对图进行压缩
		options.inSampleSize = 1;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(path, options);
			return bitmap;
		} catch (OutOfMemoryError e) {
			Debug.debug("higo", e.toString());
		}
		return null;
	}

	public static Bitmap loadFromAssets(Activity activity, String name,
			int sampleSize, Bitmap.Config config) {
		AssetManager asm = activity.getAssets();
		try {
			InputStream is = asm.open(name);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = sampleSize;
			options.inPreferredConfig = config;
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				is.close();
				return bitmap;
			} catch (OutOfMemoryError e) {
				Debug.debug("decode bitmap " + e.toString());
			}
		} catch (IOException e) {
			Debug.debug(e.toString());
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap decodeByteArrayUnthrow(byte[] data,
			BitmapFactory.Options opts) {
		try {
			return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		} catch (Throwable e) {
			Debug.debug(e.toString());
		}

		return null;
	}

	public static Bitmap rotateAndScale(Bitmap b, int degrees, float maxSideLen) {

		return rotateAndScale(b, degrees, maxSideLen, true);
	}

	// Rotates the bitmap by the specified degree.
	// If a new bitmap is created, the original bitmap is recycled.
	public static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
						b.getHeight(), m, true);
				if (null != b2 && b != b2) {
					b.recycle();
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// We have no memory to rotate. Return the original bitmap.
			}
		}
		return b;
	}

	public static Bitmap rotateNotRecycleOriginal(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees);
			try {
				return Bitmap.createBitmap(b, 0, 0, b.getWidth(),
						b.getHeight(), m, true);
			} catch (OutOfMemoryError ex) {
				// We have no memory to rotate. Return the original bitmap.
			}
		}
		return b;
	}

	public static Bitmap rotateAndScale(Bitmap b, int degrees,
			float maxSideLen, boolean recycle) {
		if (null == b || degrees == 0 && b.getWidth() <= maxSideLen + 10
				&& b.getHeight() <= maxSideLen + 10) {
			return b;
		}

		Matrix m = new Matrix();
		if (degrees != 0) {
			m.setRotate(degrees);
		}

		float scale = Math.min(maxSideLen / b.getWidth(),
				maxSideLen / b.getHeight());
		if (scale < 1) {
			m.postScale(scale, scale);
		}
		Debug.debug("degrees: " + degrees + ", scale: " + scale);

		try {
			Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
					b.getHeight(), m, true);
			if (null != b2 && b != b2) {
				if (recycle) {
					b.recycle();
				}
				b = b2;
			}
		} catch (OutOfMemoryError e) {
		}

		return b;
	}

	public static boolean saveBitmap2file(Bitmap bmp, File file,
			Bitmap.CompressFormat format, int quality) {
		if (file.isFile())
			file.delete();
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			Debug.debug(e.toString());
			return false;
		}

		return bmp.compress(format, quality, stream);
	}

	/**
	 * 生成缩略图
	 * 
	 * @param source
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap extractMiniThumb(Bitmap source, int width, int height) {
		return extractMiniThumb(source, width, height, true);
	}

	public static Bitmap extractMiniThumb(Bitmap source, int width, int height,
			boolean recycle) {
		if (source == null) {
			return null;
		}

		float scale;
		if (source.getWidth() < source.getHeight()) {
			scale = width / (float) source.getWidth();
		} else {
			scale = height / (float) source.getHeight();
		}
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		Bitmap miniThumbnail = transform(matrix, source, width, height, false);

		if (recycle && miniThumbnail != source) {
			source.recycle();
		}
		return miniThumbnail;
	}

	public static Bitmap transform(Matrix scaler, Bitmap source,
			int targetWidth, int targetHeight, boolean scaleUp) {
		int deltaX = source.getWidth() - targetWidth;
		int deltaY = source.getHeight() - targetHeight;
		if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
			/*
			 * In this case the bitmap is smaller, at least in one dimension,
			 * than the target. Transform it by placing as much of the image as
			 * possible into the target and leaving the top/bottom or left/right
			 * (or both) black.
			 */
			Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight,
					Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b2);

			int deltaXHalf = Math.max(0, deltaX / 2);
			int deltaYHalf = Math.max(0, deltaY / 2);
			Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf
					+ Math.min(targetWidth, source.getWidth()), deltaYHalf
					+ Math.min(targetHeight, source.getHeight()));
			int dstX = (targetWidth - src.width()) / 2;
			int dstY = (targetHeight - src.height()) / 2;
			Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight
					- dstY);
			c.drawBitmap(source, src, dst, null);
			return b2;
		}
		float bitmapWidthF = source.getWidth();
		float bitmapHeightF = source.getHeight();

		float bitmapAspect = bitmapWidthF / bitmapHeightF;
		float viewAspect = (float) targetWidth / targetHeight;

		if (bitmapAspect > viewAspect) {
			float scale = targetHeight / bitmapHeightF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		} else {
			float scale = targetWidth / bitmapWidthF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		}

		Bitmap b1;
		if (scaler != null) {
			// this is used for minithumb and crop, so we want to filter here.
			b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
					source.getHeight(), scaler, true);
		} else {
			b1 = source;
		}

		int dx1 = Math.max(0, b1.getWidth() - targetWidth);
		int dy1 = Math.max(0, b1.getHeight() - targetHeight);

		Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth,
				targetHeight);

		if (b1 != source) {
			b1.recycle();
		}

		return b2;
	}
}
