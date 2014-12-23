package com.epamtraining.vklite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
//import android.support.v7.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.epamtraining.vklite.fragments.WallFragment;
import com.epamtraining.vklite.imageLoader.ImageLoader;
import com.epamtraining.vklite.processors.FriendsProcessor;
import com.epamtraining.vklite.processors.NewsProcessor;
import com.epamtraining.vklite.auth.AuthHelper;
import com.epamtraining.vklite.fragments.BoItemFragment;
import com.epamtraining.vklite.fragments.FragmentDataProvider;
import com.epamtraining.vklite.fragments.FriendsFragment;
import com.epamtraining.vklite.fragments.NewsFragment;
import com.epamtraining.vklite.processors.WallProcessor;


public class MainActivity extends ActionBarActivity
        implements FragmentDataProvider {

    private CharSequence mTitle;
    private CharSequence mDrawerTitle;
    private String token;
    private ImageLoader mImageLoader;
    private NavigationDrawerItem[] mPageTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTitle  = mDrawerTitle  = getTitle();
        initNavigationDrawer();
        if (getIntent().hasExtra(AuthHelper.TOKEN)) {
            token = getIntent().getStringExtra(AuthHelper.TOKEN);
        } else {
            //Непонятно, почему токена может не быть, на всякий случай обработаем
            //ошибки авторизации обрабатываются выше
            showErrorAndFinish(getResources().getString(R.string.auth_error));
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setAdapter(new DrawingArrayAdapter(this,  mPageTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //ActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes)

        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(
                 this,
                mDrawerLayout,
                //R.drawable.ic_drawer,
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
            selectItem(0);
        }

        try {
            mImageLoader = ImageLoader.getImageLoader(getApplication());
        } catch (Exception e) {
            showErrorAndFinish(e.getMessage());
        }
    }

    private void initNavigationDrawer(){
        String []titles = getResources().getStringArray(R.array.pages);
        TypedArray imgs = getResources().obtainTypedArray(R.array.pageimages);
        mPageTitles = new NavigationDrawerItem[titles.length];
        int i = 0;
        for(String title : titles){
            mPageTitles[i++] = new NavigationDrawerItem(title, imgs.getResourceId(i - 1, 0));
        }
    }

    private void showErrorAndFinish(String errorMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setTitle(R.string.error);
        dialog.setMessage(getResources().getString(R.string.auth_error));
        dialog.setCancelable(false);
        dialog.show();
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        if (position < mPageTitles.length) {
            Fragment fragment = null;
            fragment = getSupportFragmentManager().findFragmentByTag(position + "");
            if (fragment != null) {
                popFragment(position);
            } else {
                if (mPageTitles[position].title.equals(getResources().getString(R.string.friends))) {
                    fragment = FriendsFragment.getFriendsFragment(this, this);
                } else if (mPageTitles[position].title.equals(getResources().getString(R.string.news))) {
                    fragment = NewsFragment.getNewsFragment(this, this);
                } else if (mPageTitles[position].title.equals(getResources().getString(R.string.wall))){
                    fragment = WallFragment.getWallFragment(this, this);
                };
                if ((fragment != null)) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.container, fragment, position + "");
                    ft.addToBackStack(Integer.toString(position));
                    ft.commit();
                }
            }
            mTitle = mPageTitles[position].title;
            getSupportActionBar().setTitle(mTitle);
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageLoader.clear();
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

    private boolean popFragment(int position) {
        if (mPageTitles != null) {
            getSupportFragmentManager().popBackStack(position + "", 0);
            mTitle = mPageTitles[position].title;
            getSupportActionBar().setTitle(mTitle);
            return getSupportFragmentManager().popBackStackImmediate(position + "", 0);
        } else {
            return false;
        }
    }

    public void onSectionAttached(int number) {
        String[] pages = getResources().getStringArray(R.array.pages);
        if (number <= pages.length) {
            mTitle = pages[number - 1];
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
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


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
       boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void loadData(final BoItemFragment fragment, final int offset, String lastId) {
        DataSource.DataSourceCallbacks callbacks = new DataSource.DataSourceCallbacks() {
            @Override
            public void onError(final Exception e) {
                String errorMessage = e.getMessage();
                if (e.getClass().equals(java.net.UnknownHostException.class)) {
                    errorMessage = getResources().getString(R.string.checkInetConnection);
                };
                if (TextUtils.isEmpty(errorMessage))
                    errorMessage = e.toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setNegativeButton(getString(R.string.ok), new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        fragment.onError(e);
                    }
                });
                builder.setTitle(getResources().getString(R.string.error));
                builder.setCancelable(false);
                builder.setMessage(errorMessage);
                builder.create().show();
            }

            @Override
            public void onLoadEnd() {
                fragment.onDataLoaded();
            }

            @Override
            public void onBeforeStart() {
                fragment.onBeforeStart();
            }
        };


        if (fragment instanceof FriendsFragment) {
            new DataSource(new FriendsProcessor(token, this), callbacks).fillData(this);
        } else if (fragment instanceof NewsFragment) {
            new DataSource(new NewsProcessor(token, this, lastId), callbacks).fillData(this);
        } else
        if (fragment instanceof WallFragment){
            new DataSource(new WallProcessor(token, this, offset), callbacks).fillData(this);
        }
    }

    private class NavigationDrawerItem {
        private CharSequence title;
        private int imageResId;

        NavigationDrawerItem(CharSequence title, int imageResId){
            this.title = title;
            this.imageResId = imageResId;
        }
    }

    private class DrawingArrayAdapter extends BaseAdapter {
        LayoutInflater mInflater;
        NavigationDrawerItem[] mItems;

        public DrawingArrayAdapter(Context context, NavigationDrawerItem[] objects) {
            super();
            //super(context, R.layout.drawer_list_item, R.id.itemtext, objects);
            mInflater = LayoutInflater.from(getApplicationContext());
            mItems = objects;
        }

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View cnView = convertView;
            ViewHolder holder;
            if (cnView == null){
                cnView = mInflater.inflate(R.layout.drawer_list_item, null);
                holder = new ViewHolder();
                cnView.setTag(holder);
                holder.textView = (TextView) cnView.findViewById(R.id.itemtext);
                holder.imageView = (ImageView) cnView.findViewById(R.id.img);
              } else
            {
                holder = (ViewHolder)cnView.getTag();
            }
            holder.textView.setText( mItems[position].title);
            holder.imageView.setImageResource(mItems[position].imageResId);
            //holder.textView.setCompoundDrawablesWithIntrinsicBounds(mItems[position].imageResId, 0, 0, 0);
            return cnView;
        }

        private class ViewHolder{
            TextView textView;
            ImageView imageView;
        }
    }
}
