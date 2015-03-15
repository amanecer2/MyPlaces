package com.amanecer.myplaces.obj;

/**
 * Created by amanecer on 10/02/2015.
 */
public class Row_obj {

    private String placeName;
    private String placeAdress;
    private String placeDistance;
    private String PATH;
    private double lat;
    private double lng;
    private double distanceInMeters;


    public Row_obj(String placeName, String placeAdress, String placeDistance, String PATH, double lat, double lng, double distanceInMeters) {
        this.placeName = placeName;
        this.placeAdress = placeAdress;
        this.placeDistance = placeDistance;
        this.PATH = PATH;
        this.lat = lat;
        this.lng = lng;
        this.distanceInMeters = distanceInMeters;
    }

    public void setDistanceInMeters(double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    public double getDistanceInMeters() {
        return distanceInMeters;
    }

    public Row_obj(String placeName, String placeAdress, double lat, double lng) {
        this.placeName = placeName;
        this.placeAdress = placeAdress;
        this.lat = lat;
        this.lng = lng;
    }

    public Row_obj(String placeName, String placeAdress, String placeDistance, String PATH, double lat, double lng) {
        this.placeName = placeName;
        this.placeAdress = placeAdress;
        this.placeDistance = placeDistance;
        this.PATH = PATH;
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

    public Row_obj() {
    }

    public Row_obj(String placeName, String placeAdress, String placeDistance, String PATH) {
        this.placeName = placeName;
        this.placeAdress = placeAdress;
        this.placeDistance = placeDistance;
        this.PATH = PATH;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceAdress() {
        return placeAdress;
    }

    public void setPlaceAdress(String placeAdress) {
        this.placeAdress = placeAdress;
    }

    public String getPlaceDistance() {
        return placeDistance;
    }

    public void setPlaceDistance(String placeDistance) {
        this.placeDistance = placeDistance;
    }

    public String getPATH() {
        return PATH;
    }

    public void setPATH(String PATH) {
        this.PATH = PATH;
    }
}
