package gor.gettplaces.model;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import gor.gettplaces.service.CurrentLocationEvent;
import gor.gettplaces.service.CurrentLocationService;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;


/**
 * Created by gor on 11/05/2017.
 */

public class LocationModelImpl implements ILocationModel, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //==============================================================================================
    //                              Constants
    //==============================================================================================
    private static final String TAG = LocationModelImpl.class.getSimpleName();

    //==============================================================================================
    //                              Privates
    //==============================================================================================
    private StartLocationsListener mStartLocationListener;
    private LocationsListener mLocationsListener;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;

    //==============================================================================================
    //                              Constructors
    //==============================================================================================
    public LocationModelImpl() {
        Log.d(TAG, "LocationModelImpl - C-tor");


    }

    //==============================================================================================
    //                              Interface  ILocationModel impl
    //==============================================================================================
    @Override
    public void load(Context ctx) {
        Log.d(TAG, "load");
        mContext = ctx;
        CurrentLocationEvent.START_LOCATION_UPDATE.subscribe(new CurrentLocationObserver(mStartLocationListener));
        CurrentLocationEvent.LOCATIONS_UPDATE.subscribe((new LocationsObserver(mLocationsListener)));
        askForLastKnownLocation(ctx);
        setAutoUpdateCurrentLocation();
        mGoogleApiClient.connect();
    }

    @Override
    public void finish() {
        Log.d(TAG, "finish");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    //Default setup - look at "load()"
    public void setAutoUpdateCurrentLocation() {
        mContext.startService(new Intent(mContext, CurrentLocationService.class));
    }

    @Override
    public void setManualLocation(String address) {
        mContext.stopService(new Intent(mContext, CurrentLocationService.class));
        //TODO get address position
    }

    @Override
    public void setOnLocationsUpdate(LocationsListener locationsListener) {
        Log.d(TAG, "setOnLocationsUpdate");
        mLocationsListener = locationsListener;
    }

    @Override
    public void setOnStartLocationUpdate(StartLocationsListener listener) {
        Log.d(TAG, "setOnStartLocationUpdate");
        mStartLocationListener = listener;
    }

    //==============================================================================================
    //                              Interface  ConnectionCallbacks impl
    //==============================================================================================
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected - last location available");
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (lastLocation != null) {
            publishStartLocation(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");

    }

    @Override
    public void onConnectionFailed(@android.support.annotation.NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");

    }

    //==============================================================================================
    //                              Private
    //==============================================================================================
    private void askForLastKnownLocation(Context ctx) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void publishStartLocation(Location location) {
        CurrentLocationEvent.START_LOCATION_UPDATE.onNext(location);
        getNearByPlaces(location);
    }

    private void getNearByPlaces(Location location) {

    }

    //==============================================================================================
    //                              Private Class CurrentLocationObserver
    //==============================================================================================
    private static class CurrentLocationObserver implements Observer {

        private StartLocationsListener mCurrentLocationListener;

        CurrentLocationObserver(StartLocationsListener currentLocationListener) {
            mCurrentLocationListener = currentLocationListener;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.d(TAG, "CurrentLocationObserver: onSubscribe");

        }

        @Override
        public void onNext(@NonNull Object location) {
            Log.d(TAG, "CurrentLocationObserver: OnNext");

            mCurrentLocationListener.onStartLocationLoaded((Location) location);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d(TAG, "CurrentLocationObserver: onError");

        }

        @Override
        public void onComplete() {
            Log.d(TAG, "CurrentLocationObserver: OnComplete");

        }

    }

    //==============================================================================================
    //                              Private Class LocationsObserver
    //==============================================================================================
    private static class LocationsObserver implements Observer {

        private LocationsListener mLocationsListener;

        LocationsObserver(LocationsListener locationsListener) {
            mLocationsListener = locationsListener;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.d(TAG, "LocationsObserver: onSubscribe");

        }

        @Override
        public void onNext(@NonNull Object locations) {
            Log.d(TAG, "LocationsObserver: OnNext");
            mLocationsListener.onLocationsLoaded((List<Location>) locations);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d(TAG, "LocationsObserver: onError");

        }

        @Override
        public void onComplete() {
            Log.d(TAG, "LocationsObserver: OnComplete");

        }

    }
}
