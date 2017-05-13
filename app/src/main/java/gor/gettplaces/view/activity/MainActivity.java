package gor.gettplaces.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import java.util.List;

import javax.inject.Inject;

import gor.gettplaces.GettPlacesApplication;
import gor.gettplaces.R;
import gor.gettplaces.presenter.IPresenter;
import gor.gettplaces.presenter.MainPresenter;
import gor.gettplaces.service.CurrentLocationService;
import gor.gettplaces.view.IView;
import gor.gettplaces.view.MainView;

public class MainActivity extends BaseDaggerActivity implements MainView {

    private static final int FINE_LOCATION = 17;
    private static final int CORASE_LOCATION = 18;
    @Inject
    protected MainPresenter presenter;

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

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

    }


    @Override
    IPresenter<IView> getPresenter() {
        return presenter;
    }

    @Override
    public void setLocations(List<Location> locationslist) {

    }

    @Override
    public void setCurrentLocation(Location currentLocation) {

    }
}
