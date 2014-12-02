package com.epamtraining.vklite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ImageView;

import com.epamtraining.vklite.imageLoader.ImageLoader;
import com.epamtraining.vklite.processors.FriendsProcessor;
import com.epamtraining.vklite.processors.NewsProcessor;
import com.epamtraining.vklite.processors.Processor;
import com.epamtraining.vklite.auth.AuthHelper;
import com.epamtraining.vklite.fragments.BoItemFragment;
import com.epamtraining.vklite.fragments.FragmentDataProvider;
import com.epamtraining.vklite.fragments.FriendsFragment;
import com.epamtraining.vklite.fragments.NewsFragment;

import java.util.List;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, FragmentDataProvider

{

     private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private String token;
    private ImageLoader mImageLoader;
    //  private Queue<String> queue = Collections.asLifoQueue(new ArrayDeque<String>());;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra(AuthHelper.TOKEN)) {
            token = getIntent().getStringExtra(AuthHelper.TOKEN);
        } else {
            //????
        }

        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mImageLoader = new ImageLoader(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageLoader.clear();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        boolean fragmentPopedBack = false;
        Processor processor;
        String title = "";
        String[] pages = getResources().getStringArray(R.array.pages);

        if (position < pages.length) {
             Fragment fragment = null;

            fragment = getSupportFragmentManager().findFragmentByTag(position + "");
            if (fragment != null) {
                popFragment(position);
                fragmentPopedBack = true;
            }
            else {
                if (pages[position].equals(getResources().getString(R.string.friends))) {
                   fragment = FriendsFragment.getFriendsFragment(this, token);

                } else
                if (pages[position].equals(getResources().getString(R.string.news))) {
                    fragment = NewsFragment.getNewsFragment(this, token);
                }
                else{

                }
            }

            if ((fragment != null)&&(!fragmentPopedBack)) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, fragment, position + "");
                ft.addToBackStack(position + "");
                ft.commit();
            }
        }
    }

    private boolean popFragment(int position){
        getSupportFragmentManager().popBackStack(position+"",0);
        return getSupportFragmentManager().popBackStackImmediate(position + "", 0);
    }

    public void onSectionAttached(int number) {
        String[] pages = getResources().getStringArray(R.array.pages);
        if (number <= pages.length) {
            mTitle = pages[number - 1];
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();

            MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setQueryHint(
                    getResources().getString(R.string.search));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                   // Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return true;
                }
            });

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void fillData(final List list, final BoItemFragment fragment) {
        DataSource.DataSourceCallbacks callbacks = new DataSource.DataSourceCallbacks() {
            @Override
            public void onError(final Exception e) {
                String errorMessage = e.getMessage();
                if (e.getClass().equals(java.net.UnknownHostException.class)){
                    errorMessage = getResources().getString(R.string.checkInetConnection);
                };
                if (TextUtils.isEmpty(errorMessage))
                    errorMessage = e.toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setNegativeButton(getString(R.string.ok),new AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        fragment.onError(e);
                    }
                } );
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
            }
        };


        if (fragment instanceof FriendsFragment){
           new DataSource(new FriendsProcessor(token, this), callbacks).fillData(DataSource.DataLocation.WEB, this);
        } else
        if (fragment instanceof  NewsFragment){
            new DataSource(new NewsProcessor( token, this), callbacks).fillData(DataSource.DataLocation.WEB, this);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
