package com.amanecer.myplaces.database;

/**
 * Created by amanecer on 12/01/2015.
 */
public class Api_constant {

    public static final String googleLatLngToAdressLatLng = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    public static final String googleDirectionOriging = "https://maps.googleapis.com/maps/api/directions/json?origin=";
    public static final String getGoogleDirectionDestanation = "&destination=";
   // https://maps.googleapis.com/maps/api/place/autocomplete/json?input=re&types=geocode&language=iw&key=&key=AIzaSyDjgWw1Nma3dDMw49m5upMKaEN3WvPrdUM
    public static final String key = "*************"; // use your own key;
    public static final String googleSearchByTexh = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";
    public static final String googleSearchNearMyPlace = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    public static final String google = "https://maps.googleapis.com/maps/api/place/";
    public static final String nearBy ="nearbysearch/json?";
    public static final String locationEqual = "location=";
    public static final String radius = "&radius=";
    public static final String langua = "&language=";
    public static final String typeEqual = "&types=";
    public static final String languaHeb = "iw";
    public static final String results ="results";
    public static final String geometry ="geometry";
    public static final String location = "location";
    public static final String lat ="lat";
    public static final String lng ="lng";
    public static final String icon ="icon";
    public static final String name = "name";
    public static final String opening_hours = "opening_hours";
    public static final String open_now="open_now";
    public static final String types ="types";
    public static final String status="status";
    public static final String textsearch = "textsearch/json?";
    public static final String query = "query=";

    public static final String ZERO_RESULTS ="ZERO_RESULTS";
    public static final String iconPATH = "iconPATH";
    public static final String formatted_address = "formatted_address";
    public static final String place_id = "place_id";
    public static final String vicinity ="vicinity";

    //------- goole matrix api---------------
    public static final String googleMatrixAndOrigin = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
    public static final String googleMatrixDestantion="&destinations=";
    public static final String rows ="rows";
    public static final String elements = "elements";
    public static final String distance = "distance";
    public static final String value = "value";
    public static final String next_page_token = "next_page_token";
    public static final String next_page_tokenEqual = "";
    public static final String pagetoken = "&pagetoken=";
}
