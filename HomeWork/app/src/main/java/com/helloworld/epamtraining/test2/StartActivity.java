package com.helloworld.epamtraining.test2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;


public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(this);
        if (sh.contains("username")){
            AuthSettings.setPwd(sh.getString("pwd",""));
            AuthSettings.setUserName(sh.getString("username",""));
            Intent i=new Intent(this,MainActivity.class);
            startActivity(i);
            finish();
        } else
        {
            Intent i=new Intent(this,UserEditActivity.class);
            startActivityForResult(i,UserEditActivity.iNewUser);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode==RESULT_OK)&&(requestCode==UserEditActivity.iNewUser)){
            Bundle b=data.getExtras();
            if (b!=null){
                if (b.containsKey("username")){
                String userName = b.getString("username");
                String pwd=b.getString("pwd");
                SharedPreferences.Editor pm =  PreferenceManager.getDefaultSharedPreferences(this).edit();//this.getActivity().getSharedPreferences("auth",Context.MODE_PRIVATE).edit();
                if (pm!=null){
                    pm.putString("username",userName);
                    pm.putString("pwd",pwd);
                    AuthSettings.setPwd(pwd);
                    AuthSettings.setUserName(userName);
                    pm.commit();

                        }
                        Intent i=new Intent(this,MainActivity.class);
                        startActivity(i);
                   }
            }
        }

        finish();
    }
}
