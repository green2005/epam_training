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
import com.epamtraining.vklite.db.DialogDBHelper;
import com.epamtraining.vklite.db.UsersDBHelper;

public class DialogsAdapter extends BoItemAdapter {
    private LayoutInflater mInflater;

    public DialogsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cursor cursor = (Cursor) getItem(position);
        if (cursor == null) {
            return null;
        }
        if (position == cursor.getCount() - 1) {
            loadMoreData(position + 1, null);
        }
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            v = mInflater.inflate(R.layout.item_dialogs, null);
            holder = new ViewHolder();
            holder.date = (TextView) v.findViewById(R.id.date);
            holder.message = (TextView) v.findViewById(R.id.message);
            holder.userImage = (ImageView) v.findViewById(R.id.userImage);
            holder.userName = (TextView) v.findViewById(R.id.usernametextview);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.date.setText(CursorHelper.getString(cursor, DialogDBHelper.DATE));
        populateImageView(holder.userImage, CursorHelper.getString(cursor, UsersDBHelper.IMAGE));
        holder.userName.setText(CursorHelper.getString(cursor, UsersDBHelper.NAME));
        holder.message.setText(CursorHelper.getString(cursor, DialogDBHelper.BODY));
        return v;
    }

    private class ViewHolder {
        TextView date;
        TextView userName;
        TextView message;
        ImageView userImage;
    }
}
