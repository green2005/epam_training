package com.smlivejournal.client;

import android.app.Activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import uk.co.senab.photoview.PhotoViewAttacher;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.SubMenu;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.smlivejournal.lazylist.ImageLoader;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
//import android.util.Config;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ViewImageActivity extends SherlockActivity {
	private ImageView imView;
	private ImageLoader imLoader;
	private float screenWidth;
	private float screenHeight;
	private String imgUrl;
	private PhotoViewAttacher attacher;
	private String cachePath="";
	public static final String outFileName="outexport.jpg";
	

	private static final String SHARED_FILE_NAME = "shared.png";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewimageactivity);
		imView = (ImageView) findViewById(R.id.imageView);
		imLoader = new ImageLoader(this,outFileName);
		imLoader.setUseFullSize(true);
		cachePath = imLoader.getCachePath();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			imgUrl = b.getString("imgUrl");
			screenWidth = b.getFloat("screenWidth");
			screenHeight = b.getFloat("screenHeight");
			// imLoader.setScreenSize(screenWidth, screenHeight);
			imLoader.DisplayImage(imgUrl, imView);
			attacher = new PhotoViewAttacher(imView);
		}
		
	}
	
	@Override
	public void onDestroy(){
		if (!cachePath.equalsIgnoreCase("")){
			File f=new File(cachePath,outFileName);
			if (f.exists()){
				f.delete();
			}
		}
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.share_action_provider, menu);
		// Set file with share history to the provider and set the share intent.
		MenuItem actionItem = menu
				.findItem(R.id.menu_item_share_action_provider_action_bar);
		ShareActionProvider actionProvider = (ShareActionProvider) actionItem
				.getActionProvider();
		actionProvider
				.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		// Note that you can set/change the intent any time,
		// say when the user has selected an image.
		actionProvider.setShareIntent(createShareIntent());

		// XXX: For now, ShareActionProviders must be displayed on the action
		// bar
		// Set file with share history to the provider and set the share intent.
		// MenuItem overflowItem =
		// menu.findItem(R.id.menu_item_share_action_provider_overflow);
		// ShareActionProvider overflowProvider =
		// (ShareActionProvider) overflowItem.getActionProvider();
		// overflowProvider.setShareHistoryFileName(
		// ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		// Note that you can set/change the intent any time,
		// say when the user has selected an image.
		// overflowProvider.setShareIntent(createShareIntent());

		return true;
	}

	private Intent createShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("image/*");
		Drawable d=imView.getDrawable();
	    Bitmap bmp = Bitmap.createBitmap(d.getIntrinsicWidth(),d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(bmp);
	    d.draw(canvas);
	    
		String path =  android.os.Environment.getExternalStorageDirectory()+
		        "/Android/data/" + getApplicationInfo().packageName + "/cache/files/";
		File fPath=new File(path);
		if (!fPath.exists())
		fPath.mkdirs();
		
		File f = new File(path, outFileName);
		//if (!f.exists())
		//f.mkdir();
		//FileOutputStream out;
		try {
			Uri uri = Uri.fromFile(f);
			shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
			//bmp.recycle();
			return shareIntent;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shareIntent;
		
	}

	private void copyPrivateRawResourceToPubliclyAccessibleFile() {
		InputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			inputStream = getResources().openRawResource(R.raw.robot);
			outputStream = openFileOutput(SHARED_FILE_NAME,
					Context.MODE_WORLD_READABLE | Context.MODE_APPEND);
			byte[] buffer = new byte[1024];
			int length = 0;
			try {
				while ((length = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, length);
				}
			} catch (IOException ioe) {

			}
		} catch (FileNotFoundException fnfe) {

		} finally {
			try {
				inputStream.close();
			} catch (IOException ioe) {

			}
			try {
				outputStream.close();
			} catch (IOException ioe) {

			}
		}
	}

}
