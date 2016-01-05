package com.hilllander.khunzohn.gpstracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hilllander.khunzohn.gpstracker.adapter.MarketingPagerAdapter;
import com.hilllander.khunzohn.gpstracker.util.Logger;
import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

import java.util.Timer;
import java.util.TimerTask;

public class MarketingActivity extends AppCompatActivity {

    public static final int NUM_PAGES = 4;
    public static final String KEY_FIRST_APP_LAUNCH = "key using for first app Launch";
    private static final String TAG = Logger.generateTag(MarketingActivity.class);
    View selectedIndicator;
    private ViewPager pager;
    private int currentItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        ViewUtils.setStatusBarTint(this, R.color.colorPrimaryDark);
        pager = (ViewPager) findViewById(R.id.pager);
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
                    // un select previous selected indicator and un highlight it by changing color to white
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
                //make current indicator selected and highlight it by changing color
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

                if (currentItem > NUM_PAGES - 1) {
                    gotoConnectActivity();
                }
                pager.setCurrentItem(currentItem, true);
            }
        };
        final Timer timer = new Timer("swiper", false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                currentItem++;
                if (currentItem > NUM_PAGES - 1) {
                    timer.cancel();
                }
                swiper.post(swipe);
            }
        }, 4000, 4000); // swipe pager every 4 sec

    }

    private void gotoConnectActivity() {
        Intent intent = new Intent(MarketingActivity.this, ConnectActivity.class);
        intent.putExtra(KEY_FIRST_APP_LAUNCH, true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        //disable back press
    }
}
