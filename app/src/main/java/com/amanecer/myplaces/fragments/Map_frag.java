package com.amanecer.myplaces.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.amanecer.myplaces.R;
import com.amanecer.myplaces.database.Api_constant;
import com.amanecer.myplaces.database.Constant;
import com.amanecer.myplaces.database.DBHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by amanecer on 26/01/2015.
 */
public class Map_frag extends SupportMapFragment implements OnMapReadyCallback {
    //this is the seround places;
    private GoogleMap googleMap;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ArrayList<LatLng> markerPoints;

    double locationLat;
    double locationLng;
    double meLat;
    double meLng;
    // this is me only;
    //private GoogleMap meGoogleMap;

    private Marker marker;
    DBHandler handler;
    private boolean isSmall = false;
    private String name = null;
    DBHandler dbHandler;

    public Map_frag() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        handler = new DBHandler(activity);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(Constant.sharedPrefernces, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean(Constant.firstSearchInMap,true);

        String wicthFragmentIneedToBe = sharedPreferences.getString(Constant.wicthFragmentINeedToBe,Constant.mapFragment);
        name = sharedPreferences.getString(Constant.name, "");

        if (!wicthFragmentIneedToBe.equals(Constant.searchFragment))
            editor.putString(Constant.wicthFragmentAmI, Constant.mapFragment);
        editor.commit();
        String wicthFragmentAmI = sharedPreferences.getString(Constant.wicthFragmentAmI, "");
        getMapAsync(this);


    }


    @Override
    public void onDetach() {
        super.onDetach();

    }



    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onStop() {
        super.onStop();
        //when leaving this fragment say your the anoither one

    }

    public void searchByTextAndAddMarkers() {

        Cursor cursor = null;
        cursor = handler.getAllByCursorFilterTableName(Constant.places);
        int columId = cursor.getColumnIndex(Api_constant.place_id);
        int columLat = cursor.getColumnIndex(Api_constant.lat);
        int columLng = cursor.getColumnIndex(Api_constant.lng);
        ;
        int columIcon = cursor.getColumnIndex(Api_constant.iconPATH);
        ;
        int columName = cursor.getColumnIndex(Api_constant.name);
        ;
        int columAdress = cursor.getColumnIndex(Api_constant.formatted_address);
        ;
        //int columLng    =   4;


        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            double lat = cursor.getDouble(2);
            double lng = cursor.getDouble(3);
            String title = cursor.getString(5);
            String adress = cursor.getString(8);

            addMarker(lat, lng, title);
        }
    }

    public void setMyPlacesAndBeOnCenter(double lat, double lng) {

        LatLng position = new LatLng(lat, lng);
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14));

        googleMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(getString(R.string.IamHere))
        );
    }

    public void clearAllMarkers() {
        googleMap.clear();
    }

    public void addMarker(double lat, double lng, String title) {
        LatLng position = new LatLng(lat, lng);
        //  googleMap.setMyLocationEnabled(true);
        // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 14));

        googleMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(title)
        );
        /* googleMap.addMarker(new MarkerOptions()
            .position(position)
                .title(title)
              );*/

    }



    @Override
    public void onMapReady(final GoogleMap googleMap) {
        //this.meGoogleMap = googleMap;
        this.googleMap = googleMap;
        //  Toast.makeText(getActivity(), "onmapReady", Toast.LENGTH_LONG).show();
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        isSmall = sharedPreferences.getBoolean(Constant.isSamll,false);

        // Initializing
        markerPoints = new ArrayList<LatLng>();

        if (isSmall){
            if (name != null) {
                meAndThePlace(name);
            }



        }
    }

    public void drawDirectionOnMap(LatLng point){

        clearAllMarkers();
        if(googleMap!=null){

            // Already two locations
            if(markerPoints.size()>1){
                markerPoints.clear();
                googleMap.clear();
            }

            // Adding new item to the ArrayList
            markerPoints.add(point);

            // Creating MarkerOptions
            MarkerOptions options = new MarkerOptions();

            // Setting the position of the marker
            options.position(point);

            /**
             * For the start location, the color of marker is GREEN and
             * for the end location, the color of marker is RED.
             */
            if(markerPoints.size()==1){
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }else if(markerPoints.size()==2){
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }

            // Add new marker to the Google Map Android API V2
            googleMap.addMarker(options);

            // Checks, whether start and end locations are captured
            if(markerPoints.size() >= 2){
                LatLng origin = markerPoints.get(0);
                LatLng dest = markerPoints.get(1);

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
        }
    }




    public void meAndThePlace(String name) {

        try {
            clearAllMarkers();
            int positon = handler.getSpecificDetailsFilterNameTablePlacesByCursorPosition(name);
            Cursor place = handler.getAllByCursorFilterTableName(Constant.places);
            place.moveToPosition(positon);
            int locationLatColum = place.getColumnIndex(Api_constant.lat);
            int locationLngColum = place.getColumnIndex(Api_constant.lng);
            int locationNameColum = place.getColumnIndex(Api_constant.name);

            String locationName = place.getString(locationNameColum);
            locationLat = place.getDouble(locationLatColum);
            locationLng = place.getDouble(locationLngColum);

           /* Cursor latLngsCursor = handler.getLastLngLngByCursor();
            latLngsCursor.moveToFirst();
            int latColum = latLngsCursor.getColumnIndex(Constant.lat);
            int lngColum = latLngsCursor.getColumnIndex(Constant.lng);

            double meLat = latLngsCursor.getDouble(latColum);
            double meLng = latLngsCursor.getDouble(lngColum);*/
            String lngString = sharedPreferences.getString(Constant.lngString, null);
            String latString = sharedPreferences.getString(Constant.latString, null);

            if (latString != null && lngString != null) {
                meLat = Double.parseDouble(latString);
                meLng = Double.parseDouble(lngString);


                setMyPlacesAndBeOnCenter(meLat, meLng);

                // Getting Map for the SupportMapFragment
                drawDirectionOnMap(new LatLng(locationLat,locationLng));
                drawDirectionOnMap(new LatLng(meLat,meLng));

            }
            addMarker(locationLat, locationLng, locationName);
        } catch (Exception e) {

        }
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+Api_constant.key;

        return url;
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            googleMap.addPolyline(lineOptions);
        }


    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
}




