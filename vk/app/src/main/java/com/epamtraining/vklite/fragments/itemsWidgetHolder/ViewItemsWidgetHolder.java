package com.epamtraining.vklite.fragments.itemsWidgetHolder;


import android.view.View;
import android.widget.AdapterView;

import com.epamtraining.vklite.adapters.BoItemAdapter;

public  interface ViewItemsWidgetHolder {
     void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener);
     void addFooterView(View view);
      void setVisibility(int visible);
     void setAdapter(BoItemAdapter adapter);
     void setFooterVisible(boolean isVisible);
}
