package com.hilllander.khunzohn.gpstracker.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.hilllander.khunzohn.gpstracker.util.Logger;

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

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            OnMessageRecieveListener onMessageRecieveListener = (OnMessageRecieveListener) context;
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
                        smsBody = messages[i].getMessageBody();
                    }
                } catch (Exception e) {
                    Logger.e(TAG, e.getMessage());

                }
                if (isRegisteredAddress(sender)) {
                    int messageType = checkForMessageType(smsBody);
                    switch (messageType) {
                        case MESSAGE_TYPE_GEO_DATA:
                            String latLon[] = extractLatLon(smsBody);
                            onMessageRecieveListener.onGeoDataReceived(latLon[0], latLon[1], sender);
                            break;
                        case MESSAGE_TYPE_NO_ADMIN_OK:
                            onMessageRecieveListener.onNoAdminOkReceived(sender);
                            break;
                        case MESSAGE_TYPE_ADMIN_OK:
                            onMessageRecieveListener.onAdminOkReceived(sender);
                            break;
                        case MESSAGE_TYPE_RESUME_OK:
                            onMessageRecieveListener.onResumeOkReceived(sender);
                            break;
                        case MESSAGE_TYPE_BEGIN:
                            onMessageRecieveListener.onBeginOkReceived(sender);
                            break;
                        case MESSAGE_TYPE_PASSWORD_OK:
                            onMessageRecieveListener.onPasswordOkReceived(sender);
                            break;
                    }
                }
            }
        }

    }

    private String[] extractLatLon(String smsBody) {
        //make sure smsBody starts with "http"
        if (smsBody.startsWith("http")) {
            String url = smsBody.split(" ")[0];
            String rawLatLon = url.split("=")[1];
            String rawLat = rawLatLon.split(",")[0];
            String rawLon = rawLatLon.split(",")[1];
            return new String[]{rawLat.substring(1), rawLon.substring(1)};
        }
        Logger.e(TAG, "Something went wrong while extracting Lat Lon");
        return new String[]{"0", "0"};
    }

    private int checkForMessageType(String smsBody) {
        if (smsBody.startsWith("begin"))
            return MESSAGE_TYPE_BEGIN;
        else if (smsBody.startsWith("password"))
            return MESSAGE_TYPE_PASSWORD_OK;
        else if (smsBody.startsWith("resume"))
            return MESSAGE_TYPE_RESUME_OK;
        else if (smsBody.startsWith("admin"))
            return MESSAGE_TYPE_ADMIN_OK;
        else if (smsBody.startsWith("no admin"))
            return MESSAGE_TYPE_NO_ADMIN_OK;
        else if (smsBody.startsWith("http"))
            return MESSAGE_TYPE_GEO_DATA;
        else return -1; //unknown type
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

        void onGeoDataReceived(String lat, String lon, String sender);
    }
}
