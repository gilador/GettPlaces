package gor.gettplaces.model;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import gor.gettplaces.network.pojo.address.Prediction;
import gor.gettplaces.network.pojo.geoLocation.Geometry;
import gor.gettplaces.network.pojo.places.Result;

/**
 * Created by gor on 10/05/2017.
 */

public interface ILocationModel {


    interface LocationsListener {
        void onLocationsLoaded(List<Result> locationList);
    }

    interface StartLocationsListener {
        void onStartLocationLoaded(LatLng startLocation);
    }

    interface AddressLookUpCompleteListener {
        void onAddressLookComplete(List<Prediction> suggestions);
    }

    interface GeocodingCompleteListener {
        void onGeocodingComplete(Geometry geometry);
    }

    void init(Context ctx);
    void finish();
    void loadAutoUpdateCurrentLocation();
    void loadManualLocation(Geometry geometry);
    void loadSurroundingLocations(LatLng location);
    void translateAddressToGeo(String address);
    void lookForAddress(String addressPrefix);
    void setOnLocationsUpdateListener(LocationsListener locationsListener);
    void setOnStartLocationUpdateListener(StartLocationsListener startLocationsListener);
    void setOnAddressLookUpCompleteListener(AddressLookUpCompleteListener onAddressLookUpCompleteListener);
    void setGeocodingCompleteListener(GeocodingCompleteListener geocodingCompleteListener);
}
