package shatova.my_redial;

import android.Manifest;
import android.content.ComponentName;
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
    Button exitButton;

    String TAG = "myLOG";

    String number;
    int attempts = 0;
    EditText attemptsEditText;
    TextView counter;

    ComponentName component;




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
        //Log.d(TAG, "on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*if (savedInstanceState != null){
            attempts = savedInstanceState.getInt("attempts");
            call();
        }*/

        attemptsEditText = findViewById(R.id.atteptsNum);


        requestAsked = false;
        //callEnded = true;

        phoneNumber = findViewById(R.id.phone_number);
        counter = findViewById(R.id.counter);





        if (!hasPermission(getApplicationContext(), PERMISSIONS)) {
            Log.d(TAG, "Request Permissions");
            ActivityCompat.requestPermissions(this, PERMISSIONS, MY_PERMISSIONS_REQUEST);

        }//else{
            //Log.d(TAG, "PERMISSIONS GRANTED");
        //}


        callButton = findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "CALL button pressed");

                attempts = Integer.parseInt(attemptsEditText.getText().toString());
                number = phoneNumber.getText().toString();

                Intent intent = new Intent(getApplicationContext(), CallService.class);
                intent.putExtra("number", phoneNumber.getText().toString());
                intent.putExtra("attempts", attempts);

                startService(intent);

            }
        });

        exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "EXIT button pressed");
                onDestroy();
            }
        });


        //disable CallStatus Broadcast receiver declared in manifest
        component = new ComponentName(getApplication(), CallStatusReceiver.class);
        int status = getApplicationContext().getPackageManager().getComponentEnabledSetting(component);
        if(status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            Log.d(TAG, "receiver is enabled");
        } else if(status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            Log.d(TAG, "receiver is disabled");
        }

        //Enable
        getApplicationContext().getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);


    }


    public boolean hasPermission(Context context, String... permissions) {
        //Log.d(TAG, "hasPermission");
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


/*
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        Log.d(TAG, "SavedInstance === state ===");
        savedInstanceState.putInt("attempts", attempts);
    }*/

    @Override
    public void onResume() {
        Log.d(TAG, "=== onResume === ");
        super.onResume();

        String lastNumber = CallLog.Calls.getLastOutgoingCall(this);
        if (!lastNumber.isEmpty()){
            phoneNumber.setText(lastNumber);}

        //Log.d(TAG, "LAST OUTGOING!!!!!   " + lastNumber);


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        //stop CallService
        stopService(new Intent(this, CallService.class));


        //Disable
        getApplicationContext().getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
        //Enable
        //getApplicationContext().getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);



        finish();


    }





}


