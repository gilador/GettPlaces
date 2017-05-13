package gor.gettplaces.presenter;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.Marker;

import java.util.List;

import gor.gettplaces.view.IView;

/**
 * Created by gor on 10/05/2017.
 */

public interface MainPresenter<T> extends IPresenter<T> {

    void onMapReady(Context ctx);
    void onMarkerClick(Marker theMarker);
}
