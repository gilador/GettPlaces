package gor.gettplaces.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import gor.gettplaces.presenter.IPresenter;
import gor.gettplaces.presenter.MainPresenter;
import gor.gettplaces.service.CurrentLocationService;
import gor.gettplaces.view.IView;
import gor.gettplaces.view.MainView;

public class MainActivity extends BaseDaggerActivity implements MainView, OnMapReadyCallback {

    private static final int FINE_LOCATION = 17;
    private static final int CORASE_LOCATION = 18;
    @Inject
    protected MainPresenter mPresenter;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.activity_main);

        ((GettPlacesApplication) getApplication()).getComponent().inject(this);

        startService(new Intent(this, CurrentLocationService.class));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    CORASE_LOCATION);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    IPresenter<IView> getPresenter() {
        return mPresenter;
    }

    @Override
    public void setLocations(List<Location> locationslist) {

    }

    @Override
    public void setCurrentLocation(Location currentLocation) {
        LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(current)
                .title("->You Are Here<-"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this,"HI",Toast.LENGTH_SHORT).show();
        mPresenter.onMapReady(this);
        mMap = googleMap;
    }
}
