package com.example.projectanika;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService
{
    private String Tag = "Problem...";
    protected ResultReceiver receiver;
    private List<Address> addresses = null;
    private String errorMsg = "";

    public FetchAddressIntentService() {
        super("Geo-coding Service");
    }
    @Override
    protected void onHandleIntent(Intent intent)
    {
        if(intent == null) //To avoid null pointer exception
            return;

        Location location = intent.getParcelableExtra("LOCATION");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try
        {
            //Actual Geo-coding here
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        }
        catch(IOException ioException)
        {
            errorMsg = "Service not available";
            Log.e(Tag, errorMsg, ioException);
        }
        catch(IllegalArgumentException illegalargexp)
        {
            errorMsg = "Invalid Co-ordinate";
            Log.e(Tag, errorMsg, illegalargexp);
        }

        //Data sending back to main Activity
        if(addresses == null || addresses.size() == 0) //To avoid null pointer exception
        {
            errorMsg += "No address found";
            Log.e(Tag, errorMsg);
        }
        else
        {
            Address address = addresses.get(0);
            ArrayList<String> addressFrags = new ArrayList<String>();
            for(int i = 0 ; i <= address.getMaxAddressLineIndex(); i++)
            {
                addressFrags.add(address.getAddressLine(i));
            }
            Bundle bundle = new Bundle();
            bundle.putString("RESULT", TextUtils.join(".", addressFrags));

            //Sending back data wrapping into Receiver of main Activity
            receiver = intent.getParcelableExtra("RECEIVER");
            receiver.send(1, bundle);
        }
    }
}