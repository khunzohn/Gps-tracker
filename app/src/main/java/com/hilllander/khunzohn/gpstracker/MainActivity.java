package com.hilllander.khunzohn.gpstracker;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hilllander.khunzohn.gpstracker.database.dao.DeviceDao;
import com.hilllander.khunzohn.gpstracker.database.model.Device;
import com.hilllander.khunzohn.gpstracker.fragment.MarketingFragments;
import com.hilllander.khunzohn.gpstracker.reciever.USSDReciever;
import com.hilllander.khunzohn.gpstracker.util.Logger;
import com.hilllander.khunzohn.gpstracker.util.USSD;
import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mm.technomation.mmtext.MMButtonView;

public class MainActivity extends AppCompatActivity implements USSDReciever.OnMessageRecieveListener {
    private static final String TAG = Logger.generateTag(MainActivity.class);
    private int checkCount = 0;
    private boolean geoDataFetched = false;
    private float latitude;
    private float longitude;
    private String trackedDevice, lastTrackedTime, lastTrackedDate;
    private List<Device> trackedDevices;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        final View progressBarLayout = findViewById(R.id.progressBarLayout);
        ViewUtils.setStatusBarTint(this, R.color.colorPrimaryDark);
        GlobalApplication.setCurrentMessageReceiveListener(this);
        trackedDevices = new ArrayList<>();
        fab = (FloatingActionButton) findViewById(R.id.fab);
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
//            USSD.queryGeo(simNum, USSD.DEAFULT_PASSWORD, connectorFlag);
            if (MarketingFragments.TEXT == connectorFlag) {
                fab.setEnabled(false);
                String message = getString(R.string.dialog_message_fetching_geo);
                ViewUtils.showProgressBar(this, progressBarLayout, message, true);
                final Handler errorDialogShower = new Handler();
                final Runnable show = new Runnable() {
                    @Override
                    public void run() {
                        showErrorDialog();
                        ViewUtils.showProgressBar(MainActivity.this, progressBarLayout, false);
                        fab.setEnabled(true);
                    }
                };
                final Timer connectionStatusChecker = new Timer("connection status checker", false);
                connectionStatusChecker.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        checkCount++;
                        if (checkCount > 2 && !geoDataFetched) {
                            errorDialogShower.post(show);
                            connectionStatusChecker.cancel();
                        } else if (geoDataFetched) {

                        }
                    }
                }, 3000, 3000);//checker connection status every 3 sec

            }

        }
    }

    private void showErrorDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Can't fetch geo data!");
        dialog.setContentView(R.layout.dialog_geo_fetching_failed);
        MMButtonView btOk = (MMButtonView) dialog.findViewById(R.id.btOk);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
            latitude = Float.parseFloat(lat);
            longitude = Float.parseFloat(lon);
            trackedDevice = sender;
            lastTrackedDate = date;
            lastTrackedTime = time;
            Device device = new Device();
            device.setLatitude(latitude);
            device.setLongitude(longitude);
            device.setTrackedTime(lastTrackedTime);
            device.setTrackedDate(lastTrackedDate);
            device.setSimNumber(trackedDevice);
            device.setDeviceName(Device.DEFAULT_NAME);
            device.setDeviceType(Device.DEFAULT_TYPE);
            device.setPassword(USSD.DEAFULT_PASSWORD);
            device.setAuthorization(Device.UN_AUTHORIZED);
            CreateDeviceAsync createDevice = new CreateDeviceAsync(this);
            createDevice.execute(device);

        } else {

        }
    }


    private class CreateDeviceAsync extends AsyncTask<Device, Void, Device> {
        private DeviceDao dao;

        CreateDeviceAsync(Context context) {
            dao = new DeviceDao(context);
        }

        @Override
        protected void onPreExecute() {
            try {
                dao.open();
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
        }

        @Override
        protected Device doInBackground(Device... params) {
            Device device = params[0];
            Device createdDevice = null;
            if (null != device) {
                try {
                    createdDevice = dao.createDevice(device);
                } catch (SQLException e) {
                    Logger.e(TAG, e.getLocalizedMessage());
                }
            }
            return createdDevice;
        }

        @Override
        protected void onPostExecute(Device device) {
            try {
                dao.close();
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            if (null != device) {
                geoDataFetched = true;
                trackedDevices.add(device);
            }

        }
    }
}
