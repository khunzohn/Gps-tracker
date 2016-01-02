package com.hilllander.khunzohn.gpstracker.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.common.ErrorDialogFragment;
import com.hilllander.khunzohn.gpstracker.GlobalApplication;
import com.hilllander.khunzohn.gpstracker.MarketingActivity;
import com.hilllander.khunzohn.gpstracker.R;
import com.hilllander.khunzohn.gpstracker.reciever.USSDReciever;
import com.hilllander.khunzohn.gpstracker.util.Logger;
import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

import java.util.Timer;
import java.util.TimerTask;

import mm.technomation.mmtext.MMButtonView;
import mm.technomation.mmtext.MMTextView;

/**
 *Created by khunzohn on 12/31/15.
 */
public class MarketingFragments extends Fragment implements USSDReciever.OnMessageRecieveListener {
    public static final int TEXT = 10;
    public static final int PHONE = 11;
    private static final String KEY_POSITION = "key positoin";
    private static final String TAG = Logger.generateTag(MarketingFragments.class);
    private MMTextView tvTextConnect;
    private MMTextView tvPhoneConnect;
    private EditText etSimNum;
    private ConnectorListener connectorListener;
    private boolean connectionHasSucceeded = false;
    private int checkCount;
    private int connectorFlag;
    private MMButtonView btConnect;
    private View progressBarLayout;

    public MarketingFragments() {

    }

    public static MarketingFragments newInstance(int position) {
        MarketingFragments fragment = new MarketingFragments();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        GlobalApplication.setCurrentListener(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        try {
            connectorListener = (ConnectorListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("MarketingActivity must implement ConnectorListener interface.");
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        connectorListener = null;
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        int position = args.getInt(KEY_POSITION);
        final View view;
        if (position == MarketingActivity.NUM_PAGES - 1) { // last fragment
            view = inflater.inflate(R.layout.fragment_market_login, container, false);
            progressBarLayout = view.findViewById(R.id.progressBarLayout);
            tvTextConnect = (MMTextView) view.findViewById(R.id.tvTextConnect);
            tvPhoneConnect = (MMTextView) view.findViewById(R.id.tvPhoneConnect);
            final MMTextView tvInputSimCard = (MMTextView) view.findViewById(R.id.tvInputSimCard);
            etSimNum = (EditText) view.findViewById(R.id.etSimNum);
            MMTextView skip = (MMTextView) view.findViewById(R.id.skip);
            skip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectorListener.onConnectLater();
                }
            });
            btConnect = (MMButtonView) view.findViewById(R.id.btConnect);
            btConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final String num = etSimNum.getEditableText().toString();
                    if (num.equals("")) {
                        tvInputSimCard.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
                    } else {
                        tvInputSimCard.setTextColor(getActivity().getResources().getColor(android.R.color.white));
                        showProgressBar(true);
                        v.setEnabled(false);
                        etSimNum.setEnabled(false);
                        connectorListener.connect(num, connectorFlag);

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
                                if (!connectionHasSucceeded && checkCount > 6) {
                                    //TODO increase checkCount for production
                                    // connection not succeed till 30 sec(6*5000 = 30,000) then show error dialog
                                    connectionStatusChecker.cancel();
                                    errorDialogShower.post(show);
                                } else if (!connectionHasSucceeded && checkCount > 2) { //connection success
                                    //TODO re correct temp success algorithm
                                    connectionStatusChecker.cancel();
                                    connectorListener.onSucceeded(num);
                                }
                            }
                        }, 3000, 3000); //check connection status for every 5 sec
                        //TODO replace with 5000 for production
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
        } else {
            view = inflater.inflate(R.layout.fragment_market, container, false);
            MMTextView tvIstruction = (MMTextView) view.findViewById(R.id.tvInstruction);
            tvIstruction.setText(String.valueOf(position));
            switch (position) {
                case 0:
                    tvIstruction.setMyanmarText(getActivity().getString(R.string.instruction_one));
                    break;
                case 1:
                    tvIstruction.setMyanmarText(getActivity().getString(R.string.instruction_two));
                    break;
                case 2:
                    tvIstruction.setMyanmarText(getActivity().getString(R.string.instruction_three));
                    break;
            }
        }

        return view;
    }

    private void showErrorDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("Connecting fail!");
        dialog.setContentView(R.layout.dialog_connecting_fail);
        MMButtonView later = (MMButtonView) dialog.findViewById(R.id.later);
        MMButtonView retry = (MMButtonView) dialog.findViewById(R.id.retry);
        final ErrorDialogFragment errorDialog = ErrorDialogFragment.newInstance(dialog);
        errorDialog.setCancelable(false);
        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btConnect.setEnabled(true);
                etSimNum.setEnabled(true);
                errorDialog.dismiss();
                connectorListener.onConnectLater();
            }
        });
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btConnect.setEnabled(true);
                etSimNum.setEnabled(true);
                errorDialog.dismiss();
            }
        });
        errorDialog.show(getActivity().getFragmentManager(), "error dialog");
    }

    private void connectWith(int connFlag) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
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

    @Override
    public void onBeginOkReceived(String sender) {
        Logger.d(TAG, "Begin ok from : " + sender);
        connectionHasSucceeded = true;
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
    public void onGeoDataReceived(String lat, String lon, String sender) {

    }

    private void showProgressBar(final boolean visible) {
        final String message = getActivity().getString(R.string.dialog_message_connecting);
        ViewUtils.showProgressBar(getActivity(), progressBarLayout, message, visible);
    }
    public interface ConnectorListener {
        void connect(String simNum, int connectorFlag);

        void onConnectLater();

        void onSucceeded(String simNum);
    }
}

