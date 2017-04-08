package com.example.a0132535.wifirssimapper;

/**
 * Created by a0229883 on 07-Apr-17.
 */

import android.util.Log;
import java.util.Arrays;

import static java.lang.Math.pow;

public final class CalculateDistanceFromRSSI {
    public  static final String TAG = "MYTAG";
    public static void printVal(){
        int A[] = {-58,-57,-56,-58,-59,-56,-56,-57,-57,-57};
        int B[] = {-73,-81,-80,-81,-84,-83,-88,-82,-84,-87};
        int C[] = {-77,-86,-82,-85,-83,-88,-83,-85,-85,-84};

        Arrays.sort(A);
        // Arrays.sort(B);
        Arrays.sort(C);


        for(int i=0; i<10; i++) {
            double logval, exponent, result;
            logval = Math.log10(2416);
            logval *= 20;
            exponent = ((27.55 - logval+B[i])/20);
            result = Math.pow(10, exponent);
            String num = Double.toString(result);
            Log.w(TAG, num);
        }

    }
}
