package com.example.user.findplacesnearfinal.SugarDataBase;

import com.orm.SugarRecord;

/**
 * Created by user on 16/03/2018.
 */

public class FavorietsDB extends SugarRecord {

    private double lat;
    private double lng;
    private String icon;
    private String name;
    private String isOpen;
    private String photo_reference;
    private double rating;
    private String address;

    public FavorietsDB() {
    }

    public FavorietsDB(double lat, double lng, String icon, String name, String isOpen, String photo_reference, double rating, String address) {
        this.lat = lat;
        this.lng = lng;
        this.icon = icon;
        this.name = name;
        this.isOpen = isOpen;
        this.photo_reference = photo_reference;
        this.rating = rating;
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getOpening_hours() {
        return isOpen;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public double getRating() {
        return rating;
    }

    public String getAddress() {
        return address;
    }
}
