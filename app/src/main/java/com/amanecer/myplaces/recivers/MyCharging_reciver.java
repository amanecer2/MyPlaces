package com.amanecer.myplaces.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * Created by amanecer on 15/02/2015.
 */
public class MyCharging_reciver extends BroadcastReceiver {

        public static final String action = "com.amanecer.myplaces.recivers.MyCharging_reciver";
    @Override
    public void onReceive(Context context, Intent intent) {

        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        int  level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
        int  scale= intent.getIntExtra(BatteryManager.EXTRA_SCALE,0);

        float batteryPct = level / (float)scale;
        String txt="";
        String power="";
        if(acCharge){
            power="Power by AC ";
        }else {
            power="Power by USB ";
        }
        if (isCharging){
            txt = "Charging. " + level + " %."+power;
        }

    }
}
