package gor.gettplaces.model;

import android.location.Location;
import android.util.Log;

import java.util.List;

import gor.gettplaces.service.CurrentLocationEvent;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;


/**
 * Created by gor on 11/05/2017.
 */

public class LocationModelImpl implements ILocationModel {

    //==============================================================================================
    //                              Constants
    //==============================================================================================
    private static final String TAG = LocationModelImpl.class.getSimpleName();

    //==============================================================================================
    //                              Privates
    //==============================================================================================
    private CurrentLocationsListener mCurrentLocationListener;
    private LocationsListener mLocationsListener;

//    private Location mCurrentLocation;
//    private List<Location> mLocationsList;

    //==============================================================================================
    //                              Constructors
    //==============================================================================================
    public LocationModelImpl(){
        Log.d(TAG,"LocationModelImpl - C-tor");

        CurrentLocationEvent.CURRENT_LOCATION_UPDATE.subscribe(new CurrentLocationObserver(mCurrentLocationListener));
        CurrentLocationEvent.LOCATIONS_UPDATE.subscribe((new LocationsObserver(mLocationsListener)));
    }

    //==============================================================================================
    //                              Interface  ILocationModel impl
    //==============================================================================================
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
}
