package gor.gettplaces.model;

import android.content.Context;
import android.location.Location;

import java.util.List;

/**
 * Created by gor on 10/05/2017.
 */

public interface ILocationModel {

    interface LocationsListener {
        void onLocationsLoaded(List<Location> locationList);
    }

    interface StartLocationsListener {
        void onStartLocationLoaded(Location currentLocation);
    }

    void load(Context ctx);
    void finish();
    void setAutoUpdateCurrentLocation();
    void setManualLocation(String address);
    void setOnLocationsUpdate(LocationsListener locationsListener);
    void setOnStartLocationUpdate(StartLocationsListener startLocationsListener);
}
