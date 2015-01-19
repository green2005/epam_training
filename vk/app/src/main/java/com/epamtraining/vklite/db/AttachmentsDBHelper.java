package com.epamtraining.vklite.db;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import com.epamtraining.vklite.bo.Attachments;
import com.epamtraining.vklite.bo.BoItem;
import com.epamtraining.vklite.bo.attachment.Attachment;

import java.util.ArrayList;
import java.util.List;

public class AttachmentsDBHelper extends BODBHelper {

    private static final String TABLENAME = "Attachments";
    public static Uri CONTENT_URI = Uri.parse(CONTENT_URI_PREFIX
            + AUTHORITY + "/" + TABLENAME);

    public static final String ATTACHMENT_ID = "attach_id";
    public static final String URL = "url";
    public static final String ALBUM_ID = "album_id";
    public static final String TEXT = "text";
    public static final String DATE = "date";
    public static final String OWNER_ID = "owner_id";
    public static final String TITLE = "title";
    public static final String PHOTO = "photo";
    public static final String POST_ID = "post_id";
    public static final String ATTACHMENT_TYPE = "attach_type";
    public static String [] fields = {BaseColumns._ID, ATTACHMENT_ID, URL, ALBUM_ID, TEXT, DATE, OWNER_ID, TITLE, PHOTO, POST_ID, ATTACHMENT_TYPE};

    public AttachmentsDBHelper() {
    }

    @Override
    public String getTableName() {
        return TABLENAME;
    }

    @Override
    public String[] fieldNames() {
        return fields;
    }

    @Override
    public Uri getContentUri() {
        return CONTENT_URI;
    }

    @Override
    public ContentValues getContentValue(BoItem item) {
        throw new UnsupportedOperationException("Not realized");
    }

    @Override
    public List<ContentValues> getContentValues(BoItem item, PostSourceId postSource) {
        if (item == null){
            return null;
        }
        if (!(item instanceof Attachments)){
            throw new IllegalArgumentException("Could process Attachments only");
        }
        List<ContentValues> contentValues = new ArrayList<>();
        Attachments attachments = (Attachments) item;
        for (Attachment attachment : attachments.getAttachments()){
            ContentValues cv = new ContentValues();
            cv.put(ATTACHMENT_ID, attachment.getId());
            cv.put(ALBUM_ID, attachment.getAlbumId());
            cv.put(TEXT, attachment.getText());
            cv.put(DATE, attachment.getDate());
            cv.put(OWNER_ID, attachment.getOwnerId());
            cv.put(TITLE, attachment.getTitle());
            cv.put(URL, attachment.getUrl());
            cv.put(PHOTO, attachment.getPhoto());
            cv.put(ATTACHMENT_TYPE, attachment.getAttachmentType());
            if (postSource != null) {
                cv.put(POST_ID, postSource.getId());
            }
            contentValues.add(cv);
        }
        return contentValues;
    }

    public List<String> getAdditionalSQL() {
        List<String> sql = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sql.add("drop trigger if exists delete_wall");
        sb.append(" CREATE TRIGGER delete_wall ");
        sb.append(String.format(" AFTER DELETE ON %s ",new WallDBHelper().getTableName()));
        sb.append(" FOR EACH ROW ");
        sb.append(" BEGIN ");
        sb.append(String.format(" DELETE FROM %s WHERE %s = OLD.%s;", getTableName(), POST_ID, WallDBHelper.POST_ID));
        sb.append(" END ");
        sql.add(sb.toString());
        sb.setLength(0);

        sql.add(" drop trigger if exists delete_news ");
        sb.append(" CREATE TRIGGER delete_news ");
        sb.append(String.format(" AFTER DELETE ON %s ",new NewsDBHelper().getTableName()));
        sb.append(" FOR EACH ROW ");
        sb.append(" BEGIN ");
        sb.append(String.format(" DELETE FROM %s WHERE %s = OLD.%s;", getTableName(), POST_ID, NewsDBHelper.POST_ID));
        sb.append(" END ");
        sql.add(sb.toString());

        return sql;
    }
}
