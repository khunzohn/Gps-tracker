package com.hilllander.khunzohn.gpstracker.util;

import android.net.Uri;
import android.telephony.SmsManager;

/**
 * Created by ubunphyu on 12/31/15.
 */
public class USSD {
    private static final String SHARP = Uri.decode("#");
    public static final String begin = SHARP + "begin" + SHARP + "123456" + SHARP;
    private static final SmsManager sm = SmsManager.getDefault();

    public static void smsBegin(String desAddress) {
        sm.sendTextMessage(desAddress, null, begin, null, null);
    }

}
