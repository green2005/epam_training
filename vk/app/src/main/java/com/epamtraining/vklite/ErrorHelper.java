package com.epamtraining.vklite;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.IOException;

public class ErrorHelper {
    public static void showError(Context context, String errorMessage) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setNegativeButton(context.getResources().getString(R.string.ok), new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }

            });
            builder.setTitle(context.getResources().getString(R.string.error));
            builder.setCancelable(false);
            builder.setMessage(errorMessage);
            builder.create().show();
        }
    }

    public static void showError(Context context, int resId) {
        if (context != null) {
            String errorMessage = context.getResources().getString(resId);
            showError(context, errorMessage);
        }
    }

    public static void showError(Context context, Exception e) {
        if (context != null) {
            String errorMessage;
            if (e instanceof  VKException) {
                errorMessage = context.getResources().getString(R.string.vkError) + "/" + e.getMessage();
            } else if (e instanceof IOException) {
                errorMessage = context.getResources().getString(R.string.checkInetConnection);
            } else {
                errorMessage = e.getMessage();
            }
            ;
            if (TextUtils.isEmpty(errorMessage))
                errorMessage = e.toString();
            showError(context, errorMessage);
        }
    }
}
