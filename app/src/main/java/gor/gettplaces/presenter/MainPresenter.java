package gor.gettplaces.presenter;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.Marker;

import java.util.List;

import gor.gettplaces.model.ILocationModel;
import gor.gettplaces.network.pojo.address.Prediction;
import gor.gettplaces.view.IView;

/**
 * Created by gor on 10/05/2017.
 */

public interface MainPresenter<T> extends IPresenter<T>,
        ILocationModel.LocationsListener,
        ILocationModel.StartLocationsListener,
        ILocationModel.AddressLookUpCompleteListener,
        ILocationModel.GeocodingCompleteListener {

    void onMapReady(Context ctx);
    void onGpsButtonClick();
    void onMarkerClick(Marker theMarker);
    void onSearchForAddress(String address);
    void onSuggestionֻSelected(Prediction predictionSuggestion);
}
