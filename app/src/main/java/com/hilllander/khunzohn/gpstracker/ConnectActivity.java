package com.hilllander.khunzohn.gpstracker;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ErrorDialogFragment;
import com.hilllander.khunzohn.gpstracker.database.dao.DeviceDao;
import com.hilllander.khunzohn.gpstracker.database.model.Device;
import com.hilllander.khunzohn.gpstracker.database.table.DeviceTable;
import com.hilllander.khunzohn.gpstracker.fragment.ConnectionWarningFragment;
import com.hilllander.khunzohn.gpstracker.fragment.MarketingFragments;
import com.hilllander.khunzohn.gpstracker.reciever.USSDReciever;
import com.hilllander.khunzohn.gpstracker.util.Logger;
import com.hilllander.khunzohn.gpstracker.util.USSD;
import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import mm.technomation.mmtext.MMButtonView;
import mm.technomation.mmtext.MMTextView;

public class ConnectActivity extends AppCompatActivity implements USSDReciever.OnMessageRecieveListener {
    public static final int TEXT = 10;
    public static final int PHONE = 11;
    public static final String KEY_UP_ENABLE = "key for up enable";
    public static final String KEY_DEVICE_EXTRA = "key for device extra";
    private static final long CHECK_PERIOD = 3000;
    private static final int MAX_CHECK_OUT = 30; //20*3000 1 min
    private static final String TAG = Logger.generateTag(ConnectActivity.class);
    private MMTextView tvTextConnect;
    private MMTextView tvPhoneConnect;
    private EditText etSimNum;
    private MMTextView tvSkip;
    private boolean connectionHasSucceeded = false;
    private int checkCount;
    private int connectorFlag;
    private MMButtonView btConnect;
    private View progressBarLayout;
    private Device createdDevice;
    private boolean firstAppLaunch;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        GlobalApplication.setCurrentMessageReceiveListener(this);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        tvTextConnect = (MMTextView) findViewById(R.id.tvTextConnect);
        tvPhoneConnect = (MMTextView) findViewById(R.id.tvPhoneConnect);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_activity_connect));
        setSupportActionBar(toolbar);
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            boolean upEnable = bundle.getBoolean(KEY_UP_ENABLE, false);
            firstAppLaunch = bundle.getBoolean(MarketingActivity.KEY_FIRST_APP_LAUNCH, false);
            ActionBar actionBar = getSupportActionBar();
            if (null != actionBar) {
                actionBar.setDisplayHomeAsUpEnabled(upEnable);
            }
        }
        final MMTextView tvInputSimCard = (MMTextView) findViewById(R.id.tvInputSimCard);
        etSimNum = (EditText) findViewById(R.id.etSimNum);
        tvSkip = (MMTextView) findViewById(R.id.skip);
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectLater();
            }
        });
        ViewUtils.setStatusBarTint(this, R.color.colorPrimaryDark);
        btConnect = (MMButtonView) findViewById(R.id.btConnect);
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String num = etSimNum.getEditableText().toString();
                if (num.equals("")) {
                    tvInputSimCard.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    tvInputSimCard.setTextColor(getResources().getColor(android.R.color.white));
                    showProgressBar(true);
                    enableComponents(false);
                    connect(num, connectorFlag);
                }
            }
        });


        connectWith(TEXT);
        tvTextConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectWith(TEXT);
            }


        });
        tvPhoneConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectWith(PHONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (firstAppLaunch) {
            // disable back pressed
        } else {
            super.onBackPressed();
        }
    }

    private void onSucceeded(Device device) {
        Logger.d(TAG, null != device ? "device created: " + device.toString() : "device is null!");
        if (firstAppLaunch) {
            Intent main = new Intent(ConnectActivity.this, MainActivity.class);
            main.putExtra(KEY_DEVICE_EXTRA, device);
            startActivity(main);
            finish();
        } else {
            Intent returnedIntent = new Intent();
            returnedIntent.putExtra(KEY_DEVICE_EXTRA, device);
            setResult(RESULT_OK, returnedIntent);
            finish();
        }
    }

    private void connect(String num, int connectorFlag) {
        Logger.d(TAG, "connector flag" + connectorFlag);
        if (firstAppLaunch) {
            switch (connectorFlag) {
                case MarketingFragments.TEXT:
                    USSD.smsBegin(num);
                    Logger.d(TAG, "begin sent!");
                    break;
                case MarketingFragments.PHONE:
                    //TODO replace with phone call connecting
                    USSD.smsBegin(num);
                    Logger.d(TAG, "begin sent!");
                    break;
            }
        } else {
            USSD.queryGeo(num, USSD.DEAFULT_PASSWORD);
            Logger.d(TAG, "query geo sent!");
        }
        final Handler errorDialogShower = new Handler();
        final Runnable show = new Runnable() {
            @Override
            public void run() {
                showProgressBar(false);
                showErrorDialog();
            }
        };
        final Timer connectionStatusChecker = new Timer("status checker", false);
        connectionStatusChecker.schedule(new TimerTask() {
            @Override
            public void run() {
                checkCount++;
                if (!connectionHasSucceeded && checkCount > MAX_CHECK_OUT) {
                    // connection not succeed till 60 sec(20*3000 = 30,000) then show error dialog
                    connectionStatusChecker.cancel();
                    checkCount = 0;
                    errorDialogShower.post(show);
                } else if (connectionHasSucceeded) { //connection success
                    connectionStatusChecker.cancel();
                    checkCount = 0;
                    onSucceeded(createdDevice);
                }
            }
        }, CHECK_PERIOD, CHECK_PERIOD); //check connection status for every 3 sec
    }

    private void connectLater() {
        if (firstAppLaunch) {
            Intent main = new Intent(ConnectActivity.this, MainActivity.class);
            startActivity(main);
            finish();
        } else {
            finish();
        }


    }

    private void connectWith(int connFlag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (connFlag) {
            case TEXT:
                tvTextConnect.setSelected(true);
                tvPhoneConnect.setSelected(false);
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.warningContent, ConnectionWarningFragment.getInstance(TEXT))
                        .commit();
                connectorFlag = TEXT;
                break;
            case PHONE:
                tvTextConnect.setSelected(false);
                tvPhoneConnect.setSelected(true);
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.warningContent, ConnectionWarningFragment.getInstance(PHONE))
                        .commit();
                connectorFlag = PHONE;
                break;
        }
    }

    private void enableComponents(boolean enable) {
        btConnect.setEnabled(enable);
        etSimNum.setEnabled(enable);
        tvSkip.setEnabled(enable);
    }

    private void showProgressBar(final boolean visible) {
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

    private void showErrorDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Connecting fail!");
        dialog.setContentView(R.layout.dialog_connecting_fail);
        MMButtonView later = (MMButtonView) dialog.findViewById(R.id.later);
        MMButtonView retry = (MMButtonView) dialog.findViewById(R.id.retry);
        final ErrorDialogFragment errorDialog = ErrorDialogFragment.newInstance(dialog);
        errorDialog.setCancelable(false);
        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableComponents(true);
                errorDialog.dismiss();
                connectLater();
            }
        });
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableComponents(true);
                errorDialog.dismiss();
            }
        });
        errorDialog.show(getFragmentManager(), "error dialog");
    }

    @Override
    public void onBeginOkReceived(String sender) {
        Logger.d(TAG, "begin ok received");
        USSD.queryGeo(sender, USSD.DEAFULT_PASSWORD);
        Logger.d(TAG, "query sent.");
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
        Logger.d(TAG, "lat: " + lat + " lon : " + lon + " date : " + date + " time : " + time + " sender : " + sender);
        if (!lat.equals("0") && !lon.equals("0") && !date.equals("") && !time.equals("")) {
            float latitude = Float.parseFloat(lat);
            float longitude = Float.parseFloat(lon);
            Device device = new Device();
            device.setLatitude(latitude);
            device.setLongitude(longitude);
            device.setTrackedTime(date);
            device.setTrackedDate(time);
            device.setSimNumber(sender);
            device.setDeviceName(Device.DEFAULT_NAME);
            device.setDeviceType(Device.DEFAULT_TYPE);
            device.setPassword(USSD.DEAFULT_PASSWORD);
            device.setAuthorization(Device.UN_AUTHORIZED);
            device.setPhotoUrl(DeviceTable.DEFAULT_PHOTO_URL);
            CreateDeviceAsync createDevice = new CreateDeviceAsync(this);
            createDevice.execute(device);
        } else {
            makeToast("empty geo received!");
        }
    }

    private void makeToast(String message) {
        if (BuildConfig.DEBUG)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
        protected void onPostExecute(Device deviceCreated) {
            try {
                dao.close();
                Logger.d(TAG, "dao closed");
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            if (null != deviceCreated) {
                createdDevice = deviceCreated;
                connectionHasSucceeded = true;
                showProgressBar(false);
            }
        }
    }
}
