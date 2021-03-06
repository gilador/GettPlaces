package gor.gettplaces;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

import gor.gettplaces.network.pojo.places.Geometry;

/**
 * Created by gor on 10/05/2017.
 */

public class Utils {
    public static boolean isLocationPermissionGranted(Context ctx) {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return false;
        }
        return true;
    }

    public static LatLng convertGeoToLocation(@NonNull Geometry geometry) {
        LatLng location = new LatLng(geometry.getLocation().getLat(),geometry.getLocation().getLng());
        return location;
    }

    public static LatLng convertGeoToLocation(gor.gettplaces.network.pojo.geoLocation.Geometry geometry) {
        LatLng location = new LatLng(geometry.getLocation().getLat(),geometry.getLocation().getLng());
        return location;
    }

    public static LatLng convertGeoToLocation(Location lastLocation) {
        LatLng location = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
        return location;
    }
}
