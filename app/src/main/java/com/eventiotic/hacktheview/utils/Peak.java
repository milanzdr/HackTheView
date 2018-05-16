package com.eventiotic.hacktheview.utils;

import java.math.BigInteger;

public class Peak {
    BigInteger id;
    Double lat;
    Double lon;
    Tags tags;
    double az;
    float angleloc;

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

    public void setAz(double a) {
        this.az=a;
    }
}
