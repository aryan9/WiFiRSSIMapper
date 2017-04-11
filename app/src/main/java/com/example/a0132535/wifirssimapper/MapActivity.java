package com.example.a0132535.wifirssimapper;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

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
        IV.setBackgroundColor(Color.rgb(250, 0, 0));

        IV1 = (ImageView)findViewById(R.id.SQ16);
        IV1.setBackgroundColor(Color.rgb(0, 250, 0));
    }
}
