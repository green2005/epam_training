package com.epamtraining.vklite.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epamtraining.vklite.CursorHelper;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.db.MessagesDBHelper;
import com.epamtraining.vklite.db.UsersDBHelper;
import com.epamtraining.vklite.imageloader.ImageLoader;

public class MessagesAdapter extends CursorRecyclerViewAdapter<MessagesAdapter.MessagesHolder> {
    private LayoutInflater mInflater;

    public MessagesAdapter(Context context, ImageLoader imageLoader) {
        super(imageLoader);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        if (cursor != null && cursor.moveToPosition(position)) {
            return CursorHelper.getInt(cursor, MessagesDBHelper.OUT);
        }
        return super.getItemViewType(position);
    }

    @Override
    public MessagesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == 0) {
            v = mInflater.inflate(R.layout.item_messages_in, null);
        } else if (viewType == 1) {
            v = mInflater.inflate(R.layout.item_messages_out, null);
        } else {
            throw new IllegalArgumentException("unknown message type");
        }
        MessagesHolder holder = new MessagesHolder(v);
        v.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(MessagesHolder viewHolder, int position) {
        Cursor cursor = getCursor();
        if (cursor == null) {
            return;
        }
        cursor.moveToPosition(position);
        if (cursor.getPosition() == cursor.getCount() - 1) {
            loadMoreData(cursor.getPosition() + 1, null);
        }
        viewHolder.userName.setText(CursorHelper.getString(cursor, UsersDBHelper.NAME));
        viewHolder.date.setText(CursorHelper.getString(cursor, MessagesDBHelper.DATE));
        viewHolder.message.setText(CursorHelper.getString(cursor, MessagesDBHelper.BODY));
        getImageLoader().loadImage(viewHolder.userImage, CursorHelper.getString(cursor, UsersDBHelper.IMAGE_FULL));
    }

    class MessagesHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView userName;
        private TextView message;
        private ImageView userImage;

        public MessagesHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            userName = (TextView) itemView.findViewById(R.id.usernametextview);
            message = (TextView) itemView.findViewById(R.id.text);
            userImage = (ImageView) itemView.findViewById(R.id.profileimageview);
        }
    }
}
