package com.hilllander.khunzohn.gpstracker.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hilllander.khunzohn.gpstracker.MarketingActivity;
import com.hilllander.khunzohn.gpstracker.R;

import mm.technomation.mmtext.MMButtonView;
import mm.technomation.mmtext.MMTextView;

/**
 *Created by khunzohn on 12/31/15.
 */
public class MarketingFragments extends Fragment {
    public static final int TEXT = 10;
    public static final int PHONE = 11;
    private static final String KEY_POSITION = "key positoin";
    private MMTextView tvTextConnect;
    private MMTextView tvPhoneConnect;
    private int connectorFlag;
    private EditText etSimNum;
    private MMButtonView btConnect;
    private Connector connector;

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
    public void onAttach(Context context) {
        try {
            connector = (Connector) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("MarketingActivity must implement Connector interface.");
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        connector = null;
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        int position = args.getInt(KEY_POSITION);
        View view;
        if (position == MarketingActivity.NUM_PAGES - 1) { // last fragment
            view = inflater.inflate(R.layout.fragment_market_login, container, false);
            tvTextConnect = (MMTextView) view.findViewById(R.id.tvTextConnect);
            tvPhoneConnect = (MMTextView) view.findViewById(R.id.tvPhoneConnect);
            final MMTextView tvInputSimCard = (MMTextView) view.findViewById(R.id.tvInputSimCard);
            etSimNum = (EditText) view.findViewById(R.id.etSimNum);
            btConnect = (MMButtonView) view.findViewById(R.id.btConnect);
            btConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String num = etSimNum.getEditableText().toString();
                    if (num.equals("")) {
                        tvInputSimCard.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
                    } else {
                        tvInputSimCard.setTextColor(getActivity().getResources().getColor(android.R.color.white));
                        etSimNum.setCursorVisible(false);
                        btConnect.setClickable(false);
                        connector.connect(num, connectorFlag);

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

    private void connectWith(int connectorFlag) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        switch (connectorFlag) {
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

    public interface Connector {
        void connect(String simNum, int connectorFlag);
    }
}

