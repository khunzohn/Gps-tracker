package com.hilllander.khunzohn.gpstracker;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hilllander.khunzohn.gpstracker.fragment.ConnectionWarningFragment;

import java.util.Timer;
import java.util.TimerTask;

import mm.technomation.mmtext.MMTextView;

public class MarketActivity extends AppCompatActivity {
    public static final int TEXT = 10;
    public static final int PHONE = 11;
    private static final int NUM_PAGES = 4;
    View selectedIndicator;
    private ViewPager pager;
    private int currentItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        pager = (ViewPager) findViewById(R.id.pager);
        final View iZero = findViewById(R.id.indicatorZero);
        final View iOne = findViewById(R.id.indicatorOne);
        final View iTwo = findViewById(R.id.indicatorTwo);
        final View iThree = findViewById(R.id.indicatorThree);
        selectedIndicator = iZero;
        selectedIndicator.setSelected(true);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(currentItem);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                if (null != selectedIndicator) {
                    selectedIndicator.setSelected(false);
                }
                switch (position) {
                    case 0:
                        selectedIndicator = iZero;
                        break;
                    case 1:
                        selectedIndicator = iOne;
                        break;
                    case 2:
                        selectedIndicator = iTwo;
                        break;
                    case 3:
                        selectedIndicator = iThree;
                        break;
                    default:
                        selectedIndicator = iZero;
                }

                selectedIndicator.setSelected(true);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        final Handler updater = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (currentItem == NUM_PAGES - 1) {
                    currentItem--;
                }
                pager.setCurrentItem(currentItem + 1, true);
            }
        };
        Timer timer = new Timer("swiper", false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updater.post(update);
            }
        }, 4000, 4000);

    }

    @Override
    public void onBackPressed() {

    }

    public static class MarketFragments extends Fragment {
        private static final String KEY_POSITION = "key positoin";
        private MMTextView tvTextConnect;
        private MMTextView tvPhoneConnect;
        private FrameLayout warningContent;
        private int connectorFlag;

        public MarketFragments() {

        }

        public static MarketFragments newInstance(int position) {
            MarketFragments fragment = new MarketFragments();
            Bundle args = new Bundle();
            args.putInt(KEY_POSITION, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            int position = args.getInt(KEY_POSITION);
            View view;
            if (position == NUM_PAGES - 1) { // last fragment
                view = inflater.inflate(R.layout.fragment_market_login, container, false);
                tvTextConnect = (MMTextView) view.findViewById(R.id.tvTextConnect);
                tvPhoneConnect = (MMTextView) view.findViewById(R.id.tvPhoneConnect);
                warningContent = (FrameLayout) view.findViewById(R.id.warningContent);

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
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MarketFragments.newInstance(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
