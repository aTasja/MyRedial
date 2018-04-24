package shatova.my_redial;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.WebView;

public class MyPhoneStateListener extends PhoneStateListener {

    public static String callStatus = null;
    private static int mLastState;
    String TAG = "myLOG";

    public void onCallStateChanged(int state, String incomingNumber) {

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:

                if (state != mLastState){
                    Log.d(TAG, "onCallStateChange Call Ended = " + "IDLE");
                    callStatus = "IDLE"; }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:

                if (state != mLastState){
                    Log.d(TAG, "onCallStateChange Call Ended = " + "OFFHOOK");
                    callStatus = "OFFHOOK";}
                break;
            case TelephonyManager.CALL_STATE_RINGING:

                if (state != mLastState){
                    Log.d(TAG, "onCallStateChange Call Ended = " + "RINGING");
                    callStatus = "RINGING";}
                break;
        }
        mLastState = state;
    }

    public String getStatus(){
        return callStatus;
    }

}