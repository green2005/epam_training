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

public class DialogsAdapter extends BoItemAdapter {
    private LayoutInflater mInflater;

    public DialogsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //TODO replace with local variable
        if (getCursor() == null) return null;
        getCursor().moveToPosition(position);
        if (position == getCursor().getCount() - 1) {
            loadMoreData(position+1, null);
        }

        View v = convertView;
        ViewHolder holder = null;
        if (v == null){
            v = mInflater.inflate(R.layout.item_dialogs, null);
            holder = new ViewHolder();
            holder.date = (TextView)v.findViewById(R.id.date);
            holder.message = (TextView)v.findViewById(R.id.message);
            holder.userImage = (ImageView)v.findViewById(R.id.userImage);
            holder.userName = (TextView)v.findViewById(R.id.usernametextview);
            v.setTag(holder);
        } else
        {
            holder = (ViewHolder) v.getTag();
        }
        holder.date.setText(getCursor().getString(getCursor().getColumnIndex(VKContentProvider.DIALOGS_COLUMN_DATE)));
        loadImage(getCursor().getColumnIndex(VKContentProvider.USERS_COLUMN_IMAGE), holder.userImage);
        holder.userName.setText(getCursor().getString(getCursor().getColumnIndex(VKContentProvider.USERS_COLUMN_NAME)));
        holder.message.setText(getCursor().getString(getCursor().getColumnIndex(VKContentProvider.DIALOGS_COLUMN_BODY)));
        return v;
    }

    private void loadImage(int imageColumnIndex, ImageView imageView){
        if (TextUtils.isEmpty(getCursor().getString(imageColumnIndex))) {
            imageView.setVisibility(View.GONE);
        } else {
            if (getImageLoader() != null) {
                imageView.setVisibility(View.VISIBLE);
                getImageLoader().loadImage(imageView, getCursor().getString(imageColumnIndex));
            }
        }
    }

    private class ViewHolder{
        TextView date;
        TextView userName;
        TextView message;
        ImageView userImage;
    }
}
