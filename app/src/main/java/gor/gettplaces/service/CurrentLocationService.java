package gor.gettplaces.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import gor.gettplaces.Utils;

/**
 * Created by gor on 10/05/2017.
 */

public class CurrentLocationService extends Service implements LocationListener {

    //=============================================================================================
    //                               Consts
    //=============================================================================================
    private static final long MIN_TIME_BW_UPDATES = 10;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final String TAG = CurrentLocationService.class.getSimpleName();
    private final CurrentLocationEvent mLatestLocation = CurrentLocationEvent.CURRENT_LOCATION_UPDATE;

    //=============================================================================================
    //                               Privates
    //=============================================================================================
    private LocationManager mLocationManager;

    //=============================================================================================
    //                               Service Impl
    //=============================================================================================
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStart");
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.removeUpdates(this);
        boolean isNetworkAvailable = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (Utils.isLocationPermissionGranted(this)) {
            Log.d(TAG,"onStart 1");
            try {

                if (isNetworkAvailable) {
                    Log.d(TAG,"onStart 2");

                    mLocationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                } else if (isGPSEnabled) {

                    mLocationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    return super.onStartCommand(intent, flags, startId);
                }
            } catch (SecurityException e) {
                Log.w(TAG, "cant get location updates");
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mLocationManager.removeUpdates(this);
        super.onDestroy();
    }

    //=============================================================================================
    //                               LocationListener Impl
    //=============================================================================================

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG,"onLocationChanged");
        mLatestLocation.CURRENT_LOCATION_UPDATE.onNext(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    //=============================================================================================
    //                               Private Impl
    //=============================================================================================


}


