package com.hilllander.khunzohn.gpstracker.util;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.hilllander.khunzohn.gpstracker.R;

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
}
