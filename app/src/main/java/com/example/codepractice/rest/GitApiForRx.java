package com.example.codepractice.rest;


import com.example.codepractice.home.UserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface GitApiForRx {

    //@Headers({"Cache-Control: max-age=640000", "User-Agent: My-App-Name"})
    @GET("users/{username}/repos")
    Observable<List<UserModel>> getUserDetailUsingRx(@Path("username") String userName);


    /*
    adding header value dynamically
    @Multipart
    @POST("/some/endpoint")
    Call<SomeResponse> someEndpoint(@Header("Cache-Control") int maxAge)
    */
}
