package com.hilllander.khunzohn.gpstracker;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.hilllander.khunzohn.gpstracker.adapter.MainRecyclerAdapter;
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

public class MainActivity extends AppCompatActivity implements USSDReciever.OnMessageRecieveListener,
        MainRecyclerAdapter.OnDeviceOnClickListener {
    private static final String TAG = Logger.generateTag(MainActivity.class);
    private int checkCount = 0;
    private boolean geoDataFetched = false;
    private float latitude;
    private float longitude;
    private String trackedDevice, lastTrackedTime, lastTrackedDate;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private View progressBarLayout;
    private MainRecyclerAdapter adapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        ViewUtils.setStatusBarTint(this, R.color.colorPrimaryDark);
        GlobalApplication.setCurrentMessageReceiveListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MainRecyclerAdapter(this);
        adapter.setOnClickListener(this);
        recyclerView.setAdapter(adapter);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Bundle bundle = getIntent().getExtras();
        //activity started from MarketingActivity's connecting succeeded
        if (null != bundle) {
            final String simNum = bundle.getString(MarketingActivity.KEY_SIM_NUMBER);
            final int connectorFlag = bundle.getInt(MarketingActivity.KEY_CONNECTOR_FLAG);

            if (MarketingFragments.TEXT == connectorFlag) {
                fab.setEnabled(false);

                showProgressBar(true);
                final Handler errorDialogShower = new Handler();
                final Runnable show = new Runnable() {
                    @Override
                    public void run() {
                        showErrorDialog();
                        showProgressBar(false);
                        fab.setEnabled(true);
                    }
                };
                final Timer connectionStatusChecker = new Timer("connection status checker", false);
                connectionStatusChecker.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        checkCount++;
                        if (checkCount == 2) {
                            USSD.queryGeo(simNum, USSD.DEAFULT_PASSWORD, MarketingFragments.TEXT);
                            Logger.d(TAG, "Query sent");
                        }
                        if (checkCount > 10 && !geoDataFetched) {
                            errorDialogShower.post(show);
                            connectionStatusChecker.cancel();
                            Logger.d(TAG, "geo fetching failed");
                        } else if (geoDataFetched) {
                            connectionStatusChecker.cancel();
                            Logger.d(TAG, "geo fetching succeeded");
                        }
                    }
                }, 3000, 3000);//checker connection status every 3 sec

            }

        }
        FetchDevicesFromDb fetcher = new FetchDevicesFromDb(this);
        fetcher.execute();
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
        Logger.d(TAG, "lat : " + lat + " lon : " + lon + " date : " + date + " time : " + time + " sender : " + sender);
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
            makeToast("empty geo received!");
        }
    }

    @Override
    public void onClickEdit(Device device) {
        makeToast("Edit : " + device.getDeviceName());
    }

    @Override
    public void onClickLock(Device device) {
        makeToast("lock : " + device.getDeviceName());
    }

    @Override
    public void onClickProfile(Device device) {
        makeToast("profile : " + device.getDeviceName());
    }

    @Override
    public void onClickGoToMap(Device device) {
        makeToast("go to map : " + device.getDeviceName());
    }

    private void makeToast(String message) {
        if (BuildConfig.DEBUG)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showProgressBar(boolean visible) {
        final String message = getString(R.string.dialog_message_fetching_geo);
        ViewUtils.showProgressBar(this, progressBarLayout, message, visible);
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
                Logger.d(TAG, "dao opened");
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
            Logger.d(TAG, "created evice : " + String.valueOf(createdDevice));
            return createdDevice;
        }

        @Override
        protected void onPostExecute(Device createdDevice) {
            try {
                dao.close();
                Logger.d(TAG, "dao closed");
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            if (null != createdDevice) {
                List<Device> trackedDevices = new ArrayList<>();
                trackedDevices.add(createdDevice);
                geoDataFetched = true;
                adapter.setDevices(trackedDevices);
                showProgressBar(false);
            }

        }
    }

    private class FetchDevicesFromDb extends AsyncTask<Void, Void, List<Device>> {
        private DeviceDao dao;

        public FetchDevicesFromDb(Context context) {
            dao = new DeviceDao(context);
        }

        @Override
        protected void onPreExecute() {
            try {
                showProgressBar(true);
                dao.open();
                Logger.d(TAG, "dao open");
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
                showProgressBar(false);
            }
        }

        @Override
        protected List<Device> doInBackground(Void... params) {
            List<Device> devices = new ArrayList<>();
            try {
                devices = dao.getAllDevices();
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            Logger.d(TAG, "devices size : " + devices.size());
            showProgressBar(false);
            return devices;
        }

        @Override
        protected void onPostExecute(List<Device> devices) {
            try {
                dao.close();
                Logger.d(TAG, "dao closed");
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            adapter.setDevices(devices);
            adapter.setOnClickListener(MainActivity.this);
            Logger.d(TAG, "Tracked devices size : " + devices.size());
        }
    }
}
