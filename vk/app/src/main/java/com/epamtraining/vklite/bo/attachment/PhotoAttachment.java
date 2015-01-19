package com.epamtraining.vklite.bo.attachment;


import com.epamtraining.vklite.bo.Attachments;

import org.json.JSONException;
import org.json.JSONObject;
/*
id	идентификатор фотографии.
положительное число
album_id	идентификатор альбома, в котором находится фотография.
int (числовое значение)
owner_id	идентификатор владельца фотографии.
int (числовое значение)
user_id	идентификатор пользователя, загрузившего фото (если фотография размещена в сообществе). Для фотографий, размещенных от имени сообщества, user_id=100.
положительное число
photo_75	url копии фотографии с максимальным размером 75x75px.
строка
photo_130	url копии фотографии с максимальным размером 130x130px.
строка
photo_604	url копии фотографии с максимальным размером 604x604px.
строка
photo_807	url копии фотографии с максимальным размером 807x807px.
строка
photo_1280	url копии фотографии с максимальным размером 1280x1024px.
строка
photo_2560	url копии фотографии с максимальным размером 2560x2048px.
строка
width	ширина оригинала фотографии в пикселах.
положительное число
height	высота оригинала фотографии в пикселах.
положительное число
text	текст описания фотографии.
строка
date	дата добавления в формате unixtime.
положительное число
 */

public class PhotoAttachment implements Attachment {
    private static final String ID = "id";
    private static final String ALBUM_ID = "album_id";
    private static final String TEXT = "text";
    private static final String DATE = "date";
    private static final String URL_604 = "photo_604";
    private static final String OWNER_ID = "owner_id";

    private long mId;
    private long mAlbumId;
    private String mText;
    private String mDate;
    private long mOwnerId;
    private String mUrl;

    public PhotoAttachment(JSONObject jo) throws JSONException {
        mId = jo.optLong(ID);
        mAlbumId = jo.optLong(ALBUM_ID);
        mText = jo.optString(TEXT);
        mDate = jo.optString(DATE);
        mUrl = jo.optString(URL_604);
        mOwnerId = jo.optLong(OWNER_ID);
    }

    @Override
    public long getId() {
       return mId;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public long getAlbumId() {
        return mAlbumId;
    }

    @Override
    public String getText() {
        return mText;
    }

    @Override
    public String getDate() {
        return mDate;
    }

    @Override
    public long getOwnerId() {
        return mOwnerId;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getPhoto() {
        return null;
    }

    @Override
    public String getAttachmentType() {
        return Attachments.ATTACHMENT_PHOTO;
    }
}
