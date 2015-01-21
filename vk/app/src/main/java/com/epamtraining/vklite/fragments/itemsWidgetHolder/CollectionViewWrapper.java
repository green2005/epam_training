package com.epamtraining.vklite.fragments.itemsWidgetHolder;

import android.view.View;
import android.widget.AdapterView;

import com.epamtraining.vklite.adapters.BoItemAdapter;

//CollectionViewWrapper<CollectionView, Adapter, OnClickListener>
//change to Abstract class
public interface CollectionViewWrapper {
     void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener);
     void addFooterView(View view);
     void setVisibility(int visible);
     void setAdapter(BoItemAdapter adapter);
     void setFooterVisible(boolean isVisible);
}
