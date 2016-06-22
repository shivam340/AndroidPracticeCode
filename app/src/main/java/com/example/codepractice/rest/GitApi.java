package com.example.codepractice.rest;


import com.example.codepractice.home.UserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GitApi {

    //@Headers({"Cache-Control: max-age=640000", "User-Agent: My-App-Name"})
    @GET("users/{username}/repos")
    Call<List<UserModel>> getUserDetail(@Path("username") String userName);


    /*
    adding header value dynamically
    @Multipart
    @POST("/some/endpoint")
    Call<SomeResponse> someEndpoint(@Header("Cache-Control") int maxAge)
    */
}
