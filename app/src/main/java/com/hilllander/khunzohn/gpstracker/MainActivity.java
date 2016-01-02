package com.hilllander.khunzohn.gpstracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hilllander.khunzohn.gpstracker.fragment.MarketingFragments;
import com.hilllander.khunzohn.gpstracker.reciever.USSDReciever;
import com.hilllander.khunzohn.gpstracker.util.USSD;
import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements USSDReciever.OnMessageRecieveListener {
    private int checkCount = 0;
    private boolean geoDataFetched = false;
    private float latitude;
    private float longitude;
    private String trackedDevice, lastTrackedTime, lastTrackedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        View progressBarLayout = findViewById(R.id.progressBarLayout);
        ViewUtils.setStatusBarTint(this, R.color.colorPrimaryDark);
        GlobalApplication.setCurrentMessageReceiveListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //activity started from MarketingActivity's connecting succeeded
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            String simNum = bundle.getString(MarketingActivity.KEY_SIM_NUMBER);
            int connectorFlag = bundle.getInt(MarketingActivity.KEY_CONNECTOR_FLAG);
            USSD.queryGeo(simNum, USSD.DEAFULT_PASSWORD, connectorFlag);
            if (MarketingFragments.TEXT == connectorFlag) {
                String message = getString(R.string.dialog_message_fetching_geo);
                ViewUtils.showProgressBar(this, progressBarLayout, message, true);
                final Timer connectionStatusChecker = new Timer("connection status checker", false);
                connectionStatusChecker.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        checkCount++;
                        if (checkCount > 2 && !geoDataFetched) {

                        } else if (geoDataFetched) {

                        }
                    }
                }, 3000, 3000);//checker connection status every 3 sec

            }

        }
    }

    @Override
    public void onBeginOkReceived(String sender) {

    }

    @Override
    public void onPasswordOkReceived(String sender) {

    }

    @Override
    public void onResumeOkReceived(String sender) {

    }

    @Override
    public void onAdminOkReceived(String sender) {

    }

    @Override
    public void onNoAdminOkReceived(String sender) {

    }

    @Override
    public void onGeoDataReceived(String lat, String lon, String date, String time, String sender) {
        if (!lat.equals("0") && !lon.equals("0") && !date.equals("") && !time.equals("")) {
            geoDataFetched = true;
            latitude = Float.parseFloat(lat);
            longitude = Float.parseFloat(lon);
            trackedDevice = sender;
            lastTrackedDate = date;
            lastTrackedTime = time;
        } else {

        }
    }
}
