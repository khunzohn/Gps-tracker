package com.hilllander.khunzohn.gpstracker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hilllander.khunzohn.gpstracker.database.model.Device;
import com.hilllander.khunzohn.gpstracker.util.NetworkUtil;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Device device;
    private float lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        device = (Device) getIntent().getExtras().getSerializable(MainActivity.KEY_DEVICE);
        if (null != device) {
            lat = device.getLatitude();
            lon = device.getLongitude();
        }
        if (!NetworkUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in gps's position and move the camera
        LatLng gps = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(gps).title("Device is here!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(gps));
    }
}
