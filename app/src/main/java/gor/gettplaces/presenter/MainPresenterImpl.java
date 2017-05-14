package gor.gettplaces.presenter;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import gor.gettplaces.model.ILocationModel;
import gor.gettplaces.network.pojo.address.Prediction;
import gor.gettplaces.network.pojo.geoLocation.Geometry;
import gor.gettplaces.network.pojo.places.Result;
import gor.gettplaces.view.MainView;

/**
 * Created by gor on 10/05/2017.
 */

public class MainPresenterImpl implements MainPresenter<MainView>{

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
        mLocationModel.setOnStartLocationUpdateListener(this);
        mLocationModel.setOnLocationsUpdateListener(this);
        mLocationModel.setOnAddressLookUpCompleteListener(this);
        mLocationModel.setGeocodingCompleteListener(this);

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
    public void onSearchForAddress(String address) {
        mLocationModel.lookForAddress(address);
        //Todo show spinner
    }

    @Override
    public void onSuggestionֻSelected(Prediction predictionSuggestion) {
        mView.closeSearchDialog();
        mLocationModel.translateAddressToGeo(predictionSuggestion.getDescription());
    }

    @Override
    public void onMapReady(Context ctx) {
        mLocationModel.init(ctx);
    }

    @Override
    public void onGpsButtonClick() {
        mLocationModel.loadAutoUpdateCurrentLocation();
    }


    //==============================================================================================
    //                              Interface ILocationModel.LocationsListener impl
    //==============================================================================================
    @Override
    public void onLocationsLoaded(List<Result> locationList) {
        mView.onLocationsUpdate(locationList);
    }

    //==============================================================================================
    //                              Interface ILocationModel.StartLocationsListener impl
    //==============================================================================================
    @Override
    public void onStartLocationLoaded(LatLng startLocation) {
        mView.onStartLocationUpdate(startLocation);
        mLocationModel.loadSurroundingLocations(startLocation);
    }

    //==============================================================================================
    //                              Interface ILocationModel.AddressLookUpCompleteListener impl
    //==============================================================================================
    @Override
    public void onAddressLookComplete(List<Prediction> suggestions) {
        mView.onAddressLookupComplete(suggestions);
    }

    //==============================================================================================
    //                              Interface ILocationModel. GeocodingCompleteListener impl
    //==============================================================================================
    @Override
    public void onGeocodingComplete(Geometry geometry) {
        mLocationModel.loadManualLocation(geometry);
    }
}
