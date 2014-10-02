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

import com.smlivejournal.client.HttpConn;
import com.smlivejournal.client.MainActivity;
import com.smlivejournal.client.R;
import com.smlivejournal.settings.Settings;
import com.smlivejournal.top.TopAdapter;
import com.smlivejournal.userblog.UserLineThread;


public class UserLine extends Fragment {
	private Context context;
	private MainActivity mainActivity;
	private LayoutInflater inflater;
	private View mainActivityView;
	private View progressView;
	private ListView lvTops;
	private Settings settings;
	private ArrayList<HashMap<String, String>> postList = new ArrayList<HashMap<String, String>>();
	private ArrayList<HashMap<String, String>> tmpList = new ArrayList<HashMap<String, String>>();
	private View selfView;
	private TopAdapter adapter;
	 
		public UserLine(Context context, LayoutInflater inflater,
			 View progressView, Settings settings) {
		this.context = context;
		this.mainActivity=(MainActivity)context;
		this.inflater = inflater;
		this.progressView = progressView;
		this.settings = settings;
		adapter=new TopAdapter(context, postList,inflater);
		adapter.setMainActivity((MainActivity) mainActivity);
		adapter.setSettings(settings);
	}
	
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			selfView = inflater.inflate(
					com.smlivejournal.client.R.layout.topmainview, null);
			
			lvTops=(ListView)selfView.findViewById(R.id.lvtops);
			lvTops.setAdapter(adapter);
			adapter.setListView(lvTops);
			return selfView;
		}
		

	public void setListView(View selfView) {
		this.selfView = selfView;
		lvTops=(ListView)selfView.findViewById(R.id.lvtops);
		lvTops.setAdapter(adapter);
		adapter.setListView(lvTops);
	}

	public View getListView() {
		return selfView;
	}
	
	public void setProgressView(View progressView){
		this.progressView=progressView;
	}
 
	public void fillUserLine(String userName,String userTag){
		postList.clear();
		//String url="www."+userName.replace("_", "-")+".livejournal.com/data/rss";
		 Handler mainHandler=new Handler(){
			   public void handleMessage(Message msg){
				   super.handleMessage(msg);
				   setProgressVisible(false);
				   postList.addAll(tmpList);
				   tmpList.clear();
				   adapter.notifyDataSetChanged();
			 };
			};
			//
			//ssssssss
		UserLineThread th=new UserLineThread(userName,userTag, tmpList, mainHandler); 
		th.setSettings(settings);
		setProgressVisible(true);
		th.start();
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
