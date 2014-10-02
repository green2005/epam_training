package com.smlivejournal.top;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.ActionBar;
import com.smlivejournal.client.MainActivity;
import com.smlivejournal.client.R;
import com.smlivejournal.settings.AuthThread;
import com.smlivejournal.settings.Settings;

public class MyJournalLine extends Fragment {
	private Context context;
	private LayoutInflater inflater;
	private MainActivity mainActivity;
	private View progressView;
	private Settings settings;
	private TopAdapter adapter;
	private ArrayList<HashMap<String, String>> ljList;
	private ArrayList<HashMap<String, String>> tmpList;
	private ListView lvTops;
	private View selfView;
	private boolean isLoading=false;
	private static String url="http://www.livejournal.com/editjournal.bml";
	

	public MyJournalLine(Context context, LayoutInflater inflater,
			 View progressView, Settings settings) {
		 ljList=new ArrayList<HashMap<String,String>>();
		 tmpList=new ArrayList<HashMap<String,String>>();
		 this.context=context;
		 this.mainActivity=(MainActivity)context;
		 this.inflater=inflater;
		 this.progressView=progressView;
		 this.settings=settings;
		 adapter=new TopAdapter(context, ljList, inflater);
		 adapter.setMainActivity((MainActivity)context);
		 adapter.setSettings(settings);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		boolean restore=selfView!=null;
		selfView = inflater.inflate(
				com.smlivejournal.client.R.layout.topmainview, null);
		//((ViewGroup) mainActivityView).addView(selfView, params);
		lvTops = (ListView) selfView.findViewById(R.id.lvtops);
		lvTops.setAdapter(adapter);
		adapter.setListView(lvTops);
		
		if (!restore)
		fillMyJournal();
		
		mainActivity.getSupportActionBar().setTitle(
				com.smlivejournal.client.R.string.sMyJournal);
		mainActivity.getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_STANDARD);
		
		return selfView;// super.onCreateView(inflater, container, savedInstanceState);
	}



	public View getListView() {
		return selfView;
	}

	public void setProgressView(View progressView) {
		this.progressView = progressView;
	}
 
	
	public void fillMyJournal(){
		ljList.clear();
		if ((settings.getUserName().equalsIgnoreCase(""))||(settings.getPwd().equalsIgnoreCase(""))){
			return;
		}
		tmpList.clear();
		Handler mainHandler=new Handler(){
			 @Override
			 public void handleMessage(Message msg){
				 setProgressVisible(false);
				 isLoading=false;
				 ljList.addAll(tmpList);
				 tmpList.clear();
				 adapter.notifyDataSetChanged();
			};
		};
		
		AuthThread thread = new AuthThread(mainHandler,
				AuthThread.iGetMyJournal, settings.getUserName(),
				settings.getPwd());
	//	String url="http://www.livejournal.com/tools/memories.bml?user="+
	//			 settings.getUserName().replace("-", "_")
	//			+"&keyword=*&filter=all";
	 	thread.setUrl(url);
		thread.setSettings(settings);
		thread.setContext(context);
		thread.setList(tmpList);
		isLoading=true;
		thread.start();
		setProgressVisible(true);
	}
	
	
	
	private void setProgressVisible(boolean progressVisible){
		if (progressView!=null){
			if (progressVisible){
				progressView.setVisibility(View.VISIBLE);
			} else
			{
				progressView.setVisibility(View.GONE);
			}
		}
		
	}

}
