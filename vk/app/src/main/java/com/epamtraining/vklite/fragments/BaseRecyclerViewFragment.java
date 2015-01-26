package com.epamtraining.vklite.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.epamtraining.vklite.fragments.wrappers.CollectionViewWrapper;
import com.epamtraining.vklite.fragments.wrappers.RecyclerViewWrapper;
import com.epamtraining.vklite.imageloader.ImageLoader;

public abstract class BaseRecyclerViewFragment extends BaseFragment{
    private RecyclerViewWrapper mWrapper;

    public CollectionViewWrapper newInstanceCollectionViewWrapper(View parentView, ImageLoader imageLoader){
        mWrapper = new RecyclerViewWrapper(getListViewResourceId(), parentView, imageLoader);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mWrapper.getCollectionView().setLayoutManager(layoutManager);

        return mWrapper;
    }

    @Override
    protected RecyclerViewWrapper getCollectionViewWrapper() {
        return mWrapper;
    }

    protected int getListViewResourceId(){
        return android.R.id.list;
    }

}
