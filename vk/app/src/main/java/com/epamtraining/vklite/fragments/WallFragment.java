package com.epamtraining.vklite.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.widget.AdapterView;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.bo.BoItem;
import com.epamtraining.vklite.db.VKContentProvider;
import com.epamtraining.vklite.activities.PostDetailActivity;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.adapters.WallAdapter;
import com.epamtraining.vklite.db.WallDBHelper;
import com.epamtraining.vklite.processors.Processor;
import com.epamtraining.vklite.processors.WallProcessor;

public class WallFragment extends BoItemFragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        DataAdapterCallback {


    private final static String[] fields = new WallDBHelper().fieldNames();

    private WallAdapter mAdapter;
    private WallProcessor mProcessor;

    public static WallFragment getNewFragment() {
        WallFragment wallFragment = new WallFragment();
        return wallFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new WallAdapter(getActivity(), R.layout.item_post, null, getDataFields(), null, 0);
        mProcessor = new WallProcessor(getActivity());
    }


    public Processor getProcessor() {
        return mProcessor;
    }

    @Override
    public String getDataUrl(int offset, String next_id) {
        return Api.getWallUrl(getActivity(), offset + "");
    }

    @Override
    public Uri getContentsUri() {
        return WallDBHelper.CONTENT_URI;
    }

    @Override
    public int getLoaderId() {
        return LoaderManagerIds.WALL.getId();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = mAdapter.getCursor();
        if (cursor != null) {
            if (cursor.moveToPosition(position)) {
                String userId = CursorHelper.getString(cursor, WallDBHelper.OWNER_ID);
                String postId = CursorHelper.getString(cursor, WallDBHelper.POST_ID);
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra(WallDBHelper.POST_ID, postId);
                intent.putExtra(WallDBHelper.OWNER_ID, userId);
                intent.putExtra(PostDetailFragment.WALL, true);
                startActivity(intent);
            }
        }
    }
}
