package com.epamtraining.vklite.fragments;

import android.view.View;
import android.widget.AdapterView;

import com.epamtraining.vklite.R;
import com.epamtraining.vklite.fragments.itemsWidgetHolder.ListViewWidgetHolder;
import com.epamtraining.vklite.fragments.itemsWidgetHolder.ViewItemsWidgetHolder;
import com.epamtraining.vklite.imageLoader.ImageLoader;

public abstract class BaseVKListViewFragment extends BaseVKFragment implements AdapterView.OnItemClickListener{

    public ViewItemsWidgetHolder getItemsWidgetHolder(View parentView, ImageLoader imageLoader){
        ViewItemsWidgetHolder holder =new ListViewWidgetHolder(getListViewResourceId(), parentView, imageLoader);
        holder.setOnItemClickListener(this);
        return holder;
    }

    //could be overriden in child classes
    protected int getListViewResourceId(){
        return R.id.itemsList;
    }
}
