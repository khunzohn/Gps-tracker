package com.hilllander.khunzohn.gpstracker;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hilllander.khunzohn.gpstracker.database.dao.DeviceDao;
import com.hilllander.khunzohn.gpstracker.database.model.Device;
import com.hilllander.khunzohn.gpstracker.reciever.USSDReciever;
import com.hilllander.khunzohn.gpstracker.util.DialogUtil;
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
public class EditAuthorizationActivity extends AppCompatActivity implements USSDReciever.OnMessageRecieveListener {
    private static final String TAG = Logger.generateTag(EditAuthorizationActivity.class);
    private View cancel, ok;
    private RadioGroup rg;
    private RadioButton autho, unAutho;
    private Device device;
    private int initCheckedId;
    private String password;
    private MMTextView title;
    private EditText authorizationNum;
    private boolean radioCheckedGotChanged = false;
    private boolean authorizationNumGotChanged = false;
    private Timer statusChecker;
    private AlertDialog progressDialog;
    private int checkCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_authorization);
        ViewUtils.setStatusBarTint(this, R.color.indigo_700);
        GlobalApplication.setCurrentMessageReceiveListener(this);
        title = (MMTextView) findViewById(R.id.title);
        authorizationNum = (EditText) findViewById(R.id.authorizationNumberValue);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ok = findViewById(R.id.ok);
        ok.setEnabled(false);
        rg = (RadioGroup) findViewById(R.id.rg);
        autho = (RadioButton) findViewById(R.id.rbAuthorizied);
        unAutho = (RadioButton) findViewById(R.id.rbUnAuthorizied);
        device = new Device();
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            device = (Device) bundle.getSerializable(EditProfileActivity.KEY_DEVICE);
            if (null != device) {
                password = device.getPassword();
                if (device.getAuthorization().equals(Device.UN_AUTHORIZED)) {
                    initCheckedId = unAutho.getId();
                } else {
                    initCheckedId = autho.getId();
                    authorizationNum.setEnabled(false);
                   /*since device is already authorized, this activity is supposed to handle
                    *un authorizing device.Authorized number saved in database will be used for that purpose.
                    *no need to enter authorized number to un authorize device*/
                }
                rg.check(initCheckedId);
            }
        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioCheckedGotChanged = true;
                if (authorizationNum.isEnabled()) { // In case of to do authorization
                    if (checkedId != initCheckedId && authorizationNumGotChanged) {
                        ok.setEnabled(true);
                    } else {
                        ok.setEnabled(false);
                    }
                } else {                             // In case of to do un authorization
                    if (checkedId != initCheckedId) {
                        ok.setEnabled(true);
                    } else {
                        ok.setEnabled(false);
                    }
                }

            }
        });
        authorizationNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                authorizationNumGotChanged = true;
                if (radioCheckedGotChanged) {
                    ok.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int checkId = rg.getCheckedRadioButtonId();
                String authorizingNum = authorizationNum.getText().toString();
                String desAddress = device.getSimNumber();
                String password = device.getPassword();
                if (checkId == autho.getId()) {
                    authorize(desAddress, password, authorizingNum);
                } else {
                    String authorizedNum = device.getAuthorization();
                    unAuthorize(desAddress, password, authorizedNum);
                }

            }
        });
    }

    private void unAuthorize(String desAddress, String password, String authorizedNum) {
        showProgress(true);
        USSD.smsNoAdmin(desAddress, password, authorizedNum);
        startCheckStatusTimer();
    }

    private void authorize(String desAddress, String password, String authorizingNum) {
        showProgress(true);
        USSD.smsAdmin(desAddress, password, authorizingNum);
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
        String title = "Authorization Failed!";
        String message = "Can't connect to the device.\n" +
                "Make sure you've entered the number correctly and" +
                "the device is turned on.";
        String negativeBtn = "retry";
        String positiveBtn = "Later";
        DialogUtil.showErrorDialog(this, title, message, negativeBtn, positiveBtn);
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
        device.setAuthorization(sender);
        UpdateDeviceToDb update = new UpdateDeviceToDb(this);
        update.execute(device);
    }

    @Override
    public void onNoAdminOkReceived(String sender) {
        device.setAuthorization(Device.UN_AUTHORIZED);
        UpdateDeviceToDb update = new UpdateDeviceToDb(this);
        update.execute(device);
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
                Intent returnedIntent = new Intent();
                returnedIntent.putExtra(EditProfileActivity.KEY_RETURNED_DEVICE, device);
                setResult(RESULT_OK, returnedIntent);
                finish();
            }
        }
    }
}
