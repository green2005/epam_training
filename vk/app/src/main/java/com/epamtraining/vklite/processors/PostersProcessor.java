package com.epamtraining.vklite.processors;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class PostersProcessor {
    private static final String GROUPS_ARRAY_NAME = "groups";
    private static final String GROUPS_NAME = "name";
    private static final String GROUP_ID = "id";
     private static final String GROUPS_IMAGEURL = "photo_100";

    private static final String USERS_ARRAY_NAME = "profiles";
    private static final String USER_FIRST_NAME = "first_name";
    private static final String USER_LAST_NAME = "last_name";
    private static final String USER_IMAGEURL = "photo_100";
    private static final String USER_ID = "id";

     class Poster {
        private String mName;
        private String mImageUrl;

        Poster(String name, String imageUrl) {
            mName = name;
            mImageUrl = imageUrl;
        }

        public String getName() {
            return mName;
        }

         //TODO
        public String getmImageUrl() {
            return mImageUrl;
        }
    }

    private JSONObject mJo;
    //TODO to Map, not threadsafe, move from member to varable in method
    private HashMap<Long, Poster> mPostersMap;

    public PostersProcessor(JSONObject jo) {
        mJo = jo;
        mPostersMap = new HashMap<Long, Poster>();
    }

    public void process() throws Exception {
        JSONArray groups = mJo.optJSONArray(GROUPS_ARRAY_NAME);
        if (groups != null) {
            for (int i = 0; i < groups.length(); i++) {
                JSONObject group = groups.getJSONObject(i);
                Poster poster = new Poster(group.optString(GROUPS_NAME), group.optString(GROUPS_IMAGEURL));
                mPostersMap.put(group.optLong(GROUP_ID), poster);
            }
        }

        JSONArray profiles = mJo.optJSONArray(USERS_ARRAY_NAME);
        if (profiles != null) {
            for (int i =0 ; i<profiles.length(); i++){
                JSONObject profile = profiles.getJSONObject(i);
                String userName =(profile.optString(USER_FIRST_NAME)+" "+profile.optString(USER_LAST_NAME)).trim();
                Poster poster = new Poster(userName, profile.optString(USER_IMAGEURL));
                mPostersMap.put(profile.optLong(USER_ID), poster);
            }
        }
    }

    public Poster getPoster(Long id) {
        return mPostersMap.get(id);
    }

}
