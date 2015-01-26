package com.epamtraining.vklite.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.ResizableImageView;
import com.epamtraining.vklite.db.NewsDBHelper;

public class NewsAdapter extends BoItemAdapter {
    private LayoutInflater mInflater;

    public NewsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
   }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cursor cursor = (Cursor) getItem(position);
        if (cursor == null){ return null;}
        if (position == cursor.getCount() - 1) {
            String startItemId = CursorHelper.getString(getCursor(), NewsDBHelper.NEXT_FROM);
            loadMoreData(position+1, startItemId);
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
            holder.image = (ResizableImageView) v.findViewById(R.id.image);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.date.setText(CursorHelper.getString(cursor, NewsDBHelper.DATE));
        holder.text.setText(CursorHelper.getString(cursor, NewsDBHelper.TEXT));
        holder.userName.setText(CursorHelper.getString(cursor, NewsDBHelper.USERNAME));

        holder.image.setOriginalImageSize(
                CursorHelper.getInt(cursor, NewsDBHelper.IMAGE_WIDTH),
                CursorHelper.getInt(cursor, NewsDBHelper.IMAGE_HEIGHT)
                );

        populateImageView(holder.image, CursorHelper.getString(cursor, NewsDBHelper.IMAGE_URL));
        populateImageView(holder.userImage, CursorHelper.getString(cursor, NewsDBHelper.USERIMAGE));
        return v;
    }

    class ViewHolder {
        private TextView date;
        private TextView text;
        private ResizableImageView image;
        private TextView userName;
        private ImageView userImage;
    }
}
