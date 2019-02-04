package com.exception.uber;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

/**
 * Created by rfn on 12/25/17.
 */

public class Apps extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local DataStore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("177945a40424b490c123b0cb8020ac7acee2f291")
                .clientKey("a2a7e47d8250e8eab7adcb048871e8dee22a46c5")
                .server("http://52.15.158.141:80/parse")
                .build()
        );


        ParseUser.enableAutomaticUser();

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
