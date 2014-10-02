package com.smlivejournal.userblog;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Path.FillType;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.smlivejournal.client.HttpConn;
import com.smlivejournal.settings.Settings;

public class UserLineThread extends Thread {
	private ArrayList<HashMap<String,String>> list;
	private String userName;
	private Handler mainHandler;
	private Settings settings;
	private String userTag="";
	
	
	public UserLineThread(String userName,String userTag,ArrayList<HashMap<String,String>> list,Handler mainHandler) {
		this.userName=userName;
		this.list=list;
		this.mainHandler=mainHandler;
		this.userTag=userTag;
	}
	
	
	public void setSettings(Settings settings){
		this.settings=settings;
	}
	
	public void run(){
		//http://anita-lebedeva.livejournal.com/data/rss
		if ((userTag!=null)&&(!userTag.equalsIgnoreCase(""))){
			String url="http://"+userName.replace("_", "-")+".livejournal.com/data/rss?tag="+URLEncoder.encode(userTag);
		  
		  HttpConn httpget=new HttpConn();
		  String html=httpget.htmlByUrl(url,true);
		  UserLineParser parser=new UserLineParser( list,html);
		  parser.setSettings(settings);
		  parser.parse();
		} else
		{	
		  UserLineParser parser=new UserLineParser( list);
		  parser.setSettings(settings);
		  parser.fillUsersJournal(userName);
		//parser.parse();
		}
		Message msg=new Message();
		msg.what=1;
		mainHandler.sendMessage(msg);
	}

}
