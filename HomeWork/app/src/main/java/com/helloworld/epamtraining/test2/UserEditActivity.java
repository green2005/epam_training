package com.helloworld.epamtraining.test2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class UserEditActivity extends Activity {
    public static int iEditUser = 1;
    public static int iNewUser = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);
        final EditText userEdit=(EditText)findViewById(R.id.userNameEdit);
        final EditText pwdEdit=(EditText)findViewById(R.id.pwdEdit);
        Button btnOk=(Button) findViewById(R.id.btnApply);
        if (btnOk!=null){
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String userName=userEdit.getText().toString();
                    String pwd=pwdEdit.getText().toString();
                    Bundle b=new Bundle();
                    b.putString("username",userName);
                    b.putString("pwd",pwd);
                    Intent i=new Intent();
                    i.putExtras(b);
                    setResult(RESULT_OK, i);
                    finish();
                }
            });
        }
        setTitle(R.string.fillAuth);
        Intent i= getIntent();
        if (i!=null){
            Bundle b=i.getExtras();
            if (b!=null){
                if (b.containsKey("username")) {
                    setTitle(R.string.changeAuth);
                    String userName = b.getString("username");
                    String pwd = b.getString("pwd");
                    pwdEdit.setText(pwd);
                    userEdit.setText(userName);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
