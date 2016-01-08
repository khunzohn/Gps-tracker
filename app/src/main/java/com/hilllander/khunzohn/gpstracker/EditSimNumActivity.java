package com.hilllander.khunzohn.gpstracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.hilllander.khunzohn.gpstracker.database.dao.DeviceDao;
import com.hilllander.khunzohn.gpstracker.database.model.Device;
import com.hilllander.khunzohn.gpstracker.reciever.USSDReciever;
import com.hilllander.khunzohn.gpstracker.util.Logger;
import com.hilllander.khunzohn.gpstracker.util.USSD;
import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import mm.technomation.mmtext.MMTextView;

/**
 * Created by khunzohn on 1/8/16.
 */
public class EditSimNumActivity extends AppCompatActivity implements USSDReciever.OnMessageRecieveListener {
    private static final String TAG = Logger.generateTag(EditSimNumActivity.class);
    private View cancel, ok;
    private MMTextView title;
    private EditText etDeviceInfo;
    private Device device;
    private int checkCount = 0;
    private Timer statusChecker;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sim_number);
        GlobalApplication.setCurrentMessageReceiveListener(this);
        ViewUtils.setStatusBarTint(this, R.color.indigo_700);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ok = findViewById(R.id.ok);
        ok.setEnabled(false);
        title = (MMTextView) findViewById(R.id.title);
        etDeviceInfo = (EditText) findViewById(R.id.etDeviceInfo);
        etDeviceInfo.setEms(10);
        etDeviceInfo.setInputType(InputType.TYPE_CLASS_PHONE);
        etDeviceInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ok.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        device = new Device();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            device = (Device) bundle.getSerializable(EditProfileActivity.KEY_DEVICE);
            if (null != device) {
                etDeviceInfo.setHint(device.getSimNumber());
                title.setMyanmarText("Enter sim card number");

            }
        }
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newSimNumber = etDeviceInfo.getText().toString();
                registerNewDevice(newSimNumber);
            }
        });
    }

    private void registerNewDevice(String newSimNumber) {
        USSD.smsBegin(newSimNumber);
        showProgressBar(true);

        final Handler error = new Handler();
        final Runnable showError = new Runnable() {
            @Override
            public void run() {
                showProgressBar(false);
                statusChecker.cancel();
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
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Connecting Failed!")
                .setMessage("Can't connect to the device.\n" +
                        "Make sure you've entered the number correctly and\n" +
                        "the device is turned on.")
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .create();
        dialog.show();
    }

    private void showProgressBar(boolean visible) {
        if (null == progressDialog) {
            progressDialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setView(R.layout.dialog_register_new_divice_progress_bar)
                    .create();
        }
        if (visible) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBeginOkReceived(String sender) {
        device.setSimNumber(sender);
        UpdateDeviceToDb update = new UpdateDeviceToDb(this);
        update.execute(device);
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
            Device deviceUpdated = new Device();
            try {
                deviceUpdated = dao.updateDevice(device);
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            return deviceUpdated;
        }

        @Override
        protected void onPostExecute(Device updatedDevice) {
            try {
                dao.open();
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            device = updatedDevice;
            statusChecker.cancel();
            showProgressBar(false);
            Intent returnedIntent = new Intent();
            returnedIntent.putExtra(EditProfileActivity.KEY_RETURNED_DEVICE, device);
            setResult(RESULT_OK, returnedIntent);
            onBackPressed();
        }
    }
}
