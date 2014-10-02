package com.smlivejournal.settings;

import com.smlivejournal.client.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AuthSettingsActivity extends Activity {

	private EditText pwdEdit;
	private EditText userNameEdit;
	private Settings settings;
	String userName = "";
	String pwd = "";
	ProgressDialog pg;

	public AuthSettingsActivity() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.smlivejournal.client.R.layout.authsettingsactivity);
		Button btnSave = (Button) findViewById(com.smlivejournal.client.R.id.btnSave);
		pwdEdit = (EditText) findViewById(com.smlivejournal.client.R.id.editPwd);
		userNameEdit = (EditText) findViewById(com.smlivejournal.client.R.id.editName);
		Bundle b = getIntent().getExtras();
		this.settings = (Settings) b.getSerializable("settings");
		pwd = b.getString("pwd");
		userName = b.getString("userName");
		pwdEdit.setText(pwd);
		userNameEdit.setText(userName);

		btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				userName = userNameEdit.getText().toString();
				pwd = pwdEdit.getText().toString();
				Handler mainHandler = new Handler() {
					public void handleMessage(Message msg) {
						if (pg != null)
							pg.dismiss();
						Bundle b = msg.getData();
						Boolean loggedIn=b.getBoolean("loggedIn");
						if (loggedIn) {
							Intent intent = new Intent();
							//settings.setIsLoggedIn(true, cookie);
							String userName = userNameEdit.getText().toString();
							intent.putExtra("userName", userName);
							String pwd = pwdEdit.getText().toString();
							intent.putExtra("pwd", pwd);
							Toast.makeText(AuthSettingsActivity.this, 
									AuthSettingsActivity.this.getResources().getString(com.smlivejournal.client.R.string.authsucceded)
									, Toast.LENGTH_LONG).show();
							//intent.putExtra("cookie", cookie);
							setResult(RESULT_OK, intent);
							finish();
						} 
					}
				};
				pg = new ProgressDialog(AuthSettingsActivity.this);
				pg.setMessage(getResources().getString(com.smlivejournal.client.R.string.auth));
				pg.setCancelable(false);
				pg.show();
				AuthThread thread = new AuthThread(mainHandler,
						AuthThread.iTryToLogin, userName, pwd);
				thread.setContext(AuthSettingsActivity.this);
				thread.setSettings(settings);
				thread.start();
			}
		});

	}

}
