package com.smlivejournal.messages;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.smlivejournal.client.MainActivity;
import com.smlivejournal.settings.Settings;

 
public class MessagesLine extends android.support.v4.app.Fragment {
	private Context context;
	private LayoutInflater inflater;
	private MainActivity mainActivity;
	private View selfView;
	private ListView messagesLv;
	private MessagesAdapter adapter;
	private List<LJMessage> msgList;
	private List<LJMessage> tmpList;
	private Settings settings;
	
	
	public MessagesLine(Context context,LayoutInflater inflater,MainActivity activity,Settings settings){
		super();
		this.context=context;
		this.inflater=inflater;
		this.mainActivity=activity;
		this.settings=settings;
		msgList=new ArrayList<LJMessage>();
		adapter=new MessagesAdapter(msgList, inflater, settings);
		tmpList=new ArrayList<LJMessage>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		selfView=inflater.inflate(com.smlivejournal.client.R.layout.topmainview, null); 
		messagesLv=(ListView)selfView.findViewById(com.smlivejournal.client.R.id.lvtops);
		messagesLv.setAdapter(adapter);
		fillMessages();
		return selfView;
	}
	
	private void fillMessages(){
		Handler mainHandler=new Handler(){
			
			
			public void handleMessage(Message msg){
				if (msg.arg1==0){
					msgList.clear();
					msgList.addAll(tmpList);
					tmpList.clear();
					adapter.notifyDataSetChanged();
				}
			}
		};
		
		try {
			MessageThread msgThread=new MessageThread(settings, MReason.mGetMessages, mainHandler);
			msgThread.setMessageList(tmpList);
			msgThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
