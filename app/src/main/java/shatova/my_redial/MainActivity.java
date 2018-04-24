package shatova.my_redial;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.aware.PublishConfig;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.ButtonBarLayout;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.util.Log;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import java.util.ArrayList;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.LogRecord;
import android.os.Handler;

public class MainActivity extends Activity {
    public final static int MY_PERMISSIONS_REQUEST = 11;

    EditText phoneNumber;
    Button callButton;

    String TAG = "myLOG";

    int attempts;
    EditText attemptsNUM;
    TextView counter;
    int intDuration = 999999999;
    boolean callEnded;

    /**
     * Use BroadcastReceiver instances for receiving intents
     */
    private BroadcastReceiver mReceiver = null;



    private boolean called;

    boolean requestAsked;
    String[] PERMISSIONS = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE,
             Manifest.permission.READ_CALL_LOG};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*if (savedInstanceState != null){
            attempts = savedInstanceState.getInt("attempts");
            call();
        }*/

        attemptsNUM = findViewById(R.id.atteptsNum);

        called = false;
        requestAsked = false;
        //callEnded = true;

        phoneNumber = findViewById(R.id.phone_number);
        counter = findViewById(R.id.counter);



        if (!hasPermission(getApplicationContext(), PERMISSIONS)) {
            Log.d(TAG, "Request Permissions");
            ActivityCompat.requestPermissions(this, PERMISSIONS, MY_PERMISSIONS_REQUEST);

        }else{
            Log.d(TAG, "PERMISSIONS GRANTED");}


        callButton = findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attempts = Integer.parseInt(attemptsNUM.getText().toString());
                Log.d(TAG, "CALL button pressed, attempts = " + attempts);
                call();
            }
        });


        // Initialize a new BroadcastReceiver instance for local intents from RadioService.
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Display a notification that radio has been connected.
                Bundle extras = intent.getExtras();
                if (extras != null){
                    String status = intent.getExtras().getString("CALL_STATE");
                    Log.d(TAG, "local intent received -- " + status);
                }




            }
        };

        // Register Local Broadcast receiver - use to receive messages from service
        IntentFilter serviceFilter = new IntentFilter("CALL_STATE");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, serviceFilter);


    }


    public boolean hasPermission(Context context, String... permissions) {
        Log.d(TAG, "hasPermission");
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void call() {
        //Intent intent = new Intent(Intent.ACTION_CALL);
        //intent.setData(Uri.parse("tel:" + phoneNumber.getText() ));
        Log.d(TAG, "call method");

        //int attemptsEditText = Integer.parseInt(attemptsNUM.getText().toString());
        if (attempts > 0 && (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)) {
            Log.d(TAG, "call number = " + phoneNumber.getText() + " attempts = " + attempts);
            getApplicationContext().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber.getText())));
            called = true;
            //Log.d(TAG, "attempts =" + attempts);
            attempts--;
            counter.setText("Осталось " + attempts + " попыток");


        }else{
            Log.d(TAG, "ATTEMPTS 0");
            Toast.makeText(this, "Количество попыток закончено", Toast.LENGTH_LONG).show();
            counter.setVisibility(View.INVISIBLE);

        }

    }
/*
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        Log.d(TAG, "SavedInstance === state ===");
        savedInstanceState.putInt("attempts", attempts);
    }*/

    @Override
    public void onResume() {
        Log.d(TAG, "=== onResume ===" + attempts + callEnded);
        super.onResume();


        ArrayList callDetails = getCallDetails(this); // [number, duration]

        if (!callDetails.isEmpty()) {
            String number = callDetails.get(0).toString();

            intDuration = Integer.parseInt(callDetails.get(1).toString());

            //Log.d(TAG, "Number === " + number + " ===");
            //Log.d(TAG, "Duration === " + intDuration + " ===");


            phoneNumber.setText(callDetails.get(0).toString());
        }
        if (callEnded && MyPhoneStateListener.callStatus.equals("OFFHOOK")){
            Log.d(TAG, "звонок начался");
            callEnded = false;
        }
        if (!callEnded){
            if (MyPhoneStateListener.callStatus == ("IDLE") && intDuration == 0) {
                Log.d(TAG, "звонок окончился");
                callEnded = true;
                call();
            }
        }



        /*
         * Use PhoneStateListener instances for listening to phone state
         *//*
        TelephonyManager mIncomingCallsReceiver = (TelephonyManager) getApplicationContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (mIncomingCallsReceiver != null) {
            mIncomingCallsReceiver.listen(new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    //Log.d(TAG, "PHONE STATE RADIO broadcast received = " + state);
                    if (state == TelephonyManager.CALL_STATE_IDLE){
                        //Log.d(TAG, "IDLE received");
                        if (called) {
                            if (intDuration == 0) {
                                Log.d(TAG, "IDLE received -- > call");
                                call();

                            }
                        }

                    }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);
        }
*/
    }



    private ArrayList getCallDetails(Context context) {

        ArrayList<String> result = new ArrayList<>();

        // StringBuilder sb = new StringBuilder();
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {


            Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            //int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            //sb.append("Call Details :");
            if (managedCursor.moveToPosition(0)) {
                //managedCursor.moveToNext();
                managedCursor.moveToPosition(0);
                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                //String callDate = managedCursor.getString(date);
                //Date callDayTime = new Date(Long.valueOf(callDate));
                String callDuration = managedCursor.getString(duration);
                //String dir = null;
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        //Log.d(TAG, "OUTGOING");
                        result.add(phNumber);
                        result.add(callDuration);
                        break;
                }
            }managedCursor.close();
        }

        return result;
    }
}


