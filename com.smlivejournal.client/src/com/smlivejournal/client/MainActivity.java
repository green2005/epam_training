package com.smlivejournal.client;

import java.util.Locale;

import ad.labs.sdk.AdView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.sidenavigation.ISideNavigationCallback;
import com.devspark.sidenavigation.SideNavigationView;
import com.devspark.sidenavigation.SideNavigationView.Mode;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.smlivejournal.friends.FriendLine;
import com.smlivejournal.messages.MessagesLine;
import com.smlivejournal.settings.Settings;
import com.smlivejournal.top.MainTop;
import com.smlivejournal.top.MemoriesLine;
import com.smlivejournal.top.MyJournalLine;
import com.smlivejournal.top.UserLine;
import com.smlivejournal.userblog.PostEditor;

public class MainActivity extends SherlockFragmentActivity {
	private SideNavigationView sView;
	private MainTop tops;
	private FriendLine friendLine;
	private MyJournalLine mjLine;
	private MemoriesLine memLine;
	private UserLine userLine;
	private UserLine uLine;
	private MessagesLine msgLine;
	private RelativeLayout topla;
	// private RelativeLayout viewsLa;

	private LinearLayout laTop;
	private LinearLayout laBottom;
	private RelativeLayout progressView;
	private RelativeLayout loadMoreView;
	private com.smlivejournal.settings.Settings settings;
	private View mainListView = null;

	public static final int iSetFriendLine = 0;
	public static final int iMakePost = 1;
	public static final int iSettingsEdit = 2;
	private AdView adView;
	private com.google.ads.AdView googleAdView;

	private boolean needSetUserLine = false;
	private String userLineName = "";
	private String userLineTag = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light);// (R.style.Sherlock___Theme_Light);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initAdView();

		Drawable d = getResources().getDrawable(
				com.smlivejournal.client.R.drawable.bg);
		getSupportActionBar().setBackgroundDrawable(d);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(
				com.smlivejournal.client.R.drawable.icomore);

		CookieSyncManager.createInstance(this);
		topla = (RelativeLayout) findViewById(R.id.laMain);
		// viewsLa = (RelativeLayout) findViewById(R.id.laviews);

		laTop = (LinearLayout) findViewById(R.id.latop);
		laBottom = (LinearLayout) findViewById(com.smlivejournal.client.R.id.laBottom);

		sView = (SideNavigationView) findViewById(R.id.sview);
		sView.setMenuItems(R.menu.mainmenu);
		sView.setMode(Mode.LEFT);
		// sView.on
		// sView.
		// sView.setBackgroundColor(getResources().getColor(
		// android.R.color.holo_blue_dark));

		progressView = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.progresslayout, null);
		laTop.addView(progressView);
		progressView.setVisibility(View.GONE);

		loadMoreView = (RelativeLayout) getLayoutInflater().inflate(
				R.layout.progresslayout, null);
		laBottom.addView(loadMoreView);
		loadMoreView.setVisibility(View.GONE);

		settings = new Settings(this, this);

		if (settings.getNoScreenSwitchoff()) {
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}

		sView.setMenuClickCallback(new ISideNavigationCallback() {

			@Override
			public void onSideNavigationItemClick(int itemId) {

				if ((itemId != R.id.topMenuItem)
						&& (itemId != R.id.friendLineMenuItem)) {
					getSupportActionBar().setNavigationMode(
							ActionBar.NAVIGATION_MODE_STANDARD);
				}

				switch (itemId) {
				case R.id.topMenuItem: {
 					tops = new MainTop(MainActivity.this, getLayoutInflater(),
							progressView, settings);
					tops.setLoadMoreView(loadMoreView);
					setFragment(tops);
					break;
				}
				case R.id.myJournalMenuItem: {
					if (checkIsAuthOK()){
					mjLine = new MyJournalLine(MainActivity.this,
							getLayoutInflater(), progressView, settings);
					setFragment(mjLine);
					};
					break;
				}
				case R.id.memories: {
					if (checkIsAuthOK()){
					// if (memLine == null) {
					memLine = new MemoriesLine(MainActivity.this,
							getLayoutInflater(), progressView, settings);
					setFragment(memLine);
					}
					break;
				}

				case R.id.friendLineMenuItem: {

					if (checkIsAuthOK()) {
						friendLine = new FriendLine(MainActivity.this,
								getLayoutInflater(), progressView, settings);
					
						friendLine.setLoadMoreView(loadMoreView);
						friendLine.showMenu();
						friendLine.restoreSettings();
						setFragment(friendLine);
					}
					break;
				}
				case R.id.settingsMenuItem: {
					settings.showEditor(MainActivity.this, MainActivity.this);
					break;
				}
			/*	case R.id.messagesMenuItem:{
					if (checkIsAuthOK()){
					
					msgLine=new MessagesLine(MainActivity.this,getLayoutInflater(),MainActivity.this,settings);
					setFragment(msgLine);}
					break;
				}
			*/	
				case R.id.addPostMenuItem: {
					Intent i = new Intent(MainActivity.this, PostEditor.class);
					Bundle b = new Bundle();
					b.putInt("reason", PostEditor.iAddPost);
					b.putSerializable("settings", settings);
					i.putExtras(b);
					startActivity(i);

					break;
				}

				}
				sView.bringToFront();
			}
		});
		getSupportActionBar().setTitle(
				com.smlivejournal.client.R.string.app_name);
		sView.toggleMenu();

	}

	private boolean checkIsAuthOK() {
		if (settings.getUserName().equalsIgnoreCase("")
				|| settings.getPwd().equalsIgnoreCase("")) {
			Toast.makeText(
					MainActivity.this,
					MainActivity.this.getResources().getString(
							com.smlivejournal.client.R.string.authneeded),
					Toast.LENGTH_LONG).show();
			return false;
		} else {
			return true;
		}

	}

	@Override
	public void onSaveInstanceState(Bundle b) {

	}

	@Override
	public void onRestoreInstanceState(Bundle b) {
		// Toast.makeText(this, "onRestore",Toast.LENGTH_LONG).show();
	}

	private void setFragment(Fragment fr) {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.framela, fr);
		ft.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.addToBackStack(null);
		ft.commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			sView.toggleMenu();
		} else
			super.onOptionsItemSelected(item);
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (friendLine != null) {
			friendLine.saveSettings();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == iSetFriendLine) {
			if (data == null)
				return;
			if (data.hasExtra("userName")) {
				String userName = data.getStringExtra("userName");
				if (!userName.equalsIgnoreCase("")) {
					needSetUserLine = true;
					userLineTag="";
					int i = userName.indexOf(":");
					if (i>0){
						userLineName=userName.substring(0,i);
						userLineTag=userName.substring(i+1,userName.length());
					} else
					userLineName = userName;
					// setUserLine(userName);
					// Toast.makeText(this, "onActivityResult",
					// Toast.LENGTH_LONG).show();
				}
			}
		} else if (requestCode == iMakePost) {
			if (data == null)
				return;
			if (data.hasExtra("done")) {
				if (data.getIntExtra("done", 0) == 1) {
					if (mjLine != null) {
						mjLine.fillMyJournal();
					}
				}
			}
		} else if (requestCode == iSettingsEdit) {
			settings.load(this);
		}
	}

	public void setUserLine(String userName,String tag) {
		UserLine userLine = new UserLine(this, getLayoutInflater(),
				progressView, settings);
		userLine.setProgressView(progressView);
		userLine.fillUserLine(userName,tag);
		getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_STANDARD);
		if ((tag!=null)&&(!tag.equalsIgnoreCase(""))){
			getSupportActionBar().setTitle(userName+" - " +tag);
		} else
		getSupportActionBar().setTitle(userName);
		setFragment(userLine);

	}

	public Settings getSettings() {
		return settings;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (needSetUserLine) {
			needSetUserLine = false;
			setUserLine(userLineName,userLineTag);
		}

		if (adView != null) {
			final String blockId = getString(R.string.admob_id);
			adView.loadAd(blockId);
			// adView.setAdShowDelay(12000);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (adView != null)
			adView.pause();

	}

	private void initAdView() {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		SharedPreferences sp = getSharedPreferences("adwords", MODE_PRIVATE);
		int ads = sp.getInt("ads", 0);

		LinearLayout laAdmob = (LinearLayout) findViewById(R.id.mainAdmob);

		String lng = Locale.getDefault().getDisplayLanguage();
		if (lng.toLowerCase().contains("en"))
			ads = 0;
		if (ads == 0) {
			ads = 1;
			googleAdView = new com.google.ads.AdView(this, AdSize.BANNER,
					getResources().getString(R.string.admob_publisher_id));
			laAdmob.addView(googleAdView, params);
			googleAdView.loadAd(new AdRequest());
		} else {
			ads = 0;
			adView = new AdView(this);
			laAdmob.addView(adView, params);
			adView.setAdShowDelay(20000);
		}
		SharedPreferences.Editor ed = sp.edit();
		ed.putInt("ads", ads);
		ed.apply();
	}

}
