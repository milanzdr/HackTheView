package com.eventiotic.hacktheview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SensorEventListener {


    private TextView mLocationInfoTextView;
    private SensorManager mSensorManager;
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];
    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];
    private String locText;
    private RecyclerView mPeakListRecyclerView;
    public static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    private Location curLocation;
    String baseUrl="https://www.overpass-api.de/api/interpreter";
    String url;
    private static final String TAG = "HackTheView";
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private double locAzimuth;
    private double curAzimuth;
    private double phoneAngle = 30.00;
    Peak[] peaks;

    // Get readings from accelerometer and magnetometer.
    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }
        updateOrientationAngles();
        this.curAzimuth=mOrientationAngles[0];
        float rotation=-mOrientationAngles[0] * 360 / (2 * 3.14159f);
        mLocationInfoTextView.setText(this.locText+ Float.toString(rotation));



    }

    private List<Peak> updatePeaks() {
        List<Peak> newPeaks = new ArrayList<Peak>();
        double dy;
        double dx;
        for (Peak peak: peaks) {
            dy=peak.getLon()-curLocation.getLongitude();
            dx=peak.getLat()-curLocation.getLatitude();
            double a = (Math.atan(Math.abs(dy/dx))) * 360 / (2 * 3.14159f);
            if(dx<0 && dy>0) {
                a = 180-a;
            } else if(dx<0 && dy<0) {
                a = 180+a;
            } else if(dx>0 && dy<0) {
                a=360-a;
            }
            peak.setAz(a);
            Log.i(TAG, peak.getTags().getName()+" "+ a);
            if(a-this.curAzimuth < (phoneAngle/2)) {
                newPeaks.add(peak);
            }
        }
        return newPeaks;
    }

    public void updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        mSensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        // "mRotationMatrix" now has up-to-date information.

        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);

        // "mOrientationAngles" now has up-to-date information.
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.



        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // Don't receive any more updates from either sensor.
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
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
        this.locText="Location fixed. Lat: "+numberFormat.format(curLocation.getLatitude())+". Long: "+numberFormat.format(curLocation.getLongitude())+". Azimuth: ";
        mLocationInfoTextView.setText(this.locText);

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                curLocation = location;

                //mLocationInfoTextView.setText("Latitude: "+curLocation.getLatitude()+". Longitude: "+curLocation.getLongitude());
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
                //Log.i(TAG, url);
                if (response != null) {
                    //Log.i(TAG, "Dobio response");
                    JSONArray jsonArray = response.optJSONArray("elements");
                    if (jsonArray != null) {
                        int resultCount = jsonArray.length();
                        if (resultCount > 0) {
                            Gson gson = new Gson();
                            peaks = gson.fromJson(jsonArray.toString(), Peak[].class);
                            showPeaks(peaks);
                            List<Peak> pks = updatePeaks();
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
