package com.epamtraining.vklite.fragments;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.epamtraining.vklite.R;
import com.epamtraining.vklite.bo.News;

import java.util.List;

public class NewsAdapter extends BaseAdapter{
    private Context mContext;
    private List<News> mNewsItems;
    private LayoutInflater mInflater;

    public NewsAdapter( Context context, List<News> items){
        mNewsItems = items;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mNewsItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNewsItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView ;
        NewsHolder holder;
        if (v == null){
            v = mInflater.inflate(R.layout.news_listview_item, null);
            holder = new NewsHolder();
            holder.date = (TextView)v.findViewById(R.id.date);
            holder.text = (TextView)v.findViewById(R.id.text);
          //  holder.text.setMovementMethod(LinkMovementMethod.getInstance());

           // holder.text.setLinksClickable(true);
           // holder.text.setMovementMethod(new LinkMovementMethod());
          //  Linkify.addLinks(holder.text, )

            v.setTag(holder);
        } else
        holder = (NewsHolder) v.getTag();
        News item = mNewsItems.get(position);
        holder.date.setText(item.getDate());
        holder.text.setText(item.getText());
      //  Linkify.addLinks(holder.text, Linkify.ALL);

        holder.text.setMovementMethod(null);
        return v;
    }

    class NewsHolder{
        private TextView date;
        private TextView text;
    }
}
