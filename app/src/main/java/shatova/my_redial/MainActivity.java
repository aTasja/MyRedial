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

    int attempts = 0;
    EditText attemptsNUM;
    TextView counter;
    int intDuration = 999999999;

    String number;
    private static int mLastState;
    String phoneListener;

    /**
     * Use BroadcastReceiver instances for receiving intents
     */
    private BroadcastReceiver mReceiver = null;
    boolean callMaking;
    boolean callEnded;



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
                called = false;
                callMaking = false;
                attempts = Integer.parseInt(attemptsNUM.getText().toString());
                number = phoneNumber.getText().toString();
                Log.d(TAG, "CALL button pressed, attempts = " + attempts);
                call();
            }
        });





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
        if (!callMaking &&(ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)) {
            Log.d(TAG, "call number = " + phoneNumber.getText() + " attempts = " + attempts);

            Intent intent = new Intent(this, CallService.class);
            intent.putExtra("number", phoneNumber.getText().toString());

            startService(intent);
            Log.d(TAG, "intent to service sent + phone status " + MyPhoneStateListener.nextCall);

            // getApplicationContext().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber.getText())));
            called = true;
            callMaking  =true;

            //callEnded = false;


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
        Log.d(TAG, "=== onResume === " + "attempts = " + attempts + " phoneListener = " +  MyPhoneStateListener.nextCall + " callMaking = " + callMaking);
        super.onResume();


        ArrayList callDetails = getCallDetails(this); // [number, duration]

        if (!callDetails.isEmpty()) {
            String number = callDetails.get(0).toString();

            intDuration = Integer.parseInt(callDetails.get(1).toString());
            phoneNumber.setText(callDetails.get(0).toString());

            //Log.d(TAG, "Number === " + number + " ===");
            //Log.d(TAG, "Duration === " + intDuration + " ===");

        }
        if (attempts > 0 && called )
            if (MyPhoneStateListener.nextCall && callMaking) {
                callMaking = false;
                call();
                attempts--;
                counter.setText("Осталось " + attempts + " попыток");
                called = false;
        }

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


