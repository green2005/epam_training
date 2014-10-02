package com.smlivejournal.settings;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.RequestDirector;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRequestDirector;
import org.apache.http.impl.client.RoutedRequest;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.util.EntityUtils;
import org.apache.xmlrpc.XmlRpcClient;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.smlivejournal.friends.FriendLineParser;
import com.smlivejournal.top.MyJournalParser;
import com.smlivejournal.top.PostParser;
import com.smlivejournal.userblog.Tag.EAccess;
import com.smlivejournal.userblog.Tag.EComments;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.mime.MultipartEntityBuilder;

public class AuthThread extends Thread {
	public static final int iGetHtml = 1;
	public static final int iTryToLogin = 2;
	public static final int iGetFriendLine = 3;
	public static final int iGetMemories = 4;
	public static final int iGetMyJournal = 5;
	public static final int iGetPostData = 6;
	public static final int iPost = 7;
	public static final int iEditPost = 9;
	public static final int iPostImage = 8;
	public static final int iAddComment = 10;
	public static final int iReply = 11;
	public static final int iAddToFav = 12;
	public static final int iGetImage  = 13; 
	public static final int iGetFriendsGroups = 14;

	public static int iTrimWidgets = 250;

	private Handler mainHandler;
	private String userName;
	private String pwd;
	private int reason;
	private ArrayList<HashMap<String, String>> list;
	private AfterLogin afterLogin;
	private Context context;
	private String hresponse = "";
	private String cookie = "";
	private Settings settings;
	private String url = "";
	private List<NameValuePair> postData;
	private String postFileName = "";
	private HttpPost imagePost;
	private String ljAuthGuid = "";
	private String title = "";
	private String body = "";
	private String postId = "";
	private String ljUserName = "";
	private HashMap<String, String> commentData;
	private String filter="";
	private com.smlivejournal.userblog.Tag postTagInfo=null;

	public AuthThread(Handler mainHandler, int reason, String userName,
			String pwd) {
		this.mainHandler = mainHandler;
		this.reason = reason;
		this.userName = userName;
		this.pwd = pwd;
	}

	public AuthThread(Handler mainHandler, int reason) {
		this.mainHandler = mainHandler;
		this.reason = reason;
	}
	
	public void setTagInfo(com.smlivejournal.userblog.Tag tagInfo){
		this.postTagInfo=tagInfo;
	} 

	public void setUrl(String url) {
		this.url = url + "";
	}
	
	public void setFilter(String filter){
		this.filter=filter;
	}

	public void setPostData(String postUrl, String title, String body) {
		// this.postData = postData;
		this.url = postUrl;
		this.title = title;
		this.body = body;
	}

	public void setPostData(String postId, String ljUserName) {
		this.postId = postId;
		this.ljUserName = ljUserName;
	}

	public void setLJAuthGuid(String ljAuthGuid) {
		this.ljAuthGuid = ljAuthGuid;
	}

	public void setPostFileName(String postFileName) {
		this.postFileName = postFileName;
	}

	public void setPostData(HttpPost post, String postUrl) {
		this.imagePost = post;
		this.url = postUrl;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
		if ((this.pwd==null)){
		this.pwd = settings.getPwd();
		this.userName = settings.getUserName();
		}
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setCommentData(HashMap<String, String> map) {
		commentData = map;
	}

	public void setList(ArrayList<HashMap<String, String>> list) {
		this.list = list;
	}

	public void setOnAfterLogin(AfterLogin onAfterLogin) {
		this.afterLogin = onAfterLogin;
	}

	private DefaultHttpClient sslClient(HttpClient client) {
		try {
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				@Override
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public void checkServerTrusted(
						java.security.cert.X509Certificate[] chain,
						String authType) throws CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}
			};
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new MySSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = client.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(ccm, client.getParams());
		} catch (Exception ex) {
			return null;
		}
	}

	public static DefaultHttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}
	
	public String getHtmlByUrl3(String searchUrl,boolean useCookies) {
		try{
			DefaultHttpClient client = new DefaultHttpClient(); // getNewHttpClient(); //getNewHttpClient(); 
			//client = sslClient(client);
			HttpGet get = new HttpGet(searchUrl);
			HttpParams params=new BasicHttpParams();
			params.setParameter("Host","www.livejournal.com");
			params.setParameter("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
			params.setParameter("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			params.setParameter("Accept-Language",
					"ru,en-us;q=0.7,en;q=0.3");
			params.setParameter("Connection", "keep-alive");
			
			get.addHeader("Host","www.livejournal.com");
			get.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
			get.addHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			get.addHeader("Accept-Language",
					"ru,en-us;q=0.7,en;q=0.3");
			get.addHeader("Connection", "keep-alive");
			
		//	if (useCookies){
			//	params.setParameter("Cookie",settings.getCookie());
			//	get.addHeader("Cookie", settings.getCookie());
			//	client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS,true);
			 	//client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,true);
			//	client.getParams().setParameter(ClientPNames.MAX_REDIRECTS,300);
			//	client.getParams().setParameter(ClientPNames.REJECT_RELATIVE_REDIRECT,true);
			//}
			
			get.setParams(params);
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			String responseText = EntityUtils.toString(entity);
			return responseText;}
			catch (Exception e){
				e.printStackTrace();
				return "";
			}
	}
	
	public String getHtmlByUrl2(String urlStr){
		URL url;
		try {
			url = new URL(urlStr);
			
		HttpURLConnection conn = (HttpURLConnection) url
				.openConnection();
		
		conn.setConnectTimeout(30000);
		conn.setReadTimeout(30000);
		conn.setInstanceFollowRedirects(true);
		//InputStream is = conn.getInputStream();
		conn.addRequestProperty("Host", "www.livejournal.com");
		conn.addRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
		conn.addRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.addRequestProperty("Accept-Language",
				"ru,en-us;q=0.7,en;q=0.3");
		conn.addRequestProperty("Connection", "keep-alive");
		conn.addRequestProperty("Cookie", settings.getCookie());
		
		BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String s;
		StringBuilder sb=new StringBuilder();
		while((s=br.readLine())!=null){
			sb.append(s);
		}
		 return sb.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
			
	}

	public String getHtmlByUrl(String urlStr, boolean useCookies) {
		try{
		DefaultHttpClient client = getNewHttpClient(); 
		client = sslClient(client);
		HttpGet get = new HttpGet(urlStr);
		HttpParams params=new BasicHttpParams();
		params.setParameter("Host", "www.livejournal.com");
		params.setParameter("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
		params.setParameter("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		params.setParameter("Accept-Language",
				"ru,en-us;q=0.7,en;q=0.3");
		params.setParameter("Connection", "keep-alive");
		if (useCookies){
			params.setParameter("Cookie",settings.getCookie());
			get.addHeader("Cookie", settings.getCookie());
		//	client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS,true);
		 	//client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,true);
		//	client.getParams().setParameter(ClientPNames.MAX_REDIRECTS,300);
		//	client.getParams().setParameter(ClientPNames.REJECT_RELATIVE_REDIRECT,true);
		}
		
		get.setParams(params);
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		String responseText = EntityUtils.toString(entity);
		return responseText;}
		catch (Exception e){
			e.printStackTrace();
			return "";
		}
	}

	 private Bundle getFriendGroups(){
		 try{
			Bundle res=new Bundle(); 
		 	Vector p;
			Hashtable method_calls = new Hashtable();
			String lj_url = "http://www.livejournal.com/interface/xmlrpc";
			XmlRpcClient xmlrpc = new XmlRpcClient(lj_url);
			String password = getMD5(pwd);
			Vector<Hashtable<String, Comparable>> params = new Vector();

			method_calls.put("username", userName);
			method_calls.put("password", pwd);
			method_calls.put("ver", "1");
			method_calls.put("clientversion", "WebServiceBook/0.0.1");
			java.util.Date now = new java.util.Date();
//			method_calls.put("year", new Integer(now.getYear() + 1900));
//			method_calls.put("mon", new Integer(now.getMonth() + 1));
//			method_calls.put("day", new Integer(now.getDate()));
//			method_calls.put("hour", new Integer(now.getHours()));
//			method_calls.put("min", new Integer(now.getMinutes()));
			params.add(method_calls);
			Object result = xmlrpc.execute("LJ.XMLRPC.getfriendgroups", params);
			res.putSerializable("friendgroups", (Serializable) result);
			return res;
			
		 } catch(Exception e){e.printStackTrace();}
		return null;
		 }

	private boolean tryToLogin2() {
		try {
			Vector p;
			Hashtable method_calls = new Hashtable();
			String lj_url = "http://www.livejournal.com/interface/xmlrpc";
			XmlRpcClient xmlrpc = new XmlRpcClient(lj_url);
			String password = getMD5(pwd);
			Vector<Hashtable<String, Comparable>> params = new Vector();

			method_calls.put("username", userName);

			method_calls.put("password", pwd);
			method_calls.put("ver", "1");
			method_calls.put("clientversion", "WebServiceBook/0.0.1");
			java.util.Date now = new java.util.Date();
			// method_calls.put("event", body);
			// method_calls.put("lineendings", "\n");
			method_calls.put("subject", title);
			method_calls.put("year", new Integer(now.getYear() + 1900));
			method_calls.put("mon", new Integer(now.getMonth() + 1));
			method_calls.put("day", new Integer(now.getDate()));
			method_calls.put("hour", new Integer(now.getHours()));
			method_calls.put("min", new Integer(now.getMinutes()));
			params.add(method_calls);

			Object result = xmlrpc.execute("LJ.XMLRPC.login", params);
			// Log.d("", result.toString());
			int userId = -1;
			if (((Hashtable) result).containsKey("userid")) {
				userId = ((Integer) ((Hashtable) result).get("userid"));
			}
			if (userId > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public void tryToLogin() {
		 
		HashMap<String, String> mResponse = new HashMap<String, String>();
		try {
			String ljLoginUrl = context.getResources().getString(
					com.smlivejournal.client.R.string.ljLoginUrl);
			ljLoginUrl = "https://www.livejournal.com/login.bml";
			// /ljLoginUrl = "https://www.livejournal.com/login.bml?nojs=1";

			// POSTDATA=mode=login&user=green_2005&password=Azerebag1&_submit=%D0%92%D1%85%D0%BE%D0%B4+
			String postData = "mode=login&user="
					+ URLEncoder.encode(userName, "utf-8") + "&password="
					+ URLEncoder.encode(pwd, "utf-8");// +"&_submit=%D0%92%D1%85%D0%BE%D0%B4+";
 
			DefaultHttpClient client = getNewHttpClient(); // new
															// DefaultHttpClient();
			client = sslClient(client); 
			HttpPost post1 = new HttpPost(ljLoginUrl);
			List<NameValuePair> names = new ArrayList<NameValuePair>(2);
			names.add(new BasicNameValuePair("Host", "www.livejournal.com"));
			names.add(new BasicNameValuePair("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0"));
			names.add(new BasicNameValuePair("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
			names.add(new BasicNameValuePair("Accept-Language",
					"ru,en-us;q=0.7,en;q=0.3"));
			names.add(new BasicNameValuePair("Referer",
					"https://www.livejournal.com/login.bml"));
			names.add(new BasicNameValuePair("Connection", "keep-alive"));
			names.add(new BasicNameValuePair("Content-Type",
					"application/x-www-form-urlencoded"));

			names.add(new BasicNameValuePair("user", URLEncoder
					.encode(userName)));
			names.add(new BasicNameValuePair("password", URLEncoder.encode(pwd)));
			names.add(new BasicNameValuePair("action%3Alogin", "Log+in"));
			post1.setEntity(new UrlEncodedFormEntity(names));
			HttpResponse response = client.execute(post1);

			HttpEntity entity = response.getEntity();
			String responseText = EntityUtils.toString(entity);
			cookie = "";
			hresponse = response.getStatusLine().toString();

			boolean loggedIn = false;
			// settings.getCookies().clear();
			for (Cookie c : client.getCookieStore().getCookies()) {
				// settings.getCookies().add(c);
				String cookieString = c.getName() + "=" + c.getValue()
						+ "; domain=" + c.getDomain();
				CookieManager.getInstance().setCookie(c.getDomain(),
						cookieString);
				cookie = cookie + c.getName() + "=" + c.getValue() + ";";
			}

			CookieSyncManager.getInstance().sync();

			if (cookie.indexOf("ljloggedin") != -1) {
				loggedIn = true;
			}

			// client.getConnectionManager().shutdown();
			if (!loggedIn) {
				cookie = "";
			}

			settings.setIsLoggedIn(loggedIn, cookie);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (afterLogin != null) {
			afterLogin.onAfterLogin(hresponse, cookie);
		}

	}

	public static String getMD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}
	
	private void getImage(){
		String url=  "http://ic.pics.livejournal.com/green_2005/67678836/10971/10971_original.jpg";

		DefaultHttpClient client = getNewHttpClient(); // new
		String crp = "";
	    //File sourceFile = new File(postFileName);
		HttpGet hg = new HttpGet("http://pics.livejournal.com/interface/simple");
		hg.addHeader("Host", "pics.livejournal.com");
		hg.addHeader("X-FB-User", settings.getUserName().replace("-", "_"));
		hg.addHeader("X-FB-Mode", "GetChallenge");
		try {
			HttpResponse r = client.execute(hg);
			//FileInputStream fisr = new FileInputStream(sourceFile);
			HttpEntity e = r.getEntity();
			InputStream isr = e.getContent();
			InputStreamReader isReader = new InputStreamReader(isr);
			BufferedReader br = new BufferedReader(isReader);
			StringBuilder sb = new StringBuilder();
			String s = "";
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
			s = sb.toString();
			String chal = "";
			// <Challenge>0DO9fogYt9vpCMgPSF738L1lTJauL3ChtFyHoUFPEeeZ3iKNsg-1399320000-26c84a275ddb4bcb40fbce60d4a377ba</Challenge>
			Pattern p = Pattern.compile("<Challenge>.*?</Challenge>");
			Matcher m = p.matcher(s);
			if (m.find()) {
				chal = m.group().substring(11);
				chal = chal.substring(0, chal.length() - 12);
			}

			String passHash = getMD5(settings.getPwd());
			crp = "crp:" + chal + ":" + getMD5(chal + passHash);
			
			if (!settings.getIsLoggedIn()) {
				userName = settings.getUserName();
				pwd = settings.getPwd();
				tryToLogin();
				if (!settings.getIsLoggedIn()) {
					return;
				}
			}
			
			HttpGet get = new HttpGet(url);
			HttpParams params=new BasicHttpParams();
			params.setParameter("Host", "ic.pics.livejournal.com");
			params.setParameter("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
			params.setParameter("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			params.setParameter("Accept-Language",
					"ru,en-us;q=0.7,en;q=0.3");
			params.setParameter("Connection", "keep-alive");
			
			params.setParameter("Cookie", "Cookie=ljuniq=leB5g78dZJaoC5H%3A1328033518%3Apgstats0; __utma=48425145.206042005.1403958664.1403963558.1404153133.3; __utmz=48425145.1403958664.1.1.utmcsr=livejournal.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __qca=P0-241956982-1328033519463; __gads=ID=f03e1ad0ff13a8c6:T=1345488076:S=ALNI_MYuNg7bDvrG-lrEB6hfkRHtLGF30A; xtvrn=$528851$531804$; xtan=2-67678836; xtant=1; xtidc=131106231201435602; _montblanc=c6%3dzb0%26bj%3dsbesh%253atrv%253amobsv%253asp_fit%253asmrtph%253amobin%26y1%3dlhf%253asxw%253alm6%253aqs2%253a65l%264i%3dr1o; _ga=GA1.2.206042005.1403958664; langpref=ru/1404163457; __utmv=48425145.|1=ljuser=TODO%3Aloggedin%2Fanonymous%2Fyou=1; ljident=2852394412.20480.0000; __utmb=48425145.47.9.1404161542648; __utmc=48425145; ljdomsess.ic.pics=v1:u67678836:s685:t1404162000:ge55fbbb2d63c55adc597e58102e7464e76e8bb01//1; ljloggedin=v2:u67678836:s685:t1404163457:g27822b04ac7f25ee1327b203d1b4e7d61aa659d4; ljsession=v1:u67678836:s685:t1404162000:gf185877fc4e5ed29d824c5463175c43976567cdf//1");
				  get.addHeader("Cookie", "Cookie=ljuniq=leB5g78dZJaoC5H%3A1328033518%3Apgstats0; __utma=48425145.206042005.1403958664.1403963558.1404153133.3; __utmz=48425145.1403958664.1.1.utmcsr=livejournal.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __qca=P0-241956982-1328033519463; __gads=ID=f03e1ad0ff13a8c6:T=1345488076:S=ALNI_MYuNg7bDvrG-lrEB6hfkRHtLGF30A; xtvrn=$528851$531804$; xtan=2-67678836; xtant=1; xtidc=131106231201435602; _montblanc=c6%3dzb0%26bj%3dsbesh%253atrv%253amobsv%253asp_fit%253asmrtph%253amobin%26y1%3dlhf%253asxw%253alm6%253aqs2%253a65l%264i%3dr1o; _ga=GA1.2.206042005.1403958664; langpref=ru/1404163457; __utmv=48425145.|1=ljuser=TODO%3Aloggedin%2Fanonymous%2Fyou=1; ljident=2852394412.20480.0000; __utmb=48425145.47.9.1404161542648; __utmc=48425145; ljdomsess.ic.pics=v1:u67678836:s685:t1404162000:ge55fbbb2d63c55adc597e58102e7464e76e8bb01//1; ljloggedin=v2:u67678836:s685:t1404163457:g27822b04ac7f25ee1327b203d1b4e7d61aa659d4; ljsession=v1:u67678836:s685:t1404162000:gf185877fc4e5ed29d824c5463175c43976567cdf//1");
				  //							ljmastersession=v2:u67678836:s689:aNugZiZWjJ7:g9d169754e4dc066e3d2f51c1a4e0acfcec20f031//1;ljloggedin=v2:u67678836:s689:t1404166827:gf04d26f41fcba056bac3fc29d47fd45f8cbc3af9;ljloggedin=v2:u67678836:s689:t1404166827:gf04d26f41fcba056bac3fc29d47fd45f8cbc3af9;langpref=ru/1404166827;langpref=ru/1404166827;ljsession=v1:u67678836:s689:t1404165600:gad502eb24fb3a8f87c2752598ee3065ba86c0290//1;ljsession=v1:u67678836:s689:t1404165600:gad502eb24fb3a8f87c2752598ee3065ba86c0290//1;ljident=2869171628.20480.0000;
			//params.setParameter("Cookie",settings.getCookie());
			//get.addHeader("Cookie", settings.getCookie());
			
			get.setParams(params);
 
			 
			//put.addHeader("X-FB-Mode", "UploadPic");
//			URL imageUrl=new URL(url);
//			HttpURLConnection conn = (HttpURLConnection) (imageUrl.openConnection());
//			conn.setRequestProperty("Host", "pics.livejournal.com");
//			conn.setRequestProperty("X-FB-User", settings.getUserName().replace("-", "_"));
//			conn.setRequestProperty("X-FB-Auth", crp);
//			conn.setConnectTimeout(30000);
//			conn.setReadTimeout(30000);
//			conn.setInstanceFollowRedirects(true);
			
	//	get.addHeader("Host", "pics.livejournal.com");
//			get.addHeader("X-FB-User", settings.getUserName().replace("-", "_"));
//			get.addHeader("X-FB-Auth", crp);
		//	get.addHeader("X-FB-UploadPic.Meta.Filename", sourceFile.getName());
			
//			InputStream is=conn.getInputStream();
			r=client.execute(get);
			
			e = r.getEntity();
			isr = e.getContent();
			//isReader = new InputStreamReader(isr);
			
			File f=new File(context.getExternalCacheDir()+"/212.jpg");
			f.createNewFile();
			
			FileOutputStream os=new FileOutputStream(f);
			byte[] buffer=new byte[1024];
			int i=0;
			while ((i = isr.read(buffer))>0){
				os.write(buffer,0,i);
			}
			os.flush();
			os.close();

			//InputStream is = new FileInputStream(sourceFile); // Context.openFileInput(postFileName);
		} catch (Exception e )
		{e.printStackTrace();
		};
		
		
	}

	private Bundle postImage(String postFileName) {
		Bundle bundle = new Bundle();
		DefaultHttpClient client = new DefaultHttpClient();
		String crp = "";
		File sourceFile = new File(postFileName);
		HttpGet hg = new HttpGet("http://pics.livejournal.com/interface/simple");
		hg.addHeader("Host", "pics.livejournal.com");
		hg.addHeader("X-FB-User", settings.getUserName().replace("-", "_"));
		hg.addHeader("X-FB-Mode", "GetChallenge");
		try {
			HttpResponse r = client.execute(hg);
			FileInputStream fisr = new FileInputStream(sourceFile);
			HttpEntity e = r.getEntity();
			InputStream isr = e.getContent();
			InputStreamReader isReader = new InputStreamReader(isr);
			BufferedReader br = new BufferedReader(isReader);
			StringBuilder sb = new StringBuilder();
			String s = "";
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
			s = sb.toString();
			String chal = "";
			// <Challenge>0DO9fogYt9vpCMgPSF738L1lTJauL3ChtFyHoUFPEeeZ3iKNsg-1399320000-26c84a275ddb4bcb40fbce60d4a377ba</Challenge>
			Pattern p = Pattern.compile("<Challenge>.*?</Challenge>");
			Matcher m = p.matcher(s);
			if (m.find()) {
				chal = m.group().substring(11);
				chal = chal.substring(0, chal.length() - 12);
			}

			String passHash = getMD5(settings.getPwd());
			crp = "crp:" + chal + ":" + getMD5(chal + passHash);

			/*
			 * hg=new HttpGet("http://pics.livejournal.com/interface/simple");
			 * hg.addHeader("Host","pics.livejournal.com");
			 * hg.addHeader("X-FB-User",settings.getUserName().replace("-",
			 * "_")); hg.addHeader("X-FB-Mode","Login");
			 * hg.addHeader("X-FB-Auth",crp);
			 * hg.addHeader("X-FB-Login.ClientVersion","MyClient/1.0");
			 * r=client.execute(hg); e= r.getEntity(); isr = e.getContent();
			 * isReader = new InputStreamReader(isr); br = new
			 * BufferedReader(isReader); sb = new StringBuilder(); s = ""; while
			 * ((s = br.readLine()) != null) { sb.append(s); } s =
			 * sb.toString();
			 */

			HttpPut put = new HttpPut(
					"http://pics.livejournal.com/interface/simple");
			put.addHeader("X-FB-Mode", "UploadPic");
			put.addHeader("X-FB-User", settings.getUserName().replace("-", "_"));
			put.addHeader("X-FB-Auth", crp);
			put.addHeader("X-FB-UploadPic.Meta.Filename", sourceFile.getName());

			InputStream is = new FileInputStream(sourceFile); // Context.openFileInput(postFileName);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int bytesRead;
			while ((bytesRead = is.read(b)) != -1) {
				bos.write(b, 0, bytesRead);
			}
			byte[] bytes = bos.toByteArray();

			ByteArrayEntity requestEntity = new ByteArrayEntity(bytes);
			put.setEntity(requestEntity);

			HttpParams params = client.getParams();
			params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);
			params.setParameter(CoreConnectionPNames.SO_TIMEOUT, new Integer(
					15000));
			params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
					new Integer(15000));

			// HttpParams params1 = new BasicHttpParams();
			// put.setParams(params1);
			put.getParams().setBooleanParameter(
					CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

			r = client.execute(put);
			e = r.getEntity();
			isr = e.getContent();
			isReader = new InputStreamReader(isr);
			br = new BufferedReader(isReader);
			sb = new StringBuilder();
			s = "";
			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
			s = sb.toString();
			p = Pattern.compile("<URL>.*?</URL>");
			m = p.matcher(s);
			if (m.find()) {
				s = m.group().substring(5);
				s = s.substring(0, s.length() - 6);
				s = "<img src=\"" + s + "\"" + " alt=\"" + sourceFile.getName()
						+ "\" title=" + sourceFile.getName() + "\">";

				bundle.putString("url", s);
			}

			is.close();
			// GET /interface/simple HTTP/1.x
			// Host: pics.livejournal.com
			// X-FB-User: bob
			// X-FB-Mode: Login
			// X-FB-Auth: crp:0123456789abcdef:0123456789abcde
			// X-FB-Login.ClientVersion: MyClient/1.0

			// crp:challenge_string:MD5(challenge_string, MD5(user_password))

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bundle;

		/*
		 * Host: pics.livejournal.com X-FB-User: bob X-FB-Mode: GetChallenge
		 */

	}

	private void doAddComment() {
		//
		try {
			Vector p;
			String journal;
			String parentTalkId = commentData.get("talkId");
			if (parentTalkId == null) {
				parentTalkId = commentData.get("parenttalkid");
			}

			Hashtable method_calls = new Hashtable();
			String lj_url = "http://www.livejournal.com/interface/xmlrpc";
			XmlRpcClient xmlrpc = new XmlRpcClient(lj_url);
			String password = getMD5(settings.getPwd());
			Vector<Hashtable<String, Comparable>> params = new Vector();

			method_calls.put("username", settings.getUserName());

			method_calls.put("password", settings.getPwd());
			method_calls.put("ver", "1");
			method_calls.put("clientversion", "WebServiceBook/0.0.1");

			java.util.Date now = new java.util.Date();
			method_calls.put("event", body);
			method_calls.put("lineendings", "\n");
			method_calls.put("subject", "");

			method_calls.put("body", commentData.get("body"));
			method_calls.put("journal", commentData.get("journal"));
			method_calls.put("parenttalkid", parentTalkId);
			method_calls.put("ditemid", commentData.get("ditemid"));

			method_calls.put("year", new Integer(now.getYear() + 1900));
			method_calls.put("mon", new Integer(now.getMonth() + 1));
			method_calls.put("day", new Integer(now.getDate()));
			method_calls.put("hour", new Integer(now.getHours()));
			method_calls.put("min", new Integer(now.getMinutes()));
			params.add(method_calls);

			Object result = xmlrpc.execute("LJ.XMLRPC.addcomment", params);
			Log.d(result.toString(), "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		;
	}

	private void doAddPost() {
		try {
			//fff
			Vector p;
			Hashtable method_calls = new Hashtable();
			String lj_url = "http://www.livejournal.com/interface/xmlrpc";
			XmlRpcClient xmlrpc = new XmlRpcClient(lj_url);
			String password = getMD5(settings.getPwd());
			Vector<Hashtable<String, Comparable>> params = new Vector();

			method_calls.put("username", settings.getUserName());

			method_calls.put("password", settings.getPwd());
			method_calls.put("ver", "1");
			method_calls.put("clientversion", "WebServiceBook/0.0.1");
			java.util.Date now = new java.util.Date();
			method_calls.put("event", body);
			method_calls.put("lineendings", "\n");
			method_calls.put("subject", title);
			method_calls.put("year", new Integer(now.getYear() + 1900));
			method_calls.put("mon", new Integer(now.getMonth() + 1));
			method_calls.put("day", new Integer(now.getDate()));
			method_calls.put("hour", new Integer(now.getHours()));
			method_calls.put("min", new Integer(now.getMinutes()));
			params.add(method_calls);
		//	sss
			Object result = xmlrpc.execute("LJ.XMLRPC.postevent", params);

		} catch (Exception e) {
			e.printStackTrace();
		}
		;

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
		int anum = (int) (postId - Math.floor(postId / 256) * 256);
		int internalId = ((postId - anum) / 256);
		return internalId + "";
		/*
		 * $anum = $public_itemid - floor($public_itemid / 256) * 256; $itemid =
		 * ($public_itemid - $anum) / 256;
		 */
	}

	private void doAddToFav() {
		String postId="";
	//	String commitPath="http://www.livejournal.com/__api/";
		String s = "";
		url = url.replace("http://", "");
		Pattern p = Pattern.compile("/.*?[.]");
		Matcher m = p.matcher(url);
		if (m.find()) {
			s = m.group().substring(1);
			s = s.substring(0, s.length() - 1);
		}
		postId=s;
		
		String ljName="";
		int i=url.indexOf(".");
		if (i>0)
			ljName=url.substring(0, i).replace("-", "_");
		
		String postPath = "http://www.livejournal.com/tools/memadd.bml?itemid="+postId+"&journal="+ljName;
		try {
			if ((settings.getUserName().equalsIgnoreCase(""))
					|| (settings.getPwd().equalsIgnoreCase(""))) {
				return;
			}
			if (!settings.getIsLoggedIn()) {
				userName = settings.getUserName();
				pwd = settings.getPwd();
				tryToLogin();
				if (!settings.getIsLoggedIn()) {
					return;
				}
			}
			
			String html=getHtmlByUrl(postPath, true);
			Pattern pFAuth=Pattern.compile("name=\"lj_form_auth\".*?/>");
			Pattern pFAuth2=Pattern.compile("value=\".*?\"");
			
			Pattern pDes=Pattern.compile("<input type=.*?/>");
		
			String sAuth="";
			String sMode="save";
			String sDes="";
			String sKeywords="";
			String sSecurity="public";
			
			Matcher m2=pFAuth.matcher(html);
			if (m2.find()){
				m2=pFAuth2.matcher(m2.group());
				if (m2.find()){
					sAuth=m2.group().substring(6).replace("\"", "");
				}
			}
			
			m2=pDes.matcher(html);
			while (m2.find()){
				if (m2.group().contains("name=\"des\"")){
					m2=pFAuth2.matcher(m2.group());
					if (m2.find()){
						sDes=m2.group().substring(6).replace("\"", "");
						//sDes=URLEncoder.encode(sDes,"utf8");
					}
					break;
				}
			}
			
			
			DefaultHttpClient client =new DefaultHttpClient(); // getNewHttpClient(); // new
 			//client = sslClient(client);
			HttpPost post = new HttpPost(postPath);
			List<NameValuePair> nameValues = new ArrayList<NameValuePair>(2);
			
			nameValues.add(new BasicNameValuePair("Host", "www.livejournal.com"));
			nameValues.add(new BasicNameValuePair("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0"));
			nameValues.add(new BasicNameValuePair("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
			nameValues.add(new BasicNameValuePair("Accept-Language", "ru,en-us;q=0.7,en;q=0.3"));
			//nameValues.add(new BasicNameValuePair("Content-Type", "text/plain; charset=UTF-8"));
			nameValues.add(new BasicNameValuePair("Cookie", settings.getCookie()));
			nameValues.add(new BasicNameValuePair("Connection", "keep-alive"));
			nameValues.add(new BasicNameValuePair("Referer", postPath));
			
//			List<NameValuePair> paramsList=new ArrayList<NameValuePair>();
//			paramsList.add(new BasicNameValuePair("lj_form_auth", sAuth));
//			paramsList.add(new BasicNameValuePair("mode", sMode));
//			paramsList.add(new BasicNameValuePair("des",sDes));
//			paramsList.add(new BasicNameValuePair("keywords", ""));
//			paramsList.add(new BasicNameValuePair("security", sSecurity));
//			post.setEntity(new UrlEncodedFormEntity(paramsList));
//ddd
	
			nameValues.add(new BasicNameValuePair("lj_form_auth",sAuth));
			nameValues.add(new BasicNameValuePair("mode", sMode));
		    nameValues.add(new BasicNameValuePair("des",sDes));
			nameValues.add(new BasicNameValuePair("keywords", ""));
			nameValues.add(new BasicNameValuePair("security", sSecurity));
		//	nameValues.add(new BasicNameValuePair("POSTDATA", 
		//			"lj_form_auth="+sAuth+"&mode="+sMode+"&des="+URLEncoder.encode(sDes)+
		//			"&keywords=&security="+sSecurity
		//			));
			//nameValues.add(new BasicNameValuePair("Accept-Encoding","gzip, deflate"));
			UrlEncodedFormEntity entity_=new UrlEncodedFormEntity(nameValues,"UTF-8");
		//	entity_.setContentEncoding(HTTP.UTF_8);		
			 
			post.addHeader("Cookie",settings.getCookie() );
			post.addHeader("lj_form_auth", sAuth);
			post.addHeader("mode", sMode);
			//post.addHeader("des",URLEncoder.encode(sDes));
			post.addHeader("keywords", "");
			
			post.addHeader("Host", "www.livejournal.com");
			post.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
			post.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			post.addHeader("Accept-Language", "ru,en-us;q=0.7,en;q=0.3");
			//post.addHeader(new BasicNameValuePair("Content-Type", "text/plain; charset=UTF-8"));
			post.addHeader("Cookie", settings.getCookie());
			post.addHeader("Connection", "keep-alive");
			post.addHeader("Referer", postPath);
			//post.addHeader("Accept-Encoding", "gzip, deflate");
			
		//	post.addHeader("POSTDATA", "lj_form_auth="+sAuth+"&mode="+sMode+"&des="+sDes+"&keywords=&security="+sSecurity
		//			);
			
			post.setEntity(entity_);
			//post.setEntity(new UrlEncodedFormEntity(nameValues));
			HttpResponse response = client.execute(post);
			 
			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity re = response.getEntity();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void doEditPost() {
		try {
		//	dd
			Vector p;
			String postId = getPostID(url);
			Hashtable method_calls = new Hashtable();
			String lj_url = "http://www.livejournal.com/interface/xmlrpc";
			XmlRpcClient xmlrpc = new XmlRpcClient(lj_url);
			String password = getMD5(settings.getPwd());
			Vector<Hashtable<String, Comparable>> params = new Vector();

			method_calls.put("username", settings.getUserName());
			method_calls.put("password", settings.getPwd());
			method_calls.put("itemid", postId);
			method_calls.put("ver", "1");
			method_calls.put("clientversion", "WebServiceBook/0.0.1");
			java.util.Date now = new java.util.Date();
			method_calls.put("event", body);
			method_calls.put("lineendings", "\n");
			method_calls.put("subject", title);
			method_calls.put("year", new Integer(now.getYear() + 1900));
			method_calls.put("mon", new Integer(now.getMonth() + 1));
			method_calls.put("day", new Integer(now.getDate()));
			method_calls.put("hour", new Integer(now.getHours()));
			method_calls.put("min", new Integer(now.getMinutes()));
			//ArrayList
			ArrayList<String> list=new ArrayList<String>();
			
			Hashtable<String, String> t=new Hashtable<String, String>();
			if (postTagInfo!=null){
				if (postTagInfo.getTags()!=null){
					t.put("taglist",postTagInfo.getTags());
				}
				String eAccess="";
				if (postTagInfo.getEAccess()==EAccess.ePublic){
					//if ()
					eAccess="public";
				}else
				if (postTagInfo.getEAccess()==EAccess.ePrivate){
					eAccess="private";
				}else
				if (postTagInfo.getEAccess()==EAccess.eFriends){
					eAccess="usemask";
					method_calls.put("allowmask", "0");
				}
				method_calls.put("security", eAccess);
				
				if (postTagInfo.adultContent()){
					t.put("adult_content", "explicit");
				} else
				{
					t.put("adult_content", "implicit");
				}	
				
				if (postTagInfo.getComments()==EComments.eNotNotify){
					t.put("opt_noemail", "1");
				} else
				{
					t.put("opt_noemail", "0");
				}
				
				if (postTagInfo.getComments()==EComments.eShutOff){
					t.put("opt_nocomments", "1");
				}else
				{
					t.put("opt_nocomments", "0");
				}
				
				if (postTagInfo.getComments()==EComments.eBlock){
					t.put("opt_lockcomments", "1");
				}else
				{
					t.put("opt_lockcomments", "0");
				}
			}
			if (!t.isEmpty())
			method_calls.put("props", t); //props={interface=web, give_features=1, personifi_tags=nterms:yes}
			 
			params.add(method_calls);

			Object result = xmlrpc.execute("LJ.XMLRPC.editevent", params);
			//Log.d("", result.toString());
			//sss
		} catch (Exception e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	public void run() {
		Bundle data = new Bundle();
		String prevUrl = "";
		boolean prevExists = false;

		switch (reason) {
		case iGetHtml: {
			// if (!settings.getIsLoggedIn()) {
			// tryToLogin();
			// if (!settings.getIsLoggedIn()) {
			// return;
			// }
			// }
			// String html = getHtmlByUrl(url, true);
			// Message msg = new Message();
			// data.put("loggedIn",loggedIn);
			// msg.setData(data);
			// mainHandler.sendMessage(msg);
			//
			break;
		}
		case iTryToLogin: {
			boolean loggedIn = tryToLogin2();
			Message msg = new Message();
			data.putBoolean("loggedIn", loggedIn);
			msg.setData(data);
			mainHandler.sendMessage(msg);

			return;
			// break;
		}
		case iGetMemories: {
			if (settings.getPwd().equalsIgnoreCase("")
					|| settings.getUserName().equalsIgnoreCase("")) {
				return;
			}

			if (!settings.getIsLoggedIn()) {
				tryToLogin();
				if (!settings.getIsLoggedIn()) {
					return;
				}
			}
			String uName = settings.getUserName().replace("_", "-");
			String html = getHtmlByUrl(url, true);
			FriendLineParser parser = new FriendLineParser(html, list);
			parser.parseMemories();
			break;
		}

		case iGetPostData: {
			if (settings.getPwd().equalsIgnoreCase("")
					|| (settings.getUserName().equalsIgnoreCase(""))) {
				return;
			}
			HashMap<String, String> map = new HashMap<String, String>();
			PostParser p = new PostParser(null, url, "");
			p.setSettings(settings);
			Bundle response = new Bundle();
			p.fillxmlrpc(response);
			Message msg = new Message();
			msg.setData(response);
			mainHandler.sendMessage(msg);
			return;
		}
		case iGetFriendsGroups:{
			Bundle b = getFriendGroups();//postImage(postFileName);
			Message msg = new Message();
			msg.setData(b);
			mainHandler.sendMessage(msg);
			return;
		}

		case iGetMyJournal: {
			if (settings.getPwd().equalsIgnoreCase("")
					|| (settings.getUserName().equalsIgnoreCase(""))) {
				return;
			}
			MyJournalParser parser = new MyJournalParser(settings, list);
			parser.fillMyJournal(); // parse();
			break;
		}

		case iPost: {
			if (settings.getPwd().equalsIgnoreCase("")
					|| settings.getUserName().equalsIgnoreCase("")) {
				return;
			}

			doAddPost();
			// doPost();
			mainHandler.sendEmptyMessage(iPost);
			return;
		}
		case iReply: {
			if (settings.getPwd().equalsIgnoreCase("")
					|| settings.getUserName().equalsIgnoreCase("")) {
				return;
			}

			doAddComment();
			// doPost();
			mainHandler.sendEmptyMessage(iPost);
			return;
		}
		case iAddToFav: {
			if (settings.getPwd().equalsIgnoreCase("")
					|| settings.getUserName().equalsIgnoreCase("")) {
				return;
			}
			doAddToFav();
			mainHandler.sendEmptyMessage(iAddToFav);
			return;
		}

		case iEditPost: {
			if (settings.getPwd().equalsIgnoreCase("")
					|| settings.getUserName().equalsIgnoreCase("")) {
				return;
			}

			doEditPost();
			mainHandler.sendEmptyMessage(iPost);
			return;
		}

		case iPostImage: {
			if (settings.getPwd().equalsIgnoreCase("")
					|| settings.getUserName().equalsIgnoreCase("")) {
				return;
			}

			// if (!settings.getIsLoggedIn()) {
			// tryToLogin();
			// if (!settings.getIsLoggedIn()) {
			// return;
			// }
			// }
			// postImage3(postFileName);
			Bundle b = postImage(postFileName);
			Message msg = new Message();
			msg.setData(b);
			mainHandler.sendMessage(msg);
			return;
		}
		case iAddComment: {
			doAddComment();
			Message msg = new Message();
			msg.setData(data);
			mainHandler.sendMessage(msg);
			return;
		}
		
		case iGetImage:{
			getImage();
			return;
		}

		case iGetFriendLine: {
			if (settings.getPwd().equalsIgnoreCase("")
					|| settings.getUserName().equalsIgnoreCase("")) {
				return;
			}
			
			String uName = settings.getUserName().replace("_", "-");
			String surl = "";
			if (url.equalsIgnoreCase("")) {
				surl = "http://www." + uName
						+ ".livejournal.com/friends?skip=0&format=light";
				// url = "http://m.livejournal.com/read/friends?filter=0";
			} else {
				surl = this.url + "";
			}

			// String html = getHtmlByUrl(surl, false);

			FriendLineParser parser = new FriendLineParser(settings, list);
			parser.fillFriendLine(filter);

			// prevUrl = parser.getPrevUrl();
			// prevExists = prevUrl.equalsIgnoreCase("");
			data.putString("prevUrl", "");// prevUrl);
			data.putBoolean("prevExists", false);// prevExists);
			break;
		}
		}

		Message msg = new Message();
		data.putString("cookie", cookie);
		data.putString("status", hresponse);

		msg.setData(data);
		mainHandler.sendMessage(msg);
	}

	private class RedirectRequestDirector extends DefaultRequestDirector
	{
	    RedirectRequestDirector(
	            final HttpRequestExecutor requestExec,
	            final ClientConnectionManager conman,
	            final ConnectionReuseStrategy reustrat,
	            final ConnectionKeepAliveStrategy kastrat,
	            final HttpRoutePlanner rouplan,
	            final HttpProcessor httpProcessor,
	            final HttpRequestRetryHandler retryHandler,
	            final RedirectHandler redirectHandler,
	            final AuthenticationHandler targetAuthHandler,
	            final AuthenticationHandler proxyAuthHandler,
	            final UserTokenHandler userTokenHandler,
	            final HttpParams params) 
	    {
	        super(requestExec, conman, reustrat, kastrat, rouplan, httpProcessor, retryHandler, redirectHandler, targetAuthHandler, proxyAuthHandler, userTokenHandler, params);

	    }
	    @Override
	    protected RoutedRequest handleResponse(RoutedRequest roureq,
	            HttpResponse response,
	            HttpContext context)
	                    throws HttpException, IOException
	    {
	        RoutedRequest req = super.handleResponse(roureq, response, context);
	        if(req != null)
	        {
	            String redirectTarget = req.getRoute().getTargetHost().getHostName();
	            req.getRequest().getOriginal().setHeader("Host", redirectTarget);
	        }
	        //req.getRequest().getOriginal().getAllHeaders()
	       
	        return req;
	    }

	}
	
	private class RedirectHttpClient extends DefaultHttpClient
	{
	    @Override
	    protected RequestDirector createClientRequestDirector(
	            final HttpRequestExecutor requestExec,
	            final ClientConnectionManager conman,
	            final ConnectionReuseStrategy reustrat,
	            final ConnectionKeepAliveStrategy kastrat,
	            final HttpRoutePlanner rouplan,
	            final HttpProcessor httpProcessor,
	            final HttpRequestRetryHandler retryHandler,
	            final RedirectHandler redirectHandler,
	            final AuthenticationHandler targetAuthHandler,
	            final AuthenticationHandler proxyAuthHandler,
	            final UserTokenHandler stateHandler,
	            final HttpParams params) {
	        return new RedirectRequestDirector(
	                requestExec,
	                conman,
	                reustrat,
	                kastrat,
	                rouplan,
	                httpProcessor,
	                retryHandler,
	                redirectHandler,
	                targetAuthHandler,
	                proxyAuthHandler,
	                stateHandler,
	                params);
	    }
	}
}
