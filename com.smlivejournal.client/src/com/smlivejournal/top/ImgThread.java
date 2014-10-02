package com.smlivejournal.top;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ImgThread extends Thread {
	private Handler mainHandler;
	private Bitmap bmp;
	private ArrayList<HashMap<String, Object>> images;
	private int imgIdx;

	public ImgThread(ArrayList<HashMap<String, Object>> images, int idx,Handler mainHandler) {
		super();
		this.images = images;
		this.imgIdx = idx;
		this.mainHandler=mainHandler;
	}

	public void run() {
		String imgUrl = "";
		HashMap<String, Object> map = new HashMap<String, Object>();
		map = images.get(imgIdx);
		imgUrl = (String) map.get("url");
		try {
			Pattern p = Pattern.compile("src=\".*?\"");
			Matcher m = p.matcher(imgUrl);
			if (m.find()) {
				imgUrl = m.group().substring(5);
				imgUrl = imgUrl.substring(0, imgUrl.length() - 1);
			}
			InputStream is = new java.net.URL(imgUrl).openStream();
			bmp = BitmapFactory.decodeStream(is);
			map.put("bmp", bmp);
			Message msg = new Message();
			Bundle b = new Bundle();
			b.putInt("rc", 0);
			b.putInt("idx",imgIdx);
			msg.setData(b);
			mainHandler.sendMessage(msg);
		} catch (Exception e) {
			Message msg = new Message();
			Bundle b = new Bundle();
			b.putInt("rc", -1);
			b.putInt("idx",imgIdx);
			msg.setData(b);
			mainHandler.sendMessage(msg);
			e.printStackTrace();
		}

	}

}
