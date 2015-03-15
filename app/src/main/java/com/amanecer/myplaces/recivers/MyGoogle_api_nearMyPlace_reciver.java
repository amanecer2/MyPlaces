package com.amanecer.myplaces.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.amanecer.myplaces.database.Api_constant;
import com.amanecer.myplaces.ui.MainActivity;
import com.amanecer.myplaces.ui.MyApllication;

/**
 * Created by amanecer on 27/01/2015.
 */
public class MyGoogle_api_nearMyPlace_reciver extends BroadcastReceiver {

    public static final String action = "com.amanecer.myplaces.recivers.MyGoogle_api_nearMyPlace_reciver";

    @Override
    public void onReceive(Context context, Intent intent) {

        MainActivity mainActivity = ((MyApllication)context.getApplicationContext()).mainActivity;
        boolean ZERO_RESULT = intent.getBooleanExtra(Api_constant.ZERO_RESULTS,false);

        if (ZERO_RESULT){
            mainActivity.onZeroResultAlertDialog();
        }else {
            mainActivity.onReciverMyNearPlacePostOnMap();
        }

    }

    public interface OnMyGoogle_api_nearMyPlace_reciver {
        public void onReciverMyNearPlacePostOnMap();
        public void onZeroResultAlertDialog();
    }

}
