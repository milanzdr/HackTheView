package com.eventiotic.hacktheview.utils;

import android.location.Location;

import java.math.BigInteger;

public class Peak {
    BigInteger id;
    Double lat;
    Double lon;
    Tags tags;
    double az;
    float angleloc;
    double distance;

    public Double getLat() {
        return lat;
    }
    public Double getLon() {
        return lon;
    }
    public BigInteger getId() {
        return id;
    }

    public Tags getTags() {
        return tags;
    }

    public double getAz() {
        return az;
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
