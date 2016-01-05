package com.hilllander.khunzohn.gpstracker.util;

import android.net.Uri;
import android.telephony.SmsManager;

import com.hilllander.khunzohn.gpstracker.fragment.MarketingFragments;

/**
 *Created by khunzohn on 12/31/15.
 */
public class USSD {
    public static final String DEAFULT_PASSWORD = "123456";
    private static final String SHARP = Uri.decode("#");
    public static final String begin = SHARP + "begin" + SHARP + "123456" + SHARP;
    public static final String password = SHARP + "password" + SHARP;
    public static final String resume = SHARP + "resume" + SHARP;
    public static final String noAdmin = SHARP + "noadmin" + SHARP;
    public static final String admin = SHARP + "admin" + SHARP;
    public static final String smsLink = SHARP + "smslink" + SHARP;
    private static final SmsManager sm = SmsManager.getDefault();
    private static final String DEBUG_ADDRESS = "09261978642"; // my personal number :p

    public static void smsBegin(String desAddress) {
        sm.sendTextMessage(desAddress, null, begin, null, null);
    }

    public static void smsSetPassword(String desAddress, String oldPass, String newPass) {
        String message = password + oldPass + SHARP + newPass + SHARP;
        sm.sendTextMessage(desAddress, null, message, null, null);
    }

    public static void smsResume(String desAddress) {
        sm.sendTextMessage(desAddress, null, resume, null, null);
    }

    public static void smsAdmin(String desAddress, String password, String authorizingNumber) {
        String message = admin + password + SHARP + authorizingNumber + SHARP;
        sm.sendTextMessage(desAddress, null, message, null, null);
    }

    public static void smsNoAdmin(String desAddress, String password, String authorizedNumber) {
        String message = noAdmin + password + SHARP + authorizedNumber + SHARP;
        sm.sendTextMessage(desAddress, null, message, null, null);
    }

    public static void queryGeo(String desAddress, String password, int connectorFlag) {

        switch (connectorFlag) {
                case MarketingFragments.PHONE:
                    //TODO implement phone query
                    break;
                case MarketingFragments.TEXT:
                    String message = smsLink + password + SHARP;
                    sm.sendTextMessage(desAddress, null, message, null, null);
                    break;
            }


    }

}
