package com.hilllander.khunzohn.gpstracker.util;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

/**
 * Created by khunzohn on 1/13/16.
 */
public class Telephony {

    private static final String TAG = Logger.generateTag(Telephony.class);

    public static void queryGeo(Activity context, String desAddress) {
        Intent call = new Intent(Intent.ACTION_CALL);
        call.setData(Uri.parse("tel:" + desAddress));

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) ==
                PackageManager.PERMISSION_GRANTED) {
            Logger.d(TAG, "Call permission granted.");
            context.startActivity(call);
        } else {
            Logger.e(TAG, "Call permission denied!");
            showCallPermissionDeniedDialog(context);
        }
    }

    private static void showCallPermissionDeniedDialog(Activity context) {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Can't make a call!")
                .setMessage("Call permission required to query geo data is denied .")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }
}
