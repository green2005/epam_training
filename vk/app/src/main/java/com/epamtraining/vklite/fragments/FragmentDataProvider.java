package com.epamtraining.vklite.fragments;

import android.widget.ImageView;

import com.epamtraining.vklite.bo.BoItem;

import java.util.List;

public interface FragmentDataProvider<Item> {
   public void loadData(BoItemFragment fragment, int offset, String lastId); //для стены - offset, для новостей  - ID
}
