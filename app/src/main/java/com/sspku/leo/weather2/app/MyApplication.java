package com.sspku.leo.weather2.app;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application{
    private static final String TAG = "MyAPP";
    private static MyApplication myApplication;
    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"MyApplictioan->onCreate");

        myApplication = this;
    }

    public static MyApplication getInstance(){
        return myApplication;
    }
}
