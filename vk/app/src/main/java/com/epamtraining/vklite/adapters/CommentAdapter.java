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
import com.epamtraining.vklite.db.CommentsDBHelper;

public class CommentAdapter extends BoItemAdapter {
    private LayoutInflater mInflater;

    public CommentAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cursor cursor = (Cursor) getItem(position);
        if (cursor == null) {
            return null;
        }
        ViewHolder holder;
        View v = convertView;
        if (position == cursor.getCount() - 1) {
            loadMoreData(position + 1, null);
        }
        if (v == null) {
            v = mInflater.inflate(R.layout.item_post_comment, null);
            holder = new ViewHolder();
            holder.date = (TextView) v.findViewById(R.id.date);
            holder.message = (TextView) v.findViewById(R.id.text);
            holder.userName = (TextView) v.findViewById(R.id.usernametextview);
            holder.userImage = (ImageView) v.findViewById(R.id.profileimageview);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.date.setText(CursorHelper.getString(cursor, CommentsDBHelper.DATE));
        populateImageView(holder.userImage, CursorHelper.getString(cursor, CommentsDBHelper.USERIMAGE));
        holder.userName.setText(CursorHelper.getString(cursor, CommentsDBHelper.USERNAME));
        holder.message.setText(CursorHelper.getString(cursor, CommentsDBHelper.TEXT));
        return v;
    }

    private class ViewHolder {
        TextView date;
        TextView userName;
        TextView message;
        ImageView userImage;
    }
}
