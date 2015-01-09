package com.epamtraining.vklite;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.epamtraining.vklite.adapters.DrawingArrayAdapter;

import com.epamtraining.vklite.fragments.FragmentType;
import com.epamtraining.vklite.imageLoader.ImageLoader;
import com.epamtraining.vklite.auth.AuthHelper;
import com.epamtraining.vklite.fragments.BoItemFragment;

import java.util.ArrayList;
import java.util.List;

//TODO move to activities
public class MainActivity extends ActionBarActivity {
    public static final int REQUEST_CODE_CHOOSE_FRIEND = 1;
    public static final String FRAGMENT_REQUEST = "request";

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;

    private FragmentType[] mFragmentTypes;
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
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new DrawingArrayAdapter(this, mFragmentTypes));
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
            //TODO change to enum
            //FragmentType.values()[0] or
            //FragmentType.NEWSFRAGMENT
            selectItem(0);
        }
    }


    private void initNavigationDrawer() {
        List<FragmentType> list = new ArrayList<>();
        for (FragmentType ft : FragmentType.values()) {
            if (ft.getIsMainActivityFragment()) {
                list.add(ft);
            }
        }
        mFragmentTypes = new FragmentType[list.size()];
        list.toArray(mFragmentTypes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            super.onActivityResult(requestCode, resultCode, data);

        }

        private class DrawerItemClickListener implements ListView.OnItemClickListener {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO
                //FragmentType.values()[position]
                selectItem(position);
            }
        }

    //TODO change to enum
    private void selectItem(int position) {
        if (position < mFragmentTypes.length) {
            BoItemFragment fragment = mFragmentTypes[position].getNewFragment();
            if ((fragment != null)) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                //TODO remove unused code
                ft.replace(R.id.container, fragment, position + "");
                ft.commit();
            }
        }
        mTitle = getResources().getString(mFragmentTypes[position].getNameResourceId());
        getSupportActionBar().setTitle(mTitle);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TODO rename static getImageLoader
        ImageLoader imageLoader = ImageLoader.getImageLoader(getApplication());
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
                //TODO refactoring, move to variable
                if (getSupportFragmentManager() != null){
                    for (Fragment ft: getSupportFragmentManager().getFragments()){
                        //TODO change to interface
                        if (ft instanceof BoItemFragment){
                            ((BoItemFragment)ft).refreshData();
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

                //TODO поиск
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
