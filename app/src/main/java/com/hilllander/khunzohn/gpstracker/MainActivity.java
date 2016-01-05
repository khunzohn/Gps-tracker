package com.hilllander.khunzohn.gpstracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.hilllander.khunzohn.gpstracker.adapter.MainRecyclerAdapter;
import com.hilllander.khunzohn.gpstracker.database.model.Device;
import com.hilllander.khunzohn.gpstracker.util.Logger;
import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MainRecyclerAdapter.OnDeviceOnClickListener {
    private static final String TAG = Logger.generateTag(MainActivity.class);
    private static final int REQUEST_CODE_CONNECT = 505;
    private List<Device> devices;
    private MainRecyclerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        ViewUtils.setStatusBarTint(this, R.color.colorPrimaryDark);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MainRecyclerAdapter(this);
        devices = new ArrayList<>();
        adapter.setOnClickListener(this);
        recyclerView.setAdapter(adapter);
        adapter.setDevices(devices);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent connect = new Intent(MainActivity.this, ConnectActivity.class);
                connect.putExtra(ConnectActivity.KEY_UP_ENABLE, true);
                startActivityForResult(connect, REQUEST_CODE_CONNECT);
            }
        });
        Bundle bundle = getIntent().getExtras();
        //activity started from ConnectActivity's connecting succeeded (triggered once at app's first lunch)
        if (null != bundle) {
            devices.add((Device) bundle.getParcelable(ConnectActivity.KEY_DEVICE_EXTRA));
            adapter.setDevices(devices);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQUEST_CODE_CONNECT == requestCode) {
            if (RESULT_OK == resultCode) {
                devices.add((Device) data.getParcelableExtra(ConnectActivity.KEY_DEVICE_EXTRA));
                adapter.setDevices(devices);
            }
        }
    }

    @Override
    public void onClickEdit(Device device) {
        makeToast("Edit : " + device.getDeviceName());
    }

    @Override
    public void onClickLock(Device device) {
        makeToast("lock : " + device.getDeviceName());
    }

    @Override
    public void onClickProfile(Device device) {
        makeToast("profile : " + device.getDeviceName());
    }

    @Override
    public void onClickGoToMap(Device device) {
        makeToast("go to map : " + device.getDeviceName());
    }

    private void makeToast(String message) {
        if (BuildConfig.DEBUG)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
