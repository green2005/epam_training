package com.epamtraining.vklite.processors;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.epamtraining.vklite.bo.Comment;
import com.epamtraining.vklite.db.CommentsDBHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CommentsProcessor extends Processor{
    private CommentsDBHelper mCommentDBHelper;
    private int mRecordsFetched;
    private Context mContext;
    private static final String ITEMS = "items";
    private String mPostId;

    public CommentsProcessor(Context context) {
        super(context);
        mContext = context;
        mCommentDBHelper = new CommentsDBHelper();
    }

    @Override
    public void process(InputStream stream, AdditionalInfoSource dataSource) throws Exception {
        JSONObject response = getVKResponseObject(stream);
        PostersProcessor posters = new PostersProcessor(response);
        posters.process();
        JSONArray commentItems = response.getJSONArray(ITEMS);

        List<ContentValues> contentValues = new ArrayList<>();
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(mContext);
        for (int i = 0; i < commentItems.length(); i++) {
            JSONObject jsonObject = commentItems.getJSONObject(i);
            Comment commentItem = new Comment(jsonObject, dateFormat);

            commentItem.setUserInfo(posters.getPoster(Math.abs(commentItem.getPosterId())));
            ContentValues value = mCommentDBHelper.getContentValue(commentItem);
            value.put(CommentsDBHelper.POST_ID, mPostId);
            contentValues.add(value);
        }
        if (isTopRequest()) {
            mContext.getContentResolver().delete(CommentsDBHelper.CONTENT_URI,
                    null,
                    null);
        }
        ContentValues vals[] = new ContentValues[contentValues.size()];
        contentValues.toArray(vals);

        mRecordsFetched = commentItems.length();
        ContentResolver resolver = mContext.getContentResolver();
        if (vals.length > 0) {
            resolver.bulkInsert(CommentsDBHelper.CONTENT_URI, vals);
        }
        resolver.notifyChange(CommentsDBHelper.CONTENT_URI, null);
    }

    @Override
    public int getRecordsFetched() {
        return mRecordsFetched;
    }

    public String getPostId(){ return mPostId;}
    public void setPostId(String postId){mPostId = postId;}
}
