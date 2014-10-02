package com.smlivejournal.top;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smlivejournal.client.MainActivity;
import com.smlivejournal.settings.Settings;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class PostReader extends Thread {
	Handler mainHandler;
	ArrayList<HashMap<String, String>> list;
	String topItemName;
	Context context;
	int msgWhat;

	private String postUrl;
	private String postId;
	private String nickName;
	private String subject;
	private CommentsReader cmReader;
	private int offset=0;
	private Settings settings;
	
	
	PostReader(ArrayList<HashMap<String, String>> list, Handler mainHandler,
			String topItemName, Context context,Settings settings) {
		super();
		this.mainHandler = mainHandler;
		this.list = list;
		this.context = context;
		this.topItemName = topItemName;
		msgWhat = MainTop.readRSS;
		this.settings=settings;
	}

	public PostReader(ArrayList<HashMap<String, String>> list, Handler mainHandler,
			Context context,String postUrl,String nickName,int msgWhat,Settings settings) {
		super();
		this.msgWhat=msgWhat; //MainTop.readPost;
		this.postUrl=postUrl;
		this.nickName=nickName;
		this.list=list;
		this.context=context;
		this.mainHandler=mainHandler;
		this.settings=settings;
	}
	
	public PostReader(ArrayList<HashMap<String,String>> list,Handler mainHandler,Context context,String postUrl,int msgWhat,Settings settings){
		super();
		this.msgWhat=msgWhat;
		this.postUrl=postUrl;
		this.list=list;
		this.context=context;
		this.mainHandler=mainHandler;
		this.settings=settings;
	}
	
	public void setCommentsReader(CommentsReader cmReader){
		this.cmReader=cmReader;
	}
	
	public void setReason(int reason){
		this.msgWhat=reason;
	}
	
	public void setOffset(int offset){
		this.offset=offset;
	}

	public void run() {
		if ((msgWhat == MainTop.readRSS)||(msgWhat==MainTop.readRSSMore)) {
			NewsParser parser = new NewsParser(list, true, topItemName, context,settings);
			parser.setReason(msgWhat);
			parser.setOffset(offset);
			 
			parser.fillNews();
			int itemCount=parser.getItemCount();
			Message msg = new Message();
			msg.what = msgWhat;
			Bundle b = new Bundle();
			b.putString("em", parser.getEm());
			b.putInt("rc", parser.getRC());
			b.putInt("itemCount",itemCount);
			b.putInt("done", 1);
			msg.setData(b);
			mainHandler.sendMessage(msg);
		} else if (msgWhat==MainTop.readPost){
			
			Message msg=new Message();
			msg.what=MainTop.readPost;
			Bundle b=new Bundle();
			PostParser parser=new PostParser(list,postUrl,nickName);
			parser.setSettings(settings);
			parser.fillPost();
			b.putString("em", parser.getEm());
			b.putString("rc", parser.getRc()+"");
			b.putInt("done", 1);
			msg.setData(b);
			mainHandler.sendMessage(msg);
		}else if (msgWhat==MainTop.readComments){
			Message msg=new Message();
			msg.what=MainTop.readPost;
			Bundle b=new Bundle();
			CommentsReader parser=cmReader; //new CommentsReader(postUrl,list);
			parser.setSettings(settings);
			
			//parser.parse3(postUrl);
			
			parser.parse1(postUrl);
			b.putString("em", parser.getEm());
			b.putString("rc", parser.getRc()+"");
			b.putInt("done", 1);
			msg.setData(b);
			mainHandler.sendMessage(msg);
		}
			
	}

}
