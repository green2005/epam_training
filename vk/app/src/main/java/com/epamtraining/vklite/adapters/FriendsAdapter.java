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
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.R;

public class FriendsAdapter extends BoItemAdapter {
   private LayoutInflater mInflater;

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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getCursor() == null) return null;
        getCursor().moveToPosition(position);
        View v ;
        ViewHolder holder ;
        v = convertView;
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
          try {
            String userName = (CursorHelper.getString(getCursor(), VKContentProvider.FRIEND_COLUMN_FIRST_NAME) +
                    " " +CursorHelper.getString(getCursor(), VKContentProvider.FRIEND_COLUMN_LAST_NAME)).trim();

            holder.tvName.setText(userName);
            CursorHelper.setText(holder.tvNick, getCursor(), VKContentProvider.FRIEND_COLUMN_NICK_NAME);
            populateImageView(holder.imPhoto, CursorHelper.getString(getCursor(), VKContentProvider.FRIEND_COLUMN_IMAGE_URL));
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
