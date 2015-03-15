package com.amanecer.myplaces.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amanecer.myplaces.R;
import com.amanecer.myplaces.database.Api_constant;
import com.amanecer.myplaces.database.Constant;
import com.amanecer.myplaces.database.DBHandler;
import com.amanecer.myplaces.obj.Row_obj;
import com.amanecer.myplaces.ui.MyBaseAdapter;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.software.shell.fab.ActionButton;

import java.util.ArrayList;

/**
 * Created by amanecer on 26/01/2015.
 */
public class Search_frag extends Fragment {

    private PullToRefreshListView mPullRefreshListView;
    private static ArrayList<Row_obj> list;
    private static MyBaseAdapter myBaseAdapter;
    private String type = "";
    String whatToSearch = "";



    private android.support.v7.widget.SwitchCompat searchTypeSwitch;

    ProgressDialog progressDialog;


    private String wicthSearchIAM;
    private boolean isSearchByText = false;
    private TextView lng_txt, lat_txt, geocorder_lat_txt, geocorder_lng_txt, radius_txt;
    private Button searchByText_btn, geocorder_btn, settings, searchNearMyPlace_btn;
    private EditText searchByText_editText, geocorder_editText;
    private SeekBar seekBar;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> types, typeToSearch;
    private OnSearch_frag callback;
    private LinearLayout layout_searchBtText, layout_searchNearMe, layout_swicth;
    private int listPosition = 4;
    private  int radius = 0;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ShowcaseView showcaseView;
    private Target targetSwicth, targetSearch, targetToolBar;
    private int counter = 0;

    private ActionButton actionButton;

    private ViewGroup.LayoutParams layoutParams;

    private DBHandler handler;

    private String mileORKm;

    public Search_frag() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);/// to ensure there is a menu!
        View v = inflater.inflate(R.layout.search_frag, container, false);

        layout_searchBtText = (LinearLayout) v.findViewById(R.id.layout_searchByText);
        layout_searchNearMe = (LinearLayout) v.findViewById(R.id.layout_searchNearMe);
        layout_swicth = (LinearLayout) v.findViewById(R.id.layout_swicth);

        mPullRefreshListView = (PullToRefreshListView) v.findViewById(R.id.pull_refresh_list);

        spinner = (Spinner) v.findViewById(R.id.spinner);

        searchTypeSwitch = (android.support.v7.widget.SwitchCompat) v.findViewById(R.id.searchTypeSwitch);

        //searchByText_btn = (Button) v.findViewById(R.id.searchByText_btn);
        searchByText_editText = (EditText) v.findViewById(R.id.searchByText_editText);

        //geocorder_btn = (Button) v.findViewById(R.id.geocorder_btn);
        //geocorder_editText = (EditText) v.findViewById(R.id.geocorder_editText);


        seekBar = (SeekBar) v.findViewById(R.id.radius);

        radius_txt = (TextView) v.findViewById(R.id.radius_txt);

        //searchNearMyPlace_btn = (Button) v.findViewById(R.id.searchNearMyPlace_btn);

        targetSearch = new ViewTarget(v.findViewById(R.id.go));
        targetSwicth = new ViewTarget(v.findViewById(R.id.searchTypeSwitch));


        actionButton = (ActionButton) v.findViewById(R.id.go);

        return v;
    }

    private static final int LOADER_ID = 0;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //this to implement the methods!
        callback = (OnSearch_frag) activity;


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());

        sharedPreferences = getActivity().getSharedPreferences(Constant.sharedPrefernces, getActivity().MODE_PRIVATE);
        editor = sharedPreferences.edit();

        setRetainInstance(true);
        editor.putString(Constant.wicthFragmentAmI, Constant.searchFragment).apply();


        boolean firstTime = sharedPreferences.getBoolean(Constant.showCaseFirstTimeAppLoaded, false);
        if (!firstTime) {
            //for the wellcome show case view;
            editor.putBoolean(Constant.showCaseFirstTimeAppLoaded, true).apply();
            setShowcaseView();
        }


        handler = new DBHandler(getActivity());
    }


    public void setShowcaseView() {
        showcaseView = new ShowcaseView.Builder(getActivity())
                .setTarget(Target.NONE)
                .setContentTitle(R.string.firstTimeLuanchTitle)
                .setContentText(R.string.firstTimeLuanchMsg)
                .setStyle(R.style.transprens)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (counter) {
                            case 0:
                                showcaseView.setShowcase(targetSwicth, true);
                                showcaseView.setContentText(getActivity().getString(R.string.firstTimeLuanchShowSwicthText));
                                showcaseView.setContentTitle(getActivity().getString(R.string.firstTimeLuanchShowSwicthTitle));
                                break;
                            case 1:
                                showcaseView.setShowcase(targetSearch, true);
                                showcaseView.setContentText(getActivity().getString(R.string.firstTimeLuanchShowSearchBtnText));
                                showcaseView.setContentTitle(getActivity().getString(R.string.firstTimeLuanchShowSearchBtnTitle));
                                break;

                            default:
                                counter = 0;
                                showcaseView.hide();
                                break;

                        }
                        counter++;
                    }

                })

                .build();

        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_BOTTOM);

        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 14))
                .intValue();
        lps.setMargins(margin, margin, margin, margin);
        showcaseView.setButtonPosition(lps);


    }


    public void saveStringToShardPref(String whatToSave,String defualValue ){
        editor.putString(whatToSave,defualValue).apply();
    }

    public String getStringFromShardPref(String whatToGet, String defualValue){
        return sharedPreferences.getString(whatToGet,defualValue);
    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onStart() {
        super.onStart();
        wicthSearchIAM = sharedPreferences.getString(Constant.wictchSearchIAM, Constant.searchFragment);

        list = new ArrayList<>();
        // handler.onLocationReady();
        if (handler.onLocationReady()) {
            //chech if there MyLocation data; before that it was explosing al the time;
            if (wicthSearchIAM.equals(Constant.favorite_places)) {
                list = handler.getRowObjFilterByAction(Constant.favorite_places);//this is the proble,m!
                layout_searchBtText.setVisibility(View.GONE);
                layout_searchNearMe.setVisibility(View.GONE);
                layout_swicth.setVisibility(View.GONE);
                actionButton.hide();
            } else {
                list = handler.getRowObjFilterByAction(Constant.places);//this is the proble,m!

            }

        }

        mileORKm = sharedPreferences.getString(Constant.milesOrKm, Constant.km);


        radius = sharedPreferences.getInt(Constant.radiusSave,700);
        seekBar.setProgress(radius);
        if (mileORKm.equals(Constant.km)) {
            radius_txt.setText(radius + " " + getString(R.string.meters));
        } else {
            //String numString = 700 * handler._MetersToFeet + "";

            double num = radius * handler._MetersToFeet;
            int numCast = (int) num;
            radius_txt.setText(num + " " + getString(R.string.feet));
        }

        typeToSearch = new ArrayList<>();
        types = new ArrayList<>();
        types();
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, types);
        spinner.setAdapter(adapter);


        int setSppiner = sharedPreferences.getInt(Constant.setSpinner,4);
        spinner.setSelection(setSppiner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listPosition = position;
                editor.putInt(Constant.setSpinner,listPosition).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        searchTypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layout_searchNearMe.setVisibility(View.VISIBLE);
                    layout_searchBtText.setVisibility(View.GONE);
                    isSearchByText = false;
                    /*editor.putString(Constant.searchTypeSwitch,Constant.searchNearMe);
                    editor.apply();*/
                } else {
                    layout_searchNearMe.setVisibility(View.GONE);
                    layout_searchBtText.setVisibility(View.VISIBLE);
                    isSearchByText = true;

                }
            }
        });


        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setMessage(getString(R.string.StartSearchDialogMassge));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                Boolean isInternetConnection = sharedPreferences.getBoolean(Constant.isInternetConnection, true);
                saveStringToShardPref(Constant.next_page_token,"");// to erease the next page string so it will search for a new search;
                if (isInternetConnection) {
                    //wictch type of search;
                    if (isSearchByText) {
                        if (handler.onLocationReady()) {
                            callback.onGetMyLocation_btn();
                            whatToSearch = searchByText_editText.getText().toString();
                            callback.onSerachByTextBtn_startService(whatToSearch,"");
                        } else {
                            callback.onGetMyLocation_btn();
                        }
                    } else {
                        if (handler.onLocationReady()) {
                            type = typeToSearch.get(listPosition);
                            // the list typeToSearch is a list for google search
                            // and the list types is a human list.
                            saveStringToShardPref(Constant.type,types.get(listPosition));
                            //saving the radius and the type ass string.
                            saveStringToShardPref(Constant.radius,radius+"");
                            callback.onGetMyLocation_btn();// search for location;
                            callback.onSearchNearMeBtn_startService(type, radius, 0, true , null);
                        } else {
                            callback.onGetMyLocation_btn();// search for location;
                        }
                    }
                }else {
                    Toast.makeText(getActivity(),getString(R.string.noInternet),Toast.LENGTH_LONG).show();
                }

            }
        });



        //---------adapt how long for nearMe search;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String mileORKm = sharedPreferences.getString(Constant.milesOrKm, Constant.km);

                if (mileORKm.equals(Constant.km)) {
                    radius_txt.setText(progress + " " + getString(R.string.meters));

                } else {
                    double feet = 3.2808399 * progress;
                    int feetVastToInt = (int) feet;
                    radius_txt.setText(feetVastToInt + " " + getString(R.string.feet));
                }

                radius = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(Constant.radiusSave,radius).apply();
            }
        });


        //handle the pull to refresh-----------------


        myBaseAdapter = new MyBaseAdapter(getActivity(), list);
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                // Do work to refresh the list here.



                new GetData().execute();
            }
        });

        mPullRefreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                Toast.makeText(getActivity(), "End of List!", Toast.LENGTH_SHORT).show();

            }
        });

        mPullRefreshListView.setAdapter(myBaseAdapter);
        mPullRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                boolean isIntenertConction = sharedPreferences.getBoolean(Constant.isInternetConnection,false);
                if (isIntenertConction){
                    final int pos = position - 1;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.pullToRefreshItemClickTitle) + list.get(pos).getPlaceName());
                    builder.setMessage(getString(R.string.pullToRefreshItemClickMsg) + " " + list.get(pos).getPlaceAdress() + " ?");
                    builder.setCancelable(true)
                            .setPositiveButton(getString(R.string.navigate), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    callback.onSelectPlace(list.get(pos).getPlaceName());

                                }
                            });
                    builder.setNegativeButton(getString(R.string.cancel), null);
                    builder.show();
                }else {
                    Toast.makeText(getActivity(),getString(R.string.noInternet),Toast.LENGTH_LONG).show();
                }
            }
        });

        //-- handle a empty listView
        TextView textView = new TextView(getActivity());
        textView.setText(getString(R.string.emptyListVie));
        mPullRefreshListView.setEmptyView(textView);


        ListView actualListView = mPullRefreshListView.getRefreshableView();
        actualListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {

                //if your are in the favorite view.. so you can't add to favorite;
                wicthSearchIAM = sharedPreferences.getString(Constant.wictchSearchIAM, Constant.searchFragment);
                if (!wicthSearchIAM.equals(Constant.favorite_places)) {
                    final int position = pos -1;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.addPlaceToFavoriteTitle) + " : " + list.get(position).getPlaceName());
                    builder.setMessage(getString(R.string.addPlaceToFavoriteMsg) + ".\n  " + list.get(position).getPlaceAdress() + "  ? ");
                    builder.setCancelable(true)
                            .setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (handler.addToFavorite(list.get(position).getPlaceName())) {

                                        myBaseAdapter.notifyDataSetChanged();

                                        Toast.makeText(getActivity(), "Place added ", Toast.LENGTH_LONG).show();

                                    } else {
                                        Toast.makeText(getActivity(), "Place already in", Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .setNegativeButton(getString(R.string.share),new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("text/plain");
                                    share.putExtra(Intent.EXTRA_TEXT, getString(R.id.iAmInThisPlace)+" "+list.get(position).getPlaceName());
                                    startActivity(share);
                                }
                            })
                            .setNeutralButton(getString(R.string.cancel), null);

                    builder.show();

                }


                return true;
            }
        });
    }

    private void types() {
        //the types are the one who show to the puople;
        // the typeToSearch is to google api;
        types.add(getString(R.string.cafeHouse));
        typeToSearch.add("cafe");
        types.add(getString(R.string.food));
        typeToSearch.add("food");
        types.add(getString(R.string.bars));
        typeToSearch.add("bar");
        types.add(getString(R.string.resturants));
        typeToSearch.add("restaurant");
        types.add(getString(R.string.gasStations));
        typeToSearch.add("gas_station");
        types.add(getString(R.string.pharmacy));
        typeToSearch.add("pharmacy");
        types.add(getString(R.string.bakery));
        typeToSearch.add("bakery");
        types.add(getString(R.string.synagogues));
        typeToSearch.add("synagogue");
        types.add(getString(R.string.schools));
        typeToSearch.add("school");

    }

    public void setLngLngOnTheText(double lat, double lng) {
        /*String stringLat = String.valueOf(lat).toString();
        String stringLng = String.valueOf(lng).toString();*/
        lat_txt.setText("" + lat);
        lng_txt.setText("" + lng);
    }

    public void setGeocorderOnText(double lat, double lng) {
        geocorder_lat_txt.setText("the lat : " + lat);
        geocorder_lng_txt.setText("the lng : " + lng);
    }

    public void replaceToFavorite() {
        actionButton.hide();
        editor.putString(Constant.wictchSearchIAM, Constant.favorite_places).apply();
        layout_searchBtText.setVisibility(View.GONE);
        layout_searchNearMe.setVisibility(View.GONE);
        layout_swicth.setVisibility(View.GONE);

        list = handler.getRowObjFilterByAction(Constant.favorite_places);

        if (list == null)
            list = new ArrayList<>(); // if the favorite is empty so new the list;


        myBaseAdapter = new MyBaseAdapter(getActivity(), list);
        //adapter.notifyDataSetChanged();
        mPullRefreshListView.setAdapter(myBaseAdapter);
        // adapter.setNotifyOnChange(true);
        mPullRefreshListView.onRefreshComplete();


    }

    public void showSearch() {
        layout_searchBtText.setVisibility(View.GONE);
        layout_searchNearMe.setVisibility(View.VISIBLE);
        layout_swicth.setVisibility(View.VISIBLE);
        actionButton.show();
    }


    public class GetData extends AsyncTask<Void, Void, String> {
        //pull to refresh wating and do something
        @Override
        protected String doInBackground(Void... params) {
            try {

                //wait and do your show
                Thread.sleep(2000);
                //boolean isInternetConnection = sharedPreferences.getBoolean(Constant.isInternetConnection,false);
               // if (!isInternetConnection)
              //      return null;
            } catch (InterruptedException e) {
                e.getCause();
            }
            // the action button is reseting this string to ""
            return getStringFromShardPref(Api_constant.next_page_token,"");
        }

        @Override
        protected void onPostExecute(String s) {

            //------- get the location in the start or open massage to open setting if gps is off;
            if (s!=null){
                callback.onGetMyLocation_btn();
                //---------------------------------
                //do the serach again if your are
                if (isSearchByText) {
                    if (s.equals("")){
                        callback.onSerachByTextBtn_startService(whatToSearch,"");
                    }else {
                        callback.onSerachByTextBtn_startService(whatToSearch,s);
                    }

                } else {
                    type = typeToSearch.get(listPosition);
                    if (s.equals("")){
                        callback.onSearchNearMeBtn_startService(type, radius, 0, true , null);
                    }else {
                        callback.onSearchNearMeBtn_startService(type, radius, 0, false ,s);
                    }

                }

                myBaseAdapter.notifyDataSetChanged();
                mPullRefreshListView.onRefreshComplete();
            }else{
                Toast.makeText(getActivity(),getString(R.string.noInternet),Toast.LENGTH_LONG).show();
                mPullRefreshListView.onRefreshComplete();
            }

            super.onPostExecute(s);


        }
    }


    public void fillPullToRefresh() {
        progressDialog.dismiss();// dismissed the dialog bar;
        handler = new DBHandler(getActivity());
        String s = "d";
        try {
            list = handler.getRowObjFilterByAction(Constant.places);
        } catch (Exception e) {
            e.getCause();

        }

        // myBaseAdapter.notifyDataSetChanged();
        //somwhow the notofi dosent work!
        if (list != null) {
            myBaseAdapter = new MyBaseAdapter(getActivity(), list);
            //adapter.notifyDataSetChanged();
            mPullRefreshListView.setAdapter(myBaseAdapter);
            // adapter.setNotifyOnChange(true);
            mPullRefreshListView.onRefreshComplete();
        }


    }

    public void emptyPullToRefresh() {

        list = new ArrayList<>();
        myBaseAdapter = new MyBaseAdapter(getActivity(), list);
        mPullRefreshListView.setAdapter(myBaseAdapter);
        mPullRefreshListView.onRefreshComplete();
    }

    public void onZeroResultAlertDialog () {
        progressDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.ZERO_RESULAT_title));
        builder.setMessage(getString(R.string.ZERO_RESULAT_mag));
        builder.setCancelable(true);
        builder.show();

    }


    public interface OnSearch_frag {
        public void onGetMyLocation_btn();

        public void onGeocorder_btn(String location);

        public void onSerachByTextBtn_startService(String whatToSearh,String pageToken);

        public void onCleaeMarker();

        public void onOpenLocationSetting();

        public void onSearchNearMeBtn_startService(String type, int radius, int token, boolean deleteAllData,String next_page_token);

        public void onSelectPlace(String name);



    }


   /* private class MyLocation extends */
}
