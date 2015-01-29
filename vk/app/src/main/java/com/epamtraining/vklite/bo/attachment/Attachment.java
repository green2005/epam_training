package com.epamtraining.vklite.bo.attachment;

public interface Attachment {
    public long getId();

    public String getUrl();

    public long getAlbumId();

    public String getText();

    public String getDate();

    public long getOwnerId();

    public String getTitle();

    public String getPhoto();//used for video preview

    public String getAttachmentType();

    public int getWidth();  //used for images

    public int getHeight(); //used for images
}
