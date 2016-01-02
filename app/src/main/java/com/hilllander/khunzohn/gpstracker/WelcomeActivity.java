
package com.hilllander.khunzohn.gpstracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

public class WelcomeActivity extends AppCompatActivity {
    private static final long TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ViewUtils.setStatusBarTint(this, R.color.colorPrimaryDark);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent market = new Intent(WelcomeActivity.this, MarketingActivity.class);
                startActivity(market);
                finish();
            }
        }, TIME);
    }

    @Override
    public void onBackPressed() {
    }
}
