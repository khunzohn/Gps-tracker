package com.hilllander.khunzohn.gpstracker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.hilllander.khunzohn.gpstracker.util.Logger;
import com.hilllander.khunzohn.gpstracker.util.NetworkUtil;
import com.hilllander.khunzohn.gpstracker.util.PermissionUtils;
import com.hilllander.khunzohn.gpstracker.util.Telephony;
import com.hilllander.khunzohn.gpstracker.util.USSD;
import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import mm.technomation.mmtext.MMTextView;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        USSDReciever.OnMessageRecieveListener, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMyLocationButtonClickListener {

    private static final String TAG = Logger.generateTag(MapsActivity.class);
    private static final int REQUEST_FINE_LOCATION = 45;
    public static CameraPosition GPS;
    private GoogleMap mMap;
    private Device device;
    private float lat, lon;
    private MMTextView name;
    private TextView tvLatValue, tvLongVal, tvDateTime;
    private Timer statusChecker;
    private int checkCount = 0;
    private int connectorFlag = ConnectActivity.TEXT;
    private AlertDialog progressDialog;
    private String STANDARD = "Standard";
    private String SATELLITE = "Satellite";
    private boolean mPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        name = (MMTextView) findViewById(R.id.name);
        MMTextView syn = (MMTextView) findViewById(R.id.sync);
        tvLatValue = (TextView) findViewById(R.id.tvLatValue);
        tvLongVal = (TextView) findViewById(R.id.tvLongValue);
        tvDateTime = (TextView) findViewById(R.id.tvDateTime);
        GlobalApplication.setCurrentMessageReceiveListener(this);
        device = (Device) getIntent().getExtras().getSerializable(MainActivity.KEY_DEVICE);
        if (null != device) {
            setValue(device);
        }

        ViewUtils.setStatusBarTint(this, R.color.green_700);
        if (!NetworkUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
        }
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGpsAvailable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGpsAvailable) {
            showEnableGpsDialog();
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

    private void showEnableGpsDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Enable location")
                .setMessage("Go to setting to enable Location?")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent setting = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(setting);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        dialog.show();
    }

    private boolean isMapReady() {
        return mMap != null;
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
        switch (connectorFlag) {
            case ConnectActivity.PHONE:
                Telephony.queryGeo(MapsActivity.this, device.getSimNumber());
                break;
            case ConnectActivity.TEXT:
                USSD.queryGeo(device.getSimNumber(), device.getPassword());
                break;
        }
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
                if (checkCount > 20) { //1 minutes
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
                        syncLocation();
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
        if (progressDialog == null) {
            progressDialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setView(R.layout.dialog_register_new_divice_progress_bar)
                    .create();
        }
        if (visible) {
            progressDialog.show();
        } else {
            progressDialog.hide();
        }
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
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        enableMyLocation();
        moveToDevice();
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, REQUEST_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else {
            if (null != mMap) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (REQUEST_FINE_LOCATION != requestCode) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (isMapReady()) {
                enableMyLocation();
            }
        } else {
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private void moveToDevice() {
        // Add a marker in gps's position and move the camera
        LatLng gps = new LatLng(lat, lon);
        if (isMapReady()) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(gps).title("Device is here!"));
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(GPS), 50, null);
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

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Moving to my location", Toast.LENGTH_SHORT).show();
        return false;
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
                if (null != statusChecker)
                    statusChecker.cancel();
                setResult(RESULT_OK);
            }
        }
    }

}
