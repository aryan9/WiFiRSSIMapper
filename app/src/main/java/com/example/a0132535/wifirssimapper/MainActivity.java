package com.example.a0132535.wifirssimapper;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    TextView tvScanResults, tvCurrWiFiRSSI;
    private final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 27;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    private final String TAG = "WIFI";
    boolean locationPermissionGranted = false;
    boolean wifiScanResultsReceived = true;
    RSSIDistanceDbHelper RssiDistanceDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RssiDistanceDb = new RSSIDistanceDbHelper(getApplicationContext());
        tvScanResults = (TextView) findViewById(R.id.tvScanResults);
        tvCurrWiFiRSSI = (TextView) findViewById(R.id.tvCurrWiFiRSSI);

        // Initiate wifi service manager
        mainWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // Check for wifi is disabled
        if (mainWifi.isWifiEnabled() == false)
        {
            // If wifi disabled then enable it
            Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled",
                    Toast.LENGTH_LONG).show();

            mainWifi.setWifiEnabled(true);
        }

        // wifi scaned value broadcast receiver
        receiverWifi = new WifiReceiver();

        // Register broadcast receiver
        // Broacast receiver will automatically call when number of wifi connections changed
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        final FloatingActionButton addValueToDb = (FloatingActionButton)findViewById(R.id.fabAddValueToDb);
        addValueToDb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                new Thread(new Runnable() {
                    public void run() {
                       String rssi = tvCurrWiFiRSSI.getText().toString();
                        DialogFragment dialog = new AddValuesToDb();
//                        TextView rssiValue = (TextView) findViewById(R.id.tvValueRssi);
//                        rssiValue.setText(rssi);
                        dialog.show(getSupportFragmentManager(),"missiles");
                        Log.d("Dialog", "After Dialog");
                    }
                }).start();
            }
        });

        final Button getRSSI = (Button) findViewById(R.id.buttonGetRSSI);
        getRSSI.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                new Thread(new Runnable() {
                    public void run() {
                       WifiInfo wifiInfo = mainWifi.getConnectionInfo();
                        String[] args = {Integer.toString(wifiInfo.getRssi())};
                        new UpdateGUITask().execute(args);
                        //Log.d(TAG, wifiInfo.toString());
                    }
                }).start();
            }
        });

        final Button scanWifi = (Button)findViewById(R.id.buttonScanWiFi);
        scanWifi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                new Thread(new Runnable() {
                    public void run() {
                        // a potentially  time consuming task
                        //while(true) {
                        Log.d(TAG, "Starting Scan ... ");
                        String[] args = {"Wait for Scan Results"};
                        new UpdateGUITask2().execute(args);

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
                            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                            while(!locationPermissionGranted);

                        }else{
                            //getScanningResults();
                            //do something, permission was previously granted; or legacy device
                        }
//                try{
                        if(locationPermissionGranted && wifiScanResultsReceived) {
                            wifiScanResultsReceived = false;
                            mainWifi.startScan();
                        }
                    }
                }).start();
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission
            //mWifiListener.getScanningResults();
            //mainWifi.startScan();
            locationPermissionGranted = true;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        return super.onCreateOptionsMenu(menu);
    }

//    public boolean onMenuItemSelected(int featureId, MenuItem item) {
//        mainWifi.startScan();
//        //mainText.setText("Starting Scan");
//        return super.onMenuItemSelected(featureId, item);
//    }

    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }
    // Broadcast receiver class called its receive method
    // when number of wifi connections changed

    public void Auxtask(){

    }

    class WifiReceiver extends BroadcastReceiver {

        // This method call when number of wifi connections changed
        public synchronized void onReceive(Context c, Intent intent) {

            sb = new StringBuilder();
            wifiList = mainWifi.getScanResults();
            sb.append("\n        Number Of Wifi connections :"+wifiList.size()+"\n\n");

            for(int i = 0; i < wifiList.size(); i++){

                sb.append(new Integer(i+1).toString() + ". ");
                sb.append((wifiList.get(i)).toString());
                sb.append("\n\n");
            }
            Log.d(TAG,sb.toString());
            tvScanResults.setText(sb);
            wifiScanResultsReceived = true;
        }

    }


    private class UpdateGUITask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... params) {

            //Log.d(TAG,"THIS IS IT");
            return params[0];
        }

        protected void onPostExecute(String result) {
            tvCurrWiFiRSSI.setText(result);
        }
    }
    private class UpdateGUITask2 extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... params) {

            //Log.d(TAG,"THIS IS IT");
            return params[0];
        }

        protected void onPostExecute(String result) {
            tvScanResults.setText("Wait for the results here!");
        }
    }

    private class RssiDistanceDbWriterTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                //System.out.print(params[1]);
                // Gets the data repository in write mode
                SQLiteDatabase db = RssiDistanceDb.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();

                values.put(RSSIDistanceContract.FeedEntry.COLUMN_NAME_TIMESTAMP, params[0]);
                values.put(RSSIDistanceContract.FeedEntry.COLUMN_NAME_RSSI, params[1]);
                values.put(RSSIDistanceContract.FeedEntry.COLUMN_NAME_DISTANCE, params[2]);

                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(RSSIDistanceContract.FeedEntry.TABLE_NAME, null, values);
                return "done";
            }
            catch (Exception e) {
                e.printStackTrace();
//                errorFlag = true;
//                errorInfo = "Error while logging in Search History DB.";
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }

}
