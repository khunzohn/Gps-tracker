package com.hilllander.khunzohn.gpstracker;

import android.app.Application;

import com.hilllander.khunzohn.gpstracker.reciever.USSDReciever;

/**
 * Created by ubunphyu on 1/1/16.
 */
public class GlobalApplication extends Application {
    private static USSDReciever.OnMessageRecieveListener currentListener;

    public static void setCurrentListener(USSDReciever.OnMessageRecieveListener listener) {
        currentListener = listener;
    }

    public static USSDReciever.OnMessageRecieveListener getCurrentMessageListener() {
        return currentListener;
    }
}
