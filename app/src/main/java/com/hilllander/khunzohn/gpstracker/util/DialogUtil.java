package com.hilllander.khunzohn.gpstracker.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 *Created by khunzohn on 1/8/16.
 */
public class DialogUtil {
    public static void showErrorDialog(final Activity activity, String title, String message, String negative, String positive) {
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.onBackPressed();
                    }
                })
                .create();
        dialog.show();
    }
}
