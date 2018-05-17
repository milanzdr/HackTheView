package com.eventiotic.hacktheview.utils;

public class Utils {

    public static double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }
    public static double radiansToDegrees(double radians) {
        return radians * 180/Math.PI;
    }
    public static double distanceInKmBetweenEarthCoordinates(double lat1, double lon1, double lat2, double lon2) {
        double earthRadiusKm = 6371.00;

        double dLat = degreesToRadians(lat2-lat1);
        double dLon = degreesToRadians(lon2-lon1);

        lat1 = degreesToRadians(lat1);
        lat2 = degreesToRadians(lat2);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadiusKm * c;
    }
    public static double getAzimuth(double azimuth) {
        if(azimuth<0) {
            return 360+azimuth;
        } else {
            return azimuth;
        }
    }
}
