package com.example.ngab_sen;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public class GetAddressTask extends AsyncTask<Location, Void, String> {
    private Context mContext;
    private OnTaskCompleted mListener;

    public GetAddressTask (Context context, OnTaskCompleted listener) {
        mContext = context;
        mListener = listener;

    }
    @Override
    protected String doInBackground(Location... locations) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        Location location = locations[0];
        List<Address> addresses = null;
        String result = "";
        try{
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        }catch (IOException e) {
            result= "Service Not Available";
        }
        if (addresses==null && addresses.size()==0){
            result = "No Address Found!";
        }else{
            Address address = addresses.get(0);
            ArrayList<String> addressParts = new ArrayList<>();
            for (int i=0;i<address.getMaxAddressLineIndex();i++){
                addressParts.add(address.getAddressLine(i));
            }
            result= TextUtils.join("\n", addressParts);
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        mListener.onTaskCompleted(s);
    }

    interface OnTaskCompleted {
        void onTaskCompleted(String result);
    }
}
