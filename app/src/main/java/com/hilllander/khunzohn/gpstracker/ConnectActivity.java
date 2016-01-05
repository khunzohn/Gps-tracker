package com.hilllander.khunzohn.gpstracker;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ErrorDialogFragment;
import com.hilllander.khunzohn.gpstracker.fragment.ConnectionWarningFragment;
import com.hilllander.khunzohn.gpstracker.fragment.MarketingFragments;
import com.hilllander.khunzohn.gpstracker.reciever.USSDReciever;
import com.hilllander.khunzohn.gpstracker.util.Logger;
import com.hilllander.khunzohn.gpstracker.util.USSD;
import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

import java.util.Timer;
import java.util.TimerTask;

import mm.technomation.mmtext.MMButtonView;
import mm.technomation.mmtext.MMTextView;

public class ConnectActivity extends AppCompatActivity implements USSDReciever.OnMessageRecieveListener {
    public static final int TEXT = 10;
    public static final int PHONE = 11;
    public static final String KEY_UP_ENABLE = "key for up enable";
    private static final long CHECK_PERIOD = 3000;
    private static final int MAX_CHECK_OUT = 20; //20*3000 1 min
    private static final String TAG = Logger.generateTag(ConnectActivity.class);
    private static final String KEY_SIM_NUMBER = "key sim number";
    private static final String KEY_CONNECTOR_FLAG = "key for connector flag";
    private MMTextView tvTextConnect;
    private MMTextView tvPhoneConnect;
    private EditText etSimNum;
    private MMTextView tvSkip;
    private boolean connectionHasSucceeded = false;
    private int checkCount;
    private int connectorFlag;
    private MMButtonView btConnect;
    private View progressBarLayout;

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
                                //TODO increase checkCount for production
                                // connection not succeed till 60 sec(20*3000 = 30,000) then show error dialog
                                connectionStatusChecker.cancel();
                                errorDialogShower.post(show);
                            } else if (connectionHasSucceeded) { //connection success
                                connectionStatusChecker.cancel();
                                onSucceeded(num, connectorFlag);
                            }
                        }
                    }, CHECK_PERIOD, CHECK_PERIOD); //check connection status for every 3 sec
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

    private void onSucceeded(String num, int connectorFlag) {
        Intent main = new Intent(ConnectActivity.this, MainActivity.class);
        main.putExtra(KEY_SIM_NUMBER, num);
        main.putExtra(KEY_CONNECTOR_FLAG, connectorFlag);
        startActivity(main);
        finish();
    }

    private void connect(String num, int connectorFlag) {
        Logger.d(TAG, "connector flag" + connectorFlag);
        switch (connectorFlag) {
            case MarketingFragments.TEXT:
                USSD.smsBegin(num);
                break;
            case MarketingFragments.PHONE:
                //TODO replace with phone call connecting
                USSD.smsBegin(num);
                break;
        }
    }

    private void connectLater() {
        Intent main = new Intent(ConnectActivity.this, MainActivity.class);
        startActivity(main);
        finish();
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
        final String message = getString(R.string.dialog_message_connecting);
        ViewUtils.showProgressBar(this, progressBarLayout, message, visible);
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
}
