package com.hilllander.khunzohn.gpstracker.util;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hilllander.khunzohn.gpstracker.R;

/**
 * Created by khunzohn on 12/30/15.
 */
public class ViewUtils {
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
}
