package com.example.user.findplacesnearfinal.Service;

import com.example.user.findplacesnearfinal.Model.allResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by user on 20/02/2018.
 */

public interface GitHubService {

    // text searching
    @GET("/maps/api/place/textsearch/json")
    Call<allResults> getTextSearchResults (@Query("query") String query, @Query("key") String key);

    // use user location to search places near
    @GET("/maps/api/place/nearbysearch/json")
    Call<allResults> getNearbyResults(@Query("location") String location, @Query("radius") String radius, @Query("keyword") String keyword,
                                      @Query("key") String key);


/*
    for photo url -
    https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=INSERT_PHOTOREFERENCE&key=AIzaSyDTLvyt5Cry0n5eJDXWJNTluMHRuDYYc5s
*/
}
