package com.epamtraining.vklite.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.DataSource;
import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.adapters.NewsAdapter;
import com.epamtraining.vklite.processors.NewsProcessor;
import com.epamtraining.vklite.processors.Processor;


public class NewsFragment extends BoItemFragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        DataAdapterCallback {

    private final static String[] fields = new String[]{VKContentProvider.NEWS_COULMN_ID, VKContentProvider.NEWS_COLUMN_IMAGE_URL
            , VKContentProvider.NEWS_COLUMN_DATE, VKContentProvider.NEWS_COLUMN_URL, VKContentProvider.NEWS_COLUMN_TEXT,
            VKContentProvider.NEWS_COLUMN_RAW_DATE, VKContentProvider.NEWS_COLUMN_POST_ID, VKContentProvider.NEWS_COLUMN_USERNAME,
            VKContentProvider.NEWS_COLUMN_USERIMAGE, VKContentProvider.NEWS_COLUMN_NEXT_FROM};
    private BoItemAdapter mAdapter;
    private Processor mProcessor;

    public static NewsFragment getNewFragment( ) {
        NewsFragment newsFragment = new NewsFragment();
        return newsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new NewsAdapter(getActivity(), R.layout.post_listview_item, null, getDataFields(), null, 0);
        mProcessor = new NewsProcessor(getActivity());
    }

    @Override
    public FragmentType getItemFragmentType() {
        return FragmentType.NEWSFRAGMENT;
    }

    @Override
    public String[] getDataFields() {
        return fields;
    }

    @Override
    public BoItemAdapter getAdapter() {
       return mAdapter;
    }

    @Override
    public Processor getProcessor() {
        return mProcessor;
    }


}
