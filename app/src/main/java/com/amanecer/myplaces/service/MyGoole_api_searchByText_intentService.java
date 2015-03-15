package com.amanecer.myplaces.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amanecer.myplaces.database.Api_constant;
import com.amanecer.myplaces.database.Constant;
import com.amanecer.myplaces.database.DBHandler;
import com.amanecer.myplaces.recivers.MyGoogle_api_searchByText_reciver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by amanecer on 27/01/2015.
 */
public class MyGoole_api_searchByText_intentService extends IntentService {

    DBHandler handler;
    Location location;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    String next_page_token;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * /*@param name Used to name the worker thread, important only for debugging.
     */
    public MyGoole_api_searchByText_intentService() {
        super("");
        handler = new DBHandler(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //erasing all the last dataa!

        sharedPreferences = getSharedPreferences(Constant.sharedPrefernces,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Intent intent1 = new Intent(MyGoogle_api_searchByText_reciver.action);

        next_page_token = intent.getStringExtra(Constant.next_page_token);
        String whatToSearch = intent.getStringExtra(Constant.whatToSearh);
        String search = "";


        if (whatToSearch!=null){
            if (!next_page_token.equals("") ){
                 search = Api_constant.googleSearchByTexh+Api_constant.next_page_tokenEqual+next_page_token+whatToSearch+Api_constant.key;
            }else {
                search = Api_constant.googleSearchByTexh+whatToSearch+Api_constant.key;
            }
            search = handler.stringWithSpaceToNoSpace(search);

            String response  = sendHttpRequest(search);

            if (response!=null){
                if (next_page_token.equals("") )
                    handler.clearTablePlaces(); // if there is no token so cear the table before parsing;
                if (googleJsonParser(response)){

                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
                }else {
                    intent.putExtra(Constant.ZERO_RESULT,Constant.ZERO_RESULT);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent1);
                }
            }
        }
    }



    private String sendHttpRequest(String urlString) {
        BufferedReader input = null;
        HttpURLConnection httpCon = null;
        InputStream input_stream =null;
        InputStreamReader input_stream_reader = null;
        StringBuilder response = new StringBuilder();
        try{
            URL url = new URL(urlString);
            httpCon = (HttpURLConnection)url.openConnection();
            if(httpCon.getResponseCode()!= HttpURLConnection.HTTP_OK){
                Log.e("tag", "Cannot Connect to : " + urlString);
                return null;
            }

            input_stream = httpCon.getInputStream();
            input_stream_reader = new InputStreamReader(input_stream);
            input = new BufferedReader(input_stream_reader);
            String line ;
            while ((line = input.readLine())!= null){
                response.append(line +"\n");
            }



        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(input!=null){
                try {
                    input_stream_reader.close();
                    input_stream.close();
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(httpCon != null){
                    httpCon.disconnect();
                }
            }
        }
        return response.toString();
    }

    private Bitmap downloadImage(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            InputStream is = httpCon.getInputStream();
            int fileLength = httpCon.getContentLength();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead, totalBytesRead = 0;
            byte[] data = new byte[2048];
            // mDialog.setMax(fileLength);
            // Read the image bytes in chunks of 2048 bytes
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
                totalBytesRead += nRead;
                //publishProgress(totalBytesRead);
            }
            buffer.flush();
            byte[] image = buffer.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean googleJsonParser(String response){
       // handler.clearTablePlaces();
        /* going to have;
        formatted_address :TEXT;   the adress
        location :lad lng double;
        icon : TEXT;
        id : TEXT;
        name:TEXT;                  the name
        open_now : BOOLEAN;
        place_id : TEXT;
        types : TEXT array;

        can have :
        rating :double;
        */

        boolean flag = false;
        ContentValues v=new ContentValues();



        try {
            JSONObject object = new JSONObject(response);
            String flagResult = object.getString(Api_constant.status);
            if (flagResult.equals(Api_constant.ZERO_RESULTS)){
                Log.d("response","there is no resulat. zero result");
                editor.putString(Api_constant.next_page_token, null).apply();
                return flag;

            }

            try {
                //thisSearchPageToken is the string for next search to give the rest of the token;
                String thisSearchPageToken = object.getString(Api_constant.next_page_token);
                editor.putString(Api_constant.next_page_token, thisSearchPageToken).apply();
            }catch (Exception e){
                e.getCause();
                editor.putString(Api_constant.next_page_token, null).apply();
            }

            JSONArray array = object.getJSONArray(Api_constant.results);
            for (int i = 0; i < array.length(); i++) {
                JSONObject one = array.getJSONObject(i);

                try {
                    JSONObject geometry = one.getJSONObject(Api_constant.geometry);
                    JSONObject location = geometry.getJSONObject(Api_constant.location);

                    double lat = location.getDouble(Api_constant.lat);
                    double lng = location.getDouble(Api_constant.lng);



                    v.put(Constant.lat,lat);
                    v.put(Constant.lng,lng);
                }catch (JSONException e){
                    e.getCause();

                }

                String iconPATH =one.getString(Api_constant.icon);
                String name = one.getString(Api_constant.name);
                String place_id = one.getString(Api_constant.place_id);
                String formatted_address = one.getString(Api_constant.formatted_address);


                v.put(Api_constant.iconPATH,iconPATH);
                v.put(Api_constant.name,name);
                v.put(Api_constant.place_id,place_id);
                v.put(Api_constant.formatted_address,formatted_address);

                try {
                    JSONObject opening_hours = one.getJSONObject(Api_constant.opening_hours);
                    boolean open_now = opening_hours.getBoolean(Api_constant.open_now);
                    v.put(Api_constant.open_now,open_now);

                }catch (JSONException e){
                    e.getCause();
                    Log.d("open now : "," don't know");
                }

                JSONArray types = one.getJSONArray(Api_constant.types);
                String[] thetypes=new String[types.length()];
                String typeInString ="";
                for (int j = 0; j < types.length(); j++) {
                    thetypes[j]=types.getString(j);
                    typeInString+=thetypes[j]+",";
                }
                v.put(Api_constant.types,typeInString);




                flag = true;
                try {
                    if (!next_page_token.equals("")){
                        if( handler.uniqueCheck(name,Constant.places) == false );
                            continue;
                    }
                    handler.insertNew(Constant.places,v);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("handler.insertNew(Constant.places,v);"," failed");
                }
            }
        }catch (JSONException e){
            e.getCause();

        }

        return flag;
    }


}
