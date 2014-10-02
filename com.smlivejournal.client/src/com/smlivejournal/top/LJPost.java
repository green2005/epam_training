package com.smlivejournal.top;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ad.labs.sdk.AdView;
import ad.labs.sdk.AdView.OnAdRequestListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.view.menu.MenuItemImpl;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.actionbarsherlock.view.SubMenu; 
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.android.gms.plus.PlusClient;
import com.perm.kate.api.Api;
import com.smlivejournal.fb.FbPost;
import com.smlivejournal.google.GooglePost;
import com.smlivejournal.client.MainActivity;
import com.smlivejournal.client.R;
import com.smlivejournal.client.R.id;
import com.smlivejournal.client.R.layout;
import com.smlivejournal.client.R.style;
import com.smlivejournal.quickaction.ActionItem;
import com.smlivejournal.quickaction.QuickAction;
import com.smlivejournal.quickaction.QuickAction.OnActionItemClickListener;
import com.smlivejournal.settings.AuthThread;
import com.smlivejournal.settings.Settings;
import com.smlivejournal.userblog.PostEditor;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.VKRequest.VKRequestListener;

public class LJPost extends SherlockActivity implements TabListener {
	private String postUrl;
	private String postId;
	private String nickName;
	private String subject;
	private static int vkLogin = -1;
	private static final String[] sMyScope = new String[] { VKScope.FRIENDS,
		VKScope.WALL, VKScope.PHOTOS, VKScope.NOHTTPS };
	 
	private ArrayList<HashMap<String, String>> post;
	private ArrayList<HashMap<String, String>> tmpPost;

	private ArrayList<HashMap<String, String>> comments;
	private ArrayList<HashMap<String, String>> tmpComments;

	private PostReader reader;
	private PostReader commentsReader;
	private float screenHeight = 0;
	private float screenWidth = 0;
	private ListView lvPost;
	private ListView lvComments;
	private RelativeLayout laProgress;
	private RelativeLayout laCommentProgress;
	private PostAdapter adapter;
	private CommentsAdapter commentsAdapter;
	private RelativeLayout ljComment;
	private RelativeLayout ljPost;
	private CommentsReader cmReader;
	private Handler commentsHandler;
	private Settings settings;
	private Context context;
	ProgressDialog pg;
	private AdView adView;
	private QuickAction mQuickAction;
	private RelativeLayout laTop;

	private FbPost fbpost;
	private GooglePost gpost;
	private PlusClient mPlusClient;
	private com.google.ads.AdView googleAdView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Sherlock___Theme_Light);
		super.onCreate(savedInstanceState);
		Bundle b = getIntent().getExtras();
		this.context = this;
		settings = (Settings) b.getSerializable("settings");

		if (settings != null) {
			if (settings.getNoScreenSwitchoff()) {
				getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			}
		}

		Drawable d = getResources().getDrawable(
				com.smlivejournal.client.R.drawable.bg);
		getSupportActionBar().setBackgroundDrawable(d);

		postUrl = b.getString("post_url");
		nickName = b.getString("journal");

		setContentView(com.smlivejournal.client.R.layout.ljpost);

		ljPost = (RelativeLayout) findViewById(com.smlivejournal.client.R.id.ljpost);
		ljComment = (RelativeLayout) findViewById(com.smlivejournal.client.R.id.ljcomments);
		ljComment.setVisibility(View.GONE);
		ljPost.setVisibility(View.VISIBLE);

		laTop = (RelativeLayout) findViewById(com.smlivejournal.client.R.id.laTop);

		laProgress = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.progresslayout, null);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		RelativeLayout ltMain = (RelativeLayout) findViewById(R.id.ltmain);
		ltMain.addView(laProgress, params);
		// ljPost.addView(laProgress,params);
		setProgressVisible(true);

		laCommentProgress = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.progresslayout, null);
		ljComment.addView(laCommentProgress);

		post = new ArrayList<HashMap<String, String>>();
		tmpPost = new ArrayList<HashMap<String, String>>();
		lvPost = (ListView) findViewById(R.id.lvPost);

		adapter = new PostAdapter(this, this, post, settings,postUrl);
		lvPost.setAdapter(adapter);

		comments = new ArrayList<HashMap<String, String>>();
		tmpComments = new ArrayList<HashMap<String, String>>();
		lvComments = (ListView) findViewById(R.id.lvComments);

		cmReader = new CommentsReader(postUrl, tmpComments, this, ljComment,
				laCommentProgress);
		cmReader.setSettings(settings);

		commentsAdapter = new CommentsAdapter(this, this, comments);
		commentsAdapter.setSettings(settings);
		lvComments.setOnScrollListener(cmReader); // (new
													// PostScrollListener(LJPost.this));
		lvComments.setAdapter(commentsAdapter);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab tab = getSupportActionBar().newTab();
		tab.setText(R.string.post);
		tab.setTabListener(this);
		getSupportActionBar().addTab(tab);
		Drawable bg = getResources().getDrawable(
				com.smlivejournal.client.R.drawable.bg);
		// tab.getCustomView().setBackgroundDrawable(bg);

		tab = getSupportActionBar().newTab();

		tab.setText(R.string.comments);
		tab.setTabListener(this);
		getSupportActionBar().addTab(tab);

		commentsHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.getData().getInt("done") == 1) {
					comments.addAll(tmpComments);
					commentsAdapter.notifyDataSetChanged();
					// setCommentPorgressVisible(false);
					cmReader.setCommentsLoadingdone();
					tmpComments.clear();
					if (pg != null) {
						pg.dismiss();
						pg = null;
					}

					// setProgressVisible(false);
				}

			};
		};

		/*
		 * <string name="facebook">Facebook</string> <string
		 * name="vk">VK</string> <string name="gplus">Google+</string>
		 */

		cmReader.setCommentsHandler(commentsHandler);

		ActionItem aFaceBook = new ActionItem();
		aFaceBook.setTitle(this
				.getString(com.smlivejournal.client.R.string.facebook));
		Drawable dr = getResources().getDrawable(
				com.smlivejournal.client.R.drawable.fb);
		// BitmapFactory.decodeResource(getResources(),
		// com.smlivejournal.client.R.drawable.fb)
		aFaceBook.setIcon(dr);

		ActionItem avk = new ActionItem();
		avk.setTitle(getString(com.smlivejournal.client.R.string.vk));
		dr = getResources().getDrawable(com.smlivejournal.client.R.drawable.vk);
		avk.setIcon(dr);

		ActionItem aGPlus = new ActionItem();
		aGPlus.setTitle(getResources().getString(
				com.smlivejournal.client.R.string.gplus));
		dr = getResources().getDrawable(com.smlivejournal.client.R.drawable.g);
		aGPlus.setIcon(dr);

		mQuickAction = new QuickAction(this);
		mQuickAction.addActionItem(aFaceBook);
		mQuickAction.addActionItem(avk);
		mQuickAction.addActionItem(aGPlus);
		mQuickAction
				.setOnActionItemClickListener(new OnActionItemClickListener() {

					@Override
					public void onItemClick(int pos) {
						switch (pos) {
						case 0: {
							if (fbpost == null) {
								fbpost = new FbPost(context, LJPost.this);
							}
							fbpost.postMessage(getPostText());
							break;
						}
						case 1: {
							

							String appId=LJPost.this.getString(com.smlivejournal.client.R.string.vkapiid);
							SdkListener listener = new SdkListener();
							VKSdk.initialize(listener, appId);
							VKSdk.authorize(sMyScope);
							
						///	tttt
							
							break;
						}
						case 2: {
							GooglePost gPost=new GooglePost(LJPost.this, getPostText());	
							gPost.post();
							
							break;
						}
						}
					}
				});
	
	  initAdView();
	}

	private String getPostText() {
		String postText = "";
		String title = "";
		String date = "";
		String userName = "";
		
		String pText="";
		
		// <a href="http://www.livejournal.com">test </href>
		if (post.size() > 0) {
			HashMap<String, String> map = post.get(0);
			title = map.get("title");
			date = map.get("pubdate");
			userName = map.get("userName");
			int i=0;
			while ((pText.length()<200)&&(i<post.size())){
				if (post.get(i).containsKey("text")){
				  pText=pText+post.get(i).get("text");
				}
				i++;
			}
		}
		
		String wrote=getResources().getString(com.smlivejournal.client.R.string.swrote);
		if (pText.length()>0){
			pText=pText.substring(0, 200)+"...";
		}
		
		postText =date+" " +userName + " "+wrote+" "  + " \n " +title  + " \n " + postUrl+"\n"+pText;
		return postText;
	}

	private void fillComments() {
		if (!cmReader.commentsRead()) {

			tmpComments.clear();
			cmReader.readComments(tmpComments, commentsHandler, this, postUrl);
		}
	}

	public void refreshComments() {
		comments.clear();
		commentsAdapter.notifyDataSetChanged();
		tmpComments.clear();
		cmReader.readComments(tmpComments, commentsHandler, this, postUrl);
		// setProgressVisible(true);
		pg = new ProgressDialog(this);
		pg.setTitle("");
		pg.setCancelable(false);
		pg.setMessage(getResources().getString(
				com.smlivejournal.client.R.string.please_wait));
		pg.show();
	}

	private void fillPost() {
		if (post.isEmpty()) {
			Handler mainHandler = new Handler() {
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					if (msg.getData().getInt("done") == 1) {
						post.clear();
						post.addAll(tmpPost);
						adapter.notifyDataSetChanged();
						setProgressVisible(false);
						tmpPost.clear();
					}
				}

			};
			tmpPost.clear();
			reader = new PostReader(tmpPost, mainHandler, LJPost.this, postUrl,
					nickName, MainTop.readPost, settings);
			reader.start();
		}
	}

	@Override
	protected void onDestroy() {
		lvPost.setAdapter(null);
		adapter.clearCache();
		VKUIHelper.onDestroy(this);
		
		super.onDestroy();
		// clearImages();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		int reason = -1;
		Bundle b = null;
		
		VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
		 

		if (data != null) {
			b = data.getExtras();
			if (b.containsKey("reason")) {
				reason = b.getInt("reason");
			}
		}
		if (fbpost != null)
			fbpost.onActivityResult(requestCode, resultCode, data);

		if (reason == PostEditor.iReply) {
			refreshComments();

		} else if (requestCode == PostEditor.iAddComment) {
			if (data != null) {
				HashMap<String, String> map = new HashMap<String, String>();
				b.putString("posturl", postUrl);
				if ((!b.containsKey("journal"))
						|| (b.getString("journal").equalsIgnoreCase(""))) {
					b.remove("journal");
					b.putString("journal", getPostJournal());
				}
				if ((!b.containsKey("ditemid"))
						|| (b.getString("ditemid").equalsIgnoreCase(""))) {
					b.remove("ditemid");
					b.putString("ditemid", getPostID());
				}

				if ((!b.containsKey("poster"))
						|| (b.getString("poster").equalsIgnoreCase(""))) {
					b.remove("poster");
					b.putString("poster", settings.getUserName());
				}

				b.putString("postid", getPostID());
				b.putString("userName", nickName);
				cmReader.addComment(b, this);
			}
		} else {

			if (data != null) {
				if (data.hasExtra("userName")) {
					String userName = data.getStringExtra("userName");
					loadUserLine(userName);
				}
			}
		}
	}

	private String getPostID() {
		String s = "";
		int i = postUrl.indexOf("/", 8);
		if (i > 0) {
			s = postUrl.substring(i + 1).replace(".html", "");
		}
		return s;
	}

	private String getPostJournal() {
		String s = "";
		int i = postUrl.indexOf("/", 2);
		// postUrl.substring(postUrl.indexOf("/", 1)+2).substring(0,
		// postUrl.indexOf(".")-1);
		if (i > 0) {
			s = postUrl.substring(i + 2);
			i = s.indexOf(".");
			s = s.substring(0, i);
		}

		return s;
	}

	public void loadUserLine(String userName) {
		Intent i = new Intent();
		i.putExtra("userName", userName);
		// getIntent().putExtra("userName",userName);
		setResult(Activity.RESULT_OK, i);
		finish();
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {

		if (tab.getPosition() == 0) {
			ljComment.setVisibility(View.GONE);
			ljPost.setVisibility(View.VISIBLE);
			fillPost();

		} else if (tab.getPosition() == 1) {
			ljComment.setVisibility(View.VISIBLE);
			ljPost.setVisibility(View.GONE);
			fillComments();
		}

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	private void setProgressVisible(Boolean visible) {
		if (laProgress != null) {
			if (visible) {
				laProgress.setVisibility(View.VISIBLE);
			} else {
				laProgress.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		View v = findViewById(com.smlivejournal.client.R.id.iShare);
		return true;
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// <item android:id="@+id/iAddToFav"
		// android:title="@string/add_to_favourities"
		// android:icon="@drawable/favorities"/>
		// <item android:id="@+id/iAddComment"
		// android:title="@string/add_comment"
		// android:icon="@drawable/addcomment"/>
		// <item android:id="@+id/iShare" android:title="@string/share"
		// android:icon="@drawable/share" android:visible="true" />

		getSupportMenuInflater().inflate(
				com.smlivejournal.client.R.menu.ljpostmenu, menu);
		MenuItem addComment = menu
				.findItem(com.smlivejournal.client.R.id.iAddComment);
		if (addComment != null) {
			addComment
					.setOnMenuItemClickListener(new OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							Intent i = new Intent(LJPost.this, PostEditor.class);
							Bundle b = new Bundle();
							b.putInt("reason", PostEditor.iAddComment);
							b.putString("posturl", postUrl);
							b.putSerializable("settings", settings);
							i.putExtras(b);
							startActivityForResult(i, PostEditor.iAddComment);
							return false;
						}
					});
		}

		MenuItem share = menu.findItem(com.smlivejournal.client.R.id.iShare); // itemMore.add(com.smlivejournal.client.R.string.share);
		// getActionBar(). (ImageButton)share
		// share.setIcon(com.smlivejournal.client.R.drawable.share);
		share.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				View v2 = laTop; // item.getActionView();
				// v2.findViewById(com.smlivejournal.client.R.id.iShare)
				mQuickAction.show(v2);// hhhhhhh
				mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
				return true;
			}
		});

		MenuItem addToFav = (MenuItem) menu
				.findItem(com.smlivejournal.client.R.id.iAddToFav);
		// addToFav.setIcon(com.smlivejournal.client.R.drawable.favorities);
		addToFav.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				addItemToFavourities();
				return false;
			}
		});

		MenuItem sub = (MenuItem) menu
				.findItem(com.smlivejournal.client.R.id.menu_item_more);
		if (sub != null) {
			sub.setIcon(com.smlivejournal.client.R.drawable.abs__ic_menu_moreoverflow_holo_dark);
		}

		/*
		 * MenuItem actionItem =
		 * menu.findItem(R.id.menu_item_share_action_provider_action_bar);
		 * ShareActionProvider actionProvider = (ShareActionProvider)
		 * actionItem.getActionProvider();
		 * actionProvider.setShareHistoryFileName
		 * (ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		 */

		/*
		 * SubMenu itemMore = menu.addSubMenu("item more");
		 * itemMore.setIcon(com.
		 * smlivejournal.client.R.drawable.abs__ic_menu_moreoverflow_holo_dark);
		 * 
		 * menu.a
		 * 
		 * MenuItem addComment = itemMore
		 * .add(com.smlivejournal.client.R.string.add_comment);
		 * addComment.setIcon(com.smlivejournal.client.R.drawable.addcomment);
		 * addComment.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		 * 
		 * @Override public boolean onMenuItemClick(MenuItem item) { Intent i =
		 * new Intent(LJPost.this, PostEditor.class); Bundle b = new Bundle();
		 * b.putInt("reason", PostEditor.iAddComment);
		 * b.putString("posturl",postUrl);
		 * b.putSerializable("settings",settings); i.putExtras(b);
		 * startActivityForResult(i, PostEditor.iAddComment); return false; }
		 * });
		 * 
		 * MenuItem addToFav
		 * =itemMore.add(com.smlivejournal.client.R.string.add_to_favourities);
		 * addToFav.setIcon(com.smlivejournal.client.R.drawable.favorities);
		 * addToFav.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		 * 
		 * @Override public boolean onMenuItemClick(MenuItem item) {
		 * addItemToFavourities(); return false; } });
		 * 
		 * //itemMore.add(Menu.NONE, itemId, order, titleRes) MenuItem share =
		 * itemMore.add(com.smlivejournal.client.R.string.share);
		 * //getActionBar().
		 * share.setIcon(com.smlivejournal.client.R.drawable.share);
		 * share.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		 * 
		 * @Override public boolean onMenuItemClick(MenuItem item) {
		 * 
		 * //findViewById() mQuickAction.show(item.getActionView());//hhhhhhh
		 * mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
		 * 
		 * return true; } });
		 */

		// itemMore.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	private void addItemToFavourities() {
		final ProgressDialog pg = new ProgressDialog(this);
		Handler mh = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (pg != null) {
					pg.dismiss();
					Toast.makeText(context, 
							LJPost.this.getResources().getString(com.smlivejournal.client.R.string.itemaded), 
							Toast.LENGTH_LONG)
							.show();
				}

			}

		};
		if (settings == null) {
			return;
		}

		if ((settings.getUserName() == null)
				|| (settings.getUserName().equalsIgnoreCase(""))) {
			return;
		}

		String dItemId = getPostID();
		String ljName = getPostJournal();
		AuthThread thread = new AuthThread(mh, AuthThread.iAddToFav);
		thread.setSettings(settings);
		thread.setPostData(dItemId, ljName);
		thread.setUrl(postUrl);
		String mess = getResources().getString(
				com.smlivejournal.client.R.string.please_wait);
		pg.setMessage(mess);// com.smlivejournal.client.R.string.please_wait);
		pg.setCancelable(false);
		pg.show();
		thread.setContext(this);
		thread.start();
		// pg=new ProgressDialog(this);
	}

	private void setCommentPorgressVisible(Boolean visible) {
		if (laCommentProgress != null) {
			if (visible) {
				laCommentProgress.setVisibility(View.VISIBLE);
			} else {
				laCommentProgress.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (adView != null) {
			final String blockId = getString(R.string.admob_id);
						adView.loadAd(blockId);
//			// adView.setAdShowDelay(15000);
		}
		
		VKUIHelper.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (adView != null)
			adView.pause();
	}

	private void initAdView() {
		// RelativeLayout.LayoutParams params = new
		// RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
		// RelativeLayout.LayoutParams.WRAP_CONTENT);
		// params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		// RelativeLayout layout = (RelativeLayout)findViewById(R.id.ltmain);
		// layout.addView(adView, params);

		// TextView tv=new TextView(this);
		// tv.setText("test");
		// layout.addView(tv,params);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);


		LinearLayout la = (LinearLayout) findViewById(R.id.mainAdmob);
		
		SharedPreferences sp = getSharedPreferences("adwords", MODE_PRIVATE);
		int ads = sp.getInt("adsljpost", 0);
	
		String lng=Locale.getDefault().getDisplayLanguage();
		if (lng.toLowerCase().contains("en")) 
			ads=0;
		
		if (ads==0){
		ads=1;
		googleAdView = new com.google.ads.AdView(this, AdSize.BANNER,
				getResources().getString(R.string.admob_publisher_id));
		la.addView(googleAdView,params);
		googleAdView.loadAd(new AdRequest());
			
		}else
		{	
		ads=0;
		adView = new AdView(this);
		la.addView(adView,params);

		adView.setAdShowDelay(20000);
		adView.setOnAdRequestListener(new OnAdRequestListener() {
			@Override
			public void onAdLoaded() {
			}

			@Override
			public void onAdClose() {
			}

			@Override
			public void onAdClick() {
			}

			@Override
			public void onAdRequestFailed() {
			}
		});
		
		}
		SharedPreferences.Editor ed=sp.edit();
		ed.putInt("adsljpost", ads);
		ed.apply();
	}
	
	
	
	private class SdkListener extends VKSdkListener {

		@Override
		public void onCaptchaError(VKError captchaError) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTokenExpired(VKAccessToken expiredToken) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAccessDenied(VKError authorizationError) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReceiveNewToken(VKAccessToken newToken) {
			// startTestActivity();
			// Toast.makeText(MainActivity.this, "onRecieveToken",
			// Toast.LENGTH_LONG).show();
			//
			VKRequest request = VKApi.wall().post(
					
					VKParameters.from(///VKApiConst.OWNER_ID, "133173479", //"-60479154",
							VKApiConst.MESSAGE, LJPost.this.getPostText()));

			request.executeWithListener(new VKRequestListener() {
				@Override
				public void onComplete(VKResponse response) {
				Toast.makeText(LJPost.this,	
						LJPost.this.getResources().getString(com.smlivejournal.client.R.string.itemaded),
							Toast.LENGTH_LONG).show();
			
				 }

				@Override
				public void onError(VKError error) {
			 	}

				@Override
				public void attemptFailed(VKRequest request, int attemptNumber,
						int totalAttempts) {
				 	}
			});
		}

		@Override
		public void onAcceptUserToken(VKAccessToken token) {
	 	}

	}
	

}
