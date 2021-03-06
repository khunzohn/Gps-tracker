package com.hilllander.khunzohn.gpstracker.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hilllander.khunzohn.gpstracker.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import mm.technomation.mmtext.MMTextView;

/**
 * Created by khunzohn on 12/30/15.
 */
public class ViewUtils {
    private static MMTextView pbMessage;
    private static ProgressBar progressBar;
    public static void hideToolbarShawdowForLollipopPlus(Context context, Toolbar toolbar, View shawdow) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shawdow.setVisibility(View.GONE);
            toolbar.setElevation(context.getResources().getDimension(R.dimen.height_toolbar_elevation));
        }
    }

    public static int statusBarHeight(Context context) {
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int height;
        if (resId > 0) {
            height = context.getResources().getDimensionPixelSize(resId);
        } else {
            height = -1;
        }
        return height;
    }

    public static void doKitkatStuffs(Context context, AppBarLayout appBarLayout) {
        setToolbarPaddingForKitkat(context, appBarLayout);
    }

    private static void setToolbarPaddingForKitkat(Context context, AppBarLayout appBarLayout) {
        if (isKitkat()) {
            appBarLayout.setPadding(0, statusBarHeight(context), 0, 0);
        }
    }

    private static boolean isKitkat() {
        return (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT);
    }

    public static void showProgressBar(Context context, View progressBarLayout, boolean visible) {
        showProgressBar(context, progressBarLayout, 0, "", visible);
    }

    public static void showProgressBar(Context context, View progressBarLayout, int colorResId, boolean visible) {
        showProgressBar(context, progressBarLayout, colorResId, "", visible);
    }

    public static void showProgressBar(Context context, View progressBarLayout, String message, boolean visible) {
        showProgressBar(context, progressBarLayout, 0, message, visible);
    }

    public static void showProgressBar(Context context, View progressBarLayout, int colorResId, String message, boolean visible) {
        int color = colorResId > 0 ? colorResId : android.R.color.white;
        if (null == progressBar)
            progressBar = (ProgressBar) progressBarLayout.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(context.getResources().getColor(color), PorterDuff.Mode.MULTIPLY);
        if (null == pbMessage)
            pbMessage = (MMTextView) progressBarLayout.findViewById(R.id.pbMessage);
        if (message.equals(""))
            pbMessage.setVisibility(View.GONE);
        else
            pbMessage.setMyanmarText(message);
        progressBarLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public static void setStatusBarTint(Activity context, int resId) {
        SystemBarTintManager sm = new SystemBarTintManager(context);
        sm.setStatusBarTintEnabled(true);
        sm.setStatusBarTintResource(resId);
    }

    public static void makeToast(final Context context, final String message) {
        //TODO delete it for production
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * (This code may be used under the terms of Apache License – Version 2)
     *
     * @param bitmap to be rounded
     * @return rounded bitmap
     *modified by khunzohn on 1/7/16. raised roundPx from 12 to 180
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 180;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
