package com.amanecer.myplaces.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.FloatMath;
import android.util.Log;

import com.amanecer.myplaces.obj.LatLng;
import com.amanecer.myplaces.obj.Row_obj;

import java.util.ArrayList;

/**
 * Created by amanecer on 13/01/2015.
 */
public class DBHandler {

    DBHelper helper;
    public static final double _MetersToFeet = 3.2808399;
    public static final double _FeetYoMeters = 0.3048;
    public static Double _MilesToKilometers = 1.609344;
    public static Double _KmToMiles = 0.8684;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor  editor;

    public DBHandler(Context activity) {
        helper = new DBHelper(activity,Constant.dbFile,null,Constant.version);
        /*sharedPreferences = activity.getSharedPreferences(Constant.sharedPrefernces,0);
        editor = sharedPreferences.edit();*/

    }


   /* public void saveStringToShardPref(String whatToSave,String defualValue ){
        editor.putString(whatToSave,defualValue).apply();
    }

    public String getStringFromShardPref(String whatToGet, String defualValue){
        return sharedPreferences.getString(whatToGet,defualValue);
    }*/


    public ArrayList<LatLng> getLastLngLngByArrylist(){
        ArrayList<LatLng> latLng = new ArrayList<>();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(Constant.myLocationNow, null, null, null, null, null, null);

            int latColum = cursor.getColumnIndex(Api_constant.lat);
            int lngColum = cursor.getColumnIndex(Api_constant.lng);

            cursor.moveToFirst();
            double lat = cursor.getDouble(latColum);
            double lng = cursor.getDouble(lngColum);
            LatLng latLngClass = new LatLng(lat, lng);
            latLng.add(latLngClass);

            return latLng;
        }catch (Exception e){
            e.getCause();
            cursor.close();
            return null;
        }finally {
            if (db.isOpen())
                db.close();
        }
    }

    public Cursor getLastLngLngByCursor(){


        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        String[] colums = {Api_constant.lat,Api_constant.lng};
        try {
            cursor = db.query(Constant.myLocationNow, colums, null, null, null, null, null);


            return cursor;
        }catch (Exception e){
            e.getCause();
            cursor.close();
            return null;
        }
    }

    public ArrayList<Row_obj> getSpecificDetailsFilterNameTablePlaces(String name){

        Cursor cursor = getAllByCursorFilterTableName(Constant.places);

        ArrayList<Row_obj> list = new ArrayList<>();
        int nameColum = cursor.getColumnIndex(Api_constant.name );
        int columAdress = cursor.getColumnIndex(Api_constant.formatted_address );
        int columlat = cursor.getColumnIndex(Api_constant.lat );
        int columlng = cursor.getColumnIndex(Api_constant.lng );

        cursor.moveToFirst();
        while (cursor.moveToNext()){
            if (cursor.getString(nameColum).equals(name)){
                    double lat = cursor.getDouble(columlat);
                    double lng = cursor.getDouble(columlng);
                    String asress = cursor.getString(columAdress);
                    list.add(new Row_obj(name,asress,lat,lng));
                cursor.close();
                return list;
            }
        }
        cursor.close();
        return null;
    }


    public int getSpecificDetailsFilterNameTablePlacesByCursorPosition(String name){

        Cursor cursor = getAllByCursorFilterTableName(Constant.places);


        int nameColum = cursor.getColumnIndex(Api_constant.name );


        cursor.moveToFirst();
        while (cursor.moveToNext()){
            if (cursor.getString(nameColum).equals(name)){

                return cursor.getPosition();
            }
        }
        cursor.close();
        return -1;
    }


    public ArrayList<Row_obj> getRowObjFilterByAction(String table){

        ArrayList<Row_obj> list= new ArrayList<>();
        Cursor cursor = null;
        SQLiteDatabase db = helper.getReadableDatabase();// after changinh orientation this is nhull;
        Cursor latLng = null;
        double lat=0;
        double lng=0;
        try {
            latLng = db.query(Constant.myLocationNow, null, null, null, null, null, null);

            int latColum = latLng.getColumnIndex(Constant.lat);
            int lngColum = latLng.getColumnIndex(Constant.lng);

            latLng.moveToFirst();
             lat = latLng.getDouble(latColum);
             lng = latLng.getDouble(lngColum);


        /*ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs = getLastLngLngByArrylist();
        double lat = latLngs.get(0).getLat();
        double lng = latLngs.get(0).getLng();*/

        cursor = getAllByCursorFilterTableName(table);

        int placeNameColum = cursor.getColumnIndex(Api_constant.name);
        int placeAdressColum = cursor.getColumnIndex(Api_constant.formatted_address);
        int placeIconPathColum = cursor.getColumnIndex(Api_constant.iconPATH);
        int placelatColum = cursor.getColumnIndex(Api_constant.lat);
        int placelngColum = cursor.getColumnIndex(Api_constant.lng);

        cursor.moveToFirst();
        while(cursor.moveToNext()){



            double locationLat = cursor.getDouble(placelatColum);
            double locationLng = cursor.getDouble(placelngColum);
           /// float[] res = new float[0];
            // Location.distanceBetween(lat,lng,locationLat,locationLng,res);
            double disTanceInMeteters = gps2m((float)locationLat,(float)locationLng,(float)lat,(float)lng);
            double maters = Math.floor(disTanceInMeteters)/1000;
            Row_obj obj = new Row_obj();
            obj.setPlaceName(cursor.getString(placeNameColum));
            obj.setPlaceAdress(cursor.getString(placeAdressColum));
            obj.setPATH(cursor.getString(placeIconPathColum));
            obj.setPlaceDistance(locationLat + "," + locationLng);
            obj.setLng(locationLng);
            obj.setLat(locationLat);
            obj.setDistanceInMeters(maters);

            list.add(obj);
        }
        cursor.close();
        return list;
        }catch (Exception e){
            e.getCause();
        }

        return null;
    }

    public boolean onLocationReady(){
        boolean flag = true;
        try {
            ArrayList<LatLng> latLngs= new ArrayList<>();
            latLngs = getLastLngLngByArrylist();
            double lat = latLngs.get(0).getLat();
            double lng = latLngs.get(0).getLng();
        }catch (Exception e){
            return false;
        }
        return flag;
    }

    public boolean addToFavorite(String name){
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = null;
        ContentValues v = new ContentValues();
        boolean flag = false;
        cursor = getAllByCursorFilterTableName(Constant.places);
        int columName = cursor.getColumnIndex(Api_constant.name);
        int columlat = cursor.getColumnIndex(Api_constant.lat);
        int columlng = cursor.getColumnIndex(Api_constant.lng);
        int columPATH = cursor.getColumnIndex(Api_constant.iconPATH);
        int columAdress = cursor.getColumnIndex(Api_constant.formatted_address);
        int columplaceId = cursor.getColumnIndex(Api_constant.place_id);
        int columType = cursor.getColumnIndex(Api_constant.types);
        cursor.moveToFirst();
        try {
            while (cursor.moveToNext()){
                if (cursor.getString(columName).equals(name)){
                    v.put(Api_constant.name,cursor.getString(columName));
                    v.put(Api_constant.lat,cursor.getString(columlat));
                    v.put(Api_constant.lng,cursor.getString(columlng));
                    v.put(Api_constant.iconPATH,cursor.getString(columPATH));
                    v.put(Api_constant.formatted_address,cursor.getString(columAdress));
                    v.put(Api_constant.place_id,cursor.getString(columplaceId));
                    v.put(Api_constant.types,cursor.getString(columType));

                    if (uniqueCheck(cursor.getString(columName),Constant.favorite_places)){
                        insertNew(Constant.favorite_places,v);
                        return flag = true;
                    }else {
                        return flag = false;
                    }

                }
            }
        }catch (Exception e){
            e.getCause();
        }finally {
            if (db.isOpen())
                db.close();
        }
           return flag;
    }


    public boolean uniqueCheck(String name,String table){
        boolean flag = true;
        Cursor cursor = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        try {
            cursor =db.query(table,null,null,null,null,null,null);
            int columName = cursor.getColumnIndex(Api_constant.name);

            cursor.moveToFirst();
            while (cursor.moveToNext())
            {
                if (cursor.getString(columName).equals(name)){
                    cursor.close();
                    return false;
                }

            }
        }catch (Exception e){
            e.getCause();
        }
        cursor.close();
        return flag;
    }

    private double gps2m(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180/3.14169);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        float t1 = FloatMath.cos(a1)*FloatMath.cos(a2)*FloatMath.cos(b1)*FloatMath.cos(b2);
        float t2 = FloatMath.cos(a1)*FloatMath.sin(a2)*FloatMath.cos(b1)*FloatMath.sin(b2);
        float t3 = FloatMath.sin(a1)* FloatMath.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000*tt;
    }


    public String stringWithSpaceToNoSpace(String stringWithSpace){

        String[] noSpace = stringWithSpace.split(" ");
        String stringNoSpace = "";
        for (int i = 0; i < noSpace.length; i++) {
            stringNoSpace += noSpace[i]+"%20";
        }


        return stringNoSpace;
    }

    public void clearTablePlaces (){
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(Constant.places,null,null);
        }catch (Exception e){
            e.getCause();
            Log.d("clearTablePlaces", "failed");
        }finally {
            if (db.isOpen())
                db.close();

        }
    }

    public void clearTableMyLocationNow () {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(Constant.myLocationNow, null, null);
        } catch (Exception e) {
            e.getCause();
            Log.d("clearTablePlaces", "failed");
        } finally {
            if (db.isOpen())
                db.close();

        }
    }

    public void clearTableLocations (){
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(Constant.myLocations,null,null);
        }catch (Exception e){
            e.getCause();
            Log.d("clearTablePlaces", "failed");
        }finally {
            if (db.isOpen())
                db.close();

        }
    }

    public void clearTableFavorites (){
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            db.delete(Constant.favorite_places,null,null);
        }catch (Exception e){
            e.getCause();
            Log.d("clearTablePlaces", "failed");
        }finally {
            if (db.isOpen())
                db.close();

        }
    }



    public void insertNew(String tableName, ContentValues v)  {
        SQLiteDatabase db = helper.getWritableDatabase();
        boolean yes ;
        try {

            db.insert(tableName,null,v);
        }catch (Exception e){
            Log.d("insert new handler: ",e.getMessage());
        }finally {
            if (db.isOpen())
                db.close();
        }
    }

    public Cursor getAllByCursorFilterTableName(String tableName){
        Cursor cursor = null;
        SQLiteDatabase db = helper.getReadableDatabase();

         try {
                cursor= db.query(tableName,null,null,null,null,null,null);
            }catch (Exception e){
             Log.d("get All By Cursor Filter TableName",e.getMessage());
         }

        return cursor;
    }


}
