package com.hilllander.khunzohn.gpstracker;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hilllander.khunzohn.gpstracker.database.dao.DeviceDao;
import com.hilllander.khunzohn.gpstracker.database.model.Device;
import com.hilllander.khunzohn.gpstracker.reciever.USSDReciever;
import com.hilllander.khunzohn.gpstracker.util.DialogUtil;
import com.hilllander.khunzohn.gpstracker.util.Logger;
import com.hilllander.khunzohn.gpstracker.util.NetworkUtil;
import com.hilllander.khunzohn.gpstracker.util.USSD;
import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import mm.technomation.mmtext.MMTextView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, USSDReciever.OnMessageRecieveListener {

    private static final String TAG = Logger.generateTag(MapsActivity.class);
    public static CameraPosition GPS;
    private GoogleMap mMap;
    private Device device;
    private float lat, lon;
    private MMTextView name, syn;
    private TextView tvLatValue, tvLongVal, tvDateTime;
    private Timer statusChecker;
    private int checkCount = 0;
    private int connectorFlag = ConnectActivity.TEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        name = (MMTextView) findViewById(R.id.name);
        syn = (MMTextView) findViewById(R.id.sync);
        tvLatValue = (TextView) findViewById(R.id.tvLatValue);
        tvLongVal = (TextView) findViewById(R.id.tvLongValue);
        tvDateTime = (TextView) findViewById(R.id.tvDateTime);
        GlobalApplication.setCurrentMessageReceiveListener(this);
        device = (Device) getIntent().getExtras().getSerializable(MainActivity.KEY_DEVICE);
        if (null != device) {
            setValue(device);
        }
        ViewUtils.setStatusBarTint(this, R.color.blue_700);
        if (!NetworkUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
        }
        syn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncLocation();
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void syncLocation() {
        String items[] = {"Sync by sending message", "Sync by dialing phone"};
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select syncing method")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: //message
                                connectorFlag = ConnectActivity.TEXT;
                                startSync(connectorFlag);
                                break;
                            case 1: //phone
                                connectorFlag = ConnectActivity.PHONE;
                                startSync(connectorFlag);
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();

    }

    private void startSync(int connectorFlag) {
        showProgress(true);
        USSD.queryGeo(device.getSimNumber(), device.getPassword(), connectorFlag);
        startCheckStatusTimer();
    }

    private void startCheckStatusTimer() {
        final Handler error = new Handler();
        final Runnable showError = new Runnable() {
            @Override
            public void run() {
                showProgress(false);
                statusChecker.cancel();
                checkCount = 0;
                showErrorDialog();
            }
        };

        statusChecker = new Timer();
        statusChecker.schedule(new TimerTask() {
            @Override
            public void run() {
                checkCount++;
                if (checkCount > 20) {
                    statusChecker.cancel();
                    error.post(showError);
                }
            }
        }, 3000, 3000);
    }

    private void showErrorDialog() {
        String title = "Syncing location Failed!";
        String message = "Can't connect to the device.\n" +
                "Make sure you've entered the number correctly and" +
                "the device is turned on.";

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startSync(connectorFlag);
                    }
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void showProgress(boolean visible) {
        DialogUtil.showProgressBar(this, R.layout.dialog_register_new_divice_progress_bar, visible);
    }

    private void setValue(Device device) {
        lat = device.getLatitude();
        lon = device.getLongitude();
        GPS = new CameraPosition.Builder().target(new LatLng(lat, lon))
                .zoom(15.5f)
                .bearing(300)
                .tilt(50)
                .build();
        tvLatValue.setText(String.valueOf(lat));
        tvLongVal.setText(String.valueOf(lon));
        name.setMyanmarText(device.getDeviceName());
        String date = device.getTrackedDate();
        String time = device.getTrackedTime();
        String dateTime = date + " " + time;
        tvDateTime.setText(dateTime);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        moveToDevice();
    }

    private void moveToDevice() {
        // Add a marker in gps's position and move the camera
        LatLng gps = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(gps).title("Device is here!"));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(GPS), 50, null);
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
    public void onGeoDataReceived(String latitude, String longitude, String date, String time, String sender) {
        Logger.d(TAG, "lat: " + latitude + " lon : " + longitude + " date : " + date + " time : " + time + " sender : " + sender);
        if (!latitude.equals("0") && !longitude.equals("0") && !date.equals("") && !time.equals("")) {
            lat = Float.parseFloat(latitude);
            lon = Float.parseFloat(longitude);
            device.setLatitude(lat);
            device.setLongitude(lon);
            device.setTrackedDate(date);
            device.setTrackedTime(time);
            setValue(device);
            moveToDevice();
            UpdateDeviceToDb update = new UpdateDeviceToDb(this);
            update.execute(device);

        } else {
            Toast.makeText(this, "Something went wrong syncing geo data!", Toast.LENGTH_SHORT).show();
        }
    }

    private class UpdateDeviceToDb extends AsyncTask<Device, Void, Device> {
        private DeviceDao dao;

        public UpdateDeviceToDb(Context context) {
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
            Device updatedDevice = null;
            try {
                updatedDevice = dao.updateDevice(device);
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            return updatedDevice;
        }

        @Override
        protected void onPostExecute(Device updatedDevice) {
            try {
                dao.close();
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            if (null != updatedDevice) {
                showProgress(false);
                device = updatedDevice;
                statusChecker.cancel();
                setResult(RESULT_OK);
            }
        }
    }
}
