package com.hilllander.khunzohn.gpstracker;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MarketActivity extends AppCompatActivity {
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
            View view = inflater.inflate(R.layout.fragment_market, container, false);
            Bundle args = getArguments();
            int position = args.getInt(KEY_POSITION);
            TextView test = (TextView) view.findViewById(R.id.test);
            test.setText(String.valueOf(position));
            switch (position) {
                case 0:
                    test.setText(R.string.instruction_one);
                    break;
                case 1:
                    test.setText(R.string.instruction_two);
                    break;
                case 2:
                    test.setText(R.string.instruction_three);
                    break;
                case 3:
                    test.setText(R.string.instruction_four);
                    break;
            }
            return view;
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
