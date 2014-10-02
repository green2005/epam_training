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

public class MemoriesLine extends Fragment{
	private Context context;
	private LayoutInflater inflater;
	private MainActivity mainActivity;
	private View progressView;
	private Settings settings;
	private MemoriesAdapter adapter;
	private ArrayList<HashMap<String, String>> memList;
	private ArrayList<HashMap<String, String>> tmpList;
	private ListView lvTops;
	private View selfView;
	private boolean isLoading=false;
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		boolean restore=selfView!=null;
		
		selfView = inflater.inflate(
				com.smlivejournal.client.R.layout.topmainview, null);
		
		lvTops = (ListView) selfView.findViewById(R.id.lvtops);
		lvTops.setAdapter(adapter);
		adapter.setListView(lvTops);
		if (!restore)
		fillMemories();
	 	mainActivity.getSupportActionBar().setTitle(
				com.smlivejournal.client.R.string.sMemories);
	 	mainActivity.getSupportActionBar().setNavigationMode(
				ActionBar.NAVIGATION_MODE_STANDARD);
		return selfView;
	}

	public MemoriesLine(Context context, LayoutInflater inflater,
			  View progressView, Settings settings) {
		memList = new ArrayList<HashMap<String, String>>();
		tmpList = new ArrayList<HashMap<String, String>>();
		this.mainActivity = (MainActivity) context;
		this.context = context;
		this.inflater = inflater;
		this.progressView = progressView;
		this.settings = settings;
		adapter = new MemoriesAdapter(context, memList, inflater);
		adapter.setSettings(settings);
		adapter.setMainActivity((MainActivity) mainActivity);
		
	}

	 

	public View getListView() {
		return selfView;
	}

	public void setProgressView(View progressView) {
		this.progressView = progressView;
	}
 
	
	public void fillMemories(){
		memList.clear();
		if ((settings.getPwd().equalsIgnoreCase(""))
				|| (settings.getUserName().equalsIgnoreCase(""))) {
			return;
		}
		tmpList.clear();
		Handler mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.getData() != null) {
					Bundle b = msg.getData();
					
				}
				memList.addAll(tmpList);
				tmpList.clear();
				adapter.notifyDataSetChanged();
				setProgressVisible(false);
				//setLoadMoreVisible(false);
				isLoading=false;
			};
		};
		setProgressVisible(true);
		AuthThread thread = new AuthThread(mainHandler,
				AuthThread.iGetMemories, settings.getUserName(),
				settings.getPwd());
		String url="http://www.livejournal.com/tools/memories.bml?user="+
				 settings.getUserName().replace("-", "_")
				+"&keyword=*&filter=all";
		thread.setUrl(url);
		thread.setSettings(settings);
		thread.setContext(context);
		thread.setList(tmpList);
		thread.start();
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
