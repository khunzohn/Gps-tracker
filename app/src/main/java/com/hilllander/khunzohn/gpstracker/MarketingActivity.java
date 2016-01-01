package com.hilllander.khunzohn.gpstracker;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.hilllander.khunzohn.gpstracker.adapter.MarketingPagerAdapter;
import com.hilllander.khunzohn.gpstracker.fragment.MarketingFragments;
import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

import java.util.Timer;
import java.util.TimerTask;

public class MarketingActivity extends AppCompatActivity implements MarketingFragments.ConnectorListener {

    public static final int NUM_PAGES = 4;
    View selectedIndicator;
    private ViewPager pager;
    private int currentItem = 0;
    private ProgressBar progressBar;
    private View progressBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        pager = (ViewPager) findViewById(R.id.pager);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        showProgressBar(false);
        final View iZero = findViewById(R.id.indicatorZero);
        final View iOne = findViewById(R.id.indicatorOne);
        final View iTwo = findViewById(R.id.indicatorTwo);
        final View iThree = findViewById(R.id.indicatorThree);
        selectedIndicator = iZero;
        selectedIndicator.setSelected(true);
        pager.setAdapter(new MarketingPagerAdapter(getSupportFragmentManager()));
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
        final Handler swiper = new Handler();
        final Runnable swipe = new Runnable() {
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
                swiper.post(swipe);
            }
        }, 4000, 4000);

    }

    private void showProgressBar(boolean visible) {
        String message = getString(R.string.dialog_message_connecting) + "...";
        ViewUtils.showProgressBar(this, progressBarLayout, message, visible);
    }


    @Override
    public void onBackPressed() {
        //disable back press
    }

    @Override
    public void connect(String simNum, int connectorFlag) {
        switch (connectorFlag) {
            case MarketingFragments.TEXT:
//                USSD.smsBegin(simNum);
//                showProgressBar(true);

                break;
            case MarketingFragments.PHONE:
//                showProgressBar(false);

                break;
        }
        showProgressBar(true);
    }

    @Override
    public void onSucceeded() {
        showProgressBar(false);
    }
}
