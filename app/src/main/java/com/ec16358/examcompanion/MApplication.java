package com.ec16358.examcompanion;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/*
* I DID NOT WRITE THIS CLASS
*
* SOURCE FOR THIS CLASS
* https://stackoverflow.com/questions/37448186/setpersistenceenabledtrue-crashes-app
*
* Purpose: Class stops app from crashing due to fireBase cache being enabled (for offline use) by
* enabling it separately in an Application class instead.
*
* */

public class MApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}