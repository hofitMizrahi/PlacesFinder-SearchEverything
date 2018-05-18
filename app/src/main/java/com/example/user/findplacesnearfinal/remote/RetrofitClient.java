package com.example.user.findplacesnearfinal.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 This class will create a singleton of Retrofit
 */

public class RetrofitClient {

    // the API base url
    private static String BASE_URL = "https://maps.googleapis.com";

    private static Retrofit retrofit = null;

    //return retrofit Object after the build
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
