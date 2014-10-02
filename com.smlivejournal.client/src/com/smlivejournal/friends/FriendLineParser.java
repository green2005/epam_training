package com.smlivejournal.friends;

import java.net.IDN;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xmlrpc.XmlRpcClient;
import org.jsoup.helper.StringUtil;

import com.smlivejournal.settings.AuthThread;
import com.smlivejournal.settings.Settings;
 



import android.net.Uri;
import android.text.Html;
import android.util.Log;

public class FriendLineParser {
	private ArrayList<HashMap<String, String>> list;
	private String html;
	private String prevUrl = "";
	private Settings settings;
	
	
	public FriendLineParser(Settings settings, ArrayList<HashMap<String, String>> list) {
		//this.html = html + "";
		this.settings=settings;
		this.list = list;
	}
	
	public FriendLineParser(String html, ArrayList<HashMap<String, String>> list) {
		this.html = html + "";
		this.list = list;
	}
	
	
	
	public void parseMemories(){
		String body="";
		Pattern pBody=Pattern.compile("<p><li>.*?</li>");
		Pattern pUrl=Pattern.compile("href=\".*?\"");
		Pattern pText=Pattern.compile("\">.*?</a>");
		Matcher mb=pBody.matcher(html);
		while (mb.find()){
			String url="";
			String text="";
			String journal="";

			body=mb.group();
			Matcher mUrl=pUrl.matcher(body);
			if (mUrl.find()){
				url=mUrl.group().substring(6);
				url=url.substring(0,url.length()-1);
			}
			
			Matcher m=pText.matcher(body);
			if (m.find()){
				text=m.group();
				text=text.substring(2);
				text=text.substring(0,text.length()-4);
				text=Html.fromHtml(text).toString();
			}
			HashMap<String,String> map=new HashMap<String,String>();
			//url=url.replace("http:", "");
			//url=url.replace("www.", "");
			//url=url.replace("", "");
			
			
			map.put("post_url", url);
			map.put("body", text);
			list.add(map);
		}
	}

	public void fillFriendLine(String filter){
		//
		try {
			Vector p;
			Hashtable method_calls = new Hashtable( );
			String lj_url = "http://www.livejournal.com/interface/xmlrpc";
			XmlRpcClient xmlrpc = new XmlRpcClient(lj_url);
			String password = AuthThread.getMD5(settings.getPwd());
			Vector<Hashtable<String, Comparable>> params = new Vector();
			
			method_calls.put(
	                "username",
	                settings.getUserName());

	            method_calls.put(
	                "password",
	                settings.getPwd());
	            method_calls.put("ver", "1");
	            method_calls.put("clientversion", "WebServiceBook/0.0.1");
	            java.util.Date now = new java.util.Date( );
	            method_calls.put("trim_widgets", AuthThread.iTrimWidgets);
	            method_calls.put("parseljtags", "1");
	            method_calls.put("widgets_img_length", 0);
	            method_calls.put("year", new Integer(now.getYear( ) + 1900));
	            method_calls.put("mon", new Integer(now.getMonth( ) + 1));
	            method_calls.put("day", new Integer(now.getDate( )));
	            method_calls.put("hour", new Integer(now.getHours( )));
	            method_calls.put("min", new Integer(now.getMinutes( )));
	            if (filter!=null)
	            	if (StringUtil.isNumeric(filter)){
	            		int groupId=Integer.parseInt(filter);
	            		
	            		 int groupmask;//=(1 << groupId) + 1;// = 
//	            		int groupMask=groupId>>1; //(1 << groupId) + 1;
//	            		groupMask=groupMask&1;
	            		 groupmask=(int) Math.pow( 2,groupId);
//	            		
	            		method_calls.put("groupmask", new Integer(groupmask));
	            		//method_calls.put("journaltype", "C");
	            		
	            	} else
	            	if (!filter.equalsIgnoreCase("")){
	            		method_calls.put("journaltype", filter);
	            	}
	            
	            params.add(method_calls);
			    Object result = xmlrpc.execute("LJ.XMLRPC.getfriendspage", params);
			   // Log.d("", result.toString());
			    //Hashtable res=
			    //
			   // ((Hashtable)
			 //   ((Vector)((Hashtable)result).get("entries")).get(0).get("event_raw") instanceof String
			    Vector v=((Vector)((Hashtable)result).get("entries"));
			   for (int i=0;i<v.size();i++){
				   Hashtable item=((Hashtable)(v.get(i)));
				   String journalName=(String) item.get("journalname");
				   String replyCount=((Integer) item.get(("reply_count")+"")).toString();
				   //String posterName=(String)item.get("postername");
				   String journalUrl=(String)item.get("journalurl");
				   String dItemId=((Integer) item.get("ditemid")).toString();
				   String postUrl=journalUrl+"/"+dItemId+".html";
				   String body ;
				   String title;
				   if (item.get("subject_raw") instanceof String){
					   title=(String)item.get("subject_raw");
				   }else
				   {
					   title=new String((byte [])item.get("subject_raw")); 
				   }
				   
				   if (item.get("event_raw") instanceof String){
					   body=(String)item.get("event_raw");
				   }else
				   {
					   body=new String((byte [])item.get("event_raw")); 
				   }
				   
				   title=Html.fromHtml(title).toString();
				   body=Html.fromHtml(body).toString();
				   String sDate=((Integer) item.get("logtime")).toString();
				   Date postDate= new Date(Long.parseLong(sDate)* (long) 1000);
				   sDate = postDate.toGMTString();
				 		   
				   HashMap<String,String> map=new HashMap<String,String>();
				   map.put("body", body);
				   map.put("subject", title);
				   map.put("reply_count", replyCount);
				   map.put("post_url", postUrl);
				   map.put("journal", journalName);
				   map.put("time", sDate);
				   list.add(map);
				   /*
				    map.put("body", post);
			map.put("time", time);
			map.put("reply_count", reply_count);
			map.put("subject", title);
			map.put("journal", journal);
			map.put("post_url", postLink);
				    */
				}
			   
			   
			    
		} catch (Exception e) {
			e.printStackTrace();
		};

		
	}
	
	public void parseOld() {
		html = html.replace("\n", "");
		Pattern pB = Pattern.compile("\"entry hentry\".*?div class=\"hr\"");// .matcher(html.replace("\n",
																			// "")).find();

		// Pattern pB=Pattern.compile("class=\"entry hentry\".*?entrymenu");

		Pattern pTitle = Pattern.compile("data-title=\".*?\"");

		Pattern pPost = Pattern.compile("data-text=\".*?\"");
		Pattern pDate = Pattern.compile("<abbr class=\"updated\".*?</abbr>");
		Pattern pDate2 = Pattern.compile(">.*?<");
		Pattern pReplyCount = Pattern
				.compile("<li class=\"comments\">.*?</li>");
		Pattern pReplyCount2 = Pattern.compile("<span>.*? ");
		Pattern pJournal = Pattern.compile("data-poster=\".*?\"");
		Pattern pPostLink = Pattern.compile("data-url=\".*?\"");
		Pattern pPrevUrl = Pattern.compile("class=\"prev\">.*?>");
		Pattern pPrevUrl2 = Pattern.compile("href=\".*?\"");

		String body = "";
		String title = "";
		String time = "";
		String reply_count = "";
		String journal = "";
		String postLink = "";
		String post = "";
		// html.indexOf("class=\"entry hentry\"")
		// html.indexOf("entrymenu")

		Matcher mB = pB.matcher(html);
		while (mB.find()) {
			body = mB.group();
			Matcher m = pTitle.matcher(body);
			if (m.find()) {
				title = URLDecoder.decode(m.group());
				title = title.substring(12);
				title = title.substring(0, title.length() - 1);
				title = Html.fromHtml(title).toString();
			}

			m = pPost.matcher(body);
			if (m.find()) {
			}
			;

			if (m.find()) {
				post = m.group().substring(11);
				post = URLDecoder.decode(post);
				post = Html.fromHtml(post).toString();
				post = post.substring(0, post.length() - 1);
			}

			m = pDate.matcher(body);
			if (m.find()) {
				m = pDate2.matcher(m.group());
				if (m.find()) {
					time = m.group().substring(1);
					time = time.substring(0, time.length() - 1);
				}
			}

			m = pReplyCount.matcher(body);
			if (m.find()) {
				m = pReplyCount2.matcher(m.group());
				if (m.find()) {
					reply_count = m.group().replace("<span>", "").trim();
				}
			}

			m = pJournal.matcher(body);
			if (m.find()) {
				journal = m.group().substring(13);
				journal = journal.substring(0, journal.length() - 1);
			}

			m = pPostLink.matcher(body);
			if (m.find()) {
				postLink = m.group().substring(10);
				postLink = postLink.substring(0, postLink.length() - 1);
			}

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("body", post);
			map.put("time", time);
			map.put("reply_count", reply_count);
			map.put("subject", title);
			map.put("journal", journal);
			map.put("post_url", postLink);
			list.add(map);
		}
		mB = pPrevUrl.matcher(html);
		if (mB.find()) {
			mB = pPrevUrl2.matcher(mB.group());
			if (mB.find()) {
				try {
					prevUrl = mB.group().substring(6);
					prevUrl = prevUrl.substring(0, prevUrl.length() - 1)
							+ "&format=light";
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getPrevUrl() {
		return prevUrl;
	}

}
