package com.epamtraining.vklite.auth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.epamtraining.vklite.MainActivity;
import com.epamtraining.vklite.auth.AuthHelper;

public class StartActivity extends Activity {
    public static final int REQUEST_LOGIN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = getSavedToken();
        if (TextUtils.isEmpty(token)) {
            startLoginActivity();
        } else {
            startMainActivity(token);
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
                    saveToken(token);
                    startMainActivity(token);
                } else {//??
                }
                finish();
            } else
            {
                finish();
            }
        }
    }

    private void startMainActivity(String token) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(AuthHelper.TOKEN, token);
        startActivity(i);
    }

    private void startLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivityForResult(i, REQUEST_LOGIN);
    }

    private void saveToken(String token) {
        SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if (TextUtils.isEmpty(token)) {
            prefEditor.remove(AuthHelper.TOKEN);
        } else {
            try {
                token = EncrManager.encrypt(this, token);
                prefEditor.putString(AuthHelper.TOKEN, token);
            } catch (Exception e) {
                e.printStackTrace();
                prefEditor.remove(AuthHelper.TOKEN);
                //при  ошибке придется логиниться
            }
        }
        prefEditor.apply();
    }

    private String getSavedToken() {
        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(AuthHelper.TOKEN, "");
        if (!TextUtils.isEmpty(token))
            try {
                token = EncrManager.decrypt(this, token);
            } catch (Exception e) {
                e.printStackTrace();
                token = "";
                //при  ошибке придется логиниться
            }
        return token;
    }
}
