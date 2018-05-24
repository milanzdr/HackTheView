package com.eventiotic.hacktheview;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eventiotic.hacktheview.models.OSMNode;
import com.eventiotic.hacktheview.utils.OSMNodeListAdapter;
import com.eventiotic.hacktheview.utils.Utils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class OSMNodesViewActivity extends AppCompatActivity implements SensorEventListener {

    private TextView mLocationInfoTextView;
    private TextView mListTitle;
    private SensorManager mSensorManager;
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];
    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];
    private String locText;
    private RecyclerView mListRecyclerView;
    public static final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    private Location curLocation;
    private String baseUrl="";
    private String url="";
    private String TAG="";
    private RecyclerView.LayoutManager mLayoutManager;
    private OSMNodeListAdapter mAdapter;
    private double locAzimuth;
    private double curRotation;
    private int viewAngle;
    private int viewRadius;
    private List<OSMNode> foundnodes;
    Runnable updateTimer;
    public final Handler updateHandler = new Handler();
    private String nodeType="";
    private SeekBar mSeekBarViewAngle;
    private SeekBar mSeekBarViewRadius;
    private TextView mTxtViewParams;


    public void openCameraView(View view){
        Intent intent = new Intent(this, CameraViewActivity.class);
        startActivity(intent);
    }


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
        mSensorManager.getRotationMatrix(mRotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);
        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
        curRotation=Utils.radiansToDegrees(mOrientationAngles[0]);
    }

    private void showOSMNodes(List<OSMNode> foundnodes) {
        mListRecyclerView = (RecyclerView) findViewById(R.id.peakListRecyclerView);
        mListRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mListRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new OSMNodeListAdapter(foundnodes);
        mAdapter.setNodeType(nodeType);
        mListRecyclerView.setAdapter(mAdapter);
    }

    public void updateCurAngleForNodes() {
        if(foundnodes!=null) {
            for (OSMNode peak : foundnodes) {
                double angle = peak.getAz() - curRotation;
                if (Math.abs(angle) > 180) {
                    if(angle>0) {
                        angle = 360 - angle;
                    } else {
                        angle = -(360 - Math.abs(angle));
                    }
                }
                peak.setCurAngle(angle);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            this.baseUrl = extras.getString("baseUrl");
            this.TAG = extras.getString("tag");
            this.viewRadius = extras.getInt("viewRadius");
            this.viewAngle = extras.getInt("viewAngle");
            this.nodeType = extras.getString("nodeType");

        } else {
            Log.i(TAG, "Extras is null");
        }

        updateTimer = new Runnable() {
            @Override
            public void run() {
                DecimalFormat azFormat = new DecimalFormat("#.0");
                mLocationInfoTextView.setText(locText+ azFormat.format(Utils.getAzimuth(curRotation)));
                updateCurAngleForNodes();
                if (mAdapter != null && foundnodes != null) {
                        Collections.sort(foundnodes);
                        mAdapter.updateData(foundnodes, viewAngle, viewRadius);
                }
                updateHandler.postDelayed(this, 1000);
            }
        };
        updateHandler.postDelayed(updateTimer, 500);
        setContentView(R.layout.activity_osmnodes_view);
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
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        curLocation = this.getLastKnownLocation(locationManager);
        mLocationInfoTextView = (TextView) findViewById(R.id.locationInfoTextView);
        mTxtViewParams = (TextView) findViewById(R.id.txtViewParams);
        mTxtViewParams.setText("View radius:"+viewRadius/1000+"km, View angle: "+viewAngle+" deg.");
        mSeekBarViewAngle=(SeekBar) findViewById(R.id.seekBarViewAngle);
        mSeekBarViewAngle.setMax(180);
        mSeekBarViewAngle.setProgress(viewAngle);
        mSeekBarViewAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewAngle=progress;
                mTxtViewParams.setText("View radius:"+viewRadius/1000+"km, View angle: "+viewAngle+" deg.");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        mSeekBarViewRadius=(SeekBar) findViewById(R.id.seekBarViewRadius);
        mSeekBarViewRadius.setMax(viewRadius/1000);
        mSeekBarViewRadius.setProgress(viewRadius/1000);
        mSeekBarViewRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewRadius=progress*1000;
                mTxtViewParams.setText("View radius:"+viewRadius/1000+"km, View angle: "+viewAngle+" deg.");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        DecimalFormat numberFormat = new DecimalFormat("#.0000");
        this.locText="Location fixed. Lat: "+numberFormat.format(curLocation.getLatitude())+". Long: "+numberFormat.format(curLocation.getLongitude())+". Azimuth: ";
        mLocationInfoTextView.setText(this.locText);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                curLocation = location;
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


        String mTitle="";
        if(nodeType.equals("peak") || nodeType.equals("saddle") || nodeType.equals("cave_entrance") || nodeType.equals("wood") || nodeType.equals("water")) {
            url = baseUrl + "?data=[out:json];node(around:" + viewRadius + ",%20" + curLocation.getLatitude() + ",%20" + curLocation.getLongitude() + ")[natural="+nodeType+"];out;";
            if(nodeType.equals("peak")) {
                mTitle="Peaks";
            } else if(nodeType.equals("cave_entrance")) {
                mTitle="Caves";
            }else if(nodeType.equals("water")) {
                mTitle="Lakes or ponds";
            }
        } else if (nodeType.equals("waterfall")) {
            url= baseUrl + "?data=[out:json];node(around:" + viewRadius + ",%20" + curLocation.getLatitude() + ",%20" + curLocation.getLongitude() + ")[waterway=waterfall];out;";
            mTitle="waterfalls";
        } else if (nodeType.equals("spring")) {
            url= baseUrl + "?data=[out:json];(node(around:" + viewRadius + ",%20" + curLocation.getLatitude() + ",%20" + curLocation.getLongitude() + ")[natural=spring];node(around:" + viewRadius + ",%20" + curLocation.getLatitude() + ",%20" + curLocation.getLongitude() + ")[natural=hot_spring];node(around:" + viewRadius + ",%20" + curLocation.getLatitude() + ",%20" + curLocation.getLongitude() + ")[natural=geyser];);out;";
            mTitle="Springs";
        } else if(nodeType.equals("settlement")) {
            url= baseUrl + "?data=[out:json];(node(around:" + viewRadius + ",%20" + curLocation.getLatitude() + ",%20" + curLocation.getLongitude() + ")[place=town];node(around:" + viewRadius + ",%20" + curLocation.getLatitude() + ",%20" + curLocation.getLongitude() + ")[place=village];node(around:" + viewRadius + ",%20" + curLocation.getLatitude() + ",%20" + curLocation.getLongitude() + ")[place=hamlet];node(around:" + viewRadius + ",%20" + curLocation.getLatitude() + ",%20" + curLocation.getLongitude() + ")[place=isolated_dwelling];);out;";
            mTitle="Populated places";
        } else if(nodeType.equals("other")) {
            url= baseUrl + "?data=[out:json];(node(around:" + viewRadius + ",%20" + curLocation.getLatitude() + ",%20" + curLocation.getLongitude() + ")[tourism=alpine_hut];node(around:" + viewRadius + ",%20" + curLocation.getLatitude() + ",%20" + curLocation.getLongitude() + ")[natural=saddle];node(around:" + viewRadius + ",%20" + curLocation.getLatitude() + ",%20" + curLocation.getLongitude() + ")[natural=forest];node(around:" + viewRadius + ",%20" + curLocation.getLatitude() + ",%20" + curLocation.getLongitude() + ")[tourism=picnic_site];node(around:" + viewRadius + ",%20" + curLocation.getLatitude() + ",%20" + curLocation.getLongitude() + ")[tourism=wilderness_hut];node(around:" + viewRadius + ",%20" + curLocation.getLatitude() +",%20" + curLocation.getLongitude() + ")[waterway=dam];);out;";
            mTitle="Other locations";
        }

        mListTitle = (TextView) findViewById(R.id.peakListTitle);
        mListTitle.setText(mTitle+" in this direction");

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    JSONArray jsonArray = response.optJSONArray("elements");
                    if (jsonArray != null) {
                        int resultCount = jsonArray.length();
                        if (resultCount > 0) {
                            Gson gson = new Gson();
                            foundnodes = Arrays.asList(gson.fromJson(jsonArray.toString(), OSMNode[].class));
                            updateOSMNodes();
                            showOSMNodes(foundnodes);

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

    private void updateOSMNodes() {
        List<OSMNode> ps = new ArrayList<OSMNode>();
        for (OSMNode n: foundnodes) {
            n.setDistance(curLocation);
            n.setAz(curLocation);
            ps.add(n);
        }
        this.foundnodes=ps;
    }



    private Location getLastKnownLocation(LocationManager mLocationManager) {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
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
                    this.doStuff();
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }






}
