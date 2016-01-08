package com.hilllander.khunzohn.gpstracker;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hilllander.khunzohn.gpstracker.adapter.MainRecyclerAdapter;
import com.hilllander.khunzohn.gpstracker.database.dao.DeviceDao;
import com.hilllander.khunzohn.gpstracker.database.model.Device;
import com.hilllander.khunzohn.gpstracker.util.Logger;
import com.hilllander.khunzohn.gpstracker.util.ViewUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MainRecyclerAdapter.OnDeviceOnClickListener {
    public static final String KEY_DEVICE = "key for device";
    private static final String TAG = Logger.generateTag(MainActivity.class);
    private static final int REQUEST_CODE_CONNECT = 505;
    private static final int REQUEST_CODE_EDIT = 456;
    private static final int REQUEST_CODE_MAP = 222;
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
            devices.add((Device) bundle.getSerializable(ConnectActivity.KEY_DEVICE_EXTRA));
            adapter.setDevices(devices);
        }
        GetAllDevicesFromDb getAllDeviceSFromDb = new GetAllDevicesFromDb(this);
        getAllDeviceSFromDb.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            if (REQUEST_CODE_CONNECT == requestCode) {

                Device returnedDevice = (Device) data.getExtras().getSerializable(ConnectActivity.KEY_DEVICE_EXTRA);
                devices.add(returnedDevice);
                adapter.setDevices(devices);
            } else if (REQUEST_CODE_EDIT == requestCode) {
                Bundle bundle = data.getExtras();
                if (null != bundle) {
                    boolean infoEdited = bundle.getBoolean(EditProfileActivity.KEY_INFO_EDITED);
                    if (infoEdited) {
                        GetAllDevicesFromDb getAllDevicesFromDb = new GetAllDevicesFromDb(this);
                        getAllDevicesFromDb.execute();
                    }
                }
            } else if (REQUEST_CODE_MAP == requestCode) {

            }
        }
    }

    @Override
    public void onClickEdit(Device device) {
        Intent editProfile = new Intent(this, EditProfileActivity.class);
        editProfile.putExtra(KEY_DEVICE, device);
        startActivityForResult(editProfile, REQUEST_CODE_EDIT);

    }
    @Override
    public void onClickGoToMap(Device device) {
        Intent map = new Intent(MainActivity.this, MapsActivity.class);
        map.putExtra(KEY_DEVICE, device);
        startActivityForResult(map, REQUEST_CODE_MAP);
    }

    private class GetAllDevicesFromDb extends AsyncTask<Void, Void, List<Device>> {
        private DeviceDao dao;

        public GetAllDevicesFromDb(Context context) {
            dao = new DeviceDao(context);
        }

        @Override
        protected void onPreExecute() {
            try {
                dao.open();
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
        }

        @Override
        protected List<Device> doInBackground(Void... params) {
            List<Device> devices = new ArrayList<>();
            try {
                devices = dao.getAllDevices();
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            if (null != devices) {
                Logger.d(TAG, "devices size : " + devices.size());
            }
            return devices;
        }

        @Override
        protected void onPostExecute(List<Device> allDevices) {
            try {
                dao.close();
            } catch (SQLException e) {
                Logger.e(TAG, e.getLocalizedMessage());
            }
            if (null != allDevices) {
                devices = allDevices;
                adapter.setDevices(devices);
            }
        }
    }
}
