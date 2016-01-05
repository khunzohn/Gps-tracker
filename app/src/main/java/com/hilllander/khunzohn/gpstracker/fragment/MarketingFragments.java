package com.hilllander.khunzohn.gpstracker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hilllander.khunzohn.gpstracker.R;

import mm.technomation.mmtext.MMTextView;

/**
 *Created by khunzohn on 12/31/15.
 */
public class MarketingFragments extends Fragment {
    public static final int TEXT = 10;
    public static final int PHONE = 11;
    private static final String KEY_POSITION = "key positoin";
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
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        int position = args.getInt(KEY_POSITION);
        View view = inflater.inflate(R.layout.fragment_market, container, false);
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
                case 3:
                    tvIstruction.setMyanmarText(getActivity().getString(R.string.instruction_four));
            }
        return view;
    }
}

