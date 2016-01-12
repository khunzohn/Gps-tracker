package com.hilllander.khunzohn.gpstracker.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.hilllander.khunzohn.gpstracker.GlobalApplication;
import com.hilllander.khunzohn.gpstracker.util.Logger;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by khunzohn 1/1/16.
 */
public class USSDReciever extends BroadcastReceiver {
    public static final int MESSAGE_TYPE_BEGIN = 110;
    public static final int MESSAGE_TYPE_PASSWORD_OK = 111;
    public static final int MESSAGE_TYPE_RESUME_OK = 112;
    public static final int MESSAGE_TYPE_ADMIN_OK = 113;
    public static final int MESSAGE_TYPE_NO_ADMIN_OK = 114;
    public static final int MESSAGE_TYPE_GEO_DATA = 115;

    private static final String TAG = Logger.generateTag(USSDReciever.class);
    private static final String DATE = "Date:";
    private static final String TIME = "Time:";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "onRecieve triggered");
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            OnMessageRecieveListener onMessageRecieveListener = GlobalApplication.getCurrentMessageReceiveListener();
            if (null != onMessageRecieveListener) { // if message received during app closed ,it d be null

                SmsMessage[] messages;
                String smsBody = "";
                String sender = "";
                Bundle bundle = intent.getExtras();
                if (null != bundle) {
                    try {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        messages = new SmsMessage[pdus.length];

                        for (int i = 0; i < messages.length; i++) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                String format = bundle.getString("format");
                                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                            } else {
                                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            }
                            sender = messages[i].getOriginatingAddress();
                            smsBody += messages[i].getMessageBody();
                        }
                    } catch (Exception e) {
                        Logger.e(TAG, e.getMessage());

                    }
                    if (isRegisteredAddress(sender)) {
                        int messageType = checkForMessageType(smsBody);
                        switch (messageType) {
                            case MESSAGE_TYPE_GEO_DATA:
                                String latLon[] = extractLatLon(smsBody);
                                String dateTime[] = extractDateTime(smsBody);
                                Logger.d(TAG, "Geo data message received");
                                onMessageRecieveListener.onGeoDataReceived(latLon[0], latLon[1], dateTime[0], dateTime[1], sender);
                                break;
                            case MESSAGE_TYPE_NO_ADMIN_OK:
                                Logger.d(TAG, "no admin ok received!");
                                onMessageRecieveListener.onNoAdminOkReceived(sender);
                                break;
                            case MESSAGE_TYPE_ADMIN_OK:
                                Logger.d(TAG, "admin ok received!");
                                onMessageRecieveListener.onAdminOkReceived(sender);
                                break;
                            case MESSAGE_TYPE_RESUME_OK:
                                Logger.d(TAG, "resume ok received!");
                                onMessageRecieveListener.onResumeOkReceived(sender);
                                break;
                            case MESSAGE_TYPE_BEGIN:
                                Logger.d(TAG, "begin ok received!");
                                onMessageRecieveListener.onBeginOkReceived(sender);
                                break;
                            case MESSAGE_TYPE_PASSWORD_OK:
                                Logger.d(TAG, "password ok received!");
                                onMessageRecieveListener.onPasswordOkReceived(sender);
                                break;
                        }
                    }
                }
            }

        }

    }

    private String[] extractDateTime(String smsBody) {
        String date = "";
        String time = "";
        //make sure smsBody starts with "http"
        if (smsBody.startsWith("http")) {
            String infos[] = smsBody.split(" ");

            for (String rawInfo : infos) {
                if (rawInfo.startsWith(DATE)) {
                    date = rawInfo.substring(DATE.length());
                    Logger.d(TAG, "date :" + date);
                }
                if (rawInfo.startsWith(TIME)) {
                    time = rawInfo.substring(TIME.length());
                    Logger.d(TAG, "time : " + time);
                }
            }

        }
        return new String[]{date, time};
    }

    private String[] extractLatLon(String smsBody) {
        Logger.d(TAG, smsBody);
        //make sure smsBody starts with "http"
        if (isGeoSMS(smsBody)) {
            String strUrl = smsBody.split(" ")[0];
            try {
                URL url = new URL(strUrl);


                Logger.d(TAG, strUrl);
                String rawLatLon = url.getQuery();
                Logger.d(TAG, rawLatLon);
                String rawLat = rawLatLon.split(",")[0];
                String rawLon = rawLatLon.split(",")[1];
                Logger.d(TAG, "raw lat & lon : " + rawLat + " " + rawLon);
                return new String[]{rawLat.substring(3), rawLon.substring(1)};
            } catch (MalformedURLException e) {
                Logger.e(TAG, e.getMessage());
            }

        }
        Logger.e(TAG, "Something went wrong while extracting Lat Lon");
        return new String[]{"0", "0"};
    }

    private int checkForMessageType(String smsBody) {
        int type;
        if (smsBody.startsWith("begin"))
            type = MESSAGE_TYPE_BEGIN;
        else if (smsBody.startsWith("password"))
            type = MESSAGE_TYPE_PASSWORD_OK;
        else if (smsBody.startsWith("resume"))
            type = MESSAGE_TYPE_RESUME_OK;
        else if (smsBody.startsWith("admin"))
            type = MESSAGE_TYPE_ADMIN_OK;
        else if (smsBody.startsWith("no admin"))
            type = MESSAGE_TYPE_NO_ADMIN_OK;
        else if (isGeoSMS(smsBody))
            type = MESSAGE_TYPE_GEO_DATA;
        else type = -1; //unknown type
        Logger.d(TAG, "Sms type : " + type);
        return type;
    }

    private boolean isGeoSMS(String smsBody) {
        String splited[] = smsBody.split(" ");
        try {
            if (splited[4].startsWith("IMEI:"))
                Logger.d(TAG, "index [4] is starts with IMEI:");
            return true;
        } catch (IndexOutOfBoundsException e) {
            Logger.e(TAG, e.getLocalizedMessage());
            return false;
        }
    }

    private boolean isRegisteredAddress(String senderAddress) {
        //TODO fetch senders from database and check against them
        return !senderAddress.equals("");
    }

    public interface OnMessageRecieveListener {
        void onBeginOkReceived(String sender);

        void onPasswordOkReceived(String sender);

        void onResumeOkReceived(String sender);

        void onAdminOkReceived(String sender);

        void onNoAdminOkReceived(String sender);

        void onGeoDataReceived(String lat, String lon, String date, String time, String sender);
    }
}
