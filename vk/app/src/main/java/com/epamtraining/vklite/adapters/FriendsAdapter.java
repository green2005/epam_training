package com.epamtraining.vklite.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.imageLoader.ImageLoader;
import com.epamtraining.vklite.R;

public class FriendsAdapter extends BoItemAdapter {
   private LayoutInflater mInflater;


    int nickNameCol = -1;
    int nameCol = -1;
    int imageUrlCol = -1;
    int firstNameCol = -1;
    int lastNameCol = -1;

    public FriendsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (getCursor() != null)
            return getCursor().getCount();
        else
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

    private void fillColumnIndexes() {
        nickNameCol = getCursor().getColumnIndex(VKContentProvider.FRIEND_COLUMN_NICK_NAME);
        firstNameCol = getCursor().getColumnIndex(VKContentProvider.FRIEND_COLUMN_FIRST_NAME);
        lastNameCol = getCursor().getColumnIndex(VKContentProvider.FRIEND_COLUMN_LAST_NAME);
        imageUrlCol = getCursor().getColumnIndex(VKContentProvider.FRIEND_COLUMN_IMAGE_URL);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getCursor() == null) return null;
        getCursor().moveToPosition(position);
        View v = null;
        ViewHolder holder = null;
        v = convertView;
        if (v == null) {
            if (firstNameCol == -1) {
                fillColumnIndexes();
            }
            v = mInflater.inflate(R.layout.friend_listview_item, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) v.findViewById(R.id.name);
            holder.imPhoto = (ImageView) v.findViewById(R.id.photo);
            holder.tvNick = (TextView) v.findViewById(R.id.nick);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        //Friend friend = mItems.get(position);
        try {
            holder.tvName.setText(getCursor().getString(firstNameCol) + " " + getCursor().getString(lastNameCol));
            //лучше было бы в бд хранить скленные фамилию и имя

            holder.tvNick.setText(getCursor().getString(nickNameCol));
            String imageUrl = getCursor().getString(imageUrlCol);
            if (!TextUtils.isEmpty(imageUrl))
                getImageLoader().loadImage(holder.imPhoto, imageUrl);
            else
                holder.imPhoto.setImageBitmap(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    class ViewHolder {
        TextView tvName;
        TextView tvNick;
        ImageView imPhoto;
    }

}
