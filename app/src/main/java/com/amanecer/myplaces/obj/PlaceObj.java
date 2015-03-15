package com.amanecer.myplaces.obj;

/**
 * Created by amanecer on 12/01/2015.
 */
public class PlaceObj {

   private String placeName;
   private String adress;
   private double distance;
   private String img_PATH;
   private double place_lat;
   private double place_lng;

    public PlaceObj() {
    }

    public PlaceObj(String placeName, String adress, double distance, String img_PATH, double place_lat, double place_lng) {
        this.placeName = placeName;
        this.adress = adress;
        this.distance = distance;
        this.img_PATH = img_PATH;
        this.place_lat = place_lat;
        this.place_lng = place_lng;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getImg_PATH() {
        return img_PATH;
    }

    public void setImg_PATH(String img_PATH) {
        this.img_PATH = img_PATH;
    }

    public double getPlace_lat() {
        return place_lat;
    }

    public void setPlace_lat(double place_lat) {
        this.place_lat = place_lat;
    }

    public double getPlace_lng() {
        return place_lng;
    }

    public void setPlace_lng(double place_lng) {
        this.place_lng = place_lng;
    }
}
