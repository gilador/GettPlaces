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

public class MainPresenterImpl implements MainPresenter<MainView>, ILocationModel.LocationsListener, ILocationModel.CurrentLocationsListener {

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
        mLocationModel.onCurrentLocationUpdate(this);
        mLocationModel.onLocationsUpdate(this);

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
        //TODO pause the model?
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
    //                              Interface ILocationModel.CurrentLocationsListener impl
    //==============================================================================================
    @Override
    public void onCurrentLocationLoaded(Location currentLocation) {
        mView.setCurrentLocation(currentLocation);
    }
}
