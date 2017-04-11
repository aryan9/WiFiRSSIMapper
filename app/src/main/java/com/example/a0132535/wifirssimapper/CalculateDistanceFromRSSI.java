package com.example.a0132535.wifirssimapper;

/**
 * Created by a0229883 on 07-Apr-17.
 */

import android.util.Log;
import java.util.Arrays;

import static java.lang.Math.E;
import static java.lang.Math.pow;

class RSSIinformation {
    public double distanceA;
    public double distanceB;
    public double distanceC;

    public double pointA1;
    public double pointA2;

    public double pointB1;
    public double pointB2;

    public double pointC1;
    public double pointC2;

}

class APClass {
    public double distance;
    public double x;
    public double y;
}

public final class CalculateDistanceFromRSSI {
    public  static final String TAG = "MYTAG";

    public static double getDistance(double SignalStrength){

        double distance, A, B;
        A = -11.69429855;
        B = -13.00358878;

        distance = Math.pow(E, ((SignalStrength - B)/A));

        return distance;
    }
    /**
     * It needs distanceA, distanceB, distanceC, pointA1, pointA2, pointB1, pointB2, pointC1, pointC2
     */

    public static double[] getMeetingPoints(RSSIinformation InformationObject) {

        double w,z,x,y,y2;
        double[] coord = {-10000,-10000};

        w = InformationObject.distanceA * InformationObject.distanceA - InformationObject.distanceB * InformationObject.distanceB - InformationObject.pointA1 * InformationObject.pointA1 - InformationObject.pointA2* InformationObject.pointA2 + InformationObject.pointB1 * InformationObject.pointB1 + InformationObject.pointB2 * InformationObject.pointB2;

        z = InformationObject.distanceB * InformationObject.distanceB - InformationObject.distanceC * InformationObject.distanceC - InformationObject.pointB1* InformationObject.pointB1 - InformationObject.pointB2 * InformationObject.pointB2 + InformationObject.pointC1 * InformationObject.pointC1 + InformationObject.pointC2 * InformationObject.pointC2;

        x = (w * ( InformationObject.pointC2 - InformationObject.pointB2) - z * ( InformationObject.pointB2 - InformationObject.pointA2)) / (2 * (( InformationObject.pointB1 - InformationObject.pointA1) * ( InformationObject.pointC2 - InformationObject.pointB2) - ( InformationObject.pointC1 - InformationObject.pointB1) * ( InformationObject.pointB2 - InformationObject.pointA2)));

        y = (w - 2 * x * (InformationObject.pointB1 - InformationObject.pointA1)) / (2 * ( InformationObject.pointB2 - InformationObject.pointA2));

        String num2 = Double.toString(y);
        Log.w(TAG, num2);

        y2 = (z - 2 * x * ( InformationObject.pointC1 -InformationObject.pointB1)) / (2 * ( InformationObject.pointC2 - InformationObject.pointB2));

        y = (y + y2) / 2;

        coord[0] = x;
        coord[1] = y;

        String num1 = Double.toString(x);
        //String num1 = Double.toHexString(pointB1);
        Log.w(TAG, num1);

        return coord;

    }
}
