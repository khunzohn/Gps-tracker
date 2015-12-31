package com.hilllander.khunzohn.gpstracker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hilllander.khunzohn.gpstracker.MarketActivity;
import com.hilllander.khunzohn.gpstracker.R;

import mm.technomation.mmtext.MMTextView;

/**
 * Created by ubunphyu on 12/31/15.
 */
public class ConnectionWarningFragment extends Fragment {
    private static final String KEY_CONNECTOR_FLAG = "connector flag";

    public ConnectionWarningFragment() {

    }

    public static ConnectionWarningFragment getInstance(int connectorFlag) {
        ConnectionWarningFragment fragment = new ConnectionWarningFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_CONNECTOR_FLAG, connectorFlag);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection_warning, container, false);
        MMTextView tvWarning = (MMTextView) view.findViewById(R.id.tvWarning);
        int connectorFlag = getArguments().getInt(KEY_CONNECTOR_FLAG);
        String warning = "";
        switch (connectorFlag) {
            case MarketActivity.TEXT:
                warning = getActivity().getString(R.string.warning_message_for_texting_connect);
                break;
            case MarketActivity.PHONE:
                warning = getActivity().getString(R.string.warning_message_for_phone_connect);
                break;
        }
        tvWarning.setMyanmarText(warning);
        return view;
    }
}
