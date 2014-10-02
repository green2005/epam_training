package com.smlivejournal.top;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.smlivejournal.client.HttpConn;
import com.smlivejournal.client.R;
import com.smlivejournal.settings.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Html;
import android.util.Log;

//import android.util.Log;

public class NewsParser {
	private String urlRuMain = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22cyr%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A0%7D%2C%22id%22%3A1386535500000%7D";
	private String urlRuNews = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22cyr%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A14%7D%2C%22id%22%3A1387482300000%7D";
	private String urlRuPositive = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22cyr%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A4%7D%2C%22id%22%3A1387482300000%7D";
	private String urlRulHelpful = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22cyr%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A2%7D%2C%22id%22%3A1387482300000%7D";
	private String urlRuSociety = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22cyr%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A8%7D%2C%22id%22%3A1387482300000%7D";
	private String urlRuDiscussion = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22cyr%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A10%7D%2C%22id%22%3A1387482300000%7D";
	private String urlRuMedia = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22cyr%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A6%7D%2C%22id%22%3A1387482300000%7D";
	private String urlRuTravelling = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22cyr%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A16%7D%2C%22id%22%3A1387482300000%7D";
	private String urlRuEighteen = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22cyr%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A12%7D%2C%22id%22%3A1387482300000%7D";
	private String urlRuZhir = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22cyr%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A17%7D%2C%22id%22%3A1387482300000%7D";
							   
	//private String urlUaMain = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22ua%22%2C%22locale%22%3A%22ru_RU%22%7D%2C%22id%22%3A1406838600000%7D";
	private String urlUaMain = 	 "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22ua%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A0%7D%2C%22id%22%3A1386535500000%7D";
	
	//private String urlRuMain = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22cyr%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A0%7D%2C%22id%22%3A1386535500000%7D";
	
	
	private String urlUaNews = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22ua%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A14%7D%2C%22id%22%3A1406839500000%7D";
							//	http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22ua%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A14%7D%2C%22id%22%3A1406839500000%7D
	private String urlUaPositive = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22ua%22%2C%22locale%22%3A%22ua_UA%22%2C%22category_id%22%3A4%7D%2C%22id%22%3A1387482300000%7D";
	private String urlUalHelpful = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22ua%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A2%7D%2C%22id%22%3A1406840400000%7D";
	private String urlUaEighteen = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22ua%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A12%7D%2C%22id%22%3A1406840400000%7D";
	private String urlUaSociety = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22ua%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A8%7D%2C%22id%22%3A1387482300000%7D";
	private String urlUaTravelling = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22ua%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A16%7D%2C%22id%22%3A1406840400000%7D";
	private String urlUaDiscussion = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22ua%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A14%7D%2C%22id%22%3A1406840400000%7D";
	
	private String urlUaMedia = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22ua%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A6%7D%2C%22id%22%3A1387482300000%7D";
	private String urlUaZhir = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22ua%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A17%7D%2C%22id%22%3A1387482300000%7D";
	
	// "http://l-api.livejournal.com/__api/?callback=jQuery1386535500000homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22cyr%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A0%7D%2C%22id%22%3A1386535500000%7D";
	private String urlEn = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22noncyr%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A0%7D%2C%22id%22%3A1406817000000%7D";
//private String urlRuMain = "http://l-api.livejournal.com/__api/?callback=homepage__get_rating&request=%7B%22jsonrpc%22%3A%222.0%22%2C%22method%22%3A%22homepage.get_rating%22%2C%22params%22%3A%7B%22homepage%22%3A1%2C%22sort%22%3A%22visitors%22%2C%22page%22%3A0%2C%22country%22%3A%22cyr%22%2C%22locale%22%3A%22ru_RU%22%2C%22category_id%22%3A0%7D%2C%22id%22%3A1386535500000%7D";
	
	
	private List<HashMap<String, String>> newsList;
	private int rc = 0;
	private String em = "";
	private Boolean isRu;
	private HashMap<String, Integer> fieldNames;
	private String newsId;
	private Context context;
	private static final int topCount = 20;
	private int reason = -1;
	private int offset = 0;
	private Settings settings;
	private int itemCount = 0;

	NewsParser(List<HashMap<String, String>> newsList, Boolean getRuNews,
			String newsId, Context context,Settings settings) {
		super();
		this.newsList = newsList;
		isRu = getRuNews;
		this.newsId = newsId;
		fieldNames = new HashMap<String, Integer>();
		fieldNames.put("time", 1);
		fieldNames.put("position", 2);
		fieldNames.put("body", 1);
		fieldNames.put("post_url", 1);
		fieldNames.put("reply_count", 2);
		fieldNames.put("journal", 1);
		fieldNames.put("subject", 1);
		this.context = context;
		this.settings=settings;
	}

	private void putMapValue(String fieldName, String value,
			HashMap<String, String> map) {
		if (fieldNames.containsKey(fieldName)) {
			map.put(fieldName, value);
		}
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	private void parseHtml(String html) {
		try {
			html = html.replace("homepage__get_rating(", "");
			html = html.substring(0, html.length() - 1);
			JSONObject o = new JSONObject(html);
			JSONObject o2 = o.optJSONObject("result");
			JSONArray topItems = o2.optJSONArray("rating");
			itemCount = topItems.length();
			for (int i = offset; (i < topItems.length())
					&& (i < (topCount + offset)); i++) {
				JSONObject topItem = topItems.getJSONObject(i);
				HashMap<String, String> map = new HashMap<String, String>();
				for (String fieldName : fieldNames.keySet()) {
					if (fieldNames.get(fieldName) == 1) {
						if (fieldName.equalsIgnoreCase("journal")) {
							JSONArray ljuser = topItem.optJSONArray("ljuser");
							JSONObject us = ljuser.getJSONObject(0);
							String v = us.optString(fieldName);
							map.put(fieldName, v);
						} else {
							String v = topItem.optString(fieldName);
							if (fieldName.equalsIgnoreCase("body")) {
								v = Html.fromHtml(v).toString();
							}
							map.put(fieldName, v);
						}
					} else {
						String v = topItem.optInt(fieldName) + "";
						map.put(fieldName, v);
					}
				}
				newsList.add(map);
			}

			// o.optJSONArray(name)

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadMore(int offset, int count) {

	}

	public int getItemCount() {
		return itemCount;
	}

	public void setReason(int reason) {
		this.reason = reason;
	}

	public void fillNews() {
		HttpConn conn = new HttpConn();
		String html = null;
		if (isRu) {
			if (reason == MainTop.readRSSMore) {
				html = getHtml(getUrlByItemId());
			} else {
				html = conn.htmlByUrl(getUrlByItemId(),true);
			}
			if (reason != MainTop.readRSSMore) {
				saveHtml(getUrlByItemId(), html);
			}
		} else {
			html = conn.htmlByUrl(urlEn,true);
		}
		if (!html.equalsIgnoreCase("")) {
			parseHtml(html);
		}
	}

	private void saveHtml(String key, String html) {
		if (//(key.equalsIgnoreCase(urlRuMain))&&
				(reason!=MainTop.readRSSMore)) {
			SharedPreferences sp = context.getSharedPreferences(
					"com.smlivejournal.client", Context.MODE_PRIVATE);
			Editor e = sp.edit();
			e.putString(key, html);
			e.commit();
		}
	}

	private String getHtml(String key) {
		SharedPreferences sp = context.getSharedPreferences(
				"com.smlivejournal.client", Context.MODE_PRIVATE);
		if (sp.contains(key)) {
			return sp.getString(key, "");
		} else
			return "";
	}

	private String getUrlByItemId() {
		int lanID=0;
		if (settings!=null){
			if (settings.getTopLanguage().equalsIgnoreCase("cyr"))
			{lanID=0;}else
			if (settings.getTopLanguage().equalsIgnoreCase("uk"))
			{lanID=1;}else
			if (settings.getTopLanguage().equalsIgnoreCase("en"))
			{lanID=2;};
		}
		
		if (newsId.equals(context.getResources().getString(
				com.smlivejournal.client.R.string.sMain))) {
			switch (lanID){
				case 0:{return urlRuMain;}
				case 1:{return urlUaMain;}
				case 2:{return urlEn;}
			}
			//return urlRuMain;
		} else if (newsId.equals(context.getResources().getString(
				R.string.sNews))) {
			switch (lanID){
			case 0:{return urlRuNews;}
			case 1:{return urlUaNews;}
			case 2:{return urlEn;}
		}	//return urlRuNews;
		} else if (newsId.equals(context.getResources().getString(
				R.string.sPositive))) {
			switch (lanID){
			case 0:{return urlRuPositive;}
			case 1:{return urlUaPositive;}
			case 2:{return urlEn;}
		}
			//return urlRuPositive;
		} else if (newsId.equals(context.getResources().getString(
				R.string.sHelpful))) {
			switch (lanID){
			case 0:{return urlRulHelpful;}
			case 1:{return urlUalHelpful;}
			case 2:{return urlEn;}
		}
		//	return urlRulHelpful;
		} else if (newsId.equals(context.getResources().getString(
				R.string.sSociety))) {
			//return urlRuSociety;
			switch (lanID){
			case 0:{return urlRuSociety;}
			case 1:{return urlUaSociety;}
			case 2:{return urlEn;}
		}
		} else if (newsId.equals(context.getResources().getString(
				R.string.sDiscussion))) {
			switch (lanID){
			case 0:{return urlRuDiscussion;}
			case 1:{return urlUaDiscussion;}
			case 2:{return urlEn;}
		}
			//return urlRuDiscussion;
		} else if (newsId.equals(context.getResources().getString(
				R.string.sMedia))) {
			switch (lanID){
			case 0:{return urlRuMedia;}
			case 1:{return urlUaMedia;}
			case 2:{return urlEn;}
		}//	return urlRuMedia;
		} else if (newsId.equals(context.getResources().getString(
				R.string.sTravelling))) {
			//return urlRuTravelling;
			switch (lanID){
			case 0:{return urlRuTravelling;}
			case 1:{return urlUaTravelling;}
			case 2:{return urlEn;}
		}
		} else if (newsId.equals(context.getResources().getString(
				R.string.sEighteen))) {
			//return urlRuEighteen;
			switch (lanID){
			case 0:{return urlRuEighteen;}
			case 1:{return urlUaEighteen;}
			case 2:{return urlEn;}
		}
		} else if (newsId.equals(context.getResources().getString(
				R.string.sZhir))) {
			switch (lanID){
			case 0:{return urlRuZhir;}
			case 1:{return urlUaZhir;}
			case 2:{return urlEn;}
		}
			//	return urlRuZhir;
		} else {
			//return urlRuMain;
			switch (lanID){
			case 0:{return urlRuMain;}
			case 1:{return urlUaMain;}
			case 2:{return urlEn;}
		}
		}
		return urlRuMain;
	}

	public int getRC() {
		return rc;
	}

	public String getEm() {
		return em;
	}

}
