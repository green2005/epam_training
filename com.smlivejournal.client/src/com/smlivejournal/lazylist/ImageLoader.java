package com.smlivejournal.lazylist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.net.Uri;
import android.os.Handler;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class ImageLoader {
	static final String smallImageSize = "600"; // "600";

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	Handler handler = new Handler();// handler to display images in UI thread
	private float screenWidth = 0;
	private float screenHeight = 0;

	private String imageStoreFileName = "";
	private boolean useFullSize = false;

	public ImageLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	public void setUseFullSize(boolean useFullSize) {
		this.useFullSize = useFullSize;
	}

	public ImageLoader(Context context, String imageFileName) {
		this(context);
		this.imageStoreFileName = imageFileName;
	}

	public String getCachePath() {
		return fileCache.getCachePath();
	}

	public void setScreenSize(float screenWidth, float screenHeight) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
	}

	private void setImageViewSize(ImageView imv, Bitmap bmp) {
		int imWidth = bmp.getWidth();
		int imHeight = bmp.getHeight();
		if ((screenWidth > 0)
				&& ((imWidth > screenWidth / 2) || (imHeight > screenHeight / 2))) {
			float w = screenWidth; // 9 * screenWidth / 10;
			float q = imWidth / (w);
			float h = imHeight / q;
			LayoutParams lp = imv.getLayoutParams();
			lp.width = (int) w;
			lp.height = (int) h;
			imv.setLayoutParams(lp);
		}
	}

	final int stub_id = com.smlivejournal.client.R.drawable.imagepngicon;

	public void DisplayImage(String url, ImageView imageView) {
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {
			saveBitmapToFile(bitmap);
			imageView.setImageBitmap(bitmap);
			setImageViewSize(imageView, bitmap);
		} else {
			queuePhoto(url, imageView);
			imageView.setImageResource(stub_id);
		}
	}

	private void saveBitmapToFile(Bitmap bitmap) {
		if (!imageStoreFileName.equalsIgnoreCase("")) {
			String path = fileCache.getCachePath();
			File f = new File(path, imageStoreFileName);
			try {
				FileOutputStream stream = new FileOutputStream(f);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				stream.flush();
				stream.close();
			} catch (Exception e) {
			}

		}

	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;

		// from web
		try {
			Bitmap bitmap = null;
			String rUrl = url;
			if (!useFullSize) {
				// http://ic.pics.livejournal.com/veliss/9036209/118793/118793_640.jpg
				if (rUrl.contains("http://ic.pics.livejournal.com/")) {
					rUrl = rUrl.replace("_original.", "_" + smallImageSize
							+ ".");
					rUrl = rUrl.replace("_1000.", "_" + smallImageSize + ".");
					rUrl = rUrl.replace("_900.", "_" + smallImageSize + ".");
				}
			}

			// rUrl=Uri.encode(rUrl);

			URL imageUrl = new URL(rUrl);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			conn.disconnect();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
			return null;
		}
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.
			float REQUIRED_SIZE = Math.min(screenHeight, screenWidth); // 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			if ((screenWidth == 0) || (screenHeight == 0)) {
				FileInputStream stream2 = new FileInputStream(f);
				Bitmap bitmap = BitmapFactory.decodeStream(stream2);
				stream2.close();
				return bitmap;
			} else {
				while ((true)) {
					if (width_tmp / 2 < REQUIRED_SIZE
							|| height_tmp / 2 < REQUIRED_SIZE)
						break;
					width_tmp /= 2;
					height_tmp /= 2;
					scale *= 2;
				}

				// decode with inSampleSize
				BitmapFactory.Options o2 = new BitmapFactory.Options();
				o2.inSampleSize = scale;
				FileInputStream stream2 = new FileInputStream(f);
				Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
				stream2.close();
				return bitmap;
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			try {
				if (imageViewReused(photoToLoad))
					return;
				Bitmap bmp = getBitmap(photoToLoad.url);
				memoryCache.put(photoToLoad.url, bmp);
				if (imageViewReused(photoToLoad))
					return;
				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
				handler.post(bd);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null) {
				photoToLoad.imageView.setImageBitmap(bitmap);
				try {
				//	photoToLoad.imageView.postInvalidate();
					Thread.currentThread().sleep(0);
					photoToLoad.imageView.postInvalidate();
				} catch (Exception e) {
					e.printStackTrace();
				}
				;
				saveBitmapToFile(bitmap);
				setImageViewSize(photoToLoad.imageView, bitmap);
			} else
				photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

}
