package com.smlivejournal.userblog;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.smlivejournal.settings.AuthThread;
import com.smlivejournal.settings.Settings;
import com.smlivejournal.top.EditImageActivity;

public class PostEditor extends Activity implements View.OnClickListener {
	public static final int iEditPost = 1;
	public static final int iAddPost = 2;
	public static final int iAddComment = 3;
	public static final int iReply = 4;

	private String itemId;
	private String url;
	private Settings settings;
	private EditText titleView;
	private EditText editTextView;
	private String ljFormauth = "";
	private String ljAuthGuid = "";
	private Button btnSave;
	private Button btnBold;
	private Button btnItalic;
	private Button btnUnderline;
	private Button btnImage;
	private RelativeLayout progress;
	private RelativeLayout laTop;
	private String blogName = "";
	private static final String postImageUrl = "http://www.livejournal.com/up";
	private static final String addPostUrl = "http://www.livejournal.com/update.bml";
	private String postUrl;
	private int reason;
	private ProgressDialog pg;
	private HashMap<String, String> commentData;
	private Button btnTags;
	public static final int iGetTags = 1;
	private boolean tagsChanged = false;
	private Tag tagInfo;

	public PostEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.smlivejournal.client.R.layout.posteditor);
		laTop = (RelativeLayout) findViewById(com.smlivejournal.client.R.id.latop);
		LayoutInflater inflater = getLayoutInflater();
		progress = (RelativeLayout) inflater.inflate(
				com.smlivejournal.client.R.layout.progresslayout, null);
		laTop.addView(progress);
		progress.setVisibility(View.GONE);
		tagInfo = new Tag();

		// load
		btnTags = (Button) findViewById(com.smlivejournal.client.R.id.btntags);
		btnTags.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PostEditor.this.showTagsActivity();
			}
		});

		btnSave = (Button) findViewById(com.smlivejournal.client.R.id.btnSave);
		btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (reason == iAddComment) {
					Bundle b = new Bundle();
					Intent i = new Intent();
					String body = editTextView.getText().toString();
					b.putString("body", body);
					i.putExtras(b);
					setResult(reason, i);
					finish();
				} else {
					PostEditor.this.commit();
				}
			}
		});

		titleView = (EditText) findViewById(com.smlivejournal.client.R.id.titleEdit);
		editTextView = (EditText) findViewById(com.smlivejournal.client.R.id.textEdit);
		
		editTextView.setHint(com.smlivejournal.client.R.string.scomment);
		
		editTextView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		editTextView.setGravity(Gravity.TOP|Gravity.LEFT);
		editTextView.setHint(com.smlivejournal.client.R.string.scomment);
		editTextView.setSingleLine(false);
		editTextView.setLines(10);
		editTextView.setMaxLines(10);
	//	editTextView.setText(CommentFromDB);
	 	//editTextView.setFocusable(false);
		
		editTextView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
	            v.getParent().requestDisallowInterceptTouchEvent(true);
	            switch (event.getAction() & MotionEvent.ACTION_MASK){
	                case MotionEvent.ACTION_UP:
	                    v.getParent().requestDisallowInterceptTouchEvent(false);
	                break;
	            }
	            return false;
			}
		});
		
		
		//editTextView.setMovementMethod(new ScrollingMovementMethod());
		
		
		
		Bundle b = getIntent().getExtras();
		if (b != null) {
			reason = b.getInt("reason");
			settings = (Settings) b.getSerializable("settings");
			if (reason == iEditPost) {
				url = b.getString("url");
				loadPostData(url);
			} else if (reason == iAddPost) {
				url = addPostUrl;
				// loadPostData(url);
			} else if ((reason == iAddComment) || (reason == iReply)) {
				titleView.setVisibility(View.GONE);
				commentData = (HashMap<String, String>) b
						.getSerializable("commentData");
				btnTags.setVisibility(View.GONE);
			}
		}
		Typeface font = Typeface.createFromAsset(this.getAssets(),
				"fontawesome-webfont.ttf");
		btnBold = (Button) findViewById(com.smlivejournal.client.R.id.btnBold);
		btnBold.setTypeface(font);
		btnItalic = (Button) findViewById(com.smlivejournal.client.R.id.btnItalic);
		btnItalic.setTypeface(font);
		btnUnderline = (Button) findViewById(com.smlivejournal.client.R.id.btnUnderline);
		btnUnderline.setTypeface(font);
		btnImage = (Button) findViewById(com.smlivejournal.client.R.id.btnImage);
		btnImage.setTypeface(font);
		btnTags.setTypeface(font);
		btnImage.setOnClickListener(this);

		btnItalic.setOnClickListener(this);
		btnBold.setOnClickListener(this);
		btnUnderline.setOnClickListener(this);

		// setImageToBtn(btnBold);
	}

	private void loadPostData(String urlStr) {
		setProgressVisible(true);
		pg = new ProgressDialog(this);
		pg.setTitle("");
		pg.setCancelable(false);
		pg.setMessage(getResources().getString(
				com.smlivejournal.client.R.string.please_wait));
		pg.show();

		Handler mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (pg != null) {
					pg.dismiss();
					pg = null;
				}

				Bundle b = msg.getData();
				if (b != null) {
					String title = b.getString("title");
					String text = b.getString("body");
					ljFormauth = b.getString("auth");
					ljAuthGuid = b.getString("guid");
					if (title != null)
						titleView.setText(title);
					if (text != null)
						editTextView.setText(text);

					/*
					 * map.put("tagList",taglist); map.put("adultcontent",
					 * adultcontent); map.put("commentsOptions",
					 * commentsOptions); map.put("security",security);
					 */
					String tagsList = b.getString("tagList");
					String adultcontent = b.getString("adultcontent");
					String commentsOptions = b.getString("commentsOptions");
					String securityOptions = b.getString("security");
					tagInfo.setInfoByStr(tagsList, adultcontent,
							commentsOptions, securityOptions);
					setProgressVisible(false);
				}
			}
		};

		AuthThread thread = new AuthThread(mainHandler,
				AuthThread.iGetPostData, settings.getUserName(),
				settings.getPwd());
		thread.setContext(this);
		thread.setSettings(settings);

		this.url = urlStr;
		thread.setUrl(url);
		thread.start();
	}

	private void commit() {
		Handler mainHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// setProgressVisible(false);
				if (pg != null) {
					pg.dismiss();
				}

				Intent i = new Intent();
				Bundle data = new Bundle();
				data.putString("done", "1");
				data.putInt("reason", reason);
				i.putExtras(data);
				setResult(RESULT_OK, i);
				finish();
			};
		};
		setProgressVisible(true);
		AuthThread thread;

		if (reason == iReply) {
			thread = new AuthThread(mainHandler, AuthThread.iReply,
					settings.getUserName(), settings.getPwd());
		} else if (reason == iEditPost) {
			thread = new AuthThread(mainHandler, AuthThread.iEditPost,
					settings.getUserName(), settings.getPwd());
		} else
			thread = new AuthThread(mainHandler, AuthThread.iPost,
					settings.getUserName(), settings.getPwd());
		thread.setSettings(settings);
		String title = titleView.getText().toString();
		String body = editTextView.getText().toString();
		// ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
		// HashMap<String,String> map=new HashMap<String,String>();
		try {// settings.getUserName()
			if (reason == iReply) {
				commentData.put("body", body);
				thread.setCommentData(commentData);
			} else {
				thread.setTagInfo(tagInfo);
			}

			if (reason == iEditPost) {
				// map.put("subject", subject);
				// map.put("body", body);

				// setListParamsEdit(list, title, body);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		pg = new ProgressDialog(this);
		pg.setTitle("");
		pg.setCancelable(false);
		pg.setMessage(getResources().getString(
				com.smlivejournal.client.R.string.please_wait));
		pg.show();

		thread.setPostData(url, title, body);
		thread.start();

	}

	private void setListParamsEdit(ArrayList<NameValuePair> list, String title,
			String body) {
		try {
			list.add(new BasicNameValuePair("subject", title));// URLEncoder.encode(title,"utf-8")));
			list.add(new BasicNameValuePair("body", body));// URLEncoder.encode(body,"utf-8")));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent returnedIntent) {
		if (requestCode == iGetTags) {
			if (resultCode == RESULT_OK) {
				tagsChanged = true;
				Bundle b=returnedIntent.getExtras();
				if (b!=null){
					Tag info=(Tag)b.getSerializable("tag");
					if (info!=null)
					tagInfo=info;
				}
			}
		} else if (requestCode == EditImageActivity.iGetImage) {
			if (resultCode == RESULT_OK) {
				if (returnedIntent != null) {
					Bundle b = returnedIntent.getExtras();
					String fileName = b.getString("image");
					Handler mainHandler = new Handler() {
						@Override
						public void handleMessage(Message msg) {
							Bundle b = msg.getData();
							if (pg != null) {
								pg.dismiss();
							}
							if (b != null) {
								String imageUrl = b.getString("url");

								int i = editTextView.getSelectionStart();
								String s = editTextView.getText().toString();
								// String tag = "";

								s = s.substring(0, i) + imageUrl
										+ s.substring(i);// ,
															// s.length()-i);
								editTextView.setText(s);
								editTextView.setSelection(i);
							}

						}
					};
					AuthThread thread = new AuthThread(mainHandler,
							AuthThread.iPostImage);
					thread.setSettings(settings);
					pg = new ProgressDialog(this);
					pg.setCancelable(false);
					pg.setTitle("");
					pg.setMessage(getResources().getString(
							com.smlivejournal.client.R.string.please_wait));
					pg.show();

					thread.setPostFileName(fileName);
					thread.setLJAuthGuid(ljAuthGuid);
					thread.start();
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btnImage) {
			Intent i = new Intent(this, EditImageActivity.class);
			startActivityForResult(i, EditImageActivity.iGetImage);
			return;
		}
		int i = editTextView.getSelectionStart();
		String s = editTextView.getText().toString();
		String tag = "";
		if (v == btnItalic) {
			tag = "<i></i>";
		} else if (v == btnBold) {
			tag = "<b></b>";
		} else if (v == btnUnderline) {
			tag = "<u></u>";
		}
		s = s.substring(0, i) + tag + s.substring(i);// , s.length()-i);
		editTextView.setText(s);
		editTextView.setSelection(i);
	}

	private void setProgressVisible(boolean isVisible) {
		if (isVisible) {
			progress.setVisibility(View.VISIBLE);
		} else {
			progress.setVisibility(View.GONE);
		}
	}

	private void showTagsActivity() {
		Intent iTags = new Intent(this, PostTagsActivity.class);
		Bundle b = new Bundle();
		// Tag tag=new Tag();
		b.putSerializable("tag", tagInfo);
		iTags.putExtras(b);
		startActivityForResult(iTags, iGetTags);
	}

}
