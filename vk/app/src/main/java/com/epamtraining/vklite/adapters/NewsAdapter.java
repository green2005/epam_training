package com.epamtraining.vklite.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.R;

public class NewsAdapter extends BoItemAdapter {
    private LayoutInflater mInflater;

    public NewsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
   }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getCursor() == null) return null;
        //TODO change to Cursor item = (Cursor) getItem(position);
        getCursor().moveToPosition(position);
        if (position == getCursor().getCount() - 1) {
            //TODO rename
            String next_from = CursorHelper.getString(getCursor(), VKContentProvider.NEWS_COLUMN_NEXT_FROM);
            loadMoreData(position+1, next_from);
        }

        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            v = mInflater.inflate(R.layout.item_post, null);
            holder = new ViewHolder();
            holder.date = (TextView) v.findViewById(R.id.date);
            holder.text = (TextView) v.findViewById(R.id.text);
            holder.userName = (TextView) v.findViewById(R.id.usernametextview);
            holder.userImage = (ImageView) v.findViewById(R.id.profileimageview);
            holder.image = (ImageView) v.findViewById(R.id.image);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        CursorHelper.setText(holder.date, getCursor(), VKContentProvider.NEWS_COLUMN_DATE);
        CursorHelper.setText(holder.text, getCursor(), VKContentProvider.NEWS_COLUMN_TEXT);
        CursorHelper.setText(holder.userName, getCursor(), VKContentProvider.NEWS_COLUMN_USERNAME);
        populateImageView(holder.image, CursorHelper.getString(getCursor(), VKContentProvider.NEWS_COLUMN_IMAGE_URL));
        populateImageView(holder.userImage, CursorHelper.getString(getCursor(), VKContentProvider.NEWS_COLUMN_USERIMAGE));
        return v;
    }

    public void onStop() {
        if (getImageLoader() != null)
            getImageLoader().stopLoadingImages();
    }

    class ViewHolder {
        private TextView date;
        private TextView text;
        private ImageView image;
        private TextView url;
        private TextView userName;
        private ImageView userImage;
    }
}
