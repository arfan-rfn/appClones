package com.exception.uber;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;
    static Location userCurrLocation;

    // check it

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /********* check if we have the permission or not ***********/

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                userCurrLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }

    }

    public void getStarted(View view){
        Switch mode = findViewById(R.id.mode);
        String dr;
        if (mode.isChecked()){
            getLocation();
            dr = "driver";
            Intent intent = new Intent(getApplicationContext(), RequestedListActivity.class);
            intent.putExtra("lat", userCurrLocation.getLatitude());
            intent.putExtra("lng", userCurrLocation.getLongitude());
            startActivity(intent);
        }else {
            dr = "rider";
            Intent intent = new Intent(getApplicationContext(), RiderActivity.class);
//            intent.putExtra("driverOrRider", dr);
            startActivity(intent);
        }
        ParseUser currUser = ParseUser.getCurrentUser();
        currUser.put("driverOrRider", dr);
        currUser.saveInBackground();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        if (ParseUser.getCurrentUser().getString("driverOrRider") != null){
            Log.i("user", "already Log In");
        }else {
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null){
                        Log.i("new user LogIn", "successful");
                    }else {
                        Log.i("user LogIn", "Failed, error: "+ e.toString());
                    }
                }
            });
        }

    }

    public void getLocation(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                userCurrLocation = location;
                ParseUser currUser = ParseUser.getCurrentUser();
                currUser.put("userLocation", new ParseGeoPoint(userCurrLocation.getLatitude(), userCurrLocation.getLongitude()));
                currUser.saveInBackground();

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
                userCurrLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }else {

            // if we already have the permission, we will get te location.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            /***** get the old know location if there have any ******/
            userCurrLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }
    }
}
