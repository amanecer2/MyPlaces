package com.amanecer.myplaces.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by amanecer on 15/02/2015.
 */
public class MyCharging_service extends Service {

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
