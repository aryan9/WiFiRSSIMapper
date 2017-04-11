package com.example.a0132535.wifirssimapper;

/**
 * Created by a0229883 on 07-Apr-17.
 */

import android.util.Log;
import java.util.Arrays;

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

public final class CalculateDistanceFromRSSI {
    public  static final String TAG = "MYTAG";
    public static void printVal(){
        int A[] = {-58,-57,-56,-58,-59,-56,-56,-57,-57,-57};
        int B[] = {-73,-81,-80,-81,-84,-83,-88,-82,-84,-87};
        int C[] = {-77,-86,-82,-85,-83,-88,-83,-85,-85,-84};

        Arrays.sort(A);
        // Arrays.sort(B);
        Arrays.sort(C);


//        for(int i=0; i<10; i++) {
//            double logval, exponent, result;
//            logval = Math.log10(2416);
//            logval *= 20;
//            exponent = ((27.55 - logval+B[i])/20);
//            result = Math.pow(10, exponent);
            //String num = Double.toString(result);
           // Log.w(TAG, num);
        //}

    }
    /**
     * It needs distanceA, distanceB, distanceC, pointA1, pointA2, pointB1, pointB2, pointC1, pointC2
     */

    public static void getMeetingPoints(RSSIinformation InformationObject) {

        double w,z,x,y,y2;
      //  double distanceA = 4.45, distanceB = 2.85 , distanceC= 4.5;

        w = InformationObject.distanceA * InformationObject.distanceA - InformationObject.distanceB * InformationObject.distanceB - InformationObject.pointA1 * InformationObject.pointA1 - InformationObject.pointA2* InformationObject.pointA2 + InformationObject.pointB1 * InformationObject.pointB1 + InformationObject.pointB2 * InformationObject.pointB2;

        z = InformationObject.distanceB * InformationObject.distanceB - InformationObject.distanceC * InformationObject.distanceC - InformationObject.pointB1* InformationObject.pointB1 - InformationObject.pointB2 * InformationObject.pointB2 + InformationObject.pointC1 * InformationObject.pointC1 + InformationObject.pointC2 * InformationObject.pointC2;

        x = (w * ( InformationObject.pointC2 - InformationObject.pointB2) - z * ( InformationObject.pointB2 - InformationObject.pointA2)) / (2 * (( InformationObject.pointB1 - InformationObject.pointA1) * ( InformationObject.pointC2 - InformationObject.pointB2) - ( InformationObject.pointC1 - InformationObject.pointB1) * ( InformationObject.pointB2 - InformationObject.pointA2)));

        y = (w - 2 * x * (InformationObject.pointB1 - InformationObject.pointA1)) / (2 * ( InformationObject.pointB2 - InformationObject.pointA2));

        String num2 = Double.toString(y);
        Log.w(TAG, num2);

        y2 = (z - 2 * x * ( InformationObject.pointC1 -InformationObject.pointB1)) / (2 * ( InformationObject.pointC2 - InformationObject.pointB2));

        y = (y + y2) / 2;

        String num1 = Double.toString(x);
        //String num1 = Double.toHexString(pointB1);
        Log.w(TAG, num1);


    }
}
