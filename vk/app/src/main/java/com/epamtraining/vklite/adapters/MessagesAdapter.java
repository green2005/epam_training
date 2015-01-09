package com.epamtraining.vklite.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;

public class MessagesAdapter extends BoItemAdapter {
    private LayoutInflater mInflater;

    public MessagesAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getItemViewType(int position) {
        if (getCursor() == null) return 0;
        getCursor().moveToPosition(position);
        String mOut = getCursor().getString(getCursor().getColumnIndex(VKContentProvider.MESSAGES_OUT));

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
        if (getCursor() == null) return null;
        getCursor().moveToPosition(position);

        if (position == getCursor().getCount() - 1) {

            loadMoreData(position + 1, null);
        }
        int isOut = getItemViewType(position);
        ViewHolder holder = null;
        View v = null;
        int resId = -1;
        if (isOut == 1){
            //out message
            v = convertView;
            if (v == null){
                resId = R.layout.item_messages_out;
            }
        } else
        {
            //incoming message
            if (v == null){
                resId = R.layout.item_messages_in;
            }
        }
        if (v == null){
            v = mInflater.inflate(resId, null);
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
        holder.date.setText(getCursor().getString(getCursor().getColumnIndex(VKContentProvider.MESSAGES_DATE)));
        loadImage(getCursor().getColumnIndex(VKContentProvider.USERS_COLUMN_IMAGE_FULL), holder.userImage);
        holder.userName.setText(getCursor().getString(getCursor().getColumnIndex(VKContentProvider.USERS_COLUMN_NAME)));
        holder.message.setText(getCursor().getString(getCursor().getColumnIndex(VKContentProvider.MESSAGES_COLUMN_BODY)));
        return v;
    }

    private void loadImage(int imageColumnIndex, ImageView imageView) {
        if (TextUtils.isEmpty(getCursor().getString(imageColumnIndex))) {
            imageView.setVisibility(View.GONE);
        } else {
            if (getImageLoader() != null) {
                imageView.setVisibility(View.VISIBLE);
                getImageLoader().loadImage(imageView, getCursor().getString(imageColumnIndex));
            }
        }
    }

    private class ViewHolder {
        TextView date;
        TextView userName;
        TextView message;
        ImageView userImage;
    }
}
