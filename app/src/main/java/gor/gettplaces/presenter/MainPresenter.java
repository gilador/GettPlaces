package gor.gettplaces.presenter;

import android.location.Location;

import java.util.List;

import gor.gettplaces.view.IView;

/**
 * Created by gor on 10/05/2017.
 */

public interface MainPresenter<T> extends IPresenter<T> {

    void getCurrentLocation();
    void getLocations();
}
