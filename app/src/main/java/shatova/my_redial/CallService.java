package shatova.my_redial;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class CallService extends Service {

    String TAG = "myLOG";
    int mLastState;

    int attempts;
    String number;
    int tryCall;
    boolean nextCall;
    boolean havePermission;
    private android.content.BroadcastReceiver mReceiver;



    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SERVICE on Create");



        // Initialize a new BroadcastReceiver instance for local intents from ServiceReceiver.
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Display a notification that radio has been connected.
                makeCall();
            }
        };

        // Register Local Broadcast receiver - use to receive messages from service
        IntentFilter serviceFilter = new IntentFilter("possibleToMakeNextCall");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, serviceFilter);

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


        havePermission = (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED);

        Bundle extras = intent.getExtras();
        if (extras != null){
            number = intent.getStringExtra("number");
            attempts = intent.getIntExtra("attempts", 0);
            Log.d(TAG, "SRVICE intent received NUMBER  = " + number + " ATTEMPTS = " + attempts);
            tryCall = 1;
            makeCall();

        }

        //if (havePermission && tryCall == 1){

        //}



        // Restart Service if it shuts down.
        return START_STICKY;
    }

    private void makeCall(){
        int lastCallDuration = Integer.parseInt(getCallDetails(getApplicationContext()));
        Log.d(TAG, "Длительность последнего звонка = " + lastCallDuration);
        if  (lastCallDuration == 0) {
            if (tryCall <= attempts) {
                Log.d(TAG, "ЗВОНЮ в " + tryCall + " раз из " + attempts);
                try {
                    getApplicationContext().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number)));
                    tryCall++;
                } catch (SecurityException sEx) {
                    Log.d(TAG, "Security permission == " + sEx);
                }
            } else {
                Log.d(TAG, "SERVICE ПОПЫТКИ закончились");
                onDestroy();
            }
        }else {
            Log.d(TAG, "SERVICE Длительность последнего звонка больше 0");
        }

    }

    public static String getCallDetails(Context context) {


        // StringBuilder sb = new StringBuilder();
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {


            Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
            //int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            //int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            //sb.append("Call Details :");
            if (managedCursor.moveToPosition(0)) {
                managedCursor.moveToNext();
                managedCursor.moveToPosition(0);
                //String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                //String callDate = managedCursor.getString(date);
                //Date callDayTime = new Date(Long.valueOf(callDate));
                String callDuration = managedCursor.getString(duration);
                //String dir = null;
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        //Log.d(TAG, "OUTGOING");
                        return callDuration;

                }
            }managedCursor.close();
        }

        return "";
    }

    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "SERVICE onDestroy");
    }
}
