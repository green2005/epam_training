package com.samples.epamtraining.viewpagersample;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import News.NewsAdapter;
import News.NewsItem;


public class ListViewFragment extends Fragment implements DataProviderHandler {
    private Pages mPageName;
    private DataProvider mDataProvider;
    private BaseAdapter mAdapter;
    private List items;
    ListView listView;
    private static  String SCROLLPOS = "scrollPos";
    private static  String PAGENAME = "pageName";
    //private String mPageName;

    private OnFragmentInteractionListener mListener;


    public static ListViewFragment newInstance(Pages pageName, DataProvider dataProvider) throws IllegalArgumentException {
        ListViewFragment fragment = new ListViewFragment();

        fragment.setArguments(null);
        if (dataProvider == null) {
            throw new IllegalArgumentException("DataProvider parameter cannot be null");
        }
        if (pageName == null) {
            throw new IllegalArgumentException("pageName parameter cannot be null");
        }
        fragment.setmDataProvider(dataProvider);
        fragment.setPageName(pageName);
        return fragment;
    }

    public ListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    private void setmDataProvider(DataProvider provider) {
        this.mDataProvider = provider;
    }

    private void setPageName(Pages pageName) {
        this.mPageName = pageName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_listview, container, false);
        if (savedInstanceState != null){
            if (savedInstanceState.containsKey(PAGENAME))
                mPageName = (Pages)savedInstanceState.getSerializable(PAGENAME);
        }
        if (mPageName == null) return null;
        listView = (ListView) fragmentView.findViewById(R.id.pagerListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object clickedItem = items.get(position);
                if (mListener != null) {
                    mListener.onItemClick(clickedItem, mPageName, ListViewFragment.this);
                }
            }
        });

        switch (mPageName) {
            case NEWS: {
                items = new ArrayList<NewsItem>();
                mAdapter = new NewsAdapter(items, this.getActivity());
                break;
            }
            default: {
                items = new ArrayList<String>();
                mAdapter = new SimpleAdapter(items, this.getActivity());
                break;
            }
        }
        listView.setAdapter(mAdapter);
        mListener.fillDataForPage(mPageName, items, this);
        //mDataProvider.fillDataForPage(mPageName, items, this);
        return fragmentView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
            // ListView listView = (ListView)this.getView().findViewById(R.id.pagerListView);

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDataLoaded() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onException(Exception e) {
        //throw  e;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if ((savedInstanceState != null) && (listView != null)) {
            int pos = savedInstanceState.getInt(SCROLLPOS);
            listView.setSelectionFromTop(pos, 0);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putSerializable(PAGENAME,mPageName);

            if (listView != null) {
                int pos = listView.getFirstVisiblePosition();
                outState.putInt(SCROLLPOS, pos);
            }
        }

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener<ItemClicked> {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);

        public void onItemClick(ItemClicked item, Pages pageName, Fragment fragment);
        public void  fillDataForPage(final Pages pageName, final List items, final DataProviderHandler dataHadler);
    }

}
