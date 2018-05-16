package com.eventiotic.hacktheview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eventiotic.hacktheview.utils.Peak;
import com.eventiotic.hacktheview.utils.PeakListAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    private TextView mLocationInfoTextView;
    private RecyclerView mPeakListRecyclerView;
    public static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    private Location curLocation;
    String baseUrl="https://www.overpass-api.de/api/interpreter";
    String url;
    private static final String TAG = "HackTheView";
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            this.doStuff();
        }
    }

    private void doStuff() {
        //Toast.makeText(this, "All good from onCreate", Toast.LENGTH_SHORT).show();
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        curLocation = this.getLastKnownLocation(locationManager);
        mLocationInfoTextView = (TextView) findViewById(R.id.locationInfoTextView);
        DecimalFormat numberFormat = new DecimalFormat("#.0000");
        mLocationInfoTextView.setText("Location fixed. Lat: "+numberFormat.format(curLocation.getLatitude())+". Long: "+numberFormat.format(curLocation.getLongitude()));

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                curLocation = location;
                mLocationInfoTextView.setText("Latitude: "+curLocation.getLatitude()+". Longitude: "+curLocation.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        url=baseUrl+"?data=[out:json];node(around:20000,%20"+curLocation.getLatitude()+",%20"+curLocation.getLongitude()+")[natural=peak];out;";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Log.i(TAG, "Pre requesta");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, url);
                if (response != null) {
                    //Log.i(TAG, "Dobio response");
                    JSONArray jsonArray = response.optJSONArray("elements");
                    if (jsonArray != null) {
                        int resultCount = jsonArray.length();
                        if (resultCount > 0) {
                            Gson gson = new Gson();
                            Peak[] peaks = gson.fromJson(jsonArray.toString(), Peak[].class);
                            showPeaks(peaks);
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG", error.toString());
            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    private void showPeaks(Peak[] peaks) {
        mPeakListRecyclerView = (RecyclerView) findViewById(R.id.peakListRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mPeakListRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mPeakListRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new PeakListAdapter(peaks);
        mPeakListRecyclerView.setAdapter(mAdapter);
    }

    private Location getLastKnownLocation(LocationManager mLocationManager) {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            //Log.d("last known location, provider: %s, location: %s", provider,l);

            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                //Log.d("found best last known location: %s", l);
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "All good from onRequest", Toast.LENGTH_SHORT).show();
                    this.doStuff();
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }






}
