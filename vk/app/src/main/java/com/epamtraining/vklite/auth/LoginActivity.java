package com.epamtraining.vklite.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.epamtraining.vklite.R;

public class LoginActivity extends ActionBarActivity implements AuthHelper.AuthCallBack{
    private WebView mWebView;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mWebView = (WebView)findViewById(R.id.webView);
        mProgress = (ProgressBar)findViewById(R.id.progress);
        getSupportActionBar().hide();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.setWebViewClient(new WebViewLoginClient());
        mWebView.loadUrl(AuthHelper.AUTORIZATION_URL);
    }

    @Override
    public void onSuccess(String token) {
        Intent i = getIntent();
        i.putExtra(AuthHelper.TOKEN, token);
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onError(Exception e) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = getIntent();
                setResult(RESULT_CANCELED, i);
                finish();
                dialog.cancel();
            }
        });
        dialogBuilder.setTitle(getResources().getString(R.string.auth_error));
        dialogBuilder.setMessage(e.getMessage());
        dialogBuilder.setCancelable(false);
        dialogBuilder.create();
        dialogBuilder.show();
   }

    private class WebViewLoginClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (AuthHelper.proceedRedirectURL(LoginActivity.this, url, LoginActivity.this)) {
               view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideProgress();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showProgress();
        }

        private void hideProgress(){
           // LoginActivity.this.mProgress.setVisibility(View.VISIBLE);
            //LoginActivity.this.mWebView.setVisibility(View.GONE);
        }

        private void showProgress(){
            //LoginActivity.this.mProgress.setVisibility(View.GONE);
            //LoginActivity.this.mWebView.setVisibility(View.VISIBLE);
        }
    }
}
