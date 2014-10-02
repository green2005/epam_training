package com.smlivejournal.top;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.smlivejournal.client.MainActivity;
import com.smlivejournal.client.R;
import com.smlivejournal.settings.Settings;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
//import com.actionbarsherlock.R;
import com.actionbarsherlock.app.SherlockActivity;

//import com.smlivejournal.client.R;

public class MainTop extends Fragment implements OnScrollListener{
	
	
	private ListView mainListView;
	private TopAdapter adapter;
	private ArrayList<HashMap<String, String>> topList;
	private ArrayList<HashMap<String, String>> threadReaderList;
	private PostReader reader;
	public static int readRSS = 1;
	public static int readPost = 2;
	public static int readComments = 3;
	public static int readRSSMore=4;
	
	private Context context;
	private LayoutInflater inflater;
	//private View mainActivityView;
	private View selfView;
	private MainActivity mainActivity;
	private View progressView = null;
	private boolean isLoading=false;
	private View loadMoreView;
	private int visibleItemCount=0;
	private int prevTopItem=0;
	private String topItem="";
	private int itemCount=0;
	private Settings settings;
	private boolean isRestoring=false;

	public MainTop(Context context, LayoutInflater inflater,
			 View progressView,Settings settings) {
		super();
		mainActivity = (MainActivity) context;
		this.context = context;
		this.inflater = inflater;
		//this.mainActivityView = mainActivityView;
		this.progressView=progressView;
		isLoading=true;
		//fillMainTopsMenu();
		isLoading=false;
		this.settings=settings;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainActivity.getSupportActionBar().setTitle(
				com.smlivejournal.client.R.string.sTop);
		boolean restore=selfView!=null;
		
		View v=inflater.inflate(
				com.smlivejournal.client.R.layout.topmainview, container,false);
		
		ListView lv = (ListView)(v.findViewById(R.id.lvtops));
		if (lv!=null)
		lv.setOnScrollListener(this);
		selfView=v;
		if (!restore)
		{setTops();} else
		{
			selfView.setVisibility(View.VISIBLE);
			mainListView = (ListView) selfView.findViewById(R.id.lvtops);
			adapter.setSettings(settings);
			adapter.setMainActivity((MainActivity)mainActivity);
			mainListView.setAdapter(adapter);
			adapter.setListView(mainListView);
			
			final ArrayAdapter<CharSequence> topList = new ArrayAdapter<CharSequence>(
					mainActivity.getSupportActionBar().getThemedContext(),
					R.layout.sherlock_spinner_item);
			
			topList.add(context.getResources().getString(R.string.sMain));
			topList.add(context.getResources().getString(R.string.sNews));
			topList.add(context.getResources().getString(R.string.sPositive));
			topList.add(context.getResources().getString(R.string.sHelpful));
			topList.add(context.getResources().getString(R.string.sSociety));
			topList.add(context.getResources().getString(R.string.sDiscussion));
			topList.add(context.getResources().getString(R.string.sMedia));
			topList.add(context.getResources().getString(R.string.sTravelling));
			topList.add(context.getResources().getString(R.string.sEighteen));
			topList.add(context.getResources().getString(R.string.sZhir));
			
			topList.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

			mainActivity.getSupportActionBar().setListNavigationCallbacks(topList,
					new OnNavigationListener() {

						@Override
						public boolean onNavigationItemSelected(int itemPosition,
								long itemId) {
							if (isRestoring){
								isRestoring=false;
								return true;
							}
							if (isLoading) {return true;};
							saveTopNo(itemPosition);
							if (topList != null) {
								String itemName = (String) topList
										.getItem(itemPosition);
								fillTops(itemName,0,MainTop.readRSS);
							}

							return true;
						}
					});
			// mainActivity.getSupportActionBar().setSelectedNavigationItem(0);
			mainActivity.getSupportActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_LIST);
			isRestoring=true;
			mainActivity.getSupportActionBar().setSelectedNavigationItem(getTopNo());
		}
	
		return v;//super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onDetach() {
		int i=mainActivity.getSupportFragmentManager().getBackStackEntryCount();
		if (i==0){
			mainActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			String title=getResources().getString(com.smlivejournal.client.R.string.app_name);
			mainActivity.getSupportActionBar().setTitle(title);
		}
		super.onDetach();
	}
	
	public void setLoadMoreView(View loadMoreView){
		this.loadMoreView=loadMoreView;
	}

	public void setProgressView(View progressView) {
		this.progressView = progressView;
	}
	
	public void setListView(View selfView){
		this.selfView=selfView;
		ListView lv = (ListView) selfView.findViewById(R.id.lvtops);
		lv.setOnScrollListener(this);
	}
	
	public void setListView(){
		 if (selfView == null) {
			this.selfView = inflater.inflate(
					com.smlivejournal.client.R.layout.topmainview, null);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			
		//	params.addRule(RelativeLayout.BELOW,R.id.latop);
		//	params.addRule(RelativeLayout.ABOVE, R.id.mainAdmob);
		//	((ViewGroup) mainActivityView).addView(selfView, params);
		}
		ListView lv = (ListView) selfView.findViewById(R.id.lvtops);
		lv.setOnScrollListener(this);
		
	}
	
	public View getListView(){
		return selfView;
	}

	public void setTops() {
		// progress=(ProgressBar)mainActivityView.findViewById(R.id.progressBar);
		isLoading=false;
		topList = new ArrayList<HashMap<String, String>>();
		threadReaderList = new ArrayList<HashMap<String, String>>();

		selfView.setVisibility(View.VISIBLE);
		mainListView = (ListView) selfView.findViewById(R.id.lvtops);
		adapter = new TopAdapter(context, topList, inflater);
		adapter.setSettings(settings);
		adapter.setMainActivity((MainActivity)mainActivity);
		mainListView.setAdapter(adapter);
		adapter.setListView(mainListView);
		
		showMainTopsMenu();
	}

	public void fillTops(String topItemName,int offset,int reason) {
		if (reason==readRSSMore){
		setLoadMoreVisible(true);	
		}else
		{setProgressVisible(true);
		}
		topItem=topItemName;

		Handler mainHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				setProgressVisible(false);
				setLoadMoreVisible(false);
				isLoading=false;
				if ((msg.what == readRSS)||(msg.what==readRSSMore)) {
					if (msg.getData().getInt("done") == 1) {
						itemCount=msg.getData().getInt("itemCount");
						if (msg.getData().getInt("error") == 0) {
							if (msg.what!=readRSSMore){
							topList.clear();}
							topList.addAll(threadReaderList);
							threadReaderList.clear();
							MainTop.this.adapter.notifyDataSetChanged();

						} else {
							String em = msg.getData().getString("em");
							Toast.makeText(context, em, Toast.LENGTH_LONG)
									.show();
						}
					}
				}
			}

		};
		threadReaderList.clear();
		reader = new PostReader(threadReaderList, mainHandler, topItemName,
				context,settings);
		reader.setReason(reason);
		reader.setOffset(offset);
		reader.start();
	}

	private void fillMainTopsMenu() {
		final ArrayAdapter<CharSequence> topList = new ArrayAdapter<CharSequence>(
				mainActivity.getSupportActionBar().getThemedContext(),
				R.layout.sherlock_spinner_item);
		
		topList.add(context.getResources().getString(R.string.sMain));
		topList.add(context.getResources().getString(R.string.sNews));
		topList.add(context.getResources().getString(R.string.sPositive));
		topList.add(context.getResources().getString(R.string.sHelpful));
		topList.add(context.getResources().getString(R.string.sSociety));
		topList.add(context.getResources().getString(R.string.sDiscussion));
		topList.add(context.getResources().getString(R.string.sMedia));
		topList.add(context.getResources().getString(R.string.sTravelling));
		topList.add(context.getResources().getString(R.string.sEighteen));
		topList.add(context.getResources().getString(R.string.sZhir));
		topList.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		mainActivity.getSupportActionBar().setListNavigationCallbacks(topList,
				new OnNavigationListener() {

					@Override
					public boolean onNavigationItemSelected(int itemPosition,
							long itemId) {
						if (isLoading) {return true;};
						
						
						if (topList != null) {
							String itemName = (String) topList
									.getItem(itemPosition);
							saveTopNo(itemPosition);
							fillTops(itemName,0,MainTop.readRSS);
						}
						return true;
					}
				});
		// mainActivity.getSupportActionBar().setSelectedNavigationItem(0);
		mainActivity.getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_LIST);
		// mainActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
	}
	
	private void saveTopNo(int topNo){
		SharedPreferences prefs=context.getSharedPreferences("TopItem",Context.MODE_PRIVATE);
		SharedPreferences.Editor  e=prefs.edit();
		e.putInt("topNo",topNo);
		e.commit();
	}
	
	private int getTopNo(){
		int i;
		SharedPreferences prefs=context.getSharedPreferences("TopItem", Context.MODE_PRIVATE);
		i=prefs.getInt("topNo", 0);
		return i;
	}
	
	

	public void hideMainTopsMenu() {
		final ArrayAdapter<CharSequence> topList = new ArrayAdapter<CharSequence>(
				mainActivity.getSupportActionBar().getThemedContext(),
				R.layout.sherlock_spinner_item);
		topList.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		mainActivity.getSupportActionBar().setListNavigationCallbacks(topList,
				new OnNavigationListener() {

					@Override
					public boolean onNavigationItemSelected(int itemPosition,
							long itemId) {
						if (isLoading) {return true;};
						
						if (topList != null) {
							String itemName = (String) topList
									.getItem(itemPosition);
							fillTops(itemName,0,MainTop.readRSS);
						}

						return true;
					}
				});
		// mainActivity.getSupportActionBar().setSelectedNavigationItem(0);
		mainActivity.getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_STANDARD);
	}

	public void showMainTopsMenu() {
		fillMainTopsMenu();
	}

	private void setProgressVisible(Boolean visible) {
		if ((visible) && (progressView != null)) {
			progressView.setVisibility(View.VISIBLE);
		} else if 
		  ((!visible) && (progressView != null)) {
			progressView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (topList==null){return;}
		
		this.visibleItemCount = visibleItemCount;
		int i = firstVisibleItem + visibleItemCount;
		if ((totalItemCount > 0) && (i == totalItemCount) && (!isLoading)
				&& (prevTopItem < firstVisibleItem)
				&& (topList.size()<itemCount)
				&&(topItem.equalsIgnoreCase(context.getResources().getString(R.string.sMain)))
				) {
			isLoading = true;
			setLoadMoreVisible(true);
			loadMoreData();
		}
		prevTopItem = firstVisibleItem;
	}
	
	private void setLoadMoreVisible(boolean visible){
		if (loadMoreView!=null){
			if (visible){
				loadMoreView.setVisibility(View.VISIBLE);
			}else
			{
				loadMoreView.setVisibility(View.GONE);
			}
		}
	}
	
	private void loadMoreData(){
		fillTops(context.getResources().getString(R.string.sMain), topList.size(), MainTop.readRSSMore);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
}
