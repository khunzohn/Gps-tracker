package com.hilllander.khunzohn.gpstracker;

import android.app.Application;

import com.hilllander.khunzohn.gpstracker.reciever.USSDReciever;

/**
 * Created by ubunphyu on 1/1/16.
 */
public class GlobalApplication extends Application {
    private static USSDReciever.OnMessageRecieveListener currentListener;

    public static USSDReciever.OnMessageRecieveListener getCurrentMessageReceiveListener() {
        return currentListener;
    }

    public static void setCurrentMessageReceiveListener(USSDReciever.OnMessageRecieveListener listener) {
        currentListener = listener;
    }
}
