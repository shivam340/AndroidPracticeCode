package com.example.codepractice;

import android.app.Application;
import android.widget.Toast;

import com.example.codepractice.di.AppModule;
import com.example.codepractice.di.DaggerNetworkComponent;
import com.example.codepractice.di.NetworkComponent;
import com.example.codepractice.di.NetworkModule;


public class CodingApplication extends Application {

    private String BASE_URL = "https://api.github.com/";
    private NetworkComponent networkComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        networkComponent = DaggerNetworkComponent.builder()
                .networkModule(new NetworkModule(BASE_URL))
                .appModule(new AppModule(this))
                .build();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Toast.makeText(CodingApplication.this, "Application Terminated", Toast.LENGTH_SHORT).show();
    }

    public NetworkComponent getNetworkComponent() {
        return networkComponent;
    }
}
