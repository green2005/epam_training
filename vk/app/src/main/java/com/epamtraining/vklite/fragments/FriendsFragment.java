package com.epamtraining.vklite.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.epamtraining.vklite.R;
import com.epamtraining.vklite.bo.BoItem;
import com.epamtraining.vklite.bo.Friend;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment implements BoItemFragment {
    private List<Friend> items;
    private FragmentDataProvider mProvider;
    private ListView mLvItems;
    private ProgressBar mProgressBar;
    private FriendsAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    FriendsFragment(){
        super();
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    public static Fragment getFriendsFragment(FragmentDataProvider provider)  {
        FriendsFragment friends = new FriendsFragment();

        friends.setProvider(provider);
        return friends;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, null);
        Toast.makeText(getActivity(),"onCreateView",Toast.LENGTH_SHORT).show();
        if (savedInstanceState!=null){
            String s = savedInstanceState.getString("test");
            Toast.makeText(getActivity(),s+"; Restored",Toast.LENGTH_SHORT).show();
        }
        items =new ArrayList<Friend>();
        mProgressBar = (ProgressBar)v.findViewById(R.id.progress);
        mLvItems = (ListView) v.findViewById(R.id.itemsList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                items.clear();
                adapter.notifyDataSetChanged();
                mProvider.fillData(items, FriendsFragment.this);
            }
        });
        showProgress(View.VISIBLE);
        adapter = new FriendsAdapter(getActivity(), items, mProvider);
        mLvItems.setAdapter(adapter);
        mProvider.fillData(items, this);
      return v;
    }

    @Override
    public void onError(Exception e) {
        if (mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
        showProgress(View.GONE);
    }

    private void setProvider(FragmentDataProvider provider){
        mProvider = provider;
    }

    @Override
    public void onDataLoaded() {
        showProgress(View.GONE);
        if (mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
        adapter.notifyDataSetChanged();
    }

    private void showProgress(int visible){
        mProgressBar.setVisibility(visible);
        if (mProgressBar.getVisibility() == View.VISIBLE){
           mLvItems.setVisibility(View.GONE);
        } else
        {mLvItems.setVisibility(View.VISIBLE);}
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Toast.makeText(getActivity(),"onsave",Toast.LENGTH_SHORT).show();
        super.onSaveInstanceState(outState);
        outState.putString("test","test1");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }
}
