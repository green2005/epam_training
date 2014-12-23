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

import com.epamtraining.vklite.VKContentProvider;
import com.epamtraining.vklite.imageLoader.ImageLoader;
import com.epamtraining.vklite.R;

public class NewsAdapter extends BoItemAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private int mImageSize;
    private DataAdapterCallback mGetDataCallBack;
    private CursorHolder mCursorHolder;


    public NewsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mImageLoader = ImageLoader.getImageLoader(context);
    }

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
            String id = getCursor().getString(mCursorHolder.post_id_col);
            mGetDataCallBack.onGetMoreData(position, id);
        }

        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            v = mInflater.inflate(R.layout.post_listview_item, null);
            holder = new ViewHolder();
            holder.date = (TextView) v.findViewById(R.id.date);
            holder.text = (TextView) v.findViewById(R.id.text);
            holder.userName = (TextView) v.findViewById(R.id.usernametextview);
            holder.userImage = (ImageView) v.findViewById(R.id.profileimageview);
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

        holder.date.setText(getCursor().getString(mCursorHolder.date_col));
        holder.text.setText(getCursor().getString(mCursorHolder.news_col));
        holder.userName.setText(getCursor().getString(mCursorHolder.userName_col));
        loadImage(mCursorHolder.imageUrl_col, holder.image);
        loadImage(mCursorHolder.userImageUrl_col, holder.userImage);
        if (!TextUtils.isEmpty(getCursor().getString(mCursorHolder.url_col))) {
            holder.url.setVisibility(View.GONE);
        } else {
            holder.url.setVisibility(View.VISIBLE);
            holder.url.setText(getCursor().getString(mCursorHolder.url_col));
            holder.url.setTag(getCursor().getString(mCursorHolder.url_col));
        }
        return v;
    }

    private void loadImage(int imageColumnIndex, ImageView imageView){
        if (TextUtils.isEmpty(getCursor().getString(imageColumnIndex))) {
            imageView.setVisibility(View.GONE);
        } else {
            if (mImageLoader != null) {
                imageView.setVisibility(View.VISIBLE);
                mImageLoader.loadImage(imageView, getCursor().getString(imageColumnIndex));
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

        int news_col = -1;
        int id_col = -1;
        int imageUrl_col = -1;
        int date_col = -1;
        int url_col = -1;
        int raw_date_col = -1;
        int post_id_col = -1;
        int userName_col = -1;
        int userImageUrl_col = -1;

        CursorHolder(Cursor cursor){
            news_col = cursor.getColumnIndex(VKContentProvider.NEWS_COLUMN_TEXT);
            id_col = cursor.getColumnIndex(VKContentProvider.NEWS_COULMN_ID);
            imageUrl_col = cursor.getColumnIndex(VKContentProvider.NEWS_COLUMN_IMAGE_URL);
            date_col = cursor.getColumnIndex(VKContentProvider.NEWS_COLUMN_DATE);
            url_col = cursor.getColumnIndex(VKContentProvider.NEWS_COLUMN_URL);
            raw_date_col = cursor.getColumnIndex(VKContentProvider.NEWS_COLUMN_RAW_DATE);
            post_id_col = cursor.getColumnIndex(VKContentProvider.NEWS_COLUMN_POST_ID);
            userName_col = cursor.getColumnIndex(VKContentProvider.NEWS_COLUMN_USERNAME);
            userImageUrl_col  = cursor.getColumnIndex(VKContentProvider.NEWS_COLUMN_USERIMAGE);
        }
    }
}
