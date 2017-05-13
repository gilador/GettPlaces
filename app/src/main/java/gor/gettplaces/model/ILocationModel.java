package gor.gettplaces.model;

import android.location.Location;

import java.util.List;

/**
 * Created by gor on 10/05/2017.
 */

public interface ILocationModel {

    interface LocationsListener {
        void onLocationsLoaded(List<Location> locationList);
    }

    interface CurrentLocationsListener {
        void onCurrentLocationLoaded(Location currentLocation);
    }

    void onLocationsUpdate(LocationsListener locationsListener);
    void onCurrentLocationUpdate(CurrentLocationsListener currentLocationsListener);
}
