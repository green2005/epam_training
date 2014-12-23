package com.epamtraining.vklite.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epamtraining.vklite.R;
import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.imageLoader.ImageLoader;

public class WallAdapter extends BoItemAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private int mImageSize;
    private DataAdapterCallback mGetDataCallBack;
    private CursorHolder mCursorHolder;

    public WallAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mImageLoader = ImageLoader.getImageLoader(context);
    }

    @Override
    public void initAdapter(Activity activity, DataAdapterCallback callback) {
        mGetDataCallBack = callback;
        setImageViewSize(activity);
    }

    private void setImageViewSize(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        //todo переделать
        mImageSize = Math.min(screenHeight, screenWidth);
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

        if ( mCursorHolder == null) {
            mCursorHolder = new CursorHolder(getCursor());
        }
        getCursor().moveToPosition(position);
        if (position == getCursor().getCount() - 1) {
            //String id = "";//getCursor().getString(mCursorHolder.post_id_col);
            mGetDataCallBack.onGetMoreData(position, "");
        }

        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            v = mInflater.inflate(R.layout.post_listview_item, null);
            holder = new ViewHolder();
            holder.date = (TextView) v.findViewById(R.id.date);
            holder.text = (TextView) v.findViewById(R.id.text);
            holder.userName = (TextView) v.findViewById(R.id.usernametextview);
            holder.userImage = (ImageView)v.findViewById(R.id.profileimageview);

            holder.image = (ImageView) v.findViewById(R.id.image);

            holder.image.getLayoutParams().width = mImageSize;
            holder.image.getLayoutParams().height = mImageSize;


            holder.url = (TextView) v.findViewById(R.id.url);
            holder.url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = (String) v.getTag();
                    if (!TextUtils.isEmpty(url)) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        mContext.startActivity(browserIntent);
                    }
                }
            });
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }//News item = mNewsItems.get(position);

        holder.date.setText(getCursor().getString(mCursorHolder.date));
        holder.text.setText(getCursor().getString(mCursorHolder.itemText));
        holder.userName.setText(getCursor().getString(mCursorHolder.posterName));
        setImage(holder.image,  mCursorHolder.imageUrl);
        setImage(holder.userImage,  mCursorHolder.posterImage);


        if (!TextUtils.isEmpty(getCursor().getString(mCursorHolder.url))) {
            holder.url.setVisibility(View.GONE);
        } else {
            holder.url.setVisibility(View.VISIBLE);
            holder.url.setText(getCursor().getString(mCursorHolder.url));
            holder.url.setTag(getCursor().getString(mCursorHolder.url));
        }
        return v;
    }

    private void setImage(ImageView imageView, int imageUrlColumnIndex){
        String imageUrl = getCursor().getString(imageUrlColumnIndex);
        if (TextUtils.isEmpty(imageUrl)) {
            imageView.setVisibility(View.GONE);
        } else {
            if (mImageLoader != null) {
                imageView.setVisibility(View.VISIBLE);
                mImageLoader.loadImage(imageView, imageUrl);
            }
        }
    }

    public void onStop() {
        if (mImageLoader != null)
            mImageLoader.stopLoadingImages();
    }

    class ViewHolder {
        private TextView date;
        private TextView text;
        private ImageView image;
        private TextView url;
        private TextView userName;
        private ImageView userImage;
    }

    class CursorHolder{
        int itemText = -1;
        int id = -1;
        int imageUrl = -1;
        int date = -1;
        int url = -1;
        int posterName = -1;
        int posterImage = -1;

        CursorHolder(Cursor cursor){
            itemText = cursor.getColumnIndex(VKContentProvider.WALL_COLUMN_TEXT);
            id = cursor.getColumnIndex(VKContentProvider.WALL_COLUMN_ITEM_ID);
            imageUrl = cursor.getColumnIndex(VKContentProvider.WALL_COLUMN_IMAGE_URL);
            date = cursor.getColumnIndex(VKContentProvider.WALL_COLUMN_DATE);
            url = cursor.getColumnIndex(VKContentProvider.WALL_COLUMN_URL);
            date = cursor.getColumnIndex(VKContentProvider.WALL_COLUMN_RAW_DATE);
            posterName = cursor.getColumnIndex(VKContentProvider.WALL_COLUMN_USERNAME);
            posterImage = cursor.getColumnIndex(VKContentProvider.WALL_COLUMN_USERIMAGE);
        }
    }
}
