package com.exception.uber;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class RiderActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    ParseGeoPoint geoPoint;
    boolean requestProcessing;

    boolean driverFound;
    String driverUsername = "";
    ParseGeoPoint driverLocation;


    public void requestUber(View view){
        Button requestButton = (Button) view;
        ProgressBar progressBar = findViewById(R.id.progressBar);
        final ParseObject object = new ParseObject("Request");
        if (requestProcessing) {
            requestButton.setText("🤙Call an Uber");
            requestProcessing = false;
            progressBar.setVisibility(View.INVISIBLE);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if ( e == null){
                        for (ParseObject object1: objects){
                            object1.deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(ParseException e1) {
                                    if (e1 == null){
                                        Toast.makeText(RiderActivity.this, "Request Cancel successfully", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(RiderActivity.this, "somethings wrong, try again later", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }else {
                        Toast.makeText(RiderActivity.this, "somethings wrong, try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            requestButton.setText("Cancel✄Uber︎");
            requestProcessing = true;
            progressBar.setVisibility(View.VISIBLE);
            object.put("username", ParseUser.getCurrentUser().getUsername());
            object.put("location", geoPoint);
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null){
                        Toast.makeText(RiderActivity.this, "request processing", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (!driverFound && driverUsername.equals("")) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
                        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                        query.whereExists("driverUsername");
                        query.setLimit(1);
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null){
                                    if (objects.size() > 0){
                                        driverFound = true;
                                        driverUsername = objects.get(0).getString("driverUsername");
                                        Toast.makeText(getApplicationContext(), "Driver found" + driverUsername, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }else if (driverFound && !driverUsername.equals("")){
                        Log.i("check", "i got here");
                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereEqualTo("username", driverUsername);
                        query.setLimit(1);
                        query.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> objects, ParseException e) {
                                if (e == null){
                                    Log.i("check objects", objects.size()+"");
                                    if (objects.size()>0){
                                        driverLocation = objects.get(0).getParseGeoPoint("location");
                                        Log.i("driverLocation", driverLocation.toString());
                                        Toast.makeText(getApplicationContext(), "Driver location" + driverLocation.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                    handler.postDelayed(this, 3000);
                }


            };
            handler.post(runnable);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /********* check if we have the permission or not ***********/

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);
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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // if we don't have the permission we will request the permission to the user
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                // if we already have the permission, we will get te location.
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                /***** get the old know location if there have any ******/
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateLocation(location);
            }
        }else {

            // if we already have the permission, we will get te location.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            /***** get the old know location if there have any ******/
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateLocation(location);
        }
    }

    public void updateLocation(Location location){
        mMap.clear();
        geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        LatLng currLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currLocation).title("your current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 17));
//        Toast.makeText(getApplicationContext(), String.format("%f, %f", location.getAltitude(), location.getLongitude()), Toast.LENGTH_SHORT).show();
    }
}
