package shatova.my_redial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;


public class ServiceReceiver extends BroadcastReceiver {
    TelephonyManager telephony;
    MyPhoneStateListener phoneListener;
    String TAG = "myLOG";


    public void onReceive(Context context, Intent intent) {
        phoneListener = new MyPhoneStateListener();
        telephony = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        //Intent intentToActivity = new Intent(context, MainActivity.class);
        //intentToActivity.putExtra("CALL_STATE", phoneListener.getStatus());
        //LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        //Log.d(TAG, "Service Receiver intent = " + telephony.getCallState());

    }

    public void onDestroy() {
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
    }

}