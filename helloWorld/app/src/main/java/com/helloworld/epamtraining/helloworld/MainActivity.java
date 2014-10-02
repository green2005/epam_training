package com.helloworld.epamtraining.helloworld;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case (R.id.action_settings):{
                Toast.makeText(this,"settings_clicked",Toast.LENGTH_LONG).show();
                return true;
            }
            case (R.id.action_other):{
                Toast.makeText(this,"other_clicked",Toast.LENGTH_LONG).show();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
