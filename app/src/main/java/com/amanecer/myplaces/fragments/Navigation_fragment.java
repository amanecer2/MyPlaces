package com.amanecer.myplaces.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.amanecer.myplaces.R;
import com.amanecer.myplaces.database.Constant;
import com.amanecer.myplaces.database.DBHandler;
import com.amanecer.myplaces.ui.MainActivity;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

/**
 * Created by amanecer on 25/02/2015.
 */
//com.amanecer.myplaces.fragments.Navigation_fragment
public class Navigation_fragment extends Fragment{

   private boolean mUserLearnedDrawer;
   private boolean mFromSaveInstance;
   private boolean isSmall;
   private SharedPreferences sharedPreferences;
   private SharedPreferences.Editor editor;
   private String learned = "learned";
   private Button button;
   private Switch kmOrMiles;
   private View v;
   private ActionBarDrawerToggle mActionBarDrawerToggle;
   private DrawerLayout mDrawerLayout;
   private MainActivity mainActivity;
   private Search_frag search_frag;

    private ShowcaseView showcaseView;
    private int conuter =0;
    private Target target;
   private Button setting_favorites,setting_search,show_favorites,delete_favorites, show_myLocation,setting_advance,showShowCaseView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(Constant.sharedPrefernces,getActivity().MODE_PRIVATE);

        editor = sharedPreferences.edit();

        mUserLearnedDrawer = sharedPreferences.getBoolean( learned , false); // does the user know that there is a drawer?
        if (savedInstanceState!=null){
            mFromSaveInstance = true; // the user cameBack from another fragment / orientation;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.navigation_gragment,container,false);

        setting_favorites = (Button)v.findViewById(R.id.setting_favorites);
        setting_search = (Button)v.findViewById(R.id.setting_search);

        show_favorites = (Button)v.findViewById(R.id.show_favorites);
        delete_favorites = (Button)v.findViewById(R.id.delete_favorites);

        show_myLocation =(Button)v.findViewById(R.id.show_myLocation);
        kmOrMiles = (Switch)v.findViewById(R.id.kmOrMiles);

        setting_advance=(Button)v.findViewById(R.id.setting_advance);

        showShowCaseView = (Button)v.findViewById(R.id.showShowCaseView);

         target = new ViewTarget((R.id.setting_search),getActivity());
        return v;
    }


    /// all things that relate to the fragment is in here!;
    public void setUp(final DrawerLayout drawerLayout, final Toolbar toolbar, final int layout) {

         String wicthFragmentIAm = sharedPreferences.getString(Constant.wicthFragmentAmI,null);
        isSmall = sharedPreferences.getBoolean(Constant.isSamll,true);
        v = getActivity().findViewById(layout); // the view R.id of the fragment in the main activity;
        this.mDrawerLayout = drawerLayout;

        //ACTION bar drawer toggle obkjst!;
        this.mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,(R.string.drawer_open),(R.string.drawe_close)){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer){
                    mUserLearnedDrawer = true;
                    editor.putBoolean(learned,true).apply();
                }
                getActivity().invalidateOptionsMenu();// after the drawer the activity will return to be action bar again!
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();// after the drawer the activity will return to be action bar again!
            }

            // 0 is complitley close == onDrawerClosed
            // 1 is completley opened == onDrawerOpened;

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                Log.d("offset: ",slideOffset+"");
                if (slideOffset<0.6)
                    toolbar.setAlpha(1-slideOffset); // change the fading off ther toolbar; 0-- shaded 1 clear; so need to be opeset;

            }
        };
        if(!mUserLearnedDrawer && !mFromSaveInstance ){ // if the user didint open the drawer and it's the firs time the app is running(save = null)
            //   mDrawerLayout.openDrawer(v);
        }
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);/// listener for open and close..
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();// if the drawer is open and you change oroigetaion so keep the same sync
            }
        });


        show_myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"on click",Toast.LENGTH_LONG).show();
            }
        });


        delete_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSmall){
                    String witchFragmentIAm = sharedPreferences.getString(Constant.wicthFragmentAmI,null);
                    if (witchFragmentIAm!=null){
                        if(witchFragmentIAm.equals(Constant.searchFragment)){
                            search_frag = (Search_frag)getActivity().getSupportFragmentManager().findFragmentById(R.id.smallDevice_frag);
                            DBHandler dbHandler = new DBHandler(getActivity());
                            dbHandler.clearTableFavorites();
                            search_frag.emptyPullToRefresh();
                        }
                    }

                }else {
                    search_frag.emptyPullToRefresh();
                }
                Toast.makeText(getActivity(),getString(R.string.favoriteDeleted),Toast.LENGTH_LONG).show();
            }
        });

        setting_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSmall){
                    editor.putString(Constant.wictchSearchIAM,Constant.searchFragment).apply();
                    String witchFragmentIAm = sharedPreferences.getString(Constant.wicthFragmentAmI,null);
                    if (witchFragmentIAm!=null){
                        if(witchFragmentIAm.equals(Constant.searchFragment)){
                            setting_search.setVisibility(View.GONE);
                            delete_favorites.setVisibility(View.GONE);
                            setting_favorites.setVisibility(View.VISIBLE);
                            search_frag = (Search_frag)getActivity().getSupportFragmentManager().findFragmentById(R.id.smallDevice_frag);
                            search_frag.fillPullToRefresh();
                            search_frag.showSearch();

                        }

                    }
                }else {
                    setting_search.setVisibility(View.GONE);
                    delete_favorites.setVisibility(View.GONE);
                    setting_favorites.setVisibility(View.VISIBLE);
                    search_frag = (Search_frag)getActivity().getSupportFragmentManager().findFragmentById(R.id.search_frag_inBigDEvice);
                    search_frag.fillPullToRefresh();
                    search_frag.showSearch();
                }
            }
        });

        setting_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSmall){
                    String witchFragmentIAm = sharedPreferences.getString(Constant.wicthFragmentAmI,null);

                    if (witchFragmentIAm!=null){
                        if(witchFragmentIAm.equals(Constant.searchFragment)){
                            search_frag = (Search_frag)getActivity().getSupportFragmentManager().findFragmentById(R.id.smallDevice_frag);
                            search_frag.replaceToFavorite();
                            setting_search.setVisibility(View.VISIBLE);
                            setting_favorites.setVisibility(View.GONE);
                            delete_favorites.setVisibility(View.VISIBLE);
                            //editor.putString(Constant.wictchSearchIAM,Constant.favorite_places).apply();

                        }

                    }
                }else {
                    setting_search.setVisibility(View.VISIBLE);
                    delete_favorites.setVisibility(View.VISIBLE);
                    setting_favorites.setVisibility(View.GONE);
                    Map_frag map_frag = (Map_frag)getActivity().getSupportFragmentManager().findFragmentById(R.id.theMap);
                    map_frag.clearAllMarkers();
                    search_frag = (Search_frag)getActivity().getSupportFragmentManager().findFragmentById(R.id.search_frag_inBigDEvice);
                    search_frag.replaceToFavorite();
                }

            }
        });


        showShowCaseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDrawerLayout.closeDrawers();
                if (isSmall){
                    search_frag = (Search_frag)getActivity().getSupportFragmentManager().findFragmentById(R.id.smallDevice_frag);
                    search_frag.setShowcaseView();
                }else {
                    search_frag = (Search_frag)getActivity().getSupportFragmentManager().findFragmentById(R.id.search_frag_inBigDEvice);
                    search_frag.setShowcaseView();
                }

            }
        });
       /* showcaseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (conuter) {
                    case 0:


                        break;
                    case 1 :
                        drawerLayout.closeDrawer(Gravity.LEFT); // open the drawer;
                        break;

                    default:
                        showcaseView.hide();
                        break;
                }

            }

        });




*/

        kmOrMiles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // checked = mile;
                // unchecked = km;
                if (isChecked){
                    editor.putString(Constant.milesOrKm,Constant.miles).apply();
                }else {
                    editor.putString(Constant.milesOrKm,Constant.km).apply();
                }
                    String kmOrmile = sharedPreferences.getString(Constant.milesOrKm,null);
                Toast.makeText(getActivity(),getString(R.string.changeOnTheNextSearch),Toast.LENGTH_LONG).show();
            }
        });

    }

    public void close(){
        mDrawerLayout.closeDrawer(Gravity.END);
    }

    public void open(){
        mDrawerLayout.openDrawer(Gravity.START);
    }
}
