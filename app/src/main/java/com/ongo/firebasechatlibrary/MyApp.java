package com.ongo.firebasechatlibrary;

import android.app.Application;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;

/**
 * Created by gufran khan on 24-10-2018.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(getApplicationContext());
        FirebaseApp.initializeApp(getApplicationContext());
    }
}
