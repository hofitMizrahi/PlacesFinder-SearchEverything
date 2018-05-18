package com.example.user.findplacesnearfinal.Model;

/**
 * Created by user on 20/02/2018.
 */

public class Location {

    private double lat;
    private double lng;

    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
