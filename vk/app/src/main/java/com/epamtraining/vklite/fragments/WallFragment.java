package com.epamtraining.vklite.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.DataSource;
import com.epamtraining.vklite.ErrorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.adapters.NewsAdapter;
import com.epamtraining.vklite.adapters.WallAdapter;
import com.epamtraining.vklite.processors.NewsProcessor;
import com.epamtraining.vklite.processors.Processor;
import com.epamtraining.vklite.processors.WallProcessor;

public class WallFragment  extends BoItemFragment implements
         LoaderManager.LoaderCallbacks<Cursor>,
        DataAdapterCallback {



    private final static String[] fields = new String[]{VKContentProvider.WALL_COLUMN_ID ,VKContentProvider.WALL_COLUMN_ITEM_ID, VKContentProvider.WALL_COLUMN_IMAGE_URL
            , VKContentProvider.WALL_COLUMN_DATE, VKContentProvider.WALL_COLUMN_URL, VKContentProvider.WALL_COLUMN_TEXT,
            VKContentProvider.WALL_COLUMN_RAW_DATE, VKContentProvider.WALL_COLUMN_USERNAME, VKContentProvider.WALL_COLUMN_USERIMAGE};

    private WallAdapter mAdapter;
    private WallProcessor mProcessor;

    public static WallFragment getNewFragment(){
        WallFragment wallFragment = new WallFragment();
        return wallFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mAdapter = new WallAdapter(getActivity(), R.layout.post_listview_item, null, getDataFields(), null, 0);
        mProcessor = new WallProcessor(getActivity());
    }

    @Override
    public FragmentType getItemFragmentType() {
        return FragmentType.WALLFRAGMENT;
    }

    public Processor getProcessor(){
       return mProcessor;
    }

    @Override
    public String[] getDataFields() {
        return fields;
    }

    @Override
    public BoItemAdapter getAdapter() {
        return mAdapter;
    }
}
