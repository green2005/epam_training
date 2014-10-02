package com.smlivejournal.top;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.jsoup.Jsoup;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;

import com.smlivejournal.client.HttpConn;
import com.smlivejournal.lazylist.ImageLoader;
import com.smlivejournal.settings.Settings;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PostParser {
	private List<HashMap<String, String>> list;
	private int rc = 0;
	private String em = "";
	private String postUrl;
	private String nickName;
	private Settings settings;
	private static String pref = "http://users.livejournal.com/";

	public PostParser(List<HashMap<String, String>> list, String postUrl,
			String nickName) {
		super();
		this.list = list;
		this.postUrl = postUrl;
		this.nickName = nickName;

	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public int getRc() {
		return rc;
	}

	public String getEm() {
		return em;
	}

	private String getUserName(String url) {
		if (url.toLowerCase().contains(pref)) {
			String sUrl = url.toLowerCase().replace(pref, "");
			int i = sUrl.indexOf("/");
			if (i > 0) {
				return sUrl.substring(0, i);
			}
		}

		String s = "";
		Pattern p = Pattern.compile("//.*?[.]");
		Matcher m = p.matcher(url);
		if (m.find()) {
			s = m.group().substring(2);
			s = s.substring(0, s.length() - 1);
		}

		// http://tema.livejournal.com/1670849.html
		// http://users.livejournal.com/vba_/363095.html
		return s;
	}

	private String getPostID(String url) {
		if (url.toLowerCase().contains(pref)) {
			url = url.toLowerCase().replace(pref, "");
		}
		String s = "";
		url = url.replace("http://", "");
		Pattern p = Pattern.compile("/.*?[.]");
		Matcher m = p.matcher(url);
		if (m.find()) {
			s = m.group().substring(1);
			s = s.substring(0, s.length() - 1);
		}
		int postId = Integer.parseInt(s);
		int anum = (int) (postId - Math.floor(postId / 256) * 256);
		int internalId = ((postId - anum) / 256);
		return internalId + "";
		/*
		 * $anum = $public_itemid - floor($public_itemid / 256) * 256; $itemid =
		 * ($public_itemid - $anum) / 256;
		 */
	}

	public void fillxmlrpc(HashMap<String, String> map) {
		String userName = getUserName(postUrl);
		String postID = getPostID(postUrl);
		try {
			Vector p;
			Hashtable method_calls = new Hashtable();
			String lj_url = "http://www.livejournal.com/interface/xmlrpc";
			XmlRpcClient xmlrpc = new XmlRpcClient(lj_url);
			// String password = getMD5(settings.getPwd());
			Vector<Hashtable<String, Comparable>> params = new Vector();
			boolean fillUserName = false;
			if (this.settings != null) {
				if ((!this.settings.getUserName().equalsIgnoreCase(""))
						&& (!this.settings.getPwd().equalsIgnoreCase(""))) {
					fillUserName = true;
				}
			}

			if (fillUserName) {
				method_calls.put("username", settings.getUserName());

				method_calls.put("password", settings.getPwd());
			} else {
				method_calls.put("auth_method", "noauth");
			}
		
			

			method_calls.put("ver", "1");
			method_calls.put("clientversion", "WebServiceBook/0.0.1");
			method_calls.put("selecttype", "one");
			method_calls.put("itemid", postID);// postID);
			method_calls.put("usejournal", userName);
			method_calls.put("parseljtags", true);

			java.util.Date now = new java.util.Date();
			method_calls.put("lineendings", "\n");

			method_calls.put("year", new Integer(now.getYear() + 1900));
			method_calls.put("mon", new Integer(now.getMonth() + 1));
			method_calls.put("day", new Integer(now.getDate()));
			method_calls.put("hour", new Integer(now.getHours()));
			method_calls.put("min", new Integer(now.getMinutes()));
			params.add(method_calls);
			String resBody = "";
			String resTitle = "";

			Object result = xmlrpc.execute("LJ.XMLRPC.getevents", params);
			if (((Hashtable) ((Vector) ((Hashtable) result).get("events"))
					.get(0)).get("event") instanceof String) {
				resBody = (String) ((Hashtable) ((Vector) ((Hashtable) result)
						.get("events")).get(0)).get("event");
			} else
				resBody = new String(
						(byte[]) (((Hashtable) ((Vector) ((Hashtable) result)
								.get("events")).get(0)).get("event")));

			if (((Hashtable) ((Vector) ((Hashtable) result).get("events"))
					.get(0)).get("subject") == null) {
				resTitle = "";
			} else if (((Hashtable) ((Vector) ((Hashtable) result)
					.get("events")).get(0)).get("subject") instanceof Integer) {
				resTitle = ((Integer) ((Hashtable) ((Vector) ((Hashtable) result)
						.get("events")).get(0)).get("subject")).toString();
			} else if (((Hashtable) ((Vector) ((Hashtable) result)
					.get("events")).get(0)).get("subject") instanceof String) {
				resTitle = (String) ((Hashtable) ((Vector) ((Hashtable) result)
						.get("events")).get(0)).get("subject");
			} else
				// /new String(
				// / (byte[]) (((Hashtable) (((Vector) ((Hashtable) result)
				// .get("events")).get(0))).get("event")))
				resTitle = new String(
						(byte[]) (((Hashtable) (((Vector) ((Hashtable) result)
								.get("events")).get(0))).get("subject")));
			String replyCount = ""
					+ (((Hashtable) (((Vector) ((Hashtable) result)
							.get("events")).get(0))).get("reply_count"));
			String canComment = ""
					+ (((Hashtable) (((Vector) ((Hashtable) result)
							.get("events")).get(0))).get("can_comment"));
			String eventTime = ""
					+ (((Hashtable) (((Vector) ((Hashtable) result)
							.get("events")).get(0))).get("eventtime"));
			// (((Hashtable)(((Vector)((Hashtable)result).get("events")).get(0))).get("props"))

			String taglist = "";

			if (((Hashtable) ((Hashtable) (((Vector) ((Hashtable) result)
					.get("events")).get(0))).get("props")).get("taglist") != null) {

				if (((Hashtable) ((Hashtable) (((Vector) ((Hashtable) result)
						.get("events")).get(0))).get("props")).get("taglist") instanceof String) {
					taglist = ""
							+ ((Hashtable) ((Hashtable) (((Vector) ((Hashtable) result)
									.get("events")).get(0))).get("props"))
									.get("taglist");
				} else
					taglist = new String(
							(byte[]) (((Hashtable) ((Hashtable) (((Vector) ((Hashtable) result)
									.get("events")).get(0))).get("props"))
									.get("taglist")));
			}
			// taglist=new String(Base64.decode(taglist, 0));

			String adultcontent = ""
					+ ((Hashtable) ((Hashtable) (((Vector) ((Hashtable) result)
							.get("events")).get(0))).get("props"))
							.get("adult_content");
			String commentsOptions = "";
			if (((Hashtable) ((Hashtable) (((Vector) ((Hashtable) result)
					.get("events")).get(0))).get("props"))
					.get("opt_nocomments") != null) {
				if (((Hashtable) ((Hashtable) (((Vector) ((Hashtable) result)
						.get("events")).get(0))).get("props")).get(
						"opt_nocomments").equals(1))
					commentsOptions = "opt_nocomments";
			} else if (((Hashtable) ((Hashtable) (((Vector) ((Hashtable) result)
					.get("events")).get(0))).get("props"))
					.get("opt_lockcomments") != null) {
				if (((Hashtable) ((Hashtable) (((Vector) ((Hashtable) result)
						.get("events")).get(0))).get("props")).get(
						"opt_lockcomments").equals(1))
					commentsOptions = "opt_lockcomments";
			} else if (((Hashtable) ((Hashtable) (((Vector) ((Hashtable) result)
					.get("events")).get(0))).get("props")).get("opt_noemail") != null) {
				if (((Hashtable) ((Hashtable) (((Vector) ((Hashtable) result)
						.get("events")).get(0))).get("props")).get(
						"opt_noemail").equals(1))
					commentsOptions = "opt_noemail";
			}
			;
			String security = (((Hashtable) (((Vector) ((Hashtable) result)
					.get("events")).get(0))).get("security")) + "";

			if (taglist.equals(null))
				taglist = "";

			if (adultcontent.equals(null)) {
				adultcontent = "";
			} else if (adultcontent.equals("explicit")) {
				adultcontent = "1";
			}

			if (commentsOptions.equals(null)) {
				commentsOptions = "";
			}
			if (security.equals(null)) {
				security = "";
			}

			map.put("body", resBody);
			map.put("title", resTitle);
			map.put("replycount", replyCount);
			map.put("cancomment", canComment);
			map.put("eventtime", eventTime);
			map.put("userName", userName);

			map.put("taglist", taglist);
			map.put("adultcontent", adultcontent);
			map.put("commentsOptions", commentsOptions);
			map.put("security", security);
			
			String uPic=getUpic(userName);
			map.put("upic", uPic);
			
			
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private String getUpic(String userName){
		HttpConn conn = new HttpConn();
		String url="http://www.livejournal.com/allpics.bml?user="+userName;
		String html = conn.htmlByUrl(url, false);
		String s="";
		Pattern p=Pattern.compile("src='http://l-userpic.livejournal.com/.*?'");
		Matcher m=p.matcher(html);
		if (m.find()){
			s=m.group().substring(5);
			s=s.substring(0, s.length()-1);
		}
		return s;
	}

	public void fillxmlrpc(Bundle b) {
		if (b == null) {
			return;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		fillxmlrpc(map);
		for (String skey : map.keySet()) {
			b.putString(skey, map.get(skey));
		}
	}

	private String getFixUrls(String text) {
		String strRegex = "((((https?|ftp|telnet|file):((//)|(\\\\))+)|(www.))+[\\w\\d:#@%/;$()~_?\\+-=\\\\.&]*)";
		Pattern pUrl = Pattern.compile(strRegex); // (.*?)[^=\"](https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|](.*?)");
		Matcher m = pUrl.matcher(text);

		while (m.find()) {
			int i = m.start();
			if (i >= 8) {
				String s = text.substring(i - 8, i);
				if (s.contains("href")) {
					continue;
				}
				if (s.contains(">")) {
					continue;
				}
			}
			String url = m.group();
			url = url.replace(",", "");
			String newUrl = "<a href=\"" + url + "\"" + ">" + url + "</a>";
			// text=text.replace(url, newUrl);
		}
		// <a href="http:\\hui.com">http:\\hui.com</a>
		return text;
	}

	public void fillPost() {
		Pattern pComment;
		try {
			HashMap<String, String> postMap = new HashMap<String, String>();
			fillxmlrpc(postMap);

			String url = postUrl + "?format=light";
			// HttpConn conn = new HttpConn();
			// String html = conn.htmlByUrl(url, true);
			String html;
			String s;
			String title = null;
			String pubdate = null;
			String replys = null;
			String userName = null;
			String tagList = null;
			String uPic = null;
			pComment = Pattern.compile(".*?<!--");
			Pattern p = Pattern.compile("src=\".*?>");
			Pattern pImg = Pattern.compile("<img.*?>");

			Matcher m;
			
			title = postMap.get("title"); // s.substring(1, s.length() -
											// 1).trim();
			pubdate = postMap.get("eventtime");
			replys = postMap.get("replycount");
			userName = postMap.get("userName");
			tagList = postMap.get("taglist");
			uPic = postMap.get("upic");
				
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("title", title);
			map.put("pubdate", pubdate);
			map.put("replys", replys);
			map.put("userName", userName);
			map.put("taglist", tagList);
			map.put("upic", uPic);

			list.add(map);
			html = postMap.get("body");

			/*
			 * m = pComment.matcher(html); if (m.find()) { html = (String)
			 * m.group().subSequence(0, m.end() - 4); }
			 */

			m = pImg.matcher(html);
			int end = 0;
			int k = 0;
			// Html.fromHtml("<a href=\"http://www.tebebuy.ru/\"> ssssssssdc/</a>")
			// Html.fromHtml("¬ свое врем€ <a href=\"http://www.youtube.com/watch?v=6KYfqGLkSVI\" target=\"_blank\">реб€та-пранкеры выложили ролик</a>")
			while (m.find()) {
				if (m.start() >= 0) {
					Matcher m2 = p.matcher(m.group());
					if (m2.find()) {
						String s1 = html.substring(end, m.start());
						String img = m2.group(0).substring(5);
						img = img.substring(0, img.length() - 1);
						int i = img.indexOf("\"");
						if (i > 0)
							img = img.substring(0, i);
						end = m.end();
						// k+=end;
						map = new HashMap<String, String>();

						// s1 = Html.fromHtml(s1).toString();
						s1 = getFixUrls(s1);

						map.put("text", s1);
						map.put("img", img);
						list.add(map);
					}
				}
			}

			if (end != 0) {
				html = html.substring(end, html.length());
			}

			if (!html.equalsIgnoreCase("")) {
				map = new HashMap<String, String>();
				// html = Html.fromHtml(html).toString();
				html = getFixUrls(html);

				map.put("text", html);
				list.add(map);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		;

	}
}
