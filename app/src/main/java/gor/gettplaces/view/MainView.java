package gor.gettplaces.view;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import gor.gettplaces.network.pojo.address.Prediction;
import gor.gettplaces.network.pojo.places.Result;

/**
 * Created by gor on 12/05/2017.
 */

public interface MainView extends IView{
    void onLocationsUpdate(List<Result> locationslist);
    void onStartLocationUpdate(LatLng currentLocation);
    void onAddressLookupComplete(List<Prediction> suggestions);
    void closeSearchDialog();
}
