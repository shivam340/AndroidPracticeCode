package com.example.codepractice.rest;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

public class RestClient {

    private String BASE_URL = "https://api.github.com/";


    public GitApi getApiService() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(GitApi.class);
    }



    public GitApiForRx getApiServiceForRx() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        //java.lang.IllegalArgumentException: Unable to create call adapter
        // for rx.Observable<java.util.List<com.example.codepractice.home.UserModel>>
        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        //RxJavaCallAdapterFactory is used if you are trying to return rx.Observable.
        //it simply wraps a network call as an Observable type for use with RxJava.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(rxAdapter)  // to create call adapter supported to rx
                .build();

        return retrofit.create(GitApiForRx.class);
    }

}
