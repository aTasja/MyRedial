package shatova.my_redial;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.WebView;

import java.util.ArrayList;

public class MyPhoneStateListener extends PhoneStateListener {

    private String callStatus;
    private static int mLastState;
    String TAG = "myLOG";
    public static boolean nextCall;

    public void onCallStateChanged(int state, String incomingNumber) {

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                if (state != mLastState) {
                    Log.d(TAG, "phone listener = " + "IDLE");
                    if (mLastState == TelephonyManager.CALL_STATE_OFFHOOK) {
                        nextCall = true;
                        Log.d(TAG, "phone listener NEXT CALL = " + nextCall);
                    }
                    callStatus = "IDLE";
                }
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (state != mLastState) {
                    callStatus = "OFFHOOK";
                    nextCall = false;
                    Log.d(TAG, "phone listener = " + callStatus);
                }
                break;

            /*case TelephonyManager.CALL_STATE_RINGING:
                if (state != mLastState){
                    Log.d(TAG, "phone listener = " + "RINGING");
                    callStatus = "RINGING";}
                break;*/
        } mLastState = state;
    }

}