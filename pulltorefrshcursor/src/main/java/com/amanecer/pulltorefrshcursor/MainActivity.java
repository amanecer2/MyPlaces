package com.amanecer.pulltorefrshcursor;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;


public class MainActivity extends ActionBarActivity {

     CursorAdapter adapter;
     private PullToRefreshListView mPullRefreshListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Cursor cursor = null;

       /* String[] colum = new String[]{
          cursor.
        };*/

        mPullRefreshListView = (PullToRefreshListView)findViewById(R.id.pull_refresh_list);

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MainActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                // Do work to refresh the list here.
                new GetData().execute();
            }
        });






    }
    public class GetData extends AsyncTask<Void,Void,String> {
        //pull to refresh wating and do something
        @Override
        protected String doInBackground(Void... params) {
            try {

                //wait and do your show
                Thread.sleep(2000);

            }catch (InterruptedException e){
                e.getCause();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            //mListItems.add("shahar added");
            //mAdapter.notifyDataSetChanged();
            //list.add(new Obj("shahahaha","leeeeviviiv","nummmmmm"));
            //------- get the location in the start or open massage to open setting if gps is off;
           //callback.onGetMyLocation_btn();
            //---------------------------------
            //do the serach again if your are
            /*if (isSearchByText){
                callback.onSerachByTextBtn_startService(whatToSearch);
            }else {
                type = typeToSearch.get(listPosition);
                callback.onSearchNearMeBtn_startService(type,radius,0,true);
            }
*/
            //myBaseAdapter.notifyDataSetChanged();
            mPullRefreshListView.onRefreshComplete();
            super.onPostExecute(s);


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
