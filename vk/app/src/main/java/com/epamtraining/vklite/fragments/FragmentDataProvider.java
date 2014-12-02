package com.epamtraining.vklite.fragments;

import android.widget.ImageView;

import com.epamtraining.vklite.bo.BoItem;

import java.util.List;

public interface FragmentDataProvider<Item> {
   public void fillData(List<Item> items, BoItemFragment fragment );
}