package com.hilllander.khunzohn.gpstracker.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by khuzohn on 1/8/16.
 */
public class BitmapUtil {
    public static Bitmap scaledBitmap(String bitmapPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(bitmapPath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(bitmapPath, options);
    }
}
