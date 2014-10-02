package com.smlivejournal.settings;
 
import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;

public class SettingsActivity extends SherlockPreferenceActivity{
	private Settings settings;

	public SettingsActivity() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		setTheme( com.smlivejournal.client.R.style.Theme_Sherlock_Light);
		//setTheme(com.smlivejournal.client.R.style.Theme_Sherlock);
		
		super.onCreate(savedInstanceState);
		Bundle b=getIntent().getExtras();
		this.settings=(Settings)b.getSerializable("settings");
		
		addPreferencesFromResource(com.smlivejournal.client.R.xml.preferences);
		Preference auth = findPreference("authsettings");
		auth.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				//Toast.makeText(SettingsActivity.this, "ping", Toast.LENGTH_SHORT).show();
				Intent iAuth=new Intent(SettingsActivity.this,AuthSettingsActivity.class);
				Bundle b=new Bundle();
				b.putSerializable("settings", settings);
				b.putString("userName", settings.getUserName());
				b.putString("pwd",settings.getPwd());
				iAuth.putExtras(b);
				startActivityForResult(iAuth, 1);
				return true;
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	    if ((resultCode==RESULT_OK)&&(requestCode==1)){
	    	String userName=data.getStringExtra("userName");
	    	String pwd=data.getStringExtra("pwd");
	    	String cookie="";
	    	if (data.hasExtra("cookie")){
	    		cookie=data.getStringExtra("cookie");
	    		settings.setIsLoggedIn(true, cookie);
	    	}else
	    	{settings.setIsLoggedIn(false,"");}
	    	
	    	settings.setPwd(pwd);
	    	settings.setUserName(userName);
	    	settings.saveAuthSettings(SettingsActivity.this);
	    }
	}
	
	@Override
	public void onStop(){
		super.onStop();
	//	settings.load(this);
		//settings.saveSettings(this);
	}

}
