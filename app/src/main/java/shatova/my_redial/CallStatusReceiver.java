package shatova.my_redial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;


public class CallStatusReceiver extends BroadcastReceiver {


    String TAG = "myLOG";

    public void onReceive(final Context context, Intent intent) {

        //Log.d(TAG, "Intent ACTION = " + intent.getAction());

        if (intent.getAction()!=null && intent.getAction().equals("android.intent.action.PHONE_STATE")) {

            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals("IDLE")) {
                Log.d(TAG, "phone listener = " + "IDLE --> redial");
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("possibleToMakeNextCall"));

            } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals("OFFHOOK")) {
                Log.d(TAG, "phone listener = " + "OFFHOOK");
            }
        }


    }


}