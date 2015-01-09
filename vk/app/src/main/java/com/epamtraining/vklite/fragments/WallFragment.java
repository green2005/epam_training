package com.epamtraining.vklite.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.widget.AdapterView;

import com.epamtraining.vklite.Api;
import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.activities.PostDetailActivity;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.adapters.WallAdapter;
import com.epamtraining.vklite.processors.Processor;
import com.epamtraining.vklite.processors.WallProcessor;

public class WallFragment  extends BoItemFragment implements
         LoaderManager.LoaderCallbacks<Cursor>,
        DataAdapterCallback {



    private final static String[] fields = new String[]{VKContentProvider.WALL_COLUMN_ID ,VKContentProvider.WALL_COLUMN_ITEM_ID, VKContentProvider.WALL_COLUMN_IMAGE_URL
            , VKContentProvider.WALL_COLUMN_DATE, VKContentProvider.WALL_COLUMN_URL, VKContentProvider.WALL_COLUMN_TEXT,
            VKContentProvider.WALL_COLUMN_RAW_DATE, VKContentProvider.WALL_COLUMN_USERNAME, VKContentProvider.WALL_COLUMN_USERIMAGE,
            VKContentProvider.WALL_COLUMN_OWNER_ID, VKContentProvider.WALL_COLUMN_ITEM_ID
    };

    private WallAdapter mAdapter;
    private WallProcessor mProcessor;

    public static WallFragment getNewFragment(){
        WallFragment wallFragment = new WallFragment();
        return wallFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mAdapter = new WallAdapter(getActivity(), R.layout.item_post, null, getDataFields(), null, 0);
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
    public String getDataUrl(int offset, String next_id) {
        return Api.getWallUrl(getActivity(), offset + "");
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
        if (mAdapter.getCursor().moveToPosition(position)){
            String userId = CursorHelper.getString(mAdapter.getCursor(), VKContentProvider.WALL_COLUMN_OWNER_ID);
            String postId = CursorHelper.getString(mAdapter.getCursor(), VKContentProvider.WALL_COLUMN_ITEM_ID);
            Intent intent = new Intent(getActivity(), PostDetailActivity.class);
            intent.putExtra(VKContentProvider.WALL_COLUMN_ITEM_ID, postId);
            intent.putExtra(VKContentProvider.WALL_COLUMN_OWNER_ID, userId);
            intent.putExtra(PostDetailFragment.WALL, true);
            startActivity(intent);
        }
    }
}
