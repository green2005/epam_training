package com.epamtraining.vklite.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.epamtraining.vklite.R;
import com.epamtraining.vklite.fragments.FriendsFragment;

public class ListActivity extends ActionBarActivity {
    public enum ListFragments {
        FRIENDS,
        AUDIOS,
        VIDEOS,
        FOLLOWERS,
        GROUPS,
        PHOTOS,;
    }

    public static final String FRAGMENT_TYPE = "fragmentType";
    public static final String TITLE = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        Bundle params = getIntent().getExtras();
        if (params.containsKey(TITLE)){
            setTitle(params.getString(TITLE));
        }
        if (savedInstanceState == null) {
            Fragment fragment = getNewFragment(params);
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .commit();
            }
        }
    }

    private Fragment getNewFragment(Bundle params) {
        if (params == null) {
            throw new IllegalArgumentException("params cannot be null");
        }
        if (!params.containsKey(FRAGMENT_TYPE)) {
            throw new IllegalArgumentException("unknown fragment type parameter");
        }
        int ftType = params.getInt(FRAGMENT_TYPE);
        ListFragments fragmentsTypes[] = ListFragments.values();
        if (ftType < 0 || ftType > fragmentsTypes.length ){
            throw new IllegalArgumentException("unknown fragment type parameter");
        }
        ListFragments fragmentType = fragmentsTypes[ftType];
        Fragment fragment = null;
        switch (fragmentType){
            case AUDIOS:{
                break;
            }
            case VIDEOS:{

                break;
            }
            case FOLLOWERS:{

                break;
            }
            case FRIENDS:{
                fragment = FriendsFragment.newInstance(params);
                break;
            }
            case GROUPS:{

                break;
            }
            case PHOTOS:{

                break;
            }
        }
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
