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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class RequestedListActivity extends AppCompatActivity {

    // check it!

//    static ParseGeoPoint currLocation;
    ArrayList<Float> requestList = new ArrayList<>();
    static ArrayList<ParseGeoPoint> requestLocation = new ArrayList<>();
    ArrayAdapter<Float> arrayAdapter;
    ListView listView;
    static ParseGeoPoint driverLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_list);
        getSupportActionBar().setTitle("nearest request(in Kilometers)");

        listView = findViewById(R.id.requestList);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, requestList);
        listView.setAdapter(arrayAdapter);

//        Intent input = getIntent();
//        double lat = input.getDoubleExtra("lat", 0);
//        double lng = input.getDoubleExtra("lng", 0);

        driverLocation = new ParseGeoPoint(MainActivity.userCurrLocation.getLatitude(), MainActivity.userCurrLocation.getLongitude());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");
        query.whereNear("location", driverLocation);
        query.whereDoesNotExist("driverUsername");



        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null){
                    requestList.clear();
                    requestLocation.clear();
                    for (ParseObject object: objects) {
                        double d = driverLocation.distanceInKilometersTo((ParseGeoPoint) object.get("location"));
                        requestList.add((float)Math.round(d * 100)/100);
                        requestLocation.add(object.getParseGeoPoint("location"));
                    }
                    arrayAdapter.notifyDataSetChanged();

                }else {
                    Log.i("No Request", "failed! error: "+e.toString());
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DriverActivity.class);
                intent.putExtra("pos", position);
                startActivity(intent);
            }
        });


    }


    public void uploadDriverLocation(Location location){
        driverLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        ParseQuery<ParseUser> findUser = ParseQuery.getQuery("User");
        findUser.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        findUser.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null){
                    for (ParseUser user: objects){
                        user.put("driverLocation", driverLocation);
                        user.saveInBackground();
                    }
                }
            }
        });
    }

}
