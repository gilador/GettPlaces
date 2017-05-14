package gor.gettplaces.view.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import javax.inject.Inject;

import gor.gettplaces.GettPlacesApplication;
import gor.gettplaces.R;
import gor.gettplaces.Utils;
import gor.gettplaces.network.pojo.Result;
import gor.gettplaces.presenter.IPresenter;
import gor.gettplaces.presenter.MainPresenter;
import gor.gettplaces.view.IView;
import gor.gettplaces.view.MainView;

public class MainActivity extends BaseDaggerActivity implements MainView, OnMapReadyCallback {

    //=============================================================================================
    //                               Constants members
    //=============================================================================================
    private static final int FINE_LOCATION_CODE = 17;
    private static final int CORASE_LOCATION_CODE = 18;
    private static final String TAG = MainActivity.class.getSimpleName();

    //=============================================================================================
    //                               Protected Members
    //=============================================================================================
    @Inject
    protected MainPresenter mPresenter;

    //=============================================================================================
    //                               Private Members
    //=============================================================================================
    private SupportMapFragment mMapFragment;
    private boolean mMapInit;
    private GoogleMap mMap;

    //=============================================================================================
    //                               AppCompatActivity Impl
    //=============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        syncMap();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CORASE_LOCATION_CODE || requestCode == FINE_LOCATION_CODE) {
            syncMap();

        }
    }

    //=============================================================================================
    //                               Abstract BaseDaggerActivity Impl
    //=============================================================================================
    @Override
    IPresenter<IView> getPresenter() {
        return mPresenter;
    }

    //=============================================================================================
    //                               Interface MainView Impl
    //=============================================================================================
    @Override
    public void setLocations(List<Result> locationslist) {
        Log.d(TAG, "setLocations, size:" + locationslist.size());
        for (Result result : locationslist) {
            if (result != null && result.getGeometry() != null & result.getGeometry().getLocation() != null) {
                mMap.addMarker(new MarkerOptions().position(Utils.convertGeoToLocation(result.getGeometry()))
                        .title("->You Are Here<-"));
            }
        }
    }

    @Override
    public void setStartLocation(Location currentLocation) {
        LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(current)
                .title("->You Are Here<-"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 15.0f));

    }

    //=============================================================================================
    //                               Interface OnMapReadyCallback Impl
    //=============================================================================================
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "HI", Toast.LENGTH_SHORT).show();
        mPresenter.onMapReady(this);
        mMap = googleMap;
    }

    //=============================================================================================
    //                               Private
    //=============================================================================================
    private void init() {
        setContentView(R.layout.activity_main);

        ((GettPlacesApplication) getApplication()).getComponent().inject(this);

        checkPermissions();

        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_CODE);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    CORASE_LOCATION_CODE);
        }
    }

    private void syncMap() {
        if (Utils.isLocationPermissionGranted(this) && !mMapInit) {
            mMapInit = true;
            mMapFragment.getMapAsync(this);
        }
    }
}
