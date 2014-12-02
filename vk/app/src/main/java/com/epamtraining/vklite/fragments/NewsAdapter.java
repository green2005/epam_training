package com.epamtraining.vklite.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.imageLoader.ImageLoader;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.bo.News;

import java.util.List;

public class NewsAdapter extends SimpleCursorAdapter{
    private static final int MAX_IMAGE_WIDTH = 540;
    private Context mContext;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private int mImageSize;

    private int news_col = -1;
    private int id_col = -1;
    private int imageUrl_col = -1;
    private int date_col = -1;
    private int url_col = -1;

    public NewsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
        mContext =  context;
        mImageLoader = new ImageLoader(context);
   }

    public void initImageSizes(Activity activity)
    {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        mImageSize = Math.min(screenHeight, screenWidth);
        mImageSize = Math.min(mImageSize, MAX_IMAGE_WIDTH);
    }

    @Override
    public int getCount() {
        if (getCursor() !=null)
        return getCursor().getCount(); else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return getCursor();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void fillColumnIndexes(){
        /*private int news_col = -1;
        private int id_col = -1;
        private int imageUrl_col = -1;
        private int date_col = -1;
        */
        news_col = getCursor().getColumnIndex(VKContentProvider.NEWS_COLUMN_TEXT);
        id_col = getCursor().getColumnIndex(VKContentProvider.NEWS_COULMN_ID);
        imageUrl_col = getCursor().getColumnIndex(VKContentProvider.NEWS_COLUMN_IMAGE_URL);
        date_col = getCursor().getColumnIndex(VKContentProvider.NEWS_COLUMN_DATE);
        url_col = getCursor().getColumnIndex(VKContentProvider.NEWS_COLUMN_URL);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getCursor() == null) return null;
        View v = convertView ;
        NewsHolder holder;
        if (v == null){
            v = mInflater.inflate(R.layout.news_listview_item, null);
            holder = new NewsHolder();
            holder.date = (TextView)v.findViewById(R.id.date);
            holder.text = (TextView)v.findViewById(R.id.text);
            holder.image = (ImageView)v.findViewById(R.id.image);

            holder.image.getLayoutParams().width = mImageSize;
            holder.image.getLayoutParams().height = mImageSize;


            holder.url = (TextView)v.findViewById(R.id.url);
            holder.url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url =(String) v.getTag();
                    if (!TextUtils.isEmpty(url)){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        mContext.startActivity(browserIntent);
                    }
                }
            });
            v.setTag(holder);
        } else
        holder = (NewsHolder) v.getTag();
        //News item = mNewsItems.get(position);
        getCursor().moveToPosition(position);

        if (news_col == -1){
            fillColumnIndexes();
        }

        holder.date.setText(getCursor().getString(date_col));
        holder.text.setText(getCursor().getString(news_col));
        if (TextUtils.isEmpty(getCursor().getString(imageUrl_col))){
            holder.image.setVisibility(View.GONE);
        } else
        {
            holder.image.setVisibility(View.VISIBLE);
            mImageLoader.loadImage(holder.image, getCursor().getString(imageUrl_col));
        }

        if (!TextUtils.isEmpty(getCursor().getString(url_col))){
            holder.url.setVisibility(View.GONE);
        }else
        {
            holder.url.setVisibility(View.VISIBLE);
            holder.url.setText(getCursor().getString(url_col));
            holder.url.setTag(getCursor().getString(url_col));
        }
        holder.text.setMovementMethod(null);
        return v;
    }

    public void onStop(){
        if (mImageLoader != null)
        mImageLoader.stopLoadingImages();
    }

    class NewsHolder{
        private TextView date;
        private TextView text;
        private ImageView image;
        private TextView url;
    }
}
