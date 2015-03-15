package com.amanecer.myplaces.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.amanecer.myplaces.R;
import com.amanecer.myplaces.ui.MainActivity;
import com.amanecer.myplaces.ui.MyApllication;

/**
 * Created by amanecer on 05/03/2015.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    //public final String action = "com.amanecer.myplaces.recivers.NetworkChangeReceiver";
    OnNetworkChangeReceiver callback;
    @Override
    public void onReceive(final Context context, final Intent intent) {

        //callback = (OnNetworkChangeReceiver)context;

        MainActivity mainActivity= ((MyApllication)context.getApplicationContext()).mainActivity;


         ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

         android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

         android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

         final   NetworkInfo info = connMgr.getActiveNetworkInfo();
        // if there is no connection the Info object will return null -->no internet;
        if (info==null){
            Toast.makeText(context, context.getString(R.string.noInternet), Toast.LENGTH_LONG).show();
            //callback.onNoInternetConnection();
            mainActivity.onNoInternetConnection();
        }else {
            if (info.isConnected()){
                Toast.makeText(context, context.getString(R.string.yesInternet), Toast.LENGTH_LONG).show();
                //callback.onInternetConnection();
                mainActivity.onInternetConnection();
            }
        }


       /* if (wifi.isAvailable() || mobile.isAvailable()) {
            // Do something
            //callback.onInternetConnection();
           // mainActivity.onInternetConnection();
            Toast.makeText(context, info.isConnected()+""*//*context.getString(R.string.yesInternet)*//*, Toast.LENGTH_LONG).show();

            Log.d("Netowk Available ", "Flag No 1");


        }else if (!wifi.isAvailable() && !mobile.isAvailable()){
            Toast.makeText(context, context.getString(R.string.noInternet), Toast.LENGTH_LONG).show();
            //callback.onNoInternetConnection();
            //mainActivity.onNoInternetConnection();
            Log.d("Netowk not Available ", "Flag No 2");
        }*/
    }

    public interface OnNetworkChangeReceiver{
        public void onNoInternetConnection();
        public void onInternetConnection();
    }
}
