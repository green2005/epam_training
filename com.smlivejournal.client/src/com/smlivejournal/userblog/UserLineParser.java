package com.smlivejournal.userblog;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xmlrpc.XmlRpcClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.smlivejournal.settings.AuthThread;
import com.smlivejournal.settings.Settings;

import android.text.Html;
import android.util.Log;

public class UserLineParser {
	String html;
	ArrayList<HashMap<String, String>> list;
	private Settings settings;

	public UserLineParser(ArrayList<HashMap<String, String>> list) {
		// this.html = html;
		this.list = list;
	}
	
	public UserLineParser(ArrayList<HashMap<String, String>> list,String html) {
		this.html = html;
		this.list = list;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public void fillUsersJournal(String userName) {
		try
		{
		Vector p;
		Hashtable method_calls = new Hashtable( );
		String lj_url = "http://www.livejournal.com/interface/xmlrpc";
		XmlRpcClient xmlrpc = new XmlRpcClient(lj_url);
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
          //  method_calls.put("truncate", AuthThread.iTrimWidgets);
            method_calls.put("prefersubject", false);
            method_calls.put("noprops", false);
            method_calls.put("notags", false);
            method_calls.put("selecttype", "lastn");
            method_calls.put("howmany", "100");
            method_calls.put("lineendings", "\n");
            method_calls.put("usejournal", userName);
            method_calls.put("parseljtags",true);
            method_calls.put("trim_widgets", AuthThread.iTrimWidgets);
            method_calls.put("widgets_img_length", 0);
            
            method_calls.put("year", new Integer(now.getYear( ) + 1900));
            method_calls.put("mon", new Integer(now.getMonth( ) + 1));
            method_calls.put("day", new Integer(now.getDate( )));
            method_calls.put("hour", new Integer(now.getHours( )));
            method_calls.put("min", new Integer(now.getMinutes( )));
            params.add(method_calls);
		    Object result = xmlrpc.execute("LJ.XMLRPC.getevents", params);
		    Vector v=((Vector)((Hashtable)result).get("events"));
		 //   ((Hashtable)(v.get(0)))
		   for (int i=0;i<v.size();i++){
			   Hashtable item=((Hashtable)(v.get(i)));
			   String journalName= userName;//(String) item.get("poster");
			   String replyCount=((Integer) item.get(("reply_count")+"")).toString();
			   //String posterName=(String)item.get
			   //("postername");
			   String postUrl=(String)item.get("url");
			   
			   String body ;
			   String title;
			   if (item.get("subject")==null){
				   title="";
			   } else
			   if (item.get("subject") instanceof String){
				   title=(String)item.get("subject");
			   }else
			   {
				   title=new String((byte [])item.get("subject")); 
			   }
			   
			   if (item.get("event") instanceof String){
				   body=(String)item.get("event");
			   }else
			   {
				   body=new String((byte [])item.get("event")); 
			   }
			   
			   title=Html.fromHtml(title).toString();
			   body=Html.fromHtml(body).toString();
			   //1400154916
			   //new Date(Long.parseLong("1400154916")* (long) 1000).toGMTString()()
			   String sDate=((String) item.get("eventtime"));
			    //String logtime=(String)item.get("logtime");
					   
			   HashMap<String,String> map=new HashMap<String,String>();
			   map.put("body", body);
			   map.put("subject", title);
			   map.put("reply_count", replyCount);
			   map.put("post_url", postUrl);
			   map.put("journal", journalName);
			   map.put("time", sDate);
			  // map.put("canedit", "1");
			   list.add(map);
			   /*
			 map.put("time", pubDate);
						map.put("position", position + "");
						map.put("body", text);
						map.put("post_url", link);
						map.put("reply_count", commentCount);
						map.put("journal", journalName);
						map.put("subject", title);
						list.add(map);
			    */
		
	 
		   }
	} catch (Exception e) {
		e.printStackTrace();
	};
	
}

	public void parse() {
		try {
			XmlPullParserFactory pullFactory = XmlPullParserFactory
					.newInstance();
			pullFactory.setNamespaceAware(true);
			XmlPullParser parser = pullFactory.newPullParser();
			StringReader sr = new StringReader(html);
			parser.setInput(new StringReader(html));
			int evtType = parser.getEventType();
			String tagName = "";
			String title = "";
			String link = "";
			String commentCount = "";
			String pubDate = "";
			String text = "";
			String journalName = "";
			int position = 0;
			boolean isChannel = false;
			Pattern pDot = Pattern.compile(".*?[.].*?[.].*?[.]");

			while (evtType != XmlPullParser.END_DOCUMENT) {
				switch (evtType) {
				case XmlPullParser.END_TAG: {
					if (parser.getName().equalsIgnoreCase("item")) {
						HashMap<String, String> map = new HashMap<String, String>();

						/*
						 * fieldNames.put("time", 1); fieldNames.put("position",
						 * 2); fieldNames.put("body", 1);
						 * fieldNames.put("post_url", 1);
						 * fieldNames.put("reply_count", 2);
						 * fieldNames.put("journal", 1);
						 * fieldNames.put("subject", 1);
						 */
						map.put("time", pubDate);
						map.put("position", position + "");
						map.put("body", text);
						map.put("post_url", link);
						map.put("reply_count", commentCount);
						map.put("journal", journalName);
						map.put("subject", title);
						list.add(map);
					}
					tagName = "";
					break;
				}
				case XmlPullParser.START_TAG: {
					tagName = parser.getName();
					if (tagName.equalsIgnoreCase("channel")) {
						isChannel = true;
					} else if (tagName.equalsIgnoreCase("item")) {
						isChannel = false;
						position++;
					}
					break;
				}
				case XmlPullParser.TEXT: {
					if ((isChannel) && (tagName.equalsIgnoreCase("journal"))) {
						journalName = parser.getText();
					} else if (!isChannel) {
						if (tagName.equalsIgnoreCase("guid")) {
							link = parser.getText();
						} else if (tagName.equalsIgnoreCase("pubDate")) {
							pubDate = parser.getText();
						} else if (tagName.equalsIgnoreCase("title")) {
							title = Html.fromHtml(parser.getText()).toString();
						} else if (tagName.equalsIgnoreCase("reply-count")) {
							commentCount = parser.getText();
						} else if (tagName.equalsIgnoreCase("description")) {
							String s = parser.getText();
							// a name=&quot;cutid
							if (s.length()>200)
							s=s.substring(0,200)+" ...";
							/*
							int i = s.indexOf("cutid1");
							if (i > 0) {
								s = s.substring(0, i);
							} else {
								Matcher mdot = pDot.matcher(s);
								if (mdot.find()) {
									s = mdot.group();
								}

							}
							*/
							text = Html.fromHtml(s).toString();
							
						}
					}
					/*
					 * if (tagName.equalsIgnoreCase("item")){ position++;
					 * link=parser.getAttributeValue(null, "guid");
					 * pubDate=parser.getAttributeValue(null, "pubDate");
					 * title=parser.getAttributeValue(null,"title");
					 * commentCount=parser.getAttributeValue(null,
					 * "lj:reply-count"); text=parser.getAttributeValue(null,
					 * "description"); } else if
					 * (tagName.equalsIgnoreCase("channel")){
					 * journalName=parser.getAttributeValue(null, "lj:journal");
					 * }
					 */
					break;
				}
				}
				evtType = parser.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
