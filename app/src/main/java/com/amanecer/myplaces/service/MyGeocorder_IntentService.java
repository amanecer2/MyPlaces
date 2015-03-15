package com.amanecer.myplaces.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.LocalBroadcastManager;

import com.amanecer.myplaces.database.Constant;
import com.amanecer.myplaces.recivers.MyGeocorder_reciver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by amanecer on 26/01/2015.
 */
public class MyGeocorder_IntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * /*@param name Used to name the worker thread, important only for debugging.
     */
    public MyGeocorder_IntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        List<Address> resulat = new ArrayList<>();
        String location = intent.getStringExtra(Constant.location);
        if (location!=null){
            Geocoder geocoder = new Geocoder(this);
            try {
                resulat = geocoder.getFromLocationName(location,2);

                double lat = resulat.get(0).getLatitude();
                double lng = resulat.get(0).getLongitude();

                Intent intent1 = new Intent(MyGeocorder_reciver.action);
                intent1.putExtra(Constant.lat,lat);
                intent1.putExtra(Constant.lng,lng);

                LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
