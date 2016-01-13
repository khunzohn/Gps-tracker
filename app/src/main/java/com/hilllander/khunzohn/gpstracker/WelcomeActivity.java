
package com.hilllander.khunzohn.gpstracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

public class WelcomeActivity extends AppCompatActivity {
    private static final long TIME = 2000;
    private static final String KEY_FIRST_APP_LAUNCH = "key_for_first_app_launch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ViewUtils.setStatusBarTint(this, R.color.colorPrimaryDark);
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean isFirstAppLaunch = sp.getBoolean(KEY_FIRST_APP_LAUNCH, true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFirstAppLaunch) {
                    sp.edit().putBoolean(KEY_FIRST_APP_LAUNCH, false).apply();
                    Intent market = new Intent(WelcomeActivity.this, MarketingActivity.class);
                    startActivity(market);
                    finish();
                } else {
                    Intent main = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(main);
                    finish();
                }

            }
        }, TIME);
    }

    @Override
    public void onBackPressed() {
    }
}
