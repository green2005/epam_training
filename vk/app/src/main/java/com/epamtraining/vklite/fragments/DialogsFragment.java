package com.epamtraining.vklite.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.epamtraining.vklite.R;
import com.epamtraining.vklite.adapters.BoItemAdapter;
import com.epamtraining.vklite.adapters.DataAdapterCallback;
import com.epamtraining.vklite.adapters.DialogsAdapter;
import com.epamtraining.vklite.processors.DialogsProcessor;
import com.epamtraining.vklite.processors.Processor;

public class DialogsFragment extends  BoItemFragment
       implements LoaderManager.LoaderCallbacks<Cursor> , DataAdapterCallback
{

    private static final String[] fields= new String[] {
            //vkContentProviderFields go here

    };

    private BoItemAdapter mAdapter;
    private Processor mProcessor;


    @Override
    public FragmentType getItemFragmentType() {
        return FragmentType.DIALOGSFRAGMENT;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mAdapter = new DialogsAdapter(getActivity(), R.layout.post_listview_item, null, getDataFields(), null, 0);
        mProcessor = new DialogsProcessor(getActivity());
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

    public static DialogsFragment getNewFragment( ){
        DialogsFragment dialogsFragment = new DialogsFragment();
        return dialogsFragment;
    }
}
