package com.epamtraining.vklite.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.adapters.NewsAdapter;

public class NewsFragment extends BoItemFragment implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>,
        DataAdapterCallback {

    private final static String[] fields = new String[]{VKContentProvider.NEWS_COULMN_ID, VKContentProvider.NEWS_COLUMN_IMAGE_URL
            , VKContentProvider.NEWS_COLUMN_DATE, VKContentProvider.NEWS_COLUMN_URL, VKContentProvider.NEWS_COLUMN_TEXT,
            VKContentProvider.NEWS_COLUMN_RAW_DATE, VKContentProvider.NEWS_COLUMN_POST_ID, VKContentProvider.NEWS_COLUMN_USERNAME,
            VKContentProvider.NEWS_COLUMN_USERIMAGE};

    public static Fragment getNewsFragment(Activity activity, FragmentDataProvider dataProvider ) {
        NewsFragment fragment = new NewsFragment();
        NewsAdapter adapter =  new NewsAdapter(activity, R.layout.post_listview_item, null, fields, null, 0);
        fragment.init(fields, VKContentProvider.NEWS_CONTENT_URI, adapter, dataProvider);
        return fragment;
    }
}
