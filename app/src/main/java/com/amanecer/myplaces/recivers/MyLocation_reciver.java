package com.amanecer.myplaces.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.amanecer.myplaces.database.Constant;
import com.amanecer.myplaces.ui.MainActivity;
import com.amanecer.myplaces.ui.MyApllication;

/**
 * Created by amanecer on 26/01/2015.
 */
/// this reciver is no needed// just for ecexise
    // if theer is no location it wil popup the setting.
public class MyLocation_reciver extends BroadcastReceiver {
    private double lat=360,lng = 360;

    @Override
    public void onReceive(Context context, Intent intent) {

        MainActivity mainActivity = ((MyApllication)context.getApplicationContext()).mainActivity;

        String locationNull = intent.getStringExtra(Constant.locationNull);
        if (locationNull!=null){
            mainActivity.locationNullOpenLocationSettings();
        }else {
             lat = intent.getDoubleExtra(Constant.lat,360);
             lng = intent.getDoubleExtra(Constant.lng,360);
             //mainActivity.setMyLocationsInText(lat,lng);
             mainActivity.setmyLocationInMapIfBig(lat,lng);
        }
    }



    public interface OnMyLocation_reciver {
        public void setMyLocationsInText(double lat, double lng);
        public void locationNullOpenLocationSettings();
        public void setmyLocationInMapIfBig(double lat, double lng);
    }
}
