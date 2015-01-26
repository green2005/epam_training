package com.epamtraining.vklite.db;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.epamtraining.vklite.bo.BoItem;

import java.util.List;

public abstract class BODBHelper {

    public abstract String getTableName();
    public abstract String[] fieldNames();
    public abstract ContentValues getContentValue(BoItem item);
    public abstract List<ContentValues> getContentValues(BoItem item, PostSourceId postSource);

    public String getDropTableDefinition(){
        return String.format("DROP TABLE IF EXISTS %s", getTableName());
    }

    protected String getFieldDefaultValue(String FieldName){
        return null;
    }

    public String getCreateTableDefinition(){
        StringBuilder sb = new StringBuilder();
        sb.append("create table ");
        sb.append(getTableName());
        sb.append(" ( ");
        //sb.append(BaseColumns._ID);
        int i = 0;
        for (String fieldName : fieldNames()){
            if (i != 0){
                sb.append(", ");
            }
            sb.append(fieldName);
            if (BaseColumns._ID.equals(fieldName)){
                sb.append(" Integer NOT NULL PRIMARY KEY AUTOINCREMENT ");
            } else {
                sb.append(" text");
            }
            String defaultValue = getFieldDefaultValue(fieldName);
            if (!TextUtils.isEmpty(defaultValue)){
                sb.append(" default ");
                sb.append(defaultValue);
            }
            i = 1;
        }
        sb.append(") ");
        return sb.toString();
    }

    public List<String> getAdditionalSQL(){
    //used in DBManager for creating triggers, foreign keys etc..
        return null;
    }

}
