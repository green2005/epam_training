package com.epamtraining.vklite.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.db.MessagesDBHelper;
import com.epamtraining.vklite.db.UsersDBHelper;
import com.epamtraining.vklite.db.VKContentProvider;

public class MessagesAdapter extends BoItemAdapter {
    private LayoutInflater mInflater;

    public MessagesAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        if (cursor == null) return 0;
        cursor.moveToPosition(position);
        String mOut = CursorHelper.getString(cursor, MessagesDBHelper.OUT);
        if ( !TextUtils.isEmpty(mOut)&&  mOut.equals("0")) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2; //2 types - incoming and outgoing messages
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cursor cursor = (Cursor) getItem(position);
        if (cursor == null){ return null;}
        ViewHolder holder;
        View v = convertView;

        if (position == cursor.getCount() - 1) {
            loadMoreData(position + 1, null);
        }
        if (v == null){
            int isOut = getItemViewType(position);
            if (isOut == 1) {
                v = mInflater.inflate(R.layout.item_messages_out, null);
            } else
            {
                v = mInflater.inflate(R.layout.item_messages_in, null);
            }
            holder = new ViewHolder();
            holder.date = (TextView)v.findViewById(R.id.date);
            holder.message = (TextView)v.findViewById(R.id.text);
            holder.userName= (TextView)v.findViewById(R.id.usernametextview);
            holder.userImage = (ImageView)v.findViewById(R.id.profileimageview);
            v.setTag(holder);
        } else
        {
            holder = (ViewHolder) v.getTag();
        }
        holder.date.setText(CursorHelper.getString(cursor, MessagesDBHelper.DATE));
        populateImageView(holder.userImage, CursorHelper.getString(cursor, UsersDBHelper.IMAGE_FULL));
        holder.userName.setText(CursorHelper.getString(cursor, UsersDBHelper.NAME));
        holder.message.setText(CursorHelper.getString(cursor, MessagesDBHelper.BODY));
        return v;
    }

    private class ViewHolder {
        TextView date;
        TextView userName;
        TextView message;
        ImageView userImage;
    }
}
