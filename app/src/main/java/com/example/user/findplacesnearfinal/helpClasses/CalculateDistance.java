package com.example.user.findplacesnearfinal.helpClasses;

import android.location.Location;
import android.location.LocationManager;
import com.google.android.gms.maps.model.LatLng;

public class CalculateDistance {

    public CalculateDistance() {
    }

    //calculate the distance in KM between 2 points
    public double getDistance(LatLng startP, LatLng endP){

            double distance = 0;
            Location locationA = new Location(LocationManager.GPS_PROVIDER);
            locationA.setLatitude(startP.latitude);
            locationA.setLongitude(startP.longitude);
            Location locationB = new Location(LocationManager.GPS_PROVIDER);
            locationB.setLatitude(endP.latitude);
            locationB.setLongitude(endP.longitude);
            distance = locationA.distanceTo(locationB);

            return distance / 1000;
    }
}
