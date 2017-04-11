package com.example.a0132535.wifirssimapper;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.R.id.list;

public class MainActivity extends AppCompatActivity {

    TextView tvScanResults, tvCurrWiFiRSSI, tvFinalPoint;
    private final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 27,
            PERMISSIONS_REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 28;
    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    StringBuilder sb = new StringBuilder();
    private final String TAG = "WIFI", LOG_TAG = "ERROR";
    boolean locationPermissionGranted = false,
            writeExternalStoragePermissionGranted = false;
    boolean wifiScanResultsReceived = true;
    RSSIDistanceDbHelper RssiDistanceDb;
    CalculateDistanceFromRSSI DistanceClass;

    MapActivity mapActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RssiDistanceDb = new RSSIDistanceDbHelper(getApplicationContext());
        tvScanResults = (TextView) findViewById(R.id.tvScanResults);
        tvCurrWiFiRSSI = (TextView) findViewById(R.id.tvCurrWiFiRSSI);
        tvFinalPoint = (TextView) findViewById(R.id.tvFinalPoint);


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
                        Log.d("Dialog", "Before Dialog");
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
                        //System.out.println("Starting Scan ...");
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

        final Button collectReadings = (Button) findViewById(R.id.buttonStartCollection);
        collectReadings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            final int waitTimeMillis = 3000;
                            EditText etX = (EditText) findViewById(R.id.etValueXCoord);
                            EditText etY = (EditText) findViewById(R.id.etValueYCoord);

                            String distance = etX.getText().toString() + "," + etY.getText().toString();
                            String line = "";

                            for (int i = 0; i < 10; i++) {
                                Thread.sleep(waitTimeMillis);
                                WifiInfo wifiInfo = mainWifi.getConnectionInfo();
                                line += Integer.toString(wifiInfo.getRssi()) + (i < 9 ? "," : "");

                            }
                            WriteDataToFile(distance + "," + line + "\r\n",
                                    Integer.toString(waitTimeMillis));

                            ReadingFinishedNotify();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    }
                }).start();

            }

        });

        final Button openMap = (Button) findViewById(R.id.openmap);
        openMap.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent mapIntent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(mapIntent);
                //mapActivity.PlotCoordinates(2.5,3.5);
                //DistanceClass.printVal();

//                RSSIinformation InfoObject = new RSSIinformation();
//
//                InfoObject.distanceA =  4.45;
//                InfoObject.distanceB = 2.85;
//                InfoObject.distanceC = 4.5;
//
//                InfoObject.pointA1 = 0;
//                InfoObject.pointA2 = 0;
//
//                InfoObject.pointB1 = 0;
//                InfoObject.pointB2 = 6.0;
//
//                InfoObject.pointC1 = 6.02;
//                InfoObject.pointC2 = 6.01;
//
//
//                DistanceClass.getMeetingPoints(InfoObject);
            }
        });
    }


    public void ReadingFinishedNotify() {
        //Define Notification Manager
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

//Define sound URI
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_search)
                .setContentTitle("WiFiRssiMapper")
                .setContentText("Reading Values Finished. Ready for another round!")
                .setSound(soundUri); //This sets the sound to play

//Display notification
        notificationManager.notify(0, mBuilder.build());
    }

    private synchronized void WriteDataToFile(String data, String waitTimeMillis) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            Log.d("INFO", "This is the fuckup!");
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

        } else {
            if (isExternalStorageWritable(

            )) {
                final File path =
                        Environment.getExternalStoragePublicDirectory
                                (
                                        //Environment.DIRECTORY_PICTURES
                                        Environment.DIRECTORY_PODCASTS + "/RssiData/"
                                );

//                File file = getAlbumStorageDir(getApplicationContext(), "fileToSave");
                //Make sure the path directory exists.
                if (!path.exists()) {
                    // Make it, if it doesn't exit
                    path.mkdirs();
                }


                final File file = new File(path, waitTimeMillis + "_values.csv");

                // Save your stream, don't forget to flush() it before closing it.

                try {
                    FileOutputStream fOut = new FileOutputStream(file, true);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(data);
                    myOutWriter.close();

                    fOut.flush();
                    fOut.close();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(LOG_TAG, "Cannot write to external media!");
            }
        }


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
        } else if (requestCode == PERMISSIONS_REQUEST_CODE_WRITE_EXTERNAL_STORAGE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission
            //mWifiListener.getScanningResults();
            //mainWifi.startScan();
            writeExternalStoragePermissionGranted = true;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        return super.onCreateOptionsMenu(menu);
    }

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

    public File getAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PODCASTS), albumName);
        //String string = Environment.
        //Log.e(LOG_TAG,string.toString());
///
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    class WifiReceiver extends BroadcastReceiver {

        // This method call when number of wifi connections changed
        public synchronized void onReceive(Context c, Intent intent) {

            String[] APs = {"cc3200_1","cc3200_2","cc3200_3","cc3200_4"};
            sb = new StringBuilder();
            wifiList = mainWifi.getScanResults();
            sb.append("\n        Number Of Wifi connections :"+wifiList.size()+"\n\n");

            //APClass APObj = new APClass();
            ArrayList<APClass> APObject= new ArrayList<>();
            APObject.add(new APClass());
            APObject.add(new APClass());
            APObject.add(new APClass());
            APObject.add(new APClass());
            int discardDistanceID = 0;

            double currentMinDistance = 10000;

            for(int i = 0; i < wifiList.size(); i++){
                if(wifiList.get(i).SSID.toString().equals(APs[0]))
                {
                    sb.append(wifiList.get(i).SSID.toString() + "\n");
                    sb.append(wifiList.get(i).level + "\n");

                    APObject.get(0).distance = DistanceClass.getDistance(wifiList.get(i).level);
                    APObject.get(0).x = 0.012;
                    APObject.get(0).y = 0.012;
                    sb.append("Distance: ");
                    sb.append(new Double(APObject.get(0).distance).toString() + "\n");
                    sb.append("\n");

                    if(currentMinDistance > APObject.get(0).distance)
                    {
                        currentMinDistance = APObject.get(0).distance;
                        discardDistanceID = 0;
                    }
                }
                if(wifiList.get(i).SSID.toString().equals(APs[1]))
                {
                    sb.append(wifiList.get(i).SSID.toString() + "\n");
                    sb.append(wifiList.get(i).level + "\n");

                    APObject.get(1).distance = DistanceClass.getDistance(wifiList.get(i).level);
                    APObject.get(1).x = 145.012;
                    APObject.get(1).y = 0.01;
                    sb.append("Distance: ");
                    sb.append(new Double(APObject.get(1).distance).toString() + "\n");
                    sb.append("\n");

                    if(currentMinDistance > APObject.get(1).distance)
                    {
                        currentMinDistance = APObject.get(1).distance;
                        discardDistanceID = 1;
                    }
                }
                if(wifiList.get(i).SSID.toString().equals(APs[2]))
                {
                    sb.append(wifiList.get(i).SSID.toString() + "\n");
                    sb.append(wifiList.get(i).level + "\n");

                    APObject.get(2).distance = DistanceClass.getDistance(wifiList.get(i).level);
                    APObject.get(2).x = 145.001;
                    APObject.get(2).y = 125.001;
                    sb.append("Distance: ");
                    sb.append(new Double(APObject.get(2).distance).toString() + "\n");
                    sb.append("\n");

                    if(currentMinDistance > APObject.get(2).distance)
                    {
                        currentMinDistance = APObject.get(2).distance;
                        discardDistanceID = 2;
                    }
                }
                if(wifiList.get(i).SSID.toString().equals(APs[3]))
                {
                    sb.append(wifiList.get(i).SSID.toString() + "\n");
                    sb.append(wifiList.get(i).level + "\n");

                    APObject.get(3).distance = DistanceClass.getDistance(wifiList.get(i).level);
                    APObject.get(3).x = 0;
                    APObject.get(3).y = 125.012;
                    sb.append("Distance: ");
                    sb.append(new Double(APObject.get(3).distance).toString() + "\n");
                    sb.append("\n");

                    if(currentMinDistance > APObject.get(3).distance)
                    {
                        currentMinDistance = APObject.get(3).distance;
                        discardDistanceID = 3;
                    }
                }

            }
                APObject.remove(discardDistanceID);

            RSSIinformation InfoObject = new RSSIinformation();

            InfoObject.distanceA =  APObject.get(0).distance;
            InfoObject.distanceB = APObject.get(1).distance;
            InfoObject.distanceC = APObject.get(2).distance;

            InfoObject.pointA1 = APObject.get(0).x;
            InfoObject.pointA2 = APObject.get(0).y;

            InfoObject.pointB1 = APObject.get(1).x;
            InfoObject.pointB2 = APObject.get(1).y;

            InfoObject.pointC1 = APObject.get(2).x;
            InfoObject.pointC2 = APObject.get(2).y;


            final double[] value = DistanceClass.getMeetingPoints(InfoObject);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //btn.setText("#" + i);
                    tvFinalPoint.setText("x = " + Double.toString(value[0]) + " | y = " + Double.toString(value[1]));
                }
            });


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

            Log.d(TAG,"THIS IS IT");
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
