package com.exception.whatsapp;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by rfn on 12/31/17.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("999440cb43419107f991712b08e8fb8c5fb83a62")
                .clientKey("b75ac00068f5d635a0f6a81944195f90f951cbb7")
                .server("http://18.216.36.5:80/parse")
                .build()
        );
    }
}
