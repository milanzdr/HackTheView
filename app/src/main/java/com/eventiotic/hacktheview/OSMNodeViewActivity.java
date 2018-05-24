package com.eventiotic.hacktheview;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.eventiotic.hacktheview.models.OSMNode;
import com.eventiotic.hacktheview.models.OSMNodeTags;
import com.eventiotic.hacktheview.utils.Utils;

import java.text.DecimalFormat;
import java.util.List;

public class OSMNodeViewActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];
    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];
    private double curRotation;
    private OSMNode currentNode = new OSMNode();
    Runnable updateTimer;
    public final Handler updateHandler = new Handler();
    private Location curLocation;
    private TextView mLocationInfoTextView;
    private String locText;
    private final DecimalFormat azFormat = new DecimalFormat("#.0");
    private final DecimalFormat numberFormat = new DecimalFormat("#.0000");
    TextView txtNodeType;
    TextView txtTitle;
    TextView txtDistance;
    TextView txtLatitude;
    TextView txtLongitude;
    TextView txtAz;
    ImageView imgDir;
    String nodeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osmnode_view);
        Bundle extras = getIntent().getExtras();

        ImageView icon = (ImageView) findViewById(R.id.imgNodeType);


        OSMNodeTags currentNodeTags = new OSMNodeTags();
        if(extras!=null) {
            currentNodeTags.setName(extras.getString("osmname"));
            nodeType=extras.getString("osmtype");
            Log.i("", nodeType);
            currentNodeTags.setNodeType(extras.getString("osmtype"));
            currentNodeTags.setSource(extras.getString("osmsource"));
            currentNodeTags.setWikidata(extras.getString("osmwikidata"));
            currentNodeTags.setWikipedia(extras.getString("osmwikipedia"));
            currentNodeTags.setAccess(extras.getString("osmaccess"));
            currentNodeTags.setFee(extras.getString("osmfee"));
            currentNodeTags.setDescription(extras.getString("osmdesc"));
            currentNodeTags.setPopulation(extras.getString("osmpop"));

            currentNode.setTags(currentNodeTags);
            currentNode.setLat(extras.getDouble("osmlat"));
            currentNode.setLon(extras.getDouble("osmlon"));

        }
        mLocationInfoTextView = (TextView) findViewById(R.id.txtPosition);
        txtNodeType=(TextView) findViewById(R.id.txtNodeType);
        txtTitle=(TextView) findViewById(R.id.txtNodeName);
        txtDistance=(TextView) findViewById(R.id.txtDistance);
        txtAz=(TextView) findViewById(R.id.txtAz);
        txtLongitude=(TextView) findViewById(R.id.txtLon);
        txtLatitude=(TextView) findViewById(R.id.txtLat);
        imgDir=(ImageView) findViewById(R.id.pointerImage);

        if(nodeType.equals("peak")) {
            icon.setImageResource(R.drawable.ic_peak);
        } else if(nodeType.equals("spring")) {
            icon.setImageResource(R.drawable.ic_spring);
        } else if(nodeType.equals("waterfall")) {
            icon.setImageResource(R.drawable.ic_waterfall);
        } else if(nodeType.equals("cave_entrance")) {
            icon.setImageResource(R.drawable.ic_cave);
        } else if(nodeType.equals("lake")) {
            icon.setImageResource(R.drawable.ic_lake);
        } else if(nodeType.equals("settlement")) {
            icon.setImageResource(R.drawable.ic_settlement);
        }  else if(nodeType.equals("dam")) {
            icon.setImageResource(R.drawable.ic_dam);
        }  else if(nodeType.equals("forest")) {
            icon.setImageResource(R.drawable.ic_forest);
        }  else if(nodeType.equals("picnic_site")) {
            icon.setImageResource(R.drawable.ic_picnic_site);
        }  else if(nodeType.equals("saddle")) {
            icon.setImageResource(R.drawable.ic_saddle);
        }  else if(nodeType.equals("alpine_hut") || nodeType.equals("wilderness_hut")) {
            icon.setImageResource(R.drawable.ic_hut);
        }

        txtNodeType.setText(nodeType);
        String nm = currentNode.getTags().getName();
        if (nm.equals("")) nm="Unnamed";
        txtTitle.setText(nm);
        txtLongitude.setText(numberFormat.format(currentNode.getLon()));
        txtLatitude.setText(numberFormat.format(currentNode.getLat()));

        updateTimer = new Runnable() {
            @Override
            public void run() {
                locText="Location fixed. Lat: "+numberFormat.format(curLocation.getLatitude())+". Long: "+numberFormat.format(curLocation.getLongitude())+". Azimuth: ";
                mLocationInfoTextView.setText(locText+ azFormat.format(Utils.getAzimuth(curRotation)));
                updateCurAngleForNode();
                if(curLocation!=null) {
                    currentNode.setDistance(curLocation);
                    currentNode.setAz(curLocation);
                    txtAz.setText(azFormat.format(currentNode.getCurAngle())+"deg");
                    txtDistance.setText(azFormat.format(currentNode.getDistance())+"km");
                    if(Math.abs(currentNode.getCurAngle())<5) {
                        imgDir.setImageResource(R.drawable.ic_navigation_center);
                    } else if(Math.abs(currentNode.getCurAngle())>175) {
                        imgDir.setImageResource(R.drawable.ic_navigation_bottom);
                    }
                    else {
                        if (currentNode.getCurAngle()<0) {
                            if(currentNode.getCurAngle()<-90) {
                                imgDir.setImageResource(R.drawable.ic_navigation_left_bottom);
                            } else {
                                imgDir.setImageResource(R.drawable.ic_navigation_left);
                            }
                        } else if (currentNode.getCurAngle()>0) {
                            if(currentNode.getCurAngle()>90) {
                                imgDir.setImageResource(R.drawable.ic_navigation_right_bottom);
                            } else {
                                imgDir.setImageResource(R.drawable.ic_navigation_right);
                            }
                        }
                    }
                }
                updateHandler.postDelayed(this, 1000);
            }
        };
        updateHandler.postDelayed(updateTimer, 500);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        curLocation = this.getLastKnownLocation(locationManager);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                curLocation = location;
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

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

    public void updateCurAngleForNode() {
        if(currentNode!=null) {
                double angle = currentNode.getAz() - curRotation;
                if (Math.abs(angle) > 180) {
                    if(angle>0) {
                        angle = 360 - angle;
                    } else {
                        angle = -(360 - Math.abs(angle));
                    }
                }
                currentNode.setCurAngle(angle);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
        curRotation= Utils.radiansToDegrees(mOrientationAngles[0]);
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
}
