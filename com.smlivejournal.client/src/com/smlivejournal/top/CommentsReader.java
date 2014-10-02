package com.smlivejournal.top;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.xmlrpc.XmlRpcClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.smlivejournal.xmlrpc.XMLRPCClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.smlivejournal.client.HttpConn;
import com.smlivejournal.client.R;
import com.smlivejournal.settings.AuthThread;
import com.smlivejournal.settings.Settings;

public class CommentsReader implements OnScrollListener {
	private String postUrl;
	private ArrayList<HashMap<String, String>> list;
	private int pageNo = 0;
	private int pageCount = 0;
	private int commentCount = 0;
	private boolean cmRead = false;
	private PostReader postReader;
	private Context context;

	private int visibleItemCount = 0;
	private boolean isLoading = false;
	private int prevTopItem = 0;
	private RelativeLayout ljComments;
	private RelativeLayout laProgress;
	private boolean commentsSetDown = false;
	private Handler commentsReadHandler;
	private HashMap<String, String> postInfo;
	private Settings settings;
	private LJPost ljPost;
	private ProgressDialog pg;

	public CommentsReader(String postUrl,
			ArrayList<HashMap<String, String>> list, Context context,
			RelativeLayout ljComments, RelativeLayout laProgress) {
		this.postUrl = postUrl;
		this.list = list;
		this.context = context;
		this.ljComments = ljComments;
		this.laProgress = laProgress;
		pageNo = 1;
		postInfo = new HashMap<String, String>();
	}

	public void setCommentsHandler(Handler commentsHandler) {
		this.commentsReadHandler = commentsHandler;

	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	private String getUserName(String url) {
		String s = "";
		Pattern p = Pattern.compile("//.*?[.]");
		Matcher m = p.matcher(url);
		if (m.find()) {
			s = m.group().substring(2);
			s = s.substring(0, s.length() - 1);
		}
		// http://tema.livejournal.com/1670849.html
		return s;
	}

	private String getPostID(String url) {
		String s = "";
		url = url.replace("http://", "");
		Pattern p = Pattern.compile("/.*?[.]");
		Matcher m = p.matcher(url);
		if (m.find()) {
			s = m.group().substring(1);
			s = s.substring(0, s.length() - 1);
		}
		int postId = Integer.parseInt(s);
		return postId + "";
		// int anum=(int) (postId - Math.floor(postId/256)*256);
		// int internalId = ((postId-anum)/256);
		// return internalId+"";

		/*
		 * $anum = $public_itemid - floor($public_itemid / 256) * 256; $itemid =
		 * ($public_itemid - $anum) / 256;
		 */
	}

	public void parse2(String postUrl) {
		String itemId = getPostID(postUrl);
		String userName = getUserName(postUrl);
		try {
			Vector p;
			// Hashtable method_calls = new Hashtable();
			HashMap<String, Object> method_calls = new HashMap<String, Object>();

			String lj_url = "http://www.livejournal.com/interface/xmlrpc";
			XmlRpcClient xmlrpc = new XmlRpcClient(lj_url);
			// String password = getMD5(settings.getPwd());
			Vector<Hashtable<String, Comparable>> params = new Vector();

			if ((settings != null) && (!settings.getPwd().equals(""))) {
				method_calls.put("username", settings.getUserName());
				method_calls.put("password", settings.getPwd());
			} else {
				method_calls.put("auth_method", "noauth");
			}

			method_calls.put("ver", "1");
			method_calls.put("clientversion", "WebServiceBook/0.0.1");

			java.util.Date now = new java.util.Date();

			method_calls.put("ditemid", itemId);
			method_calls.put("journal", userName);
			method_calls.put("expand_strategy", "mobile_thread");
			// method_calls.put("expand_all", 0);
			// method_calls.put("expand_level", 1);
			method_calls.put("page", "1");

			// expand_strategy=mobile_thread
			method_calls.put("year", new Integer(now.getYear() + 1900));
			method_calls.put("mon", new Integer(now.getMonth() + 1));
			method_calls.put("day", new Integer(now.getDate()));
			method_calls.put("hour", new Integer(now.getHours()));
			method_calls.put("min", new Integer(now.getMinutes()));
			// params.add(method_calls);

			URI uri = URI.create("http://www.livejournal.com/interface/xmlrpc");
			XMLRPCClient client = new XMLRPCClient(uri);
			Object[] paramsXMLRpc = { method_calls };
			String result = client.callExStr("LJ.XMLRPC.getcomments",
					paramsXMLRpc);// params);

			XmlPullParserFactory xf = XmlPullParserFactory.newInstance();
			XmlPullParser xp = xf.newPullParser();
			xp.setInput(new StringReader(result));
			int i = 0;
			while (xp.getEventType() != XmlPullParser.END_DOCUMENT) {
				switch (xp.getEventType()) {
				case XmlPullParser.START_TAG: {// xp
					i = xp.getAttributeCount();
					// xp.getAttributeValue(0);
				}
				case XmlPullParser.END_TAG: {
					
				}
				}
				xp.next();
			}

			Log.d(result.toString(), "");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void parse3(String postUrl) {
		String itemId = getPostID(postUrl);
		String userName = getUserName(postUrl);

		try {
			Vector p;
			Hashtable method_calls = new Hashtable();
			String lj_url = "http://www.livejournal.com/interface/xmlrpc";
			com.smlivejournal.xmlrpc.XMLRPCClient xmlrpc = new com.smlivejournal.xmlrpc.XMLRPCClient(
					lj_url);
			// XmlRpcClient xmlrpc = new XmlRpcClient(lj_url);
			// String password = getMD5(settings.getPwd());
			Vector<Hashtable<String, Comparable>> params = new Vector();

			if ((settings != null) && (!settings.getPwd().equals(""))) {
				method_calls.put("username", settings.getUserName());
				method_calls.put("password", settings.getPwd());
			} else {
				method_calls.put("auth_method", "noauth");
			}

			method_calls.put("ver", "1");
			method_calls.put("clientversion", "WebServiceBook/0.0.1");

			java.util.Date now = new java.util.Date();

			method_calls.put("ditemid", itemId);
			method_calls.put("journal", userName);
			// method_calls.put("expand_strategy", "mobile_thread");
			method_calls.put("expand_all", 1);
			// method_calls.put("expand_level", 1);

			// method_calls.put("page", "1");

			// expand_strategy=mobile_thread
			method_calls.put("year", new Integer(now.getYear() + 1900));
			method_calls.put("mon", new Integer(now.getMonth() + 1));
			method_calls.put("day", new Integer(now.getDate()));
			method_calls.put("hour", new Integer(now.getHours()));
			method_calls.put("min", new Integer(now.getMinutes()));
			params.add(method_calls);

			Vector<HashMap> v = new Vector();
			// HashMap t=new HashMap(method_calls);

			// v.add(t);

			// Object[] data=new Object[1];
			// data[0]=t;

			String result = xmlrpc.callStr("LJ.XMLRPC.getcomments",
					method_calls); // xmlrpc.execute("LJ.XMLRPC.getcomments",
									// params);

			XmlPullParserFactory pullFactory = XmlPullParserFactory
					.newInstance();
			pullFactory.setNamespaceAware(true);
			XmlPullParser parser = pullFactory.newPullParser();
			StringReader sr = new StringReader(result);
			parser.setInput(new StringReader(result));
			int evtType = parser.getEventType();
			String s = "";
			while (evtType != XmlPullParser.END_DOCUMENT) {
				switch (evtType) {
				case XmlPullParser.TEXT: {
					s = parser.getText();
					// parser.getAttributeCount()

					break;
				}
				case XmlPullParser.START_TAG: {

					break;
				}
				case XmlPullParser.END_TAG: {

					break;
				}
				}
				evtType = parser.next();
			}

			// result = xmlrpc.call("LJ.XMLRPC.getcomments", params.toArray());
			// // xmlrpc.execute("LJ.XMLRPC.getcomments", params);

			// String s=new String((byte
			// [])((Hashtable)((Vector)((Hashtable)result).get("comments")).get(0)).get("body"));

			// xmlrpc.\
			// result.getClass()
			Log.d(result.toString(), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		;

	}

	private String getHtml(String urlStr) {
		try {
			java.net.CookieManager cm = new java.net.CookieManager();
			java.net.CookieHandler.setDefault(cm);

			/*
			 * URL url = new URL(urlStr); HttpURLConnection conn =
			 * (HttpURLConnection) url.openConnection(); String cookieString =
			 * ""; conn.setUseCaches(false); // cookieString =
			 * cookieString.replace("//1", ""); if
			 * (!cookieString.equalsIgnoreCase(""))
			 * conn.setRequestProperty("Cookie", cookieString);
			 */

			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(urlStr);
			HttpResponse response = client.execute(get);

			InputStream isr = response.getEntity().getContent();// conn.getInputStream();
			InputStreamReader reader = new InputStreamReader(isr);
			BufferedReader breader = new BufferedReader(reader);
			StringBuilder sb = new StringBuilder();
			String s = "";
			while ((s = breader.readLine()) != null) {
				sb.append(s);

			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public void parse1(String postUrl) {
		 //parse2(postUrl);
		
		 
		 
		//HttpConn conn = new HttpConn();
		String s = postUrl;
		String html = "";
	 	if (!s.contains("format=light"))
		 	s = s + "?format=light";
		
		//s ="http://lenta.ru";

		// conn.htmlByUrl(se archUrl)
		// String html = conn.htmlByUrl(s, false);
		Random r = new Random();
		String rs = "&randomNumber=" + r.nextInt();
		//s = s + rs;
		AuthThread th = new AuthThread(null, AuthThread.iGetHtml,
				settings.getUserName(), settings.getPwd());
		th.setContext(context);
		th.setSettings(settings);
		boolean cmLoaded=false;
//		if (!settings.getPwd().equalsIgnoreCase("")) {
//			if (!settings.getIsLoggedIn()) {
//				th.tryToLogin();
//			}
//		}
//	
		
	//	if (settings.getIsLoggedIn()){
	  	html = th.getHtmlByUrl(s,false);
	//		cmLoaded=true;
		//}
		
	//	if (!cmLoaded)
		//html = th.getHtmlByUrl(s, false);
		
		Pattern pCommentsStart = Pattern.compile("Site.page =.*?[}];"); // ("<div id=\"comments\".*?\"ljqrtbottomcomment\"");
		Pattern pImg = Pattern.compile("<img.*?>");
		Pattern pImg2 = Pattern.compile("src=\".*?\"");
		if (pageCount == 0) {
			fillPageCount(html);
		}

		s = "";
		String userName = "";
		String threadId = "";
		String parentId = "";
		String comment = "";
		String uPic = "";
		String date = "";
		String level = "";
		String expandHref = "";
		String talkId = "";
		String dItemId = "";
		String journalName = "";
		// html.substring(56336)
		// html.indexOf("\"replycount")

		Matcher m = pCommentsStart.matcher(html);
		if (m.find()) {
			s = m.group().substring(11);
		}
		try {
			JSONObject o = new JSONObject(s);
			if (postInfo.isEmpty()) {
				// {"journal":"green_2005","poster":"green_2005","title":"frr","ditemid":2487}
				JSONObject entry = o.optJSONObject("entry");
				if (entry != null) {
					postInfo.put("journal", entry.getString("journal"));
					postInfo.put("poster", entry.getString("poster"));
					postInfo.put("title", entry.getString("title"));
					postInfo.put("ditemid", entry.getInt("ditemid") + "");
					dItemId = entry.getInt("ditemid") + "";
					journalName = entry.getString("journal");
				}
			}
			JSONArray comments = o.optJSONArray("comments");
			for (int i = 0; i < comments.length(); i++) {
				JSONObject item = comments.getJSONObject(i);

				if (item.isNull("loaded")) {
					continue;
				}

				int loaded = item.getInt("loaded");
				if (loaded == 0)
					continue;
				userName = item.getString("uname");
				uPic = item.getString("userpic");
				threadId = item.getString("thread");
				parentId = item.getString("parent");
				date = item.getString("ctime");
				level = item.getString("level");
				talkId = item.getString("talkid");

				expandHref = "";
				if (level.equalsIgnoreCase("1")) {
					JSONArray actions = item.optJSONArray("actions");
					for (int j = 0; j < actions.length(); j++) {
						JSONObject action = actions.getJSONObject(j);
						String name = action.getString("name");
						if (name.equalsIgnoreCase("expandchilds")) {
							expandHref = action.getString("href");
							break;
						}
					}
				}

				comment = item.getString("article");

				HashMap<String, String> map = new HashMap<String, String>();

				m = pImg.matcher(comment);
				int end = 0;
				int k = 0;
				while (m.find()) {
					if (m.start() >= 0) {
						Matcher m2 = pImg2.matcher(m.group());
						if (m2.find()) {
							String s1 = comment.substring(end, m.start());
							String img = m2.group(0).substring(5);
							img = img.substring(0, img.length() - 1);
							int j = img.indexOf("\"");
							if (j > 0)
								img = img.substring(0, j);
							end = m.end();
						//	s1 = Html.fromHtml(s1).toString();
							map.put("commentText" + k, s1);
							map.put("img" + k, img);
							k++;
						}
					}
				}

				if (end != 0) {
					// comment.substring(start, end)
					comment = comment.substring(end, comment.length());
				}

				if (!comment.equalsIgnoreCase("")) {
					//comment = Html.fromHtml(comment).toString();
					map.put("commentText" + k, comment);
				}

				map.put("userName", userName);
				map.put("id", threadId);
				map.put("level", level);
				map.put("upic", uPic);
				map.put("parentID", parentId);
				map.put("date", date);
				map.put("expandHref", expandHref);
				map.put("threadId", threadId);
				map.put("talkId", talkId);
				map.put("ditemid", dItemId);
				map.put("journal", journalName);
				list.add(map);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fillPageCount(String html) {
		Pattern pcommentCount = Pattern
				.compile("span class=\"js-amount\">.*?</span>");
		Pattern pcommentCount2 = Pattern.compile("  .*? ");
		Matcher m = pcommentCount.matcher(html);
		if (m.find()) {
			m = pcommentCount2.matcher(m.group());
			if (m.find()) {
				String s = m.group().trim();
				commentCount = Integer.parseInt(s);
			}
		}
		// Pattern pPages = Pattern
		// .compile("<ul class=\"b-pager-pages\">.*?</ul>");
		// Pattern pPages2 = Pattern.compile("comments\">.*?</a>");

		// Pattern pPages3 = Pattern.compile("page=.*?#comments");
		Pattern pPages3 = Pattern.compile("page=[0-9]+.*?#comments");
		Pattern pPages4 = Pattern.compile("[0-9]+");

		String s = "";
		m = pPages3.matcher(html);
		Matcher m2;
		while (m.find()) {
			m2 = pPages4.matcher(m.group());
			if (m2.find()) {
				s = m2.group();
				try {
					int i = Integer.parseInt(s);
					pageCount=Math.max(i, pageCount);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void addComment(Bundle commentInfo, final LJPost ljPost) {
		String body = commentInfo.getString("body");
		this.ljPost = ljPost;
		Handler mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (pg != null)
					pg.dismiss();
				ljPost.refreshComments();
			}
		};

		AuthThread th = new AuthThread(mainHandler, AuthThread.iAddComment);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("body", body);
		// String
		String journal = "";
		String poster = "";
		String title = "";
		String ditemid = "";
		if (postInfo.containsKey("journal"))
			journal = postInfo.get("journal");

		if (commentInfo.containsKey("journal"))
			journal = commentInfo.getString("journal");

		if (postInfo.containsKey("poster"))
			poster = postInfo.get("poster");

		if (commentInfo.containsKey("poster"))
			poster = commentInfo.getString("poster");

		if (postInfo.containsKey("ditemid"))
			ditemid = postInfo.get("ditemid");

		if (commentInfo.containsKey("ditemid"))
			ditemid = commentInfo.getString("ditemid");

		map.put("journal", journal);
		map.put("poster", poster);
		map.put("title", postInfo.get("title"));
		map.put("ditemid", ditemid);
		map.put("posturl", postUrl);
		map.put("parenttalkid", "");

		/*
		 * postInfo.put("journal",entry.getString("journal"));
		 * postInfo.put("poster", entry.getString("poster"));
		 * postInfo.put("title",entry.getString("title"));
		 * postInfo.put("ditemid", entry.getInt("ditemid")+"");
		 */
		pg = new ProgressDialog(ljPost);
		pg.setCancelable(false);
		pg.setTitle("");
		pg.setMessage(context.getResources().getString(
				com.smlivejournal.client.R.string.please_wait));
		pg.show();
		th.setCommentData(map);
		th.setSettings(settings);
		th.start();
	}

	public void readComments(ArrayList<HashMap<String, String>> cmList,
			Handler mainHandler, Context context, String postUrl) {
		cmRead = true;
		postReader = new PostReader(cmList, mainHandler, context, postUrl,
				MainTop.readComments, settings);
		postReader.setCommentsReader(this);
		postReader.start();
	}

	public boolean commentsRead() {
		return cmRead;
	}

	public int getRc() {
		return 0;
	}

	public String getEm() {
		return "";
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.visibleItemCount = visibleItemCount;
		int i = firstVisibleItem + visibleItemCount;
		if ((totalItemCount > 0) && (i == totalItemCount) && (!isLoading)
				 && (pageNo < pageCount)) {
			isLoading = true;
			loadMoreData(visibleItemCount, firstVisibleItem, totalItemCount);
		}
		prevTopItem = firstVisibleItem;
	}

	private void loadMoreData(int visibleItemCount, int firstVisible,
			int totalItemCount) {
		setCommentsLoadingProgress();
		loadNext();
	}

	private void loadNext() {
		String s = postUrl + "?page=" + String.valueOf(++pageNo)
				+ "&format=light";
		readComments(list, commentsReadHandler, context, s);
	}

	private void setCommentsLoadingProgress() {
		if (!commentsSetDown) {
			ljComments.removeView(laProgress);
			RelativeLayout laBottom = (RelativeLayout) ljComments
					.findViewById(R.id.lvCommentsBottom);
			laBottom.addView(laProgress);
			commentsSetDown = true;
		}
		laProgress.setVisibility(View.VISIBLE);
	}

	public void setCommentsLoadingdone() {
		isLoading = false;
		laProgress.setVisibility(View.GONE);

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

}
