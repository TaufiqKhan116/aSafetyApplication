package com.example.projectanika;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    private static FusedLocationProviderClient flpc;
    private static AddressResultReceiver resultReceiver;
    private static Location lastLoc = null;
    private static SharedPreferences sharedPref;
    private static SmsManager smsMng;

    //private static TextView currentNumber;
    //private static EditText newNumber;
    private static TextView tv;
    private static TextView addrsTxt;
    private static TextView info;
    private static Button addBtn;
    private static Button call;
    private static Button touch;
    private static Button bt;

    //private static String number = "";
    //private static String Key = "key";
    private static String address;
    private static String str;
    private static String array[] = new String[4];
    private static double Latitude, Longitude;
    //private static double Latitude;
    //private static double Longitude;

    //Receiving Geo-coded data from service
    class AddressResultReceiver extends ResultReceiver
    {

        public AddressResultReceiver(Handler handler)
        {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData)
        {
            if(resultData == null) //To avoid null pointer exception
                return;

            String addressOut = resultData.getString("RESULT");

            if(addressOut != null) //To avoid null pointer exception
            {
                address = addressOut;
            }
            else
                Toast.makeText(MainActivity.this, "Can't get location", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Permission Check during installation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            String [] permissions = {Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET};
            ActivityCompat.requestPermissions(this, permissions, 97);
        }

        //newNumber     = (EditText) findViewById(R.id.enterNumber);
        //currentNumber = (TextView) findViewById(R.id.currentNumber);
        //sharedPref    = getPreferences(MODE_PRIVATE);
        //Variables allocation
        flpc          = LocationServices.getFusedLocationProviderClient(this);
        sharedPref    = getSharedPreferences("numberCol", MODE_PRIVATE);
        addrsTxt      = (TextView) findViewById(R.id.textView2);
        tv            = (TextView) findViewById(R.id.textView);
        addBtn        = (Button) findViewById(R.id.addBtn);
        call          = (Button) findViewById(R.id.call);
        touch         = (Button) findViewById(R.id.touch);
        bt            = (Button) findViewById(R.id.button);
        info          = (TextView) findViewById(R.id.info);

        //Loading saved data
        //loadData();

        //Saving data
        //Starting adding Contacts Activity
        addBtn.setOnClickListener(v->
        {
            /*
            String data;
            data = newNumber.getText().toString();
            //for saving data
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(MainActivity.Key, data);
            editor.commit();

            newNumber.setText("");
            Toast.makeText(MainActivity.this, "Number added", Toast.LENGTH_SHORT).show();
            loadData(currentNumber);
             */
            Intent intent = new Intent(MainActivity.this, Main3Activity.class);
            startActivity(intent);
        });

        //Last known location of device
        flpc.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>(){
            @Override
            public void onSuccess(Location location)
            {
                MainActivity.lastLoc = location;
                MainActivity.Latitude = location.getLatitude();
                MainActivity.Longitude = location.getLongitude();
                if(location != null) //To avoid null pointer exception
                {
                    str = "Latitude :" + location.getLatitude() + "\nLongitude :" + location.getLongitude();
                    startIntentService(); //Starting the service for Geo-coding
                }
            }
        });

        //Location callback
        LocationCallback locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null) //To avoid null pointer exception
                    return;
                for(Location loc : locationResult.getLocations())
                {
                    MainActivity.lastLoc = loc;
                    MainActivity.Latitude = loc.getLatitude();
                    MainActivity.Longitude = loc.getLongitude();
                    str = "Latitude :" + loc.getLatitude() + "\nLongitude :" + loc.getLongitude();
                }
            }
        };

        //Location request
        LocationRequest locReq = LocationRequest.create();//Instantiating location request
        locReq.setInterval(5000);
        locReq.setFastestInterval(3000);
        locReq.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        bt.setOnClickListener(e->
            {
                tv.setText(str);
                addrsTxt.setText(address);
                flpc.requestLocationUpdates(locReq, locationCallback, null);

                //Messaging segment
                smsMng = SmsManager.getDefault();
                //if(number.length() <= 0)
                  //  Toast.makeText(MainActivity.this, "No number added", Toast.LENGTH_LONG).show();
                //if(number.length() < 11)
                  //  Toast.makeText(MainActivity.this, "Number too short", Toast.LENGTH_LONG).show();
                //else
                if(str != null)
                {
                    new Thread(() ->
                    {
                        String url = "\nhttps://www.google.com/maps/place/" + Double.toString(MainActivity.Latitude) +  "," + Double.toString(MainActivity.Longitude) ;
                        loadData();
                        /*
                        if (array[0] != "")
                            smsMng.sendTextMessage(array[0], null, "I am in danger. It's an emergency!!! I need help.\nMy location :\n" + str + "\nI'm now at :\n" + address, null, null);
                        if (array[1] != "")
                            smsMng.sendTextMessage(array[1], null, "I am in danger. It's an emergency!!! I need help.\nMy location :\n" + str + "\nI'm now at :\n" + address , null, null);
                        if (array[2] != "")
                            smsMng.sendTextMessage(array[2], null, "I am in danger. It's an emergency!!! I need help.\nMy location :\n" + str + "\nI'm now at :\n" + address , null, null);
                        if (array[3] != "")
                            smsMng.sendTextMessage(array[3], null, "I am in danger. It's an emergency!!! I need help.\nMy location :\n" + str + "\nI'm now at :\n" + address , null, null);
                        */
                        if (array[0] != "")
                            smsMng.sendTextMessage(array[0], null, "I am in danger. It's an emergency!!!\nLocation :" + url + "\nAt :\n" + address, null, null);
                        if (array[1] != "")
                            smsMng.sendTextMessage(array[1], null, "I am in danger. It's an emergency!!!\nLocation :" + url + "\nAt :\n" + address , null, null);
                        if (array[2] != "")
                            smsMng.sendTextMessage(array[2], null, "I am in danger. It's an emergency!!!\nLocation :" + url + "\nAt :\n" + address , null, null);
                        if (array[3] != "")
                            smsMng.sendTextMessage(array[3], null, "I am in danger. It's an emergency!!!\nLocation :" + url + "\nAt :\n" + address , null, null);

                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this, "Message(s) sending", Toast.LENGTH_SHORT).show());
                    }).start();
                }
            }
        );

        //Caller segment
        call.setOnClickListener(e->
        {
            Intent caller = new Intent(Intent.ACTION_CALL);
            caller.setData(Uri.parse("tel:01972064882"));
            startActivity(caller);
        });

        touch.setOnTouchListener((view, event)->
        {
            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN :
                {
                    tv.setText(str);
                    addrsTxt.setText(address);
                    flpc.requestLocationUpdates(locReq, locationCallback, null);
                    SmsManager smsMng = SmsManager.getDefault();
                    /*
                    if(number.length() <= 0)
                        Toast.makeText(MainActivity.this, "No number added", Toast.LENGTH_LONG).show();
                    else
                        smsMng.sendTextMessage(number, null, str + "\n" + address + "\n:-)", null, null);
                    break;
                     */
                    /*
                    smsMng.sendTextMessage(array[0], null, "I am in danger. It's an emergency!!! I need help.\nMy location :\n" + str + "\nI'm now at :\n" + address + "\n", null, null);
                    smsMng.sendTextMessage(array[1], null, "I am in danger. It's an emergency!!! I need help.\nMy location :\n" + str + "\nI'm now at :\n" + address + "\n", null, null);
                    smsMng.sendTextMessage(array[2], null, "I am in danger. It's an emergency!!! I need help.\nMy location :\n" + str + "\nI'm now at :\n" + address + "\n", null, null);
                    smsMng.sendTextMessage(array[3], null, "I am in danger. It's an emergency!!! I need help.\nMy location :\n" + str + "\nI'm now at :\n" + address + "\n", null, null);
                    */
                    if(str != null)
                    {
                        new Thread(() ->
                        {
                            String url = "\nhttps://www.google.com/maps/place/" + Double.toString(MainActivity.Latitude) +  "," + Double.toString(MainActivity.Longitude) ;
                            loadData();
                        /*
                        if (array[0] != "")
                            smsMng.sendTextMessage(array[0], null, "I am in danger. It's an emergency!!! I need help.\nMy location :\n" + str + "\nI'm now at :\n" + address, null, null);
                        if (array[1] != "")
                            smsMng.sendTextMessage(array[1], null, "I am in danger. It's an emergency!!! I need help.\nMy location :\n" + str + "\nI'm now at :\n" + address , null, null);
                        if (array[2] != "")
                            smsMng.sendTextMessage(array[2], null, "I am in danger. It's an emergency!!! I need help.\nMy location :\n" + str + "\nI'm now at :\n" + address , null, null);
                        if (array[3] != "")
                            smsMng.sendTextMessage(array[3], null, "I am in danger. It's an emergency!!! I need help.\nMy location :\n" + str + "\nI'm now at :\n" + address , null, null);
                        */
                            if (array[0] != "")
                                smsMng.sendTextMessage(array[0], null, "I am in danger. It's an emergency!!!\nLocation :" + url + "\nAt :\n" + address, null, null);
                            if (array[1] != "")
                                smsMng.sendTextMessage(array[1], null, "I am in danger. It's an emergency!!!\nLocation :" + url + "\nAt :\n" + address , null, null);
                            if (array[2] != "")
                                smsMng.sendTextMessage(array[2], null, "I am in danger. It's an emergency!!!\nLocation :" + url + "\nAt :\n" + address , null, null);
                            if (array[3] != "")
                                smsMng.sendTextMessage(array[3], null, "I am in danger. It's an emergency!!!\nLocation :" + url + "\nAt :\n" + address , null, null);

                            runOnUiThread(() ->
                                    Toast.makeText(MainActivity.this, "Message(s) sending", Toast.LENGTH_SHORT).show());
                        }).start();
                    }
                }
                case MotionEvent.ACTION_UP :
                {
                    Intent caller = new Intent(Intent.ACTION_CALL);
                    caller.setData(Uri.parse("tel:01972064882"));
                    startActivity(caller);
                    break;
                }
            }
            return true;
        });

        //Start info intent
        info.setOnClickListener(v -> {
            Intent inf = new Intent(MainActivity.this, Main4Activity.class);
            startActivity(inf);
        });
    }

    //Loads numbers from contact activity to this activity when needed
    private void loadData()
    {
        /*
        sharedPref = getPreferences(MODE_PRIVATE);
        number = sharedPref.getString(MainActivity.Key, "");
        obj.setText(number);
        */
        for(int i = 0; i < 4; i++)
        {
            array[i] = sharedPref.getString(Integer.toString(i+1), "");
        }
    }

    protected void startIntentService()
    {
        Handler handler = new Handler();
        resultReceiver = new AddressResultReceiver(handler);
        Intent intent = new Intent(MainActivity.this, FetchAddressIntentService.class);
        intent.putExtra("RECEIVER", resultReceiver);
        intent.putExtra("LOCATION", lastLoc);
        startService(intent);
    }
}