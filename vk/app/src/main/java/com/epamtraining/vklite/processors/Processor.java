package com.epamtraining.vklite.processors;


import android.content.Context;

import com.epamtraining.vklite.DataSource;
import com.epamtraining.vklite.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public  abstract class Processor  {
    private static final String VK_ERROR_RESPONSE = "error";
    private static final String VK_ERROR_MSG = "error_msg";
    private static final String RESPONSE = "response";

    private Context mContext;
    private boolean mIsTopRequest;

    public abstract void process (InputStream stream, AdditionalInfoSource dataSource)  throws Exception;
    public abstract  int getRecordsFetched();
    public Processor(Context context){
        if (context == null){
            throw  new IllegalArgumentException("Context is null");
        }
        mContext = context;
    }

    private JSONObject getResponse(InputStream stream) throws Exception{
        if (stream == null){
            throw new Exception(mContext.getResources().getString(R.string.response_is_empty));
        }
        String s = new StringReader().readFromStream(stream);
        JSONObject serverResponse = new JSONObject(s);
        if (!serverResponse.has(RESPONSE)){  //Process VK Errors
            generateVKServerError(serverResponse);
        }
        return serverResponse;
    }

    public JSONObject getVKResponseObject(InputStream stream) throws Exception{
       JSONObject serverResponse = getResponse(stream);
       return serverResponse.optJSONObject(RESPONSE);
    }

    public JSONArray getVKResponseArray(InputStream stream) throws Exception{
        JSONObject serverResponse = getResponse(stream);
        return serverResponse.optJSONArray(RESPONSE);
    }

    private void generateVKServerError(JSONObject serverResponse) throws Exception{
        String errorMsg = null;
        if (serverResponse.has(VK_ERROR_RESPONSE)){
            errorMsg = serverResponse.getJSONObject(VK_ERROR_RESPONSE).optString(VK_ERROR_MSG);
        } else
        {
            errorMsg = mContext.getResources().getString(R.string.unknown_server_error);
        };
        throw new Exception(errorMsg);
    }

    public boolean getIsTopRequest()
    {
        return mIsTopRequest;
    }

    public void setIsTopRequest(Boolean isTop){
        mIsTopRequest = isTop;
    }

}
