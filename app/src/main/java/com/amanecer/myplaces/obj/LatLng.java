package com.amanecer.myplaces.obj;

/**
 * Created by amanecer on 25/01/2015.
 */
public class LatLng {
    double lat ;
    double lng ;

    public LatLng() {
    }

    public LatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
