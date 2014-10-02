package com.smlivejournal.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import org.apache.http.cookie.Cookie;

import com.smlivejournal.client.MainActivity;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.sax.StartElementListener;
import android.util.TypedValue;
import android.widget.TextView;

 
public class Settings implements Serializable {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//	private Context context;
///	private Activity activity; 
	
	private int textNormal;
	private int textSmall;
	private int textLarge;
	
	private int textSize;
	private boolean loadImages=true;
	private boolean noScreenSwitchOff=false;
	private String userName="";
	private String pwd="";
	private boolean isLoggedIn=false;
	private String cookie="";
	private String topLanguage="";
	//private ArrayList<Cookie> cookies; 
	private static String prefName="smljSettings";
	//private Context context;
	 
	public Settings(Context context,Activity activity) {
		
		//this.context=context;
	//	this.activity=activity;
		//cookies=new ArrayList<Cookie>();
		textNormal=Integer.parseInt(context.getResources().getString(com.smlivejournal.client.R.string.textnormal));
		textLarge=Integer.parseInt(context.getResources().getString(com.smlivejournal.client.R.string.textlarge));
		textSmall=Integer.parseInt(context.getResources().getString(com.smlivejournal.client.R.string.textsmall));
		
		load(context);
	}
	
	public void load(Context context){
		SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
		String s=prefs.getString("fontsize","0");
		textSize=Integer.parseInt(s);
		noScreenSwitchOff=prefs.getBoolean("noscreenswitchoff", false);
		loadImages=prefs.getBoolean("loadimages", true);
		userName=prefs.getString("username", "");
		pwd=prefs.getString("pwd","");
		topLanguage=prefs.getString("topLanguage", "cyr");
	}
	
	public void saveAuthSettings(Context context){
	 	SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit= prefs.edit();
		edit.putString("username", userName);
		edit.putString("pwd", pwd);
		edit.apply();
	}
	
	public String getTopLanguage(){
		return topLanguage;
	}
	
	public void saveSettings(Context context){
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit= prefs.edit();
		edit.putBoolean("loadimages", loadImages);
		edit.putBoolean("noscreenswitchoff", noScreenSwitchOff);
		edit.putString("fontsize", textSize+"");
		edit.putString(topLanguage, "topLanguage");
		edit.apply();
	}
	
	public boolean getLoadImages(){
		return loadImages;
	}
	
	public boolean getNoScreenSwitchoff(){
		return noScreenSwitchOff;
	}
	
	public void setTextViewSize(TextView tv){
		if (tv==null){return;}
		
		if (textSize==0)
		{tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSmall);} else
		if (textSize==1)
		{tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textNormal);} else
		if (textSize==2)
		{tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textLarge);}
		else
		{tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textNormal);}
	}
	
	public void setTextViewSize(TextView tv,int inc){
		if (tv==null){return;}
		
		if (textSize==0)
		{tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSmall+inc);} else
		if (textSize==1)
		{tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textNormal+inc);} else
		if (textSize==2)
		{tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textLarge+inc);}
		else
		{tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, textNormal+inc);}
	}
	
	public void showEditor(Context context,Activity activity){
		Intent iEditor  = new Intent(context,SettingsActivity.class);
		Bundle b=new Bundle();
		b.putSerializable("settings", this);
		iEditor.putExtras(b);
		activity.startActivityForResult(iEditor,MainActivity.iSettingsEdit);
	}
	
	//public ArrayList<Cookie> getCookies(){
	//	return cookies;
	//}
	
	public String getCookie(){
		return cookie;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public String getPwd(){
		return pwd;
	}
	
	public void setPwd(String pwd){
		this.pwd=pwd;
	}
	
	public void setIsLoggedIn(Boolean isLoggedIn,String cookie){
		this.isLoggedIn=isLoggedIn;
		this.cookie=cookie;
	}
	
	public boolean getIsLoggedIn(){
		return this.isLoggedIn;
	}
	
	public void setUserName(String userName){
		this.userName=userName;
	}
	
	public void setTextSize(int size){
		if (size<=2)
		textSize=size;
	}
}
