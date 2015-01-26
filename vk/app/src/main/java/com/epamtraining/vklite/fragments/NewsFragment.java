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
import com.epamtraining.vklite.activities.PostDetailActivity;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.adapters.NewsAdapter;
import com.epamtraining.vklite.db.NewsDBHelper;
import com.epamtraining.vklite.processors.NewsProcessor;
import com.epamtraining.vklite.processors.Processor;


public class NewsFragment extends BaseListViewFragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        DataAdapterCallback  {

    private final static String[] FIELDS = NewsDBHelper.FIELDS;
    private BoItemAdapter mAdapter;
    private Processor mProcessor;

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new NewsAdapter(getActivity(), R.layout.item_post, null, getDataFields(), null, 0);
        mProcessor = new NewsProcessor(getActivity());
    }

    @Override
    public String[] getDataFields() {
        return FIELDS;
    }

    @Override
    public BoItemAdapter getAdapter() {
       return mAdapter;
    }

    @Override
    public Processor getProcessor() {
        return mProcessor;
    }

    @Override
    public String getDataUrl(int offset, String nextId) {
        return Api.getNewsUrl(getActivity(), nextId);
    }

    @Override
    public Uri getContentsUri() {
        return NewsDBHelper.CONTENT_URI;
    }

    @Override
    public int getLoaderId() {
        return LoaderManagerIds.NEWS.ordinal();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       if (mAdapter.getCursor().moveToPosition(position)){
          String userId = CursorHelper.getString(mAdapter.getCursor(), NewsDBHelper.OWNER_ID);
          String postId = CursorHelper.getString(mAdapter.getCursor(), NewsDBHelper.POST_ID);
          Intent intent = new Intent(getActivity(), PostDetailActivity.class);
          intent.putExtra( NewsDBHelper.POST_ID, postId);
          intent.putExtra( NewsDBHelper.OWNER_ID, userId);
          intent.putExtra( PostDetailFragment.WALL, false);
          startActivity(intent);
       }
    }
}
