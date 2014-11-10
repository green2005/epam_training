package com.samples.epamtraining.viewpagersample;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;


public class MainActivity extends FragmentActivity implements ListViewFragment.OnFragmentInteractionListener {
    private ViewPager mPager;
    private PagerAdapter adapter;
    public static String NEWS_PAGE = "News";
    public static String FRIENDS_PAGE = "Friends";
    public static String MESSAGES_PAGE = "Messages";
    public static String FAVORITIES_PAGE = "Favorities";
    public static String PROFILE_PAGE = "Profile";
    private static int PORTRAIT = 0;
    private static  String DETAILFRAGMENT = "detailFragment";

    private final static String[] pageTitles = {NEWS_PAGE, FRIENDS_PAGE, MESSAGES_PAGE, FAVORITIES_PAGE, PROFILE_PAGE};
    private final static int PAGE_COUNT = pageTitles.length;
    private DataProvider mDataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //in case of portrait - landscape rotate
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT);
        if (fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(fragment);
            ft.commit();
            fragment = null;
        }
        mPager = (ViewPager) findViewById(R.id.mainPager);
        adapter = new MainPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mDataProvider = new DataProvider(this);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                // Toast.makeText(MainActivity.this,i+"", Toast.LENGTH_SHORT).show();
                //  Toast.makeText(this,""+i,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public DataProvider getDataProvider(){
        return mDataProvider;
    };

    @Override
    public void onItemClick(Object item, Pages pageName, Fragment fragment) {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        if (rotation == PORTRAIT) {
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra("item",(Serializable)item);
            i.putExtra("itemType",pageName);
            startActivity(i);
        } else
        {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment detailFragment = DetailFragment.newInstance(pageName, item);
            ft.replace(R.id.DetailFrame, detailFragment, DETAILFRAGMENT);
            ft.commit();
        }
    }

    @Override
    public void fillDataForPage(Pages pageName, List items, DataProviderHandler dataHadler) {
        mDataProvider.fillDataForPage(pageName, items, dataHadler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDataProvider != null) {
            mDataProvider.clearCache();
        }
    }

    private class MainPagerAdapter extends FragmentPagerAdapter {
        MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fm;
            Pages page = pageTitleToPage(pageTitles[i]);
            fm = ListViewFragment.newInstance(page, mDataProvider);
            return fm;
        }

        public Pages pageTitleToPage(String pageName) {
            if (pageName.equalsIgnoreCase(NEWS_PAGE)) {
                return Pages.NEWS;
            } else if (pageName.equalsIgnoreCase(FRIENDS_PAGE)) {
                return Pages.FRIENDS;
            } else if (pageName.equalsIgnoreCase(FAVORITIES_PAGE)) {
                return Pages.FAVORITIES;
            } else if (pageName.equalsIgnoreCase(MESSAGES_PAGE)) {
                return Pages.MESSAGES;
            } else if (pageName.equalsIgnoreCase(PROFILE_PAGE)) {
                return Pages.PROFILE;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles[position];
        }
    }

}
