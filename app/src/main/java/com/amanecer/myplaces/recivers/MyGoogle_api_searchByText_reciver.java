package com.amanecer.myplaces.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.amanecer.myplaces.database.DBHandler;
import com.amanecer.myplaces.ui.MainActivity;
import com.amanecer.myplaces.ui.MyApllication;

/**
 * Created by amanecer on 27/01/2015.
 */
public class MyGoogle_api_searchByText_reciver extends BroadcastReceiver {

    public static final String action = "com.amanecer.myplaces.recivers.MyGoogle_api_searchByText_reciver";
    private DBHandler handler;
    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity mainActivity= ((MyApllication)context.getApplicationContext()).mainActivity;
         mainActivity.onSearchByText_mapMarkers();


    }


    public interface OnMyGoogle_api_searchByText_reciver{
        public  void onSearchByText_mapMarkers();

    }
}
