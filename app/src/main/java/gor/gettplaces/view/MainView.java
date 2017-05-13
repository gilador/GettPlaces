package gor.gettplaces.view;

import android.location.Location;

import java.util.List;

/**
 * Created by gor on 12/05/2017.
 */

public interface MainView {
    void setLocations(List<Location> locationslist);
    void setStartLocation(Location currentLocation);
}
