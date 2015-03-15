package com.amanecer.myplaces.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amanecer.myplaces.R;
import com.amanecer.myplaces.database.Api_constant;
import com.amanecer.myplaces.database.Constant;
import com.amanecer.myplaces.fragments.Map_frag;
import com.amanecer.myplaces.fragments.Navigation_fragment;
import com.amanecer.myplaces.fragments.Search_frag;
import com.amanecer.myplaces.recivers.MyCharging_reciver;
import com.amanecer.myplaces.recivers.MyGeocorder_reciver;
import com.amanecer.myplaces.recivers.MyGoogle_api_nearMyPlace_reciver;
import com.amanecer.myplaces.recivers.MyGoogle_api_searchByText_reciver;
import com.amanecer.myplaces.recivers.MyLocation_reciver;
import com.amanecer.myplaces.recivers.NetworkChangeReceiver;
import com.amanecer.myplaces.recivers.ZeroResult_reciver;
import com.amanecer.myplaces.service.MyCharging_service;
import com.amanecer.myplaces.service.MyGoole_api_searchByText_intentService;
import com.amanecer.myplaces.service.MyGoole_api_searchNearMyPlace_intentService;
import com.amanecer.myplaces.service.MyLocation_intentService;


public class MainActivity extends ActionBarActivity implements MyLocation_reciver.OnMyLocation_reciver, MyGoogle_api_nearMyPlace_reciver.OnMyGoogle_api_nearMyPlace_reciver,
        Search_frag.OnSearch_frag, MyGeocorder_reciver.onMyGeocorder_reciver, MyGoogle_api_searchByText_reciver.OnMyGoogle_api_searchByText_reciver, NetworkChangeReceiver.OnNetworkChangeReceiver {

    private boolean isSmall = false;
    private Search_frag search_frag;
    private Map_frag map_frag;

    private MyLocation_reciver myLocation_reciver;
    private boolean isMyLocationServiceOn = false;
    private MyGeocorder_reciver myGeocorder_reciver;
    private MyGoogle_api_searchByText_reciver myGoogle_api_searchByText_reciver;
    private MyGoogle_api_nearMyPlace_reciver myGoogle_api_nearMyPlace_reciver;
    private MyCharging_reciver myCharging_reciver;
    private MyCharging_service myCharging_service;
    private ZeroResult_reciver zeroResult_reciver;
    private Intent batteryStatus;
    private Intent charging;
    private String whicthFragmentIAm;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String name;
    private int counter = 0;
    private boolean madeFirstSearch;
    //handle the navigation drawer;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private Navigation_fragment navigation_fragment;
    private boolean firstTimeInMap;

    private NetworkChangeReceiver networkChangeReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_appbar);


        // gives the contexts to all activitys.. so i can pass from the reciver to main UI;
        MyApllication myAplication = (MyApllication) this.getApplicationContext();
        myAplication.mainActivity = this;

        //

        //------- get the location in the start or open massage to open setting if gps is off;

        onGetMyLocation_btn();

        //---------------------------------

        sharedPreferences = this.getSharedPreferences(Constant.sharedPrefernces, MODE_PRIVATE);
        editor = sharedPreferences.edit();


        isSmall = sharedPreferences.getBoolean(Constant.isSamll, true);
        whicthFragmentIAm = sharedPreferences.getString(Constant.wicthFragmentAmI, Constant.searchFragment);

        //first time
        if (savedInstanceState == null) {
            editor.putBoolean(Constant.firstSearchInMap, true);
            editor.putString(Constant.wictchSearchIAM, Constant.searchFragment);
            editor.apply();

            // chech for the first time if there is internet conection;
            // only at the start. after it, the system will do the check(becuase it's in the manifest);
            /*ConnectivityManager connMgr = (ConnectivityManager) this
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connMgr.getActiveNetworkInfo();

            String
            if (info == null)                       this is no needed. the reciver take control
                onNoInternetConnection();              of the intenet connectivity;
            */
            if (findViewById(R.id.smallDevice_frag) != null) {
                isSmall = true;
                editor.putString(Constant.wicthFragmentAmI, Constant.searchFragment);
                editor.putBoolean(Constant.isSamll, true);
                //editor.putBoolean(Constant.firstSearchInMap, false);
                editor.commit();


                Log.d("small device : ", "entered");
                FragmentManager fm = getSupportFragmentManager();
                search_frag = new Search_frag();
                fm.beginTransaction().replace(R.id.smallDevice_frag, search_frag, Constant.searchFrag_tag).addToBackStack(Constant.searchFrag_tag).commit();

            } else {
                isSmall = false;
                editor.putBoolean(Constant.isSamll, false);

                editor.apply();

                Log.d("big device : ", "entered");
                map_frag = new Map_frag();
                search_frag = new Search_frag();
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.theMap, map_frag).commit();
                fm.beginTransaction().replace(R.id.search_frag_inBigDEvice, search_frag).commit();
            }
        } else {
            // on changing oriention;
            isSmall = sharedPreferences.getBoolean(Constant.isSamll, true);
            FragmentManager fm = getSupportFragmentManager();


            if (isSmall) {
                if (whicthFragmentIAm.equals(Constant.searchFragment)) {
                    search_frag = new Search_frag();
                    fm.beginTransaction().replace(R.id.smallDevice_frag, search_frag, Constant.searchFrag_tag).commit();
                } else if (whicthFragmentIAm.equals(Constant.mapFragment)) {
                    map_frag = new Map_frag();
                    fm.beginTransaction().replace(R.id.smallDevice_frag, map_frag, Constant.theMapTAG).commit();

                } else {
                    map_frag = new Map_frag();
                    search_frag = new Search_frag();

                    fm.beginTransaction().replace(R.id.theMap, map_frag).commit();
                    fm.beginTransaction().replace(R.id.search_frag_inBigDEvice, search_frag).commit();

                }
            }
        }
        IntentFilter intentFilterByText = new IntentFilter(MyGoogle_api_searchByText_reciver.action);
        myGoogle_api_searchByText_reciver = new MyGoogle_api_searchByText_reciver();
        LocalBroadcastManager.getInstance(this).registerReceiver(myGoogle_api_searchByText_reciver, intentFilterByText);

        myLocation_reciver = new MyLocation_reciver();
        IntentFilter intentFilterLocation = new IntentFilter(MyLocation_intentService.action);
        LocalBroadcastManager.getInstance(this).registerReceiver(myLocation_reciver, intentFilterLocation);

        myGoogle_api_nearMyPlace_reciver = new MyGoogle_api_nearMyPlace_reciver();
        IntentFilter intentFilterNear = new IntentFilter(MyGoogle_api_nearMyPlace_reciver.action);
        LocalBroadcastManager.getInstance(this).registerReceiver(myGoogle_api_nearMyPlace_reciver, intentFilterNear);

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        myCharging_reciver = new MyCharging_reciver();
        Intent batteryStatus = registerReceiver(myCharging_reciver, ifilter);


        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter  networkChangeReceiverFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver,networkChangeReceiverFilter);

        // navigation drawer handling;
        toolbar = (Toolbar) findViewById(R.id.layout_toolBar);// connect the tol bar that we made and included in the main xml;
        setSupportActionBar(toolbar);

        navigation_fragment = (Navigation_fragment) getSupportFragmentManager().findFragmentById(R.id.navigation_frag);

        navigation_fragment.setUp((DrawerLayout) findViewById(R.id.drawerLayout), toolbar, R.id.drawerLayout);// set the parameters for the drawerbar.. and act kike oncreate;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //to open the drawerBar*//*


    }


    @Override
    public void onBackPressed() {

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (isSmall) {
            String wicth = sharedPreferences.getString(Constant.wicthFragmentAmI, null);

            if (wicth.equals(Constant.searchFragment)) {
                counter++;
                if (counter == 2)
                    this.finish();
                else
                Toast.makeText(this, getString(R.string.pressBackAgainForShutDwon), Toast.LENGTH_SHORT).show();


                } else {
                    counter =0;
                    editor.putString(Constant.wicthFragmentAmI, Constant.searchFragment);
                    editor.putString(Constant.wicthFragmentINeedToBe, Constant.searchFragment);
                    editor.apply();
                    search_frag = new Search_frag();
                    fragmentManager.beginTransaction().replace(R.id.smallDevice_frag,search_frag).commit();
                }
            } else
                super.onBackPressed();

        }

        @Override
        protected void onSaveInstanceState (Bundle outState){
            super.onSaveInstanceState(outState);
            outState.putBoolean(Constant.isSamll, isSmall);
        }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            // Inflate the menu; this adds items to the action bar if it is present.
            super.onCreateOptionsMenu(menu);
            menu.clear();
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

        /*//noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this,"setting",Toast.LENGTH_LONG).show();
            return true;
        }
        if(id==R.id.show_favorites){
            FragmentManager fm = getSupportFragmentManager();
            Favorite_frag favorite_frag = new Favorite_frag();
            if (isSmall){
                fm.beginTransaction().replace(R.id.smallDevice_frag,favorite_frag,Constant.favoriteTAG)*//*.addToBackStack(Constant.favoriteTAG)*//*.addToBackStack(null).commit();
            }else {
                fm.beginTransaction().replace(R.id.search_frag_inBigDEvice,favorite_frag,Constant.favoriteTAG).commit();
            }
        }*/

            return super.onOptionsItemSelected(item);
        }

        //--------------------interface   OnMyLocation_reciver---------------
        @Override
        public void setMyLocationsInText ( double lat, double lng){
//----- not relevcent; for the tryouyt!
            FragmentManager fm = getSupportFragmentManager();
            if (isSmall) {
                search_frag = (Search_frag) fm.findFragmentById(R.id.smallDevice_frag);
            } else {
                search_frag = (Search_frag) fm.findFragmentById(R.id.search_frag_inBigDEvice);
            }
            search_frag.setLngLngOnTheText(lat, lng);
        }

        @Override
        public void locationNullOpenLocationSettings () {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.noLocationTitle));
            builder.setMessage(getString(R.string.noLocationMsg));
            builder.setCancelable(true)
                    .setPositiveButton(getString(R.string.setting), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
            builder.setNegativeButton(getString(R.string.cancel), null);
            builder.show();
        }

        @Override
        public void setmyLocationInMapIfBig ( double lat, double lng){

            //when you have location save the location and if your tablet display it.
            if (!isSmall) {

                FragmentManager fm = getSupportFragmentManager();

                map_frag = (Map_frag) fm.findFragmentById(R.id.theMap);
                map_frag.setMyPlacesAndBeOnCenter(lat, lng);
            } else {

            }


        }


        @Override
        public void onGetMyLocation_btn () {


            Intent myLocationService = new Intent(MainActivity.this, MyLocation_intentService.class);
            //for the tryout i did this reciver.. no needed.
            //this server will only go to the phone location and save to database.

            // myLocation_reciver = new MyLocation_reciver();
            // IntentFilter intentFilter = new IntentFilter(MyLocation_intentService.action);
            // LocalBroadcastManager.getInstance(this).registerReceiver(myLocation_reciver,intentFilter);

            //isMyLocationServiceOn = true; // if true and it was service and not intentService so i needed to unservice;
            try {
                //stopService(myLocationService);
            } catch (Exception e) {
                e.getCause();
            }
            startService(myLocationService);
        }

        @Override
        public void onGeocorder_btn (String location){
            // the methos that call the service after touch the geocoder btn
            //  Intent intent = new Intent(this , MyGeocorder_IntentService.class);
            // IntentFilter intentFilter = new IntentFilter(MyGeocorder_reciver.action);
            //  myGeocorder_reciver = new MyGeocorder_reciver();

            //  LocalBroadcastManager.getInstance(this).registerReceiver(myGeocorder_reciver,intentFilter);
            // intent.putExtra(Constant.location,location);
            // startService(intent);

        }

        @Override
        public void onSerachByTextBtn_startService (String whatToSearh, String pageToken){
            Intent intent = new Intent(this, MyGoole_api_searchByText_intentService.class);

            //handle zero result;
            //zeroResult_reciver = new ZeroResult_reciver();
            //LocalBroadcastManager.getInstance(this).registerReceiver(zeroResult_reciver,new IntentFilter(ZeroResult_reciver.action));

            //   IntentFilter intentFilter = new IntentFilter(MyGoogle_api_searchByText_reciver.action);
            //   myGoogle_api_searchByText_reciver = new MyGoogle_api_searchByText_reciver();
            //   LocalBroadcastManager.getInstance(this).registerReceiver(myGoogle_api_searchByText_reciver,intentFilter);
            intent.putExtra(Constant.next_page_token, pageToken);
            intent.putExtra(Constant.whatToSearh, whatToSearh);
            startService(intent);

        }


        @Override
        public void onCleaeMarker () {
            FragmentManager fm = getSupportFragmentManager();
            Map_frag map_frag1 = new Map_frag();
            map_frag1 = (Map_frag) fm.findFragmentById(R.id.theMap);
            map_frag1.clearAllMarkers();
        }

        @Override
        public void onOpenLocationSetting () {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        @Override
        public void onSearchNearMeBtn_startService (String type,int radius, int token,
        boolean deleteAllData, String next_page_token){
            onGetMyLocation_btn();
            Intent intent = new Intent(MainActivity.this, MyGoole_api_searchNearMyPlace_intentService.class);
            intent.putExtra(Constant.type, type);
            intent.putExtra(Api_constant.radius, radius);
            intent.putExtra(Constant.deletLastData, deleteAllData);
            intent.putExtra(Constant.token, 0);
            intent.putExtra(Constant.next_page_token, next_page_token);
            startService(intent);
        }



/*
    @Override
    public void onLocationReciveToCordinet(double lat, double lng) {
        //the function from the gecoder.. from string to lat lng
        FragmentManager fragmentManager = getSupportFragmentManager();
        search_frag = new Search_frag();
        if (isSmall){
            search_frag = (Search_frag)fragmentManager.findFragmentById(R.id.smallDevice_frag);
        }else {
            search_frag = (Search_frag)fragmentManager.findFragmentById(R.id.search_frag_inBigDEvice);
        }
        search_frag.setGeocorderOnText(lat,lng);
    }*/

        @Override
        public void onSearchByText_mapMarkers () {
/// after the search we need to add to the pulltorefrsh;
            FragmentManager fm = getSupportFragmentManager();
            if (isSmall) {

                search_frag = (Search_frag) fm.findFragmentById(R.id.smallDevice_frag);
                search_frag.fillPullToRefresh();

            } else {

                map_frag = (Map_frag) fm.findFragmentById(R.id.theMap);
                map_frag.searchByTextAndAddMarkers();

                Search_frag searchFrag = (Search_frag) fm.findFragmentById(R.id.search_frag_inBigDEvice);
                searchFrag.fillPullToRefresh();
            }


        }

        @Override
        public void onSelectPlace (String name){
            FragmentManager fm = getSupportFragmentManager();

            if (isSmall) {
                editor.putString(Constant.wicthFragmentAmI, Constant.mapFragment);
                Map_frag map_frag1 = new Map_frag();
                editor.putString(Constant.name, name);
                editor.commit();
                String nameTry = sharedPreferences.getString(Constant.name, "");
                Bundle bundle = new Bundle();
                bundle.putString(Constant.name, name);
                bundle.putBoolean(Constant.isSamll, isSmall);
                // madeFirstSearch = sharedPreferences.getBoolean(Constant.firstSearchInMap,false);
                map_frag1.setArguments(bundle);
                /*firstTimeInMap = sharedPreferences.getBoolean(Constant.firstSearchInMap, true);
                if (firstTimeInMap) {
                    fm.beginTransaction().replace(R.id.smallDevice_frag, map_frag1, Constant.theMapTAG).addToBackStack(Constant.theMapTAG)*//* .addToBackStack(null)*//*.commit();
                    editor.putBoolean(Constant.firstSearchInMap, false).apply();
                } else*/
                    fm.beginTransaction().replace(R.id.smallDevice_frag, map_frag1, Constant.theMapTAG)/*.addToBackStack(Constant.theMapTAG) .addToBackStack(null)*/.commit();


            } else {
                map_frag = (Map_frag) fm.findFragmentById(R.id.theMap);
                map_frag.meAndThePlace(name);

            }
        }


        @Override
        public void onReciverMyNearPlacePostOnMap () {
            FragmentManager fm = getSupportFragmentManager();
            if (isSmall) {
                search_frag = (Search_frag) fm.findFragmentById(R.id.smallDevice_frag);
                search_frag.fillPullToRefresh();
            } else {



                map_frag = (Map_frag) fm.findFragmentById(R.id.theMap);
                map_frag.clearAllMarkers();// clear the last markers;
                map_frag.searchByTextAndAddMarkers();
                search_frag.fillPullToRefresh();

            }
        }

        @Override
        public void onZeroResultAlertDialog () {
            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.ZERO_RESULAT_title));
            builder.setMessage(getString(R.string.ZERO_RESULAT_mag));
            builder.setCancelable(true);
            builder.show();*/
            FragmentManager fm = getSupportFragmentManager();
            search_frag = (Search_frag) fm.findFragmentById(R.id.smallDevice_frag);
            search_frag.onZeroResultAlertDialog();

        }
        //-----------------------------------------

    public String readShard(String what, String defaultReturn) {
        return sharedPreferences.getString(what, defaultReturn);
    }

    public void writeToShard(String what, String defaultValue) {
        editor.putString(what, defaultValue).apply();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregestry the recivers!
        if (myLocation_reciver != null) {

            LocalBroadcastManager.getInstance(this).unregisterReceiver(myLocation_reciver);
            myLocation_reciver = null;
        }
        // if(zeroResult_reciver != null){

        //     LocalBroadcastManager.getInstance(this).unregisterReceiver(zeroResult_reciver);
        //     zeroResult_reciver=null;

        // }
        if (myGoogle_api_searchByText_reciver != null) {

            LocalBroadcastManager.getInstance(this).unregisterReceiver(myGoogle_api_searchByText_reciver);
            myGoogle_api_searchByText_reciver = null;
        }
        if (myGoogle_api_nearMyPlace_reciver != null) {

            LocalBroadcastManager.getInstance(this).unregisterReceiver(myGoogle_api_nearMyPlace_reciver);
            myGoogle_api_nearMyPlace_reciver = null;
        }

        unregisterReceiver(myCharging_reciver);

        if (networkChangeReceiver!=null){
            unregisterReceiver(networkChangeReceiver);
        }

        //unregisterReceiver(networkChangeReceiver);


        /*if (zeroResult_reciver!=null){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(zeroResult_reciver);
            zeroResult_reciver=null;
        }*/

    }

    @Override
    public void onNoInternetConnection() {
        Log.d("intenet no", "no");
        editor.putBoolean(Constant.isInternetConnection, false).apply();
        String wichFragmentIAm = sharedPreferences.getString(Constant.wicthFragmentAmI,null);
        if (isSmall){
            if (wichFragmentIAm.equals(Constant.searchFragment)){
                search_frag.replaceToFavorite();
            }else {
                // if i am in the map so leave this fragment and go to favorite;

                editor.putString(Constant.wictchSearchIAM,Constant.favorite_places);// so the favorite will be displayed
                editor.putString(Constant.wicthFragmentAmI,Constant.searchFragment);// so the fragment will be appeared;
                editor.apply();
                FragmentManager fm = getSupportFragmentManager();
                Search_frag searchFrag = new Search_frag();
                fm.beginTransaction().replace(R.id.smallDevice_frag,searchFrag).commit();

            }
        }else {
            search_frag.replaceToFavorite();
        }
        // Toast.makeText(this, Resources.getSystem().getString(R.string.noInternet), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInternetConnection() {
        Log.d("intenet yes", "yes");
        editor.putBoolean(Constant.isInternetConnection, true).apply();
        // Toast.makeText(this, Resources.getSystem().getString(R.string.yesInternet), Toast.LENGTH_LONG).show();
    }


    /*@Override
    public void onFavoriteSelectPlace(String name) {
        FragmentManager fm = getSupportFragmentManager();

        if (isSmall) {
            editor.putString(Constant.wicthFragmentAmI, Constant.mapFragment);
            Map_frag map_frag1 = new Map_frag();
            editor.putString(Constant.name, name);
            editor.commit();
            String nameTry = sharedPreferences.getString(Constant.name, "");
            Bundle bundle = new Bundle();
            bundle.putString(Constant.name, name);
            bundle.putBoolean(Constant.isSamll, isSmall);

            map_frag1.setArguments(bundle);

            //madeFirstSearch = sharedPreferences.getBoolean(Constant.firstSearchInMap,false);
            fm.beginTransaction().replace(R.id.smallDevice_frag, map_frag1, Constant.theMapTAG).addToBackStack(Constant.theMapTAG) *//*.addToBackStack(null) *//*.commit();

        } else {
            Map_frag map_frag1 = (Map_frag) fm.findFragmentById(R.id.theMap);
            map_frag1.meAndThePlace(name);

        }
    }*/


}
