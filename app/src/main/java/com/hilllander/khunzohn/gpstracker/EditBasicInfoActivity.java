package com.hilllander.khunzohn.gpstracker;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;

import com.hilllander.khunzohn.gpstracker.database.dao.DeviceDao;
import com.hilllander.khunzohn.gpstracker.database.model.Device;
import com.hilllander.khunzohn.gpstracker.util.Logger;
import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

import java.sql.SQLException;

import mm.technomation.mmtext.MMEditText;
import mm.technomation.mmtext.MMTextView;

/**
 * Created by khunzohn on 1/6/16.
 */
public class EditBasicInfoActivity extends AppCompatActivity {
    public static final String KEY_RETURNED_DEVICE = "key for returned device";
    private static final String TAG = Logger.generateTag(EditBasicInfoActivity.class);
    private static final int NAME = 0;
    private static final int TYPE = 1;
    Device device = new Device();
    private LinearLayout cancel, ok;
    private MMEditText etDeviceInfo;
    private MMTextView title;
    private int infoToChange = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_basic_info);
        ViewUtils.setStatusBarTint(this, R.color.deep_purple_700);
        etDeviceInfo = (MMEditText) findViewById(R.id.etDeviceInfo);
        cancel = (LinearLayout) findViewById(R.id.cancel);
        title = (MMTextView) findViewById(R.id.title);
        ok = (LinearLayout) findViewById(R.id.ok);
        ok.setEnabled(false);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = etDeviceInfo.getText().toString();
                switch (infoToChange) {
                    case NAME:
                        device.setDeviceName(info);
                        break;
                    case TYPE:
                        device.setDeviceType(info);
                        break;
                }
                ChangeDeviceInfo change = new ChangeDeviceInfo(EditBasicInfoActivity.this);
                change.execute(device);
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            String infoTitle = bundle.getString(EditProfileActivity.KEY_TITLE);
            device = (Device) bundle.getSerializable(EditProfileActivity.KEY_DEVICE);

            if (null != infoTitle && null != device) {
                title.setMyanmarText(infoTitle);
                if (infoTitle.equals(EditProfileActivity.TITLE_CHANGE_NAME)) {
                    etDeviceInfo.setHint(device.getDeviceName());
                    infoToChange = NAME;
                } else if (infoTitle.equals(EditProfileActivity.TITLE_CHANGE_TYPE)) {
                    etDeviceInfo.setHint(device.getDeviceType());
                    infoToChange = TYPE;
                }

            }
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
        }
    }

    private void showProgressBar(boolean visible) {

    }

    private class ChangeDeviceInfo extends AsyncTask<Device, Void, Device> {
        private DeviceDao dao;

        public ChangeDeviceInfo(Context context) {
            dao = new DeviceDao(context);
        }

        @Override
        protected void onPreExecute() {
            showProgressBar(true);
            try {
                dao.open();
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
        }

        @Override
        protected Device doInBackground(Device... params) {
            Device device = params[0];
            Device deviceUpated = new Device();
            try {
                deviceUpated = dao.updateDevice(device);
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            return deviceUpated;
        }

        @Override
        protected void onPostExecute(Device deviceUpdated) {
            showProgressBar(false);
            try {
                dao.close();
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            Intent returnedIntent = new Intent();
            returnedIntent.putExtra(KEY_RETURNED_DEVICE, deviceUpdated);
            setResult(RESULT_OK, returnedIntent);
            finish();
        }
    }
}
