package com.epamtraining.vklite.activities;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.epamtraining.vklite.MainActivity;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.fragments.FriendsFragment;

public class ChooseFriendActivity  extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_friend);
        Bundle bundle = new Bundle();
        bundle.putInt(MainActivity.FRAGMENT_REQUEST, MainActivity.REQUEST_CODE_CHOOSE_FRIEND);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, FriendsFragment.getNewFragment(bundle))
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.menu_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        return super.onOptionsItemSelected(item);
    }


}