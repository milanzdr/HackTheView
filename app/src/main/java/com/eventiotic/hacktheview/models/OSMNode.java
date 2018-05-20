package com.eventiotic.hacktheview.models;

import android.location.Location;

import com.eventiotic.hacktheview.utils.Utils;

import java.math.BigInteger;

public class OSMNode implements Comparable<OSMNode> {
    BigInteger id;
    Double lat;
    Double lon;
    OSMNodeTags tags;
    double az;
    //float angleloc;
    double distance;
    double curAngle;


    @Override
    public int compareTo(OSMNode o) {
        double ca=Math.abs(this.getDistance());
        double cax=Math.abs(o.getDistance());
        //Log.i("", "Uslo u compareTo");
        if(ca>cax) {
            return 1;
        } else if (ca<=cax) {
            return -1;
        }
        return 0;
    }

    public Double getLat() {
        return lat;
    }
    public Double getLon() {
        return lon;
    }
    public BigInteger getId() {
        return id;
    }

    public OSMNodeTags getTags() {
        return tags;
    }

    public double getAz() {
        return az;
    }

    public void setCurAngle(double curAngle) {
        this.curAngle = curAngle;
    }

    public double getCurAngle() {
        return curAngle;
    }

    public void setAz(Location l) {
        double dy, dx;
        dy=this.getLon()-l.getLongitude();
        dx=this.getLat()-l.getLatitude();
        double a = Utils.radiansToDegrees(Math.atan(Math.abs(dy/dx)));
        if(dx<0 && dy>0) {
            a = 180-a;
        } else if(dx<0 && dy<0) {
            a = 180+a;
        } else if(dx>0 && dy<0) {
            a=360-a;
        }
        this.az=a;
    }

    public void setDistance(Location l) {
        this.distance=Utils.distanceInKmBetweenEarthCoordinates(l.getLatitude(), l.getLongitude(), this.getLat(), this.getLon());
    }

    public double getDistance() {
        return distance;
    }
}
