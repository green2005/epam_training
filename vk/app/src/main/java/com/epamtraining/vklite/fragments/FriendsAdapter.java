package com.epamtraining.vklite.fragments;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.epamtraining.vklite.ImageLoader.ImageLoader;
import com.epamtraining.vklite.R;
import com.epamtraining.vklite.bo.Friend;

import java.util.List;
import java.util.zip.Inflater;

public class FriendsAdapter extends BaseAdapter {
    private Context mContext;
    private List<Friend> mItems;
    private LayoutInflater mInflater;
    FragmentDataProvider mDataProvider;

    public FriendsAdapter(Context context, List<Friend> items, FragmentDataProvider provider){
        mContext = context;
        mItems = items;
        mInflater = LayoutInflater.from(context);
        mDataProvider = provider;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        ViewHolder holder = null;
        v = convertView;
        if (v == null){
             v = mInflater.inflate(R.layout.friend_listview_item, null);
            holder = new ViewHolder();
            holder.tvName = (TextView) v.findViewById(R.id.name);
            holder.imPhoto = (ImageView) v.findViewById(R.id.photo);
            holder.tvNick = (TextView) v.findViewById(R.id.nick);
            v.setTag(holder);
        } else
        {
            holder = (ViewHolder) v.getTag();
        }
        Friend friend = mItems.get(position);
        try {
            holder.tvName.setText(friend.getName());
            holder.tvNick.setText(friend.getNick());
            String imageUrl = friend.getImageUrl();
           // mImageLoader.loadImage(holder.imPhoto, imageUrl);
            mDataProvider.loadImage(holder.imPhoto, imageUrl);
        } catch (Exception e){
            e.printStackTrace();
        }

        return v;
    }

    class ViewHolder{
        TextView tvName;
        TextView tvNick;
        ImageView imPhoto;
    }

}
