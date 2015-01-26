package com.epamtraining.vklite.fragments;

import android.view.View;
import android.widget.AdapterView;

import com.epamtraining.vklite.fragments.wrappers.CollectionViewWrapper;
import com.epamtraining.vklite.fragments.wrappers.ListViewWrapper;
import com.epamtraining.vklite.imageloader.ImageLoader;

public abstract class BaseListViewFragment extends BaseFragment implements AdapterView.OnItemClickListener{
    private ListViewWrapper mWrapper;

    public CollectionViewWrapper newInstanceCollectionViewWrapper(View parentView, ImageLoader imageLoader){
        mWrapper = new ListViewWrapper(getListViewResourceId(), parentView, imageLoader);
        mWrapper.setOnItemClickListener(this);
        return mWrapper;
    }

    @Override
    protected ListViewWrapper getCollectionViewWrapper() {
        return mWrapper;
    }

    protected int getListViewResourceId(){
        return android.R.id.list;
    }
}
