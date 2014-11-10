package com.samples.epamtraining.viewpagersample;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import News.NewsItem;

public class DataProvider {
    private static final int DATAMAXLENGTH = 50;
    private static final int DATAMINLENGTH = 10;
    private Context mContext;
    private Exception loadException = null;
    private Handler handler;

    public DataProvider(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context parameter cannot be null");
        }
        this.mContext = context;
    }

    public void fillDataForPage(final Pages pageName, final List items, final DataProviderHandler dataHadler) {
        handler = new Handler();
        loadException = null;
        new Runnable() {
            @Override
            public void run() {
                switch (pageName) {
                    case NEWS:
                        fillNews((ArrayList)items);
                        break;
                    default:
                        fillSimpleItems(pageName,(ArrayList)items);
                        break;
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (dataHadler != null) {
                            dataHadler.onDataLoaded();
                            if (loadException != null) {
                                dataHadler.onException(loadException);    //throw new Exception(loadException);
                            }
                        }
                    }
                });
            }
        }.run();
    }

    private void fillSimpleItems(Pages pageName ,final ArrayList items){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String sPages = "";
        if (prefs.contains(Pages.MESSAGES.toString())) {
            sPages = prefs.getString(pageName.toString(), "");
        }
        if (!sPages.equalsIgnoreCase("")) {
            try {
                items.clear();
                items.addAll(0,(ArrayList) ObjectSerializer.deserialize(sPages));
            } catch (Exception e) {
                loadException = e;
            }
        } else {
            int len = DATAMINLENGTH + new Random().nextInt(DATAMAXLENGTH);
            for (int i = 0;i < len; i++){
                items.add("It's a "+pageName.toString().toLowerCase()+" item  #" + i);
            }
            try {
                sPages = ObjectSerializer.serialize(items);
            }catch (Exception e)
            {loadException = e;}
            SharedPreferences.Editor e = prefs.edit();
            e.putString(pageName.toString(), sPages);
            e.apply();
        }
    }


    private void fillNews(final ArrayList<NewsItem> items) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String sPages = "";
        Date now = Calendar.getInstance().getTime();
        if (prefs.contains(Pages.NEWS.toString())) {
            sPages = prefs.getString(Pages.NEWS.toString(), "");
        }
        if (!sPages.equalsIgnoreCase("")) {
                try {
                    items.clear();
                    items.addAll(0,(ArrayList) ObjectSerializer.deserialize(sPages));
                } catch (Exception e) {
                    loadException = e;
                }
        } else {
            int len = DATAMINLENGTH + new Random().nextInt(DATAMAXLENGTH);
            Spanned a1 = Html.fromHtml("<a href=\"www.lenta.ru\">lenta.ru</a>");
            Spanned a2 = Html.fromHtml("<a href=\"www.gazeta.ru\">gazeta.ru</a>");
            Spanned a3 = Html.fromHtml("<a href=\"www.bbc.com\">bbc.com</a>");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

            Spanned t1 = Html.fromHtml("it's a text message for <a href=\"www.lenta.ru\">lenta.ru</a>");
            Spanned t2 = Html.fromHtml("it's a text message for <a href=\"www.gazeta.ru\">gazeta.ru</a>");
            Spanned t3 = Html.fromHtml("it's a text message for <a href=\"www.bbc.com\">bbc.com</a>");

            for (int i = 0; i < len; i++) {
                NewsItem item = new NewsItem();
                switch (i % 3) {
                    case (0): {
                        item.setAuthor(a1.toString());
                        item.setText(t1.toString());
                        break;
                    }
                    case 1: {
                        item.setAuthor(a2.toString());
                        item.setText(t2.toString());
                        break;
                    }
                    case 2: {
                        item.setAuthor(a3.toString());
                        item.setText(t3.toString());
                        break;
                    }
                }
                item.setTitle("Item title " + i);
                try {
                    item.setDate(dateFormat.format(now));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                items.add(item);
            }
            try {
                sPages = ObjectSerializer.serialize(items);
            } catch (Exception e) {
                loadException = e;
            }
            SharedPreferences.Editor e = prefs.edit();
            e.putString(Pages.NEWS.toString(), sPages);
            e.apply();
        }
    }

    public void clearCache() {
        Pages pages[] = Pages.values();
        for (Pages p : pages) {
            clearPageCache(p);

        }
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void clearPageCache(Pages pageName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        String sName = pageName.toString();
        if (prefs.contains(sName)) {
            editor.remove(sName);
        }
        editor.apply();
    }
}
