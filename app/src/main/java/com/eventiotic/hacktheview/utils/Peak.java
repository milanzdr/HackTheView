package com.eventiotic.hacktheview.utils;

import java.math.BigInteger;

public class Peak {
    BigInteger id;
    Double lat;
    Double lon;
    Tags tags;

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
}
