package gor.gettplaces.presenter;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;

import java.util.List;

import gor.gettplaces.model.ILocationModel;
import gor.gettplaces.view.MainView;

/**
 * Created by gor on 10/05/2017.
 */

public class MainPresenterImpl implements MainPresenter<MainView>, ILocationModel.LocationsListener, ILocationModel.StartLocationsListener {

    private static final String TAG = MainPresenterImpl.class.getSimpleName();
    //==============================================================================================
    //                              Privates
    //==============================================================================================
    private ILocationModel mLocationModel;
    private MainView mView;

    //==============================================================================================
    //                              Constructors
    //==============================================================================================
    public MainPresenterImpl(ILocationModel locationModel) {
        Log.d(TAG,"MainPresenterImpl");
        mLocationModel = locationModel;
        mLocationModel.setOnStartLocationUpdate(this);
        mLocationModel.setOnLocationsUpdate(this);

    }

    //==============================================================================================
    //                              Interface IPresenter impl
    //==============================================================================================
    @Override
    public void onResume(MainView view) {
        mView = view;
    }

    @Override
    public void onPause() {
        mLocationModel.finish();
    }

    //==============================================================================================
    //                              Interface MainPresenter impl
    //==============================================================================================
    @Override
    public void onMarkerClick(Marker theMarker){

    }

    @Override
    public void onMapReady(Context ctx) {
        mLocationModel.load(ctx);
    }


    //==============================================================================================
    //                              Interface ILocationModel.LocationsListener impl
    //==============================================================================================
    @Override
    public void onLocationsLoaded(List<Location> locationList) {
        mView.setLocations(locationList);
    }

    //==============================================================================================
    //                              Interface ILocationModel.StartLocationsListener impl
    //==============================================================================================
    @Override
    public void onStartLocationLoaded(Location currentLocation) {
        mView.setStartLocation(currentLocation);
    }
}
