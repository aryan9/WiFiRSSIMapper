package com.example.a0132535.wifirssimapper;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by a0229883 on 05-Apr-17.
 */

public class MapActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_map);

        ImageView IV, IV1;
        IV = (ImageView)findViewById(R.id.SQ2);
        //IV.setBackgroundColor(Color.rgb(250, 0, 0));

        IV1 = (ImageView)findViewById(R.id.SQ16);
        //IV1.setBackgroundColor(Color.rgb(0, 250, 0));
        PlotCoordinates(205,305);
    }
    private int getStringResourceByName(String aString) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(aString, "id", packageName);
        return resId;
    }
    public void PlotCoordinates(double x, double y){

        int x1, y1, boxID;

        if(x > 601 || y > 601){
            Toast.makeText(MapActivity.this, "Invalid Coordinates", Toast.LENGTH_LONG );
        }
        else {
            x1 = (int)(x/100);
            y1 = (int)(y/100);

            boxID = (x1*6) + y1 + 1;

            String ViewID = "SQ" + Integer.toString(boxID);
            ImageView boxView = (ImageView) findViewById(getStringResourceByName(ViewID));
            boxView.setBackgroundColor(Color.rgb(250, 0, 0));

        }
    }
}
