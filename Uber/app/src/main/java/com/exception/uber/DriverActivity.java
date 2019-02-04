package com.exception.uber;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DriverActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int userLocationPos;
    LatLng driverLocation;
    LatLng userLocation;
    double driverLat, driverLng, userLat, userLng;

    public void acceptRequest(View view){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.whereEqualTo("location", RequestedListActivity.requestLocation.get(userLocationPos));
        query.setLimit(1);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    for (ParseObject object: objects){
                        object.put("driverUsername", ParseUser.getCurrentUser().getUsername());
                        object.put("driverLocation", new ParseGeoPoint(MainActivity.userCurrLocation.getLatitude(), MainActivity.userCurrLocation.getLongitude()));
                        object.saveInBackground();
                    }
                }else {
                    Toast.makeText(DriverActivity.this, "Something went wrong, try again later", Toast.LENGTH_SHORT).show();
                }
            }
        });
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", driverLat, driverLng, "your Location", userLat, userLng, "User right now");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        userLocationPos = intent.getIntExtra("pos", -1);

        driverLat = MainActivity.userCurrLocation.getLatitude();
        driverLng = MainActivity.userCurrLocation.getLongitude();
        userLat = RequestedListActivity.requestLocation.get(userLocationPos).getLatitude();
        userLng = RequestedListActivity.requestLocation.get(userLocationPos).getLongitude();

        driverLocation = new LatLng(driverLat, driverLng);
        userLocation = new LatLng(userLat, userLng);

        RelativeLayout mapLayout = (RelativeLayout)findViewById(R.id.mapLayout);
        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                ArrayList<Marker> markers = new ArrayList<>();
                markers.clear();
                markers.add(mMap.addMarker(new MarkerOptions().position(driverLocation).title("Your Location")));
                markers.add(mMap.addMarker(new MarkerOptions()
                        .position(userLocation)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                        .title("User Location")));


                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markers) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();

                int padding = 150; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                mMap.moveCamera(cu);

                mMap.animateCamera(cu);
            }
        });

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

    }
}
