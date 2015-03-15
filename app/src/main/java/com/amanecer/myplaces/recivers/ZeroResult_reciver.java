package com.amanecer.myplaces.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by amanecer on 11/02/2015.
 */
public class ZeroResult_reciver extends BroadcastReceiver {

    public static final String action = "com.amanecer.myplaces.recivers.ZeroResult_reciver";

    @Override
    public void onReceive(Context context, Intent intent) {

       // MainActivity mainActivity = ((MyApllication)context.getApplicationContext()).mainActivity;
      //  mainActivity.onZeroResultAlertDialog();

    }
}
