package gor.gettplaces.presenter;

import android.location.Location;
import android.util.Log;

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

    }

    //==============================================================================================
    //                              Interface MainPresenter impl
    //==============================================================================================
    @Override
    public void getCurrentLocation() {
        mLocationModel.onCurrentLocationUpdate(this);
    }

    @Override
    public void getLocations() {
        mLocationModel.onLocationsUpdate(this);
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
