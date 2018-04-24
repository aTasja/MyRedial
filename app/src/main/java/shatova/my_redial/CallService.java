package shatova.my_redial;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallService extends Service {

    String TAG = "myLOG";
    String mLastState;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SERVICE on Create");
    }

    /**
     * This no-op method is necessary since MusicService is a
     * so-called "Started Service".
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Hook method called every time startService() is called with an
     * Intent associated with this MusicService.
     */
    @Override
    public int onStartCommand(Intent intent,
                              int flags,
                              int startid) {

        Bundle extras = intent.getExtras();
        if (extras != null && (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)){
            String number = intent.getStringExtra("number");
            Log.d(TAG, "call intent received RINGING to " + number);
            getApplicationContext().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number)));
        }

        /*
         * Use PhoneStateListener instances for listening to phone state
         */
/*
        TelephonyManager mIncomingCallsReceiver = (TelephonyManager)getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (mIncomingCallsReceiver != null) {
            mIncomingCallsReceiver.listen(new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    //Log.d(TAG, "PHONE STATE RADIO broadcast received = " + state);
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
                break;
                    } mLastState = state;
                }


            }, PhoneStateListener.LISTEN_CALL_STATE);
        }
*/
        // Restart Service if it shuts down.
        return START_STICKY;
    }
}
