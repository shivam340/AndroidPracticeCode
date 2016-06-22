package com.example.codepractice;

import android.app.Application;
import android.widget.Toast;


public class CodingApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Toast.makeText(CodingApplication.this, "Application Terminated", Toast.LENGTH_SHORT).show();
    }
}
