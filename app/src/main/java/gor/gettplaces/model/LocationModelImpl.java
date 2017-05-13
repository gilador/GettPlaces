package gor.gettplaces.model;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import gor.gettplaces.service.CurrentLocationEvent;
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
    private CurrentLocationsListener mCurrentLocationListener;
    private LocationsListener mLocationsListener;
    private GoogleApiClient mGoogleApiClient;

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
        CurrentLocationEvent.CURRENT_LOCATION_UPDATE.subscribe(new CurrentLocationObserver(mCurrentLocationListener));
        CurrentLocationEvent.LOCATIONS_UPDATE.subscribe((new LocationsObserver(mLocationsListener)));

        LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        askForLastKnownLocation(ctx);
        mGoogleApiClient.connect();
//        Location location = getLastKnonw
//        CurrentLocationEvent.CURRENT_LOCATION_UPDATE.onNext(location);
    }

    @Override
    public void finish() {
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onLocationsUpdate(LocationsListener locationsListener) {
        Log.d(TAG,"onLocationsUpdate");
        mLocationsListener = locationsListener;
    }

    @Override
    public void onCurrentLocationUpdate(CurrentLocationsListener listener) {
        Log.d(TAG,"onCurrentLocationUpdate");
        mCurrentLocationListener = listener;
    }

    //==============================================================================================
    //                              Private Class CurrentLocationObserver
    //==============================================================================================
    private static class CurrentLocationObserver implements Observer{

        private CurrentLocationsListener mCurrentLocationListener;

        CurrentLocationObserver(CurrentLocationsListener currentLocationListener){
            mCurrentLocationListener = currentLocationListener;
        }
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.d(TAG,"onSubscribe");

        }

        @Override
        public void onNext(@NonNull Object location) {
            Log.d(TAG,"OnNext");

            mCurrentLocationListener.onCurrentLocationLoaded((Location) location);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d(TAG,"onError");

        }

        @Override
        public void onComplete() {
            Log.d(TAG,"OnComplete");

        }
    }

    //==============================================================================================
    //                              Private Class LocationsObserver
    //==============================================================================================
    private static class LocationsObserver implements Observer{

        private LocationsListener mLocationsListener;

        LocationsObserver(LocationsListener locationsListener){
            mLocationsListener = locationsListener;
        }
        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.d(TAG,"onSubscribe");

        }

        @Override
        public void onNext(@NonNull Object locations) {
            Log.d(TAG,"OnNext");
            mLocationsListener.onLocationsLoaded((List<Location>) locations);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d(TAG,"onError");

        }

        @Override
        public void onComplete() {
            Log.d(TAG,"OnComplete");

        }
    }

    //==============================================================================================
    //                              interface ConnectionCallbacks impl
    //==============================================================================================

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG,"onConnected - last location available");
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (lastLocation != null) {
            CurrentLocationEvent.CURRENT_LOCATION_UPDATE.onNext(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"onConnectionSuspended");

    }

    @Override
    public void onConnectionFailed(@android.support.annotation.NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"onConnectionFailed");

    }

    //==============================================================================================
    //                              Private
    //==============================================================================================
    private void askForLastKnownLocation(Context ctx) {
// Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(ctx)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }
}
