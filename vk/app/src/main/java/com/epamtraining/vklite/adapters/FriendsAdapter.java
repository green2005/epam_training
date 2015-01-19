package com.epamtraining.vklite.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.db.FriendDBHelper;
import com.epamtraining.vklite.db.NewsDBHelper;
import com.epamtraining.vklite.db.VKContentProvider;
import com.epamtraining.vklite.R;

public class FriendsAdapter extends BoItemAdapter {
    private LayoutInflater mInflater;

    public FriendsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cursor cursor = (Cursor) getItem(position);
        if (cursor == null){ return null;}
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            v = mInflater.inflate(R.layout.item_friends, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) v.findViewById(R.id.name);
            holder.imPhoto = (ImageView) v.findViewById(R.id.photo);
            holder.tvNick = (TextView) v.findViewById(R.id.nick);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        String userName = (CursorHelper.getString(cursor, FriendDBHelper.FIRST_NAME) +
                " " + CursorHelper.getString(cursor, FriendDBHelper.LAST_NAME)).trim();
        holder.tvName.setText(userName);
        holder.tvNick.setText(CursorHelper.getString(cursor, FriendDBHelper.NICK_NAME));
        populateImageView(holder.imPhoto, CursorHelper.getString(cursor, FriendDBHelper.IMAGE_URL));
        return v;
    }

    class ViewHolder {
        TextView tvName;
        TextView tvNick;
        ImageView imPhoto;
    }
}
