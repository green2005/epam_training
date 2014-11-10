package com.samples.epamtraining.viewpagersample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SimpleAdapter extends BaseAdapter {
    private List<String> mItems;
    private Context mContext;

    SimpleAdapter( List<String> items, Context context){
        this.mItems = items;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cnView = convertView;
        SimpleHolder holder;
        if (cnView == null){
            cnView = LayoutInflater.from(mContext).inflate(R.layout.simplelayout, null);
            holder = new SimpleHolder();
            cnView.setTag(holder);
            holder.mTextView = (TextView)cnView.findViewById(R.id.simpleTextView);
        }else
        {
            holder = (SimpleHolder)cnView.getTag();
        }
        holder.mTextView.setText(mItems.get(position));
        return cnView;
    }

    private class SimpleHolder{
        TextView mTextView;

    }
}
