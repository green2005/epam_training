package com.smlivejournal.top;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.SubMenu;
import com.smlivejournal.client.R;

public class EditImageActivity<AttributeSet> extends SherlockActivity {
	private SeekBar sb;
	private Button btnOk;
	private TextView tvq;
	private String sProgress;
	private static final int select_photo = 1;
	private static final int crop_photo = 2;
	private PhotoViewAttacher attacher;
	private ImageView imView;
	private File filePath = null;
	public static int iGetImage;
	private String fileName = null;
	private Menu m = null;
	Typeface font;
	private String tmpName = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(com.smlivejournal.client.R.style.Theme_Sherlock_Light_DarkActionBar);// R.style.Sherlock___Theme_DarkActionBar);
		// setTheme(com.smlivejournal.client.R.style.Theme_Sherlock_Light);
		super.onCreate(savedInstanceState);
		setContentView(com.smlivejournal.client.R.layout.imageeditactivity);
		sb = (SeekBar) findViewById(com.smlivejournal.client.R.id.seekBar);
		btnOk = (Button) findViewById(com.smlivejournal.client.R.id.btnOk);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i=new Intent();
				Bundle b=new Bundle();
				b.putString("image", fileName);
				i.putExtras(b);
				setResult(Activity.RESULT_OK, i);
				finish();
			}
		});
		tvq = (TextView) findViewById(com.smlivejournal.client.R.id.qtv);
		sProgress = getResources().getString(
				com.smlivejournal.client.R.string.quality);
		imView = (ImageView) findViewById(R.id.imageView);
		tvq.setText(sProgress + " " + 100);

		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				tvq.setText(sProgress + " " + progress);
				setImageCompression(progress);

			}
		});
		fillImagePath();
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, select_photo);
	}

	private void setImageCompression(int compression) {

		try {

			InputStream is = new FileInputStream(fileName);
			OutputStream fout = new FileOutputStream(tmpName);
			Bitmap bmp = BitmapFactory.decodeStream(is);
			bmp.compress(Bitmap.CompressFormat.JPEG, compression, fout);

			fout.flush();
			is.close();
			fout.close();

			is = new FileInputStream(tmpName);
			bmp = BitmapFactory.decodeStream(is);
			if (imView.getDrawable()!=null){
				try{
				((BitmapDrawable)imView.getDrawable()).getBitmap().recycle();
				}catch (Exception e){e.printStackTrace();}
			}
			imView.setImageBitmap(bmp);
			attacher = new PhotoViewAttacher(imView);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case select_photo:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = data.getData();
				InputStream imageStream;
				try {
					imageStream = getContentResolver().openInputStream(
							selectedImage);
					Bitmap bmp = BitmapFactory.decodeStream(imageStream);
					if (imView.getDrawable()!=null){
						try{
						((BitmapDrawable)imView.getDrawable()).getBitmap().recycle();
						}catch (Exception e){e.printStackTrace();}
					}

					imView.setImageBitmap(bmp);
					attacher = new PhotoViewAttacher(imView);

					fileName = filePath.getAbsolutePath() + "/"
							+ selectedImage.getLastPathSegment() + ".jpg";
					tmpName = fileName.replace(".jpg", "1.jpg");
					File fpath = new File(filePath.getAbsolutePath());
					fpath.mkdirs();
					File f = new File(fileName);
					FileOutputStream fout = new FileOutputStream(f);
					bmp.compress(Bitmap.CompressFormat.JPEG, 100, fout);
					fout.flush();
					fout.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			} else
			{finish();}
		case crop_photo: {
			if (data != null) {
				try {
					Bundle b = data.getExtras();
					Bitmap bmp = b.getParcelable("data");
					if (imView.getDrawable()!=null){
						try{
						((BitmapDrawable)imView.getDrawable()).getBitmap().recycle();
						}catch (Exception e){e.printStackTrace();}
					}
					imView.setImageBitmap(bmp);
					attacher=new PhotoViewAttacher(imView);
					File f = new File(fileName);
					FileOutputStream fStream = new FileOutputStream(f);
					bmp.compress(Bitmap.CompressFormat.JPEG, 100, fStream);
					fStream.flush();
					fStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
		}
		// onCreateOptionsMenu(m);

	}

	private void fillImagePath() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			filePath = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"smlivejournal");
		else
			filePath = this.getCacheDir();
		if (!filePath.exists())
			filePath.mkdirs();
	}

	public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu sub = menu.addSubMenu("More");
		sub.setIcon(com.smlivejournal.client.R.drawable.abs__ic_menu_moreoverflow_holo_dark);
		MenuItem crop = sub.add(0, R.style.Theme_Sherlock, 0,
				com.smlivejournal.client.R.string.scrop);
		MenuItem rotate = sub.add(0, R.style.Theme_Sherlock, 0,
				com.smlivejournal.client.R.string.srotate);
		crop.setIcon(com.smlivejournal.client.R.drawable.crop1);
		rotate.setIcon(com.smlivejournal.client.R.drawable.rotate1);

		// crop.setTitle(com.smlivejournal.client.R.string.icon_crop);
		// rotate.setTitle(com.smlivejournal.client.R.string.icon_repeat);
		// sub.add(0, R.style.Theme_Sherlock_Light, 0, "Light");
		// sub.add(0, R.style.Theme_Sherlock_Light_DarkActionBar, 0,
		// "Light (Dark Action Bar)");
		rotate.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Bitmap bmp = rotateImage(getBitmap(), 90);
				
				if (imView.getDrawable()!=null){
					try{
					((BitmapDrawable)imView.getDrawable()).getBitmap().recycle();
					}catch (Exception e){e.printStackTrace();}
				}
				
				imView.setImageBitmap(bmp);
				attacher = new PhotoViewAttacher(imView);
				bmp = getBitmap();

				File f = new File(fileName);
				try {
					FileOutputStream fstream = new FileOutputStream(f);
					bmp.compress(Bitmap.CompressFormat.JPEG, 100, fstream);
					fstream.flush();
					fstream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		});

		sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		crop.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				File file = new File(fileName);
				Uri u = Uri.fromFile(file);
				cropImage(u);
				return false;
			}
		});
		return true;
	}

	public void cropImage(Uri picUri) {
		// call the standard crop action intent
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		// indicate image type and Uri of image
		cropIntent.setDataAndType(picUri, "image/*");
		// set crop properties
		cropIntent.putExtra("crop", "true");
		// indicate aspect of desired crop
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		// indicate output X and Y
		cropIntent.putExtra("outputX", 256);
		cropIntent.putExtra("outputY", 256);
		// retrieve data on return
		cropIntent.putExtra("return-data", true);
		// start the activity - we handle returning in onActivityResult
		startActivityForResult(cropIntent, crop_photo);
	}

	private Bitmap getBitmap() {
		Drawable dr = imView.getDrawable();
		if (dr instanceof BitmapDrawable) {
			return ((BitmapDrawable) dr).getBitmap();
		} else {
			Bitmap bmp = Bitmap.createBitmap(dr.getIntrinsicWidth(),
					dr.getIntrinsicHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(bmp);
			dr.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
			dr.draw(canvas);
			return bmp;
		}

	}

	private Bitmap rotateImage(Bitmap src, float degree) {
		Matrix matrix = new Matrix();
		// setup rotation degree
		matrix.postRotate(degree);
		// return new bitmap rotated using matrix
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(),
				matrix, true);
	}

	private Intent createShareIntent() {

		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("image/*");
		Drawable d = imView.getDrawable();
		Bitmap bmp = Bitmap.createBitmap(d.getIntrinsicWidth(),
				d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		d.draw(canvas);

		String path = Environment.getExternalStorageDirectory().toString()
				+ "/smlivejournal";
		// File fPath = new File(path);
		// if (!fPath.exists())
		// fPath.mkdir();

		File f = new File(fileName);// ViewImageActivity.outFileName);
		// if (!f.exists())
		// f.mkdir();
		// FileOutputStream out;
		try {
			Uri uri = Uri.fromFile(f);
			shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
			// bmp.recycle();
			return shareIntent;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return shareIntent;

	}

}
