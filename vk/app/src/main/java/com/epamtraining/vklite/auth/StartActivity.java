package com.epamtraining.vklite.auth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.epamtraining.vklite.activities.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class StartActivity extends Activity {
    public static final int REQUEST_LOGIN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token;
        String userId;
        Map<String, String> accountInfo = getSavedCredentials();
        token = accountInfo.get(AuthHelper.TOKEN);
        userId = accountInfo.get(AuthHelper.USER_ID);
        if (TextUtils.isEmpty(token)) {
            startLoginActivity();
        } else {
            startMainActivity(token, userId);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN) {
            if ((resultCode == RESULT_OK)) {
                if (data.hasExtra(AuthHelper.TOKEN)) {
                    String token = data.getStringExtra(AuthHelper.TOKEN);
                    String currentUserId = data.getStringExtra(AuthHelper.USER_ID);
                    saveAuthCredentials(token, currentUserId);
                    startMainActivity(token, currentUserId);
                } else {//??
                }
                finish();
            } else {
                finish();
            }
        }
    }


    private void startMainActivity(String token, String userId) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(AuthHelper.TOKEN, token);
        i.putExtra(AuthHelper.USER_ID, userId);
        startActivity(i);
    }

    private void startLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivityForResult(i, REQUEST_LOGIN);
    }

    private void saveAuthCredentials(String token, String userId) {
        SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if (TextUtils.isEmpty(token)) {
            prefEditor.remove(AuthHelper.TOKEN);
            prefEditor.remove(AuthHelper.USER_ID);
        } else {
            try {
                token = EncrManager.encrypt(this, token);
                prefEditor.putString(AuthHelper.TOKEN, token);
                prefEditor.putString(AuthHelper.USER_ID, userId);
            } catch (Exception e) {
                e.printStackTrace();
                prefEditor.remove(AuthHelper.TOKEN);
                //при  ошибке придется логиниться
            }
        }
        prefEditor.apply();
    }

    private Map<String, String> getSavedCredentials() {
        HashMap<String, String> map = new HashMap<>();
        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(AuthHelper.TOKEN, "");
        String userId = PreferenceManager.getDefaultSharedPreferences(this).getString(AuthHelper.USER_ID, "");
        if (!TextUtils.isEmpty(token))
            try {
                token = EncrManager.decrypt(this, token);
            } catch (Exception e) {
                e.printStackTrace();
                token = "";
                //при  ошибке придется логиниться
            }
        map.put(AuthHelper.USER_ID, userId);
        map.put(AuthHelper.TOKEN, token);
        return map;
    }
}
