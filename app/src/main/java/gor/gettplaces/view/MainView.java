package gor.gettplaces.view;

import android.location.Location;

import java.util.List;

import gor.gettplaces.network.pojo.Result;

/**
 * Created by gor on 12/05/2017.
 */

public interface MainView {
    void setLocations(List<Result> locationslist);
    void setStartLocation(Location currentLocation);
}
