package com.epamtraining.vklite.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.DataSource;
import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKApplication;
import com.epamtraining.vklite.adapters.DrawingArrayAdapter;
import com.epamtraining.vklite.auth.AuthHelper;
import com.epamtraining.vklite.fragments.FragmentMenuItem;
import com.epamtraining.vklite.fragments.Refreshable;
import com.epamtraining.vklite.imageloader.ImageLoader;
import com.epamtraining.vklite.processors.Processor;
import com.epamtraining.vklite.processors.UserInfoProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    public static final int REQUEST_CODE_CHOOSE_FRIEND = 1;
    public static final String FRAGMENT_REQUEST = "request";

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;

    private FragmentMenuItem[] mFragmentMenuItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTitle = mDrawerTitle = getTitle();
        initNavigationDrawer();
        if (getIntent().hasExtra(AuthHelper.TOKEN)) {
            String token = getIntent().getStringExtra(AuthHelper.TOKEN);
            String userId = getIntent().getStringExtra(AuthHelper.USER_ID);
            Api.setToken((VKApplication) (getApplication()), token);
            Api.setUserId((VKApplication) (getApplication()), userId);
            updateUserInfo(userId);
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new DrawingArrayAdapter(this, mFragmentMenuItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(FragmentMenuItem.NEWS_ITEM);
        }
    }

    private void initNavigationDrawer() {
           mFragmentMenuItems = FragmentMenuItem.values();
    }


    private void updateUserInfo(final String userId ){
        Processor processor = new UserInfoProcessor(this);
        DataSource ds = new DataSource(processor, new DataSource.DataSourceCallbacks() {
            @Override
            public void onError(Exception e) {
                ErrorHelper.showError(MainActivity.this, e);
            }

            @Override
            public void onLoadEnd(int recordsFetched) {
                SharedPreferences preferences;
                preferences = MainActivity.this.getSharedPreferences(UserInfoProcessor.USER_INFO,
                        MODE_PRIVATE);
                String userName = preferences.getString(UserInfoProcessor.USER_NAME, "");
                String userImage = preferences.getString(UserInfoProcessor.USER_IMAGE, "");
                Api.setUserInfo((VKApplication)getApplication() ,userName, userImage);
            }

            @Override
            public void onBeforeStart() {

            }
        });
        ds.fillData(Api.getUserInfo(userId), this);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position < mFragmentMenuItems.length) {
                selectItem(mFragmentMenuItems[position]);
            }
        }
    }

    private void selectItem(FragmentMenuItem fragmentMenuItem) {
        Fragment fragment = fragmentMenuItem.getNewFragment();
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, fragment, null);
            ft.commit();
        }
        mTitle = getResources().getString(fragmentMenuItem.getNameResourceId());
        getSupportActionBar().setTitle(mTitle);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageLoader imageLoader = ImageLoader.get(getApplication());
        if (imageLoader != null) {
            imageLoader.clear();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem renew = menu.findItem(R.id.action_refresh);
        renew.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager != null) {
                    for (Fragment ft : fragmentManager.getFragments()) {
                        if (ft instanceof Refreshable) {
                            ((Refreshable) ft).refreshData();
                        }
                    }
                }
                return true;
            }
        });
        /*MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(
                getResources().getString(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                 Toast.makeText(MainActivity.this, "Здесь будет поиск", Toast.LENGTH_SHORT).show();


                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {


                return true;
            }
        });
        */
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //  menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
