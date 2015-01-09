package com.epamtraining.vklite.bo;

import org.json.JSONObject;

public class Friend extends BoItem {
    private JSONObject mJO;
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String NICKNAME = "nickname";
    private static final String PHOTO = "photo_100";
    private static final String ONLINE = "online";
    private static final String ID = "id";


    public Friend(JSONObject jo) {
        mJO = jo;
    }

    public String getName() throws Exception {
        try {
            return mJO.getString(FIRST_NAME)+" "+mJO.getString(LAST_NAME);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public String getFirstName() throws Exception {
        try {
            return mJO.getString(FIRST_NAME);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public String getLastName() throws Exception {
        try {
            return mJO.getString(LAST_NAME);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }


    public String getId()throws Exception{
        try {
            return mJO.getString(ID);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public String getNick() throws Exception {
        try {
            //TODO
            return mJO.optString(NICKNAME);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public String getImageUrl() throws Exception {
        try {
            return mJO.getString(PHOTO);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public boolean getIsOnline() throws Exception {
        try {
            return mJO.getBoolean(ONLINE);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
