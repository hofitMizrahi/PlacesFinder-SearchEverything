package com.example.user.findplacesnearfinal.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by user on 20/02/2018.
 */

public class allResults {

    @SerializedName("results")
    private List<Place> results;

    public allResults(List<Place> results) {
        this.results = results;
    }

    public List<Place> getResults() {
        return results;
    }

    public int size(){
        return results.size();
    }
}
