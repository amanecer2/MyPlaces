package com.amanecer.myplaces.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amanecer.myplaces.R;
import com.amanecer.myplaces.database.Api_constant;
import com.amanecer.myplaces.database.Constant;
import com.amanecer.myplaces.obj.Row_obj;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by amanecer on 10/02/2015.
 */
public class MyBaseAdapter extends BaseAdapter {
    private SharedPreferences sharedPreferences;
    private Activity activity;
    private ArrayList<Row_obj> list;
    /*private DownloadImageTask downloadImageTask;
    private DistanceBetwenn distanceBetwenn;*/
   private String desLat;
   private String desLng;

    private static Double _MilesToKilometers = 1.609344;
    private static Double _KmToMiles = 0.8684;
    Xmls xmls;
    public MyBaseAdapter(Activity activity, ArrayList<Row_obj> list) {
        this.activity = activity;
        this.list = list;
        sharedPreferences = activity.getSharedPreferences(Constant.sharedPrefernces, Context.MODE_PRIVATE);


    }




    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Xmls xmls ;
        DownloadImageTask downloadImageTask;
        DistanceBetwenn distanceBetwenn;

        downloadImageTask = new DownloadImageTask(activity);
        distanceBetwenn = new DistanceBetwenn(activity);

        Row_obj obj = list.get(position);
        if (convertView==null){
            xmls = new Xmls();

            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.costume_row,parent,false);


            xmls.placeName = (TextView)convertView.findViewById(R.id.placeName);
            xmls.placeAdress = (TextView)convertView.findViewById(R.id.placeAdress);
            xmls.placeDistance = (TextView)convertView.findViewById(R.id.placeDistance);
            xmls.layout = (LinearLayout)convertView.findViewById(R.id.costume_linearLayout);
            convertView.setTag(xmls);

        }else {
            xmls =(Xmls)convertView.getTag();


        }

        try{



            new DownloadImageTask(activity).execute(obj.getPATH());
            xmls.placeName.setText(obj.getPlaceName());
            xmls.placeAdress.setText(obj.getPlaceAdress());
            String mileOrKm = sharedPreferences.getString(Constant.milesOrKm,Constant.km);

            if (mileOrKm.equals(Constant.km)){
                xmls.placeDistance.setText( new DecimalFormat("#.##").format(obj.getDistanceInMeters())+" "+Constant.km);
            }else {
                xmls.placeDistance.setText(new DecimalFormat("#.##").format( obj.getDistanceInMeters() * _KmToMiles )+" "+Constant.miles);
            }



        }catch (Exception e){
            e.getCause();
        }

        return convertView;
    }




    public static class Xmls{

        public  TextView placeName;
        public TextView placeAdress;
        public TextView placeDistance;
        public  ImageView placePATH;
        public LinearLayout layout;
    }



    public class DistanceBetwenn extends AsyncTask<String,Integer,String>{
/////////// this class was supposed to be going to google distance api and get the distance..
        // and insted i just used a method for calculting ;
        private Activity activity;

        public DistanceBetwenn(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(String... params) {
            String oriLat = params[0];
            String oriLng = params[1];
             desLat = params[2];
             desLng = params[3];
            String googleDistance = Api_constant.googleMatrixAndOrigin+oriLat+","+oriLng+Api_constant.googleMatrixDestantion+desLat+","+desLng;
            String resopnse = sendHttpRequest(googleDistance);
            if (resopnse!=null){
                return resopnse;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TextView dis = (TextView)activity.findViewById(R.id.placeDistance);
            if (s!=null){
                int value = distanceParser(s);
                double inMeters = (double)value/1000;

                String mileOrKm = sharedPreferences.getString(Constant.milesOrKm,null);
                if (mileOrKm != null){
                    if (mileOrKm.equals(Constant.miles))
                        dis.setText("" + inMeters * _KmToMiles   + Resources.getSystem().getString(R.string.miles));
                    else
                        dis.setText("" + inMeters  + Resources.getSystem().getString(R.string.km));
                }


            }else {
                //dis.setText(desLat+","+desLng);
                dis.setText("shah"+desLat+","+desLng);
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
    public class DownloadImageTask extends AsyncTask<String, Integer,Bitmap>
    {

        private Activity mActivity;
        //private ProgressDialog mDialog;

        DownloadImageTask(Activity activity) {
            mActivity = activity;

        }

        protected Bitmap doInBackground(String... urls) {
            Log.d("doInBackground", "starting download of image");
            Bitmap image = downloadImage(urls[0]);

            return image;
        }





        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                ImageView imageView = (ImageView)activity.findViewById(R.id.placePATH);
                if (imageView!=null)
                    imageView.setImageBitmap(result);
            }
            else {

            }
            // Close the progress dialog
            //mDialog.dismiss();
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
                   // publishProgress(totalBytesRead);
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

    }

    public int distanceParser(String response){
        boolean flag = false;
        try {
            JSONObject object = new JSONObject(response);
            JSONArray rows = object.getJSONArray(Api_constant.rows);
            JSONObject one = rows.getJSONObject(0);
            JSONArray elements = one.getJSONArray(Api_constant.elements);
            JSONObject oneElement = elements.getJSONObject(0);
            JSONObject distance = oneElement.getJSONObject(Api_constant.distance);
            int value = distance.getInt(Api_constant.value);
            return value;

        }catch (Exception e){
            e.getCause();
        }





        return -1;
    }

}
