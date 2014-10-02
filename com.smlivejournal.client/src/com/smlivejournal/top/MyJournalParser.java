package com.smlivejournal.top;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xmlrpc.XmlRpcClient;
import org.jsoup.parser.Parser;

import com.smlivejournal.settings.AuthThread;
import com.smlivejournal.settings.Settings;

import android.os.Bundle;
import android.text.Html;

public class MyJournalParser {
	private String html;
	private ArrayList<HashMap<String, String>> list;
	private Settings settings;

	public MyJournalParser(String html, ArrayList<HashMap<String, String>> list) {
		this.html = html;
		this.list = list;
	}

	public MyJournalParser(Settings settings,
			ArrayList<HashMap<String, String>> list) {
		this.settings = settings;
		this.list = list;
	}

	public Bundle getPostData() {
		Matcher m;
		String title = "";
		String sPost = "";
		String sAuth = "";
		String sGuid = "";

		Pattern pTitle = Pattern.compile("id=\"subject\" value=\".*?\"");
		Pattern pBody = Pattern
				.compile("<textarea .*? id=\"body\".*?>.*?</textarea>");
		Pattern pBody2 = Pattern.compile(">.*?</textarea>");
		Pattern pAuth = Pattern.compile("name=\"lj_form_auth\" value=\".*?\"");
		Pattern pGuid = Pattern.compile("guid\":\".*?\"");

		m = pTitle.matcher(html);
		if (m.find()) {
			title = m.group().substring(20);
			title = title.substring(0, title.length() - 1);
		}
		m = pBody.matcher(html);
		if (m.find()) {
			sPost = m.group();
			m = pBody2.matcher(sPost);
			if (m.find()) {
				sPost = m.group().substring(1);
				sPost = sPost.substring(0, sPost.length() - 11);
			}
		}

		m = pAuth.matcher(html);
		if (m.find()) {
			sAuth = m.group().substring(27);
			sAuth = sAuth.substring(0, sAuth.length() - 1);
		}

		title = Html.fromHtml(title).toString();
		sPost = Html.fromHtml(sPost).toString();
		m = pGuid.matcher(html);
		if (m.find()) {
			sGuid = m.group().substring(7);
			sGuid = sGuid.substring(0, sGuid.length() - 1);
		}

		Bundle response = new Bundle();
		response.putString("title", title);
		response.putString("body", sPost);
		response.putString("auth", sAuth);
		response.putString("guid", sGuid);
		return response;
	};

	public void fillMyJournal() {
		try {
			Vector p;
			Hashtable method_calls = new Hashtable();
			String lj_url = "http://www.livejournal.com/interface/xmlrpc";
			XmlRpcClient xmlrpc = new XmlRpcClient(lj_url);
			String password = AuthThread.getMD5(settings.getPwd());
			Vector<Hashtable<String, Comparable>> params = new Vector();

			method_calls.put("username", settings.getUserName());

			method_calls.put("password", settings.getPwd());
			method_calls.put("ver", "1");
			method_calls.put("clientversion", "WebServiceBook/0.0.1");
			java.util.Date now = new java.util.Date();
			// method_calls.put("truncate", AuthThread.iTrimWidgets);
			method_calls.put("prefersubject", false);
			method_calls.put("noprops", false);
			method_calls.put("notags", false);
			method_calls.put("selecttype", "lastn");
			method_calls.put("howmany", "30");
			method_calls.put("lineendings", "\n");
			method_calls.put("usejournal", settings.getUserName());
			method_calls.put("parseljtags", true);
			method_calls.put("trim_widgets", AuthThread.iTrimWidgets);
			method_calls.put("widgets_img_length", 0);

			method_calls.put("year", new Integer(now.getYear() + 1900));
			method_calls.put("mon", new Integer(now.getMonth() + 1));
			method_calls.put("day", new Integer(now.getDate()));
			method_calls.put("hour", new Integer(now.getHours()));
			method_calls.put("min", new Integer(now.getMinutes()));
			params.add(method_calls);
			Object result = xmlrpc.execute("LJ.XMLRPC.getevents", params);
			Vector v = ((Vector) ((Hashtable) result).get("events"));
			// ((Hashtable)(v.get(0)))
			for (int i = 0; i < v.size(); i++) {
				Hashtable item = ((Hashtable) (v.get(i)));
				String journalName = settings.getUserName();// (String)
															// item.get("journalname");
				String replyCount = ((Integer) item.get(("reply_count") + ""))
						.toString();
				// String posterName=(String)item.get("postername");
				String postUrl = (String) item.get("url");

				String body = "";
				String title = "";
				if (item.get("subject") != null) {
					if (item.get("subject") instanceof String) {
						title = (String) item.get("subject");
					} else {
						title = new String((byte[]) item.get("subject"));
					}
				}

				if (item.get("event") != null) {
					if (item.get("event") instanceof String) {
						body = (String) item.get("event");
					} else {
						body = new String((byte[]) item.get("event"));
					}
				}

				title = Html.fromHtml(title).toString();
				body = Html.fromHtml(body).toString();
				// 1400154916
				// new Date(Long.parseLong("1400154916")* (long)
				// 1000).toGMTString()()
				String sDate = ((String) item.get("eventtime"));
				// String logtime=(String)item.get("logtime");

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("body", body);
				map.put("subject", title);
				map.put("reply_count", replyCount);
				map.put("post_url", postUrl);
				map.put("journal", journalName);
				map.put("time", sDate);
				map.put("canedit", "1");
				list.add(map);
				/*
				 * map.put("body", post); map.put("time", time);
				 * map.put("reply_count", reply_count); map.put("subject",
				 * title); map.put("journal", journal); map.put("post_url",
				 * postLink);
				 */
			}

			// map.put("time", date);
			// map.put("reply_count", "");
			// map.put("body", text);
			// map.put("subject", title);
			// map.put("journal", "");
			// map.put("canedit", "1");
			// map.put("itemId", entryId);
			// map.put("post_url", url);
			//

		} catch (Exception e) {
			e.printStackTrace();
		}
		;

	}

	public void parse() {
		Matcher m;
		Matcher m2;
		String entries = "";
		String entry = "";

		Pattern pEntries = Pattern
				.compile("<ul class='b-editentries'>.*?</ul>");
		Pattern pEntry = Pattern.compile("<li id=\"entry_.*?</li>");
		Pattern pEntryId = Pattern.compile("<li id=\"entry_.*?\"");
		Pattern pHeader1 = Pattern.compile("<h2>.*?</h2>");
		Pattern pHeader2 = Pattern.compile("<a href.*?</a>");
		Pattern pHeader3 = Pattern.compile(">.*?<");
		Pattern pUrl = Pattern.compile("='.*?'");
		Pattern pDate = Pattern.compile("<p class='date'>.*?</p>");
		Pattern pText = Pattern.compile("<p class='date'>.*?</li>");
		Pattern pText1 = Pattern.compile("</p>.*?</li>");

		String entryId = "";
		String title = "";
		String url = "";
		String text = "";
		String date = "";
		String s = "";

		m = pEntries.matcher(html);
		if (m.find()) {
			entries = m.group();
			m = pEntry.matcher(entries);
			while (m.find()) {
				entryId = "";
				title = "";
				url = "";
				text = "";

				entry = m.group();
				m2 = pEntryId.matcher(entry);
				if (m2.find()) {
					entryId = m2.group().substring(14);
					entryId = entryId.substring(0, entryId.length() - 1);
				}
				s = "";
				m2 = pHeader1.matcher(entry);
				if (m2.find()) {
					m2 = pHeader2.matcher(m2.group());
					if (m2.find()) {
						s = m2.group();
						m2 = pHeader3.matcher(s);
						if (m2.find()) {
							title = m2.group().substring(1).trim();
							title = title.substring(0, title.length() - 1)
									.trim();
						}
					}
				}

				if (!s.equalsIgnoreCase("")) {
					m2 = pUrl.matcher(s);
					if (m2.find()) {
						url = m2.group().substring(2);
						url = url.substring(0, url.length() - 1);
					}
				}

				m2 = pDate.matcher(entry);
				if (m2.find()) {
					m2 = pHeader3.matcher(m2.group());
					if (m2.find()) {
						date = m2.group().substring(1);
						date = date.substring(0, date.length() - 1).trim();
					}
				}

				m2 = pText.matcher(entry);
				if (m2.find()) {
					m2 = pText1.matcher(m2.group());
					if (m2.find()) {
						text = m2.group().substring(4);
						text = text.substring(0, text.length() - 5).trim();
					}

				}
				/*
				 * body.setText(map.get("body"));
				 * timeView.setText(map.get("time"));
				 * replys.setText(map.get("reply_count") + "");
				 * title.setText(map.get("subject"));
				 * journal.setText(map.get("journal"));
				 */
				text = Html.fromHtml(text).toString();

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("time", date);
				map.put("reply_count", "");
				map.put("body", text);
				map.put("subject", title);
				map.put("journal", "");
				map.put("canedit", "1");
				map.put("itemId", entryId);
				map.put("post_url", url);
				list.add(map);
			}
		}

	}
}
