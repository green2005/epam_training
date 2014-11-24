package com.epamtraining.vklite.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.epamtraining.vklite.R;
import com.epamtraining.vklite.bo.News;
import com.epamtraining.vklite.os.ObjectSerializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment implements BoItemFragment {
    private List<News> mItems;
    private ProgressBar mProgressBar;
    private ListView mLvItems;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NewsAdapter adapter;
    private FragmentDataProvider mDataProvider;

    public static Fragment getNewsFragment(FragmentDataProvider dataProvider) {
        NewsFragment fragment = new NewsFragment();
        fragment.mDataProvider = dataProvider;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news, null);
        mItems = new ArrayList<News>();
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);
        mLvItems = (ListView) v.findViewById(R.id.itemsList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mItems.clear();
                adapter.notifyDataSetChanged();
                mDataProvider.fillData(mItems, NewsFragment.this);
            }
        });
        showProgress(View.VISIBLE);
        adapter = new NewsAdapter(getActivity(), mItems);
        mLvItems.setAdapter(adapter);
        if (!restoreData()) mDataProvider.fillData(mItems, this);
        return v;
    }

    @Override
    public void onError(Exception e) {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        showProgress(View.GONE);
    }

    @Override
    public void onDataLoaded() {
        showProgress(View.GONE);
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mItems.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setMessage(getString(R.string.noNews));
            builder.setTitle(R.string.news);
            builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveData();
    }

    private void saveData() {
        //TODO
       /* SharedPreferences prefs = getActivity().getSharedPreferences("news", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (mItems.size() > 0) {
            try {
                editor.putString("items", ObjectSerializer.serialize(((Serializable) mItems)));
                int pos = mLvItems.getFirstVisiblePosition();
                mLvItems.sc
            }catch(Exception e){
                e.printStackTrace();
            }

        } else {
            editor.clear();
        }
        editor.apply();
        */
    }

    private boolean restoreData() {
      /*  SharedPreferences prefs = getActivity().getSharedPreferences("news", Context.MODE_PRIVATE);
        if (prefs != null) {
            if (prefs.contains("items")) {
                try {
                    mItems = (ArrayList<News>) com.epamtraining.vklite.os.ObjectSerializer.deserialize(prefs.getString("items", com.epamtraining.vklite.os.ObjectSerializer.serialize(new ArrayList<News>())));
                    if (prefs.contains("pos")) {
                        int pos = prefs.getInt("pos", 0);
                        adapter.notifyDataSetChanged();
                        mLvItems.smoothScrollToPosition(pos);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return  false;
                }
            }
        }*/
        return false;
    }

    private void showProgress(int visibility) {
        if (visibility == View.VISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mLvItems.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mLvItems.setVisibility(View.VISIBLE);
        }
    }
}
