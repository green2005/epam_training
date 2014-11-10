package com.samples.epamtraining.viewpagersample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import News.NewsItem;

public class DetailFragment extends Fragment {
    private Pages mPageType;
    private Object mPageContent;

    public DetailFragment() {
        super();
    }

    public  static DetailFragment newInstance(Pages pageType, Object pageContent){
        DetailFragment fragment = null;
        if (pageContent instanceof String) {
            fragment = new DetailFragment();
            fragment.mPageContent = (String) pageContent;
        } else
        if (pageContent instanceof NewsItem){
            fragment = new DetailFragment ();
            fragment.mPageContent = (NewsItem) pageContent;
        }
        fragment.mPageType = pageType;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView;
        if (mPageContent == null) {return null;};
        if (mPageType == Pages.NEWS){
            contentView =  inflater.inflate(R.layout.newsitem ,null);
            NewsItem newsItem = (NewsItem) mPageContent;
            ((TextView)( contentView.findViewById(R.id.tvDate))).setText(newsItem.getDate());
            ((TextView)( contentView.findViewById(R.id.tvTitle))).setText(newsItem.getTitle());
            ((TextView)( contentView.findViewById(R.id.tvAuthor))).setText(newsItem.getAuthor());
            ((TextView)( contentView.findViewById(R.id.tvText))).setText(newsItem.getText());
        } else
        {
            contentView =  inflater.inflate(R.layout.simplelayout ,null);
            ((TextView)( contentView.findViewById(R.id.simpleTextView))).setText(mPageContent.toString());
        }
        return contentView;
    }
}
