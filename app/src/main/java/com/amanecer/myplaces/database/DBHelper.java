package com.amanecer.myplaces.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by amanecer on 26/01/2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tablePlaces = "CREATE TABLE "+Constant.places+" ("
                +Constant._id                   +" INTEGER PRIMARY KEY, "
                +Api_constant.place_id          +" TEXT, "
                +Api_constant.next_page_token   +" TEXT, "
                +Api_constant.lat               +" DOUBLE , "
                +Api_constant.lng               +" DOUBLE , "
                +Api_constant.iconPATH          +" TEXT, "
                +Api_constant.name              +" TEXT, "
                +Api_constant.open_now          +" BOOLEAN, "
                +Api_constant.types             +" TEXT ,"
                +Api_constant.formatted_address +" TEXT"
                + ");" ;

        String tableFavoritePlaces = "CREATE TABLE "+Constant.favorite_places+" ("
                +Constant._id       +" INTEGER PRIMARY KEY, "
                +Api_constant.place_id +" TEXT, "
                +Api_constant.lat       +" DOUBLE , "
                +Api_constant.lng       +" DOUBLE , "
                +Api_constant.iconPATH  +" TEXT, "
                +Api_constant.name      +" TEXT, "
                +Api_constant.open_now  +" BOOLEAN, "
                +Api_constant.types     +" TEXT ,"
                +Api_constant.formatted_address +" TEXT"
                + ");" ;

        String tableMyLocations = "CREATE TABLE "+Constant.myLocations+" ("
                +Constant._id +" INTEGER PRIMARY KEY, "
                +Constant.lat +" DOUBLE , "
                +Constant.lng +" DOUBLE , "
                +Constant.time+" DOUBLE  "
                +");";
        String tableMyLocationNow = "CREATE TABLE "+Constant.myLocationNow+" ("
                +Constant._id           +" INTEGER PRIMARY KEY, "
                +Constant.lat           +" DOUBLE , "
                +Constant.lng           +" DOUBLE , "
                +Constant.time          +" DOUBLE , "
                +Constant.locationName  +" TEXT  "
                +");";
        try {

            db.execSQL(tablePlaces);
            db.execSQL(tableMyLocations);
            db.execSQL(tableMyLocationNow);
            db.execSQL(tableFavoritePlaces);

        }catch (Exception e){
            e.getCause();
            Log.d("execSQL", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
