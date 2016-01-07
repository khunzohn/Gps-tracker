package com.hilllander.khunzohn.gpstracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.hilllander.khunzohn.gpstracker.database.model.Device;
import com.hilllander.khunzohn.gpstracker.util.Logger;
import com.hilllander.khunzohn.gpstracker.util.USSD;
import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

import mm.technomation.mmtext.MMButtonView;
import mm.technomation.mmtext.MMTextView;

/**
 *Created by khunzohn on 1/6/16.
 */
public class EditProfileActivity extends AppCompatActivity {
    public static final String KEY_TITLE = "key for title";
    public static final String TITLE_CHANGE_NAME = "Change name";
    public static final String TITLE_CHANGE_TYPE = "Change type";
    public static final String KEY_DEVICE = "key for device";
    private static final String TAG = Logger.generateTag(EditProfileActivity.class);
    private static final int REQUEST_EDIT_NAME = 456;
    private static final int REQUEST_EDIT_TYPE = 421;
    private MMTextView tvNameValue;
    private ImageButton ibEditName;
    private MMTextView tvTypeValue;
    private ImageButton ibEditType;
    private MMTextView tvSimnumberValue;
    private ImageButton ibEditSimNumber;
    private MMTextView tvAuthorizationValue;
    private ImageButton ibEditAuthorization;
    private MMTextView tvPasswordValue;
    private ImageButton ibEditPassword, ibProfile;
    private Device device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ViewUtils.setStatusBarTint(this, R.color.indigo_700);
        tvNameValue = (MMTextView) findViewById(R.id.tvNameValue);
        tvTypeValue = (MMTextView) findViewById(R.id.tvTypeValue);
        tvSimnumberValue = (MMTextView) findViewById(R.id.tvSimnumberValue);
        tvAuthorizationValue = (MMTextView) findViewById(R.id.tvAuthorizationValue);
        tvPasswordValue = (MMTextView) findViewById(R.id.tvPasswordValue);
        device = new Device();
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            device = (Device) bundle.getSerializable(MainActivity.KEY_DEVICE);
            if (null != device) {
                setValue(device);
            }
        } else {
            Logger.e(TAG, "device is null!");
        }

        ibEditName = (ImageButton) findViewById(R.id.ibEditName);
        ibEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editName = new Intent(EditProfileActivity.this, EditBasicInfoActivity.class);
                editName.putExtra(KEY_DEVICE, device);
                editName.putExtra(KEY_TITLE, TITLE_CHANGE_NAME);
                startActivityForResult(editName, REQUEST_EDIT_NAME);
            }
        });

        ibEditType = (ImageButton) findViewById(R.id.ibEditType);
        ibEditType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editName = new Intent(EditProfileActivity.this, EditBasicInfoActivity.class);
                editName.putExtra(KEY_DEVICE, device);
                editName.putExtra(KEY_TITLE, TITLE_CHANGE_TYPE);
                startActivityForResult(editName, REQUEST_EDIT_TYPE);
            }
        });
        ibEditSimNumber = (ImageButton) findViewById(R.id.ibEditSimNumber);
        ibEditSimNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ibEditAuthorization = (ImageButton) findViewById(R.id.ibEditAuthorization);
        ibEditAuthorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ibEditPassword = (ImageButton) findViewById(R.id.ibEditPassword);
        ibEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ibProfile = (ImageButton) findViewById(R.id.ibProfile);
        ibProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        final MMButtonView fine = (MMButtonView) findViewById(R.id.fine);
        fine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setValue(Device device) {
        tvNameValue.setMyanmarText(device.getDeviceName());
        tvTypeValue.setMyanmarText(device.getDeviceType());
        tvAuthorizationValue.setMyanmarText(device.getAuthorization());
        tvSimnumberValue.setMyanmarText(device.getSimNumber());
        if (device.getPassword().equals(USSD.DEAFULT_PASSWORD)) {
            tvPasswordValue.setMyanmarText("no password");
        } else {
            tvPasswordValue.setMyanmarText("secured with password");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            if (REQUEST_EDIT_NAME == requestCode || REQUEST_EDIT_TYPE == requestCode) {
                Bundle bundle = data.getExtras();
                if (null != bundle) {
                    Device device = (Device) bundle.getSerializable(EditBasicInfoActivity.KEY_RETURNED_DEVICE);
                    if (null != device)
                        setValue(device);
                }
            }
        }
    }
}
