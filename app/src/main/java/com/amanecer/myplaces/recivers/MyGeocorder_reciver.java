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
public class MyGeocorder_reciver extends BroadcastReceiver {

    public static final String action = "com.amanecer.myplaces.recivers.MyGeocorder_reciver";

    @Override
    public void onReceive(Context context, Intent intent) {

        MainActivity mainActivity = ((MyApllication)context.getApplicationContext()).mainActivity;

        double lat = intent.getDoubleExtra(Constant.lat,360);
        double lng = intent.getDoubleExtra(Constant.lng,360);
        if (lat!=360 && lng !=360){
          //  mainActivity.onLocationReciveToCordinet(lat,lng);
        }

    }

    public interface onMyGeocorder_reciver{
     //   public void onLocationReciveToCordinet(double lat,double lng);
    }
}
