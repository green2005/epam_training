package com.epamtraining.vklite.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.app.Fragment;

import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.adapters.WallAdapter;

public class WallFragment  extends BoItemFragment implements
        android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>,
        DataAdapterCallback {

    private final static String[] fields = new String[]{VKContentProvider.WALL_COLUMN_ID ,VKContentProvider.WALL_COLUMN_ITEM_ID, VKContentProvider.WALL_COLUMN_IMAGE_URL
            , VKContentProvider.WALL_COLUMN_DATE, VKContentProvider.WALL_COLUMN_URL, VKContentProvider.WALL_COLUMN_TEXT,
            VKContentProvider.WALL_COLUMN_RAW_DATE, VKContentProvider.WALL_COLUMN_USERNAME, VKContentProvider.WALL_COLUMN_USERIMAGE};

    public static Fragment getWallFragment(Activity activity, FragmentDataProvider dataProvider ) {
        WallFragment fragment = new WallFragment();
        WallAdapter adapter =  new WallAdapter(activity, R.layout.post_listview_item, null, fields, null, 0);
        fragment.init(fields, VKContentProvider.WALL_CONTENT_URI, adapter, dataProvider);
        return fragment;
    }}
