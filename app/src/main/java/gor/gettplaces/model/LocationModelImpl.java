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
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import gor.gettplaces.R;
import gor.gettplaces.Utils;
import gor.gettplaces.bus.AddressEventBus;
import gor.gettplaces.bus.LocationEventBus;
import gor.gettplaces.network.pojo.address.AddressLookUpResponse;
import gor.gettplaces.network.pojo.address.Prediction;
import gor.gettplaces.network.pojo.geoLocation.GeoLocationRespone;
import gor.gettplaces.network.pojo.geoLocation.Geometry;
import gor.gettplaces.network.pojo.places.NearBySearchResponse;
import gor.gettplaces.network.pojo.places.Result;
import gor.gettplaces.network.service.PlacesService;
import gor.gettplaces.service.CurrentLocationService;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by gor on 11/05/2017.
 */

public class LocationModelImpl implements ILocationModel, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //==============================================================================================
    //                              Constants
    //==============================================================================================
    private static final String TAG = LocationModelImpl.class.getSimpleName();
    private final PlacesService mService;

    //==============================================================================================
    //                              Privates
    //==============================================================================================
    private AddressLookUpCompleteListener mAddressLookUpCompleteListener;
    private GeocodingCompleteListener mGeocodingCompleteListener;
    private StartLocationsListener mStartLocationListener;
    private LocationsListener mLocationsListener;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;

    //==============================================================================================
    //                              Constructors
    //==============================================================================================
    public LocationModelImpl() {
        Log.d(TAG, "LocationModelImpl - C-tor");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PlacesService.SERVICE_ENDPOINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mService = retrofit.create(PlacesService.class);

    }

    //==============================================================================================
    //                              Interface  ILocationModel impl
    //==============================================================================================
    @Override
    public void init(Context ctx) {
        Log.d(TAG, "init");
        mContext = ctx;
        LocationEventBus.START_LOCATION_UPDATE.subscribe(new CurrentLocationObserver(mStartLocationListener));
        LocationEventBus.LOCATIONS_UPDATE.subscribe((new LocationsObserver(mLocationsListener)));
        LocationEventBus.GEOCODING_UPDATE.subscribe((new GeocodingObserver(mGeocodingCompleteListener)));
        AddressEventBus.ADDRESS_LOOKUP_UPDATE.subscribe((new AddressLookupObserver(mAddressLookUpCompleteListener)));
        askForLastKnownLocation(ctx);
        loadAutoUpdateCurrentLocation();
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
    //Default setup - look at "init()"
    public void loadAutoUpdateCurrentLocation() {
        publishLastKnownLocation();
        mContext.startService(new Intent(mContext, CurrentLocationService.class));
    }

    @Override
    public void loadManualLocation(Geometry geometry) {
        mContext.stopService(new Intent(mContext, CurrentLocationService.class));
        publishStartLocation(Utils.convertGeoToLocation(geometry));
    }

    @Override
    public void loadSurroundingLocations(LatLng location) {
        getNearByPlaces(location);
    }

    @Override
    public void translateAddressToGeo(String address) {
        String apiKey = mContext.getString(R.string.GOOGLE_MAPS_API_KEY);
        mService.getGeo(apiKey, address)
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<GeoLocationRespone, Geometry>() {
                    @Override
                    public Geometry apply(@NonNull GeoLocationRespone geoLocationRespone) throws Exception {
                        return geoLocationRespone.getResults().get(0).getGeometry();
                    }
                })
                .subscribe(new GeocodingObserver(mGeocodingCompleteListener));
    }

    @Override
    public void lookForAddress(String addressPrefix) {
        String apiKey = mContext.getString(R.string.GOOGLE_MAPS_API_KEY);
        mService.getAddress(apiKey, addressPrefix)
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<AddressLookUpResponse, List<Prediction>>() {
                    @Override
                    public List<Prediction> apply(@NonNull AddressLookUpResponse addressLookUpResponse) throws Exception {
                        return addressLookUpResponse.getPredictions();
                    }
                })
                .subscribe(new AddressLookupObserver(mAddressLookUpCompleteListener));
    }

    @Override
    public void setOnLocationsUpdateListener(LocationsListener locationsListener) {
        Log.d(TAG, "setOnLocationsUpdateListener");
        mLocationsListener = locationsListener;
    }

    @Override
    public void setOnStartLocationUpdateListener(StartLocationsListener listener) {
        Log.d(TAG, "setOnStartLocationUpdateListener");
        mStartLocationListener = listener;
    }

    @Override
    public void setOnAddressLookUpCompleteListener(AddressLookUpCompleteListener onAddressLookUpCompleteListener) {
        Log.d(TAG, "setOnAddressLookUpCompleteListener");
        mAddressLookUpCompleteListener = onAddressLookUpCompleteListener;

    }

    @Override
    public void setGeocodingCompleteListener(GeocodingCompleteListener geocodingCompleteListener) {
        Log.d(TAG, "setGeocodingCompleteListener");
        mGeocodingCompleteListener = geocodingCompleteListener;
    }

    //==============================================================================================
    //                              Interface  GoogleApiClient.ConnectionCallbacks impl
    //==============================================================================================
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected - last location available");
        publishLastKnownLocation();
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

    private void getNearByPlaces(LatLng location) {
        String apiKey = mContext.getString(R.string.GOOGLE_MAPS_API_KEY);
        String latLng = location.latitude + "," + location.longitude;
        mService.getNearByPlaces(apiKey, latLng, PlacesService.DEFAULT_RADIUS)
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<NearBySearchResponse, List<Result>>() {
                    @Override
                    public List<Result> apply(@NonNull NearBySearchResponse nearBySearchResponse) throws Exception {
                        return nearBySearchResponse.getResults();
                    }
                })
                .subscribe(new LocationsObserver(mLocationsListener));
    }

    private void publishStartLocation(LatLng location) {
        if(location != Utils.convertGeoToLocation(getLastKnownLocation())) {
            LocationEventBus.START_LOCATION_UPDATE.onNext(location);
        }
    }

    private void publishLastKnownLocation() {
        Location lastLocation = getLastKnownLocation();

        if (lastLocation != null) {
            publishStartLocation(Utils.convertGeoToLocation(lastLocation));
        }
    }

    @Nullable
    private Location getLastKnownLocation() {
        Location lastLocation = null;

        if (mGoogleApiClient.isConnected()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }
        return lastLocation;
    }

    //==============================================================================================
    //                              Private Class CurrentLocationObserver
    //==============================================================================================
    private static class CurrentLocationObserver implements Observer<LatLng> {

        private StartLocationsListener mCurrentLocationListener;

        CurrentLocationObserver(StartLocationsListener currentLocationListener) {
            mCurrentLocationListener = currentLocationListener;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.d(TAG, "CurrentLocationObserver: onSubscribe");

        }

        @Override
        public void onNext(@NonNull LatLng location) {
            Log.d(TAG, "CurrentLocationObserver: OnNext, location: " + location.latitude + "," + location.longitude);

            mCurrentLocationListener.onStartLocationLoaded(location);
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
    private static class LocationsObserver implements Observer<List<Result>> {

        private LocationsListener mLocationsListener;

        LocationsObserver(LocationsListener locationsListener) {
            mLocationsListener = locationsListener;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.d(TAG, "LocationsObserver: onSubscribe");

        }

        @Override
        public void onNext(@NonNull List<Result> locations) {
            Log.d(TAG, "LocationsObserver: OnNext");
            mLocationsListener.onLocationsLoaded(locations);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d(TAG, "LocationsObserver: onError" + e);

        }

        @Override
        public void onComplete() {
            Log.d(TAG, "LocationsObserver: OnComplete");

        }

    }

    //==============================================================================================
    //                              Private Class AddressLookupObserver
    //==============================================================================================
    private static class AddressLookupObserver implements Observer<List<Prediction>> {

        private AddressLookUpCompleteListener mAddressLookUpCompleteListener;

        AddressLookupObserver(AddressLookUpCompleteListener addressLookUpCompleteListener) {
            mAddressLookUpCompleteListener = addressLookUpCompleteListener;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.d(TAG, "AddressLookUpCompleteListener: onSubscribe");

        }

        @Override
        public void onNext(@NonNull List<Prediction> addresses) {
            Log.d(TAG, "AddressLookUpCompleteListener: OnNext");
            mAddressLookUpCompleteListener.onAddressLookComplete(addresses);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d(TAG, "AddressLookUpCompleteListener: onError" + e);

        }

        @Override
        public void onComplete() {
            Log.d(TAG, "AddressLookUpCompleteListener: OnComplete");

        }
    }

    //==============================================================================================
    //                              Private Class AddressLookupObserver
    //==============================================================================================
    private static class GeocodingObserver implements Observer<Geometry> {

        private GeocodingCompleteListener mGeocodingCompleteListener;

        GeocodingObserver(GeocodingCompleteListener geocodingCompleteListener) {
            mGeocodingCompleteListener = geocodingCompleteListener;
        }

        @Override
        public void onSubscribe(@NonNull Disposable d) {
            Log.d(TAG, "GeocodingObserver: onSubscribe");

        }

        @Override
        public void onNext(@NonNull Geometry geometry) {
            Log.d(TAG, "GeocodingObserver: OnNext");
            mGeocodingCompleteListener.onGeocodingComplete(geometry);
        }

        @Override
        public void onError(@NonNull Throwable e) {
            Log.d(TAG, "GeocodingObserver: onError" + e);

        }

        @Override
        public void onComplete() {
            Log.d(TAG, "GeocodingObserver: OnComplete");

        }
    }
}
