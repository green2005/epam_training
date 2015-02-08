package com.epamtraining.vklite.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.epamtraining.vklite.R;
import com.epamtraining.vklite.fragments.MessagesRecyclerViewFragment;
import com.epamtraining.vklite.fragments.Refreshable;

public class MessagesActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container,
                            MessagesRecyclerViewFragment.getNewFragment(getIntent().getExtras()))
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_messages, menu);
        MenuItem refreshItem = menu.findItem(R.id.action_refresh);
        refreshItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                refreshMessages();
                return false;
            }
        });
        return true;
    }

    private void refreshMessages() {
        if (getSupportFragmentManager() != null) {
            for (Fragment ft : getSupportFragmentManager().getFragments()) {
                if (ft instanceof Refreshable) {
                    ((Refreshable) ft).refreshData();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if ( id == android.R.id.home){
            finish();
        }
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
