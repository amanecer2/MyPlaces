package com.amanecer.myplaces.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.amanecer.myplaces.database.Constant;
import com.amanecer.myplaces.database.DBHandler;
import com.amanecer.myplaces.recivers.ZeroResult_reciver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;

/**
 * Created by amanecer on 26/01/2015.
 */
public class MyLocation_intentService extends IntentService implements GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMyLocationChangeListener, GoogleApiClient.ConnectionCallbacks {

    private DBHandler handler;

    private GoogleApiClient googleApiClient;
    private Location location;
    private double lat = 360, lng = 360;
    private Intent intent;
    public final static String action = "com.amanecer.myplaces.recivers.MyLocation_reciver";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * <p/>
     * /*@param name  Used to name the worker thread, important only for debugging.
     */

    public MyLocation_intentService() {
        super("");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String i = "";
        builtGoogleApiClient(this);
        googleApiClient.connect();
        sharedPreferences = getSharedPreferences(Constant.sharedPrefernces, MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }


    protected synchronized void builtGoogleApiClient(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        int err = connectionResult.getErrorCode();
        String ere = String.valueOf(err);
        Intent intent1 = new Intent(action);
        intent1.putExtra(Constant.locationNull, Constant.locationNull);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
    }

    @Override
    public void onMyLocationChange(Location location) {
        this.location = location;

    }

    @Override
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (location != null) {
            try{
                // if i got the location just save it!
                handler = new DBHandler(this);
                lat = location.getLatitude();
                lng = location.getLongitude();
                String latString = String.valueOf(lat);
                String lngString = String.valueOf(lng);
                editor.putString(Constant.latString, latString);
                editor.putString(Constant.lngString, lngString);
                editor.commit();
                handler.clearTableMyLocationNow();
                intent = new Intent(action);

                intent.putExtra(Constant.lat, lat);
                intent.putExtra(Constant.lng, lng);
                ContentValues v = new ContentValues();
                v.put(Constant.lat, lat);
                v.put(Constant.lng, lng);
                handler.insertNew(Constant.myLocationNow, v);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            }catch (Exception e){
                e.getCause();
                //if there is a problem with the location say somthing;
                Intent intent1 = new Intent(ZeroResult_reciver.action);
                intent1.putExtra(Constant.locationNull, Constant.locationNull);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
            }


        } else {


            Intent intent1 = new Intent(ZeroResult_reciver.action);
            intent1.putExtra(Constant.locationNull, Constant.locationNull);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Intent intent1 = new Intent(ZeroResult_reciver.action);
        intent1.putExtra(Constant.locationNull, Constant.locationNull);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent1);
    }
}
