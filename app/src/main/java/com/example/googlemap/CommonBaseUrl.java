package com.example.googlemap;

public class CommonBaseUrl {

    public static final String baseUrl = "https://maps.googleapis.com";
    public static RetrofitApi getGoogleApi(){

        return RetrofitClient.getClient(baseUrl).create(RetrofitApi.class);
    }

}
