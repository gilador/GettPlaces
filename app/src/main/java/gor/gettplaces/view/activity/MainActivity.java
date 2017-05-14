package gor.gettplaces.view.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import javax.inject.Inject;

import gor.gettplaces.GettPlacesApplication;
import gor.gettplaces.R;
import gor.gettplaces.Utils;
import gor.gettplaces.network.pojo.address.Prediction;
import gor.gettplaces.network.pojo.places.Result;
import gor.gettplaces.presenter.IPresenter;
import gor.gettplaces.presenter.MainPresenter;
import gor.gettplaces.view.IView;
import gor.gettplaces.view.MainView;
import gor.gettplaces.view.adapter.AddressSuggestionAdapter;
import gor.gettplaces.view.animation.WidthExpandAnimation;

public class MainActivity extends BaseDaggerActivity implements MainView, OnMapReadyCallback, AddressSuggestionAdapter.SuggestionClickListener {

    //=============================================================================================
    //                               Constants members
    //=============================================================================================
    private static final int FINE_LOCATION_CODE = 17;
    private static final int CORASE_LOCATION_CODE = 18;
    private static final String TAG = MainActivity.class.getSimpleName();

    //=============================================================================================
    //                               Protected Members
    //=============================================================================================
    private AddressSuggestionAdapter mAddressSuggestionAdapter;

    //=============================================================================================
    //                               Protected Members
    //=============================================================================================
    @Inject
    protected MainPresenter mPresenter;

    //=============================================================================================
    //                               Private Members
    //=============================================================================================
    private SupportMapFragment mMapFragment;
    private EditText mSearch;
    private EditText mFocusThief;
    private boolean mMapInit;
    private GoogleMap mMap;
    private RecyclerView mSuggestionsList;
    private ImageView mGpsButton;
    private View mMagnifyingGlass;

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

    @Override
    public void onBackPressed() {
        if (!mSearch.getText().toString().equals("")) {
            mSearch.setText("");
            mGpsButton.requestFocus();
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } else {
            super.onBackPressed();
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
    public void onLocationsUpdate(List<Result> locationslist) {
        Log.d(TAG, "onLocationsUpdate, size:" + locationslist.size());
        for (Result result : locationslist) {
            if (result != null && result.getGeometry() != null & result.getGeometry().getLocation() != null) {
                mMap.addMarker(new MarkerOptions().position(Utils.convertGeoToLocation(result.getGeometry()))
                        .title(result.getName()));
            }
        }
    }

    @Override
    public void onStartLocationUpdate(LatLng currentLocation) {
        LatLng current = new LatLng(currentLocation.latitude, currentLocation.longitude);
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(current)
                .title("->You Are Here<-")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.home_pin)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 15.0f));

    }

    @Override
    public void onAddressLookupComplete(List<Prediction> suggestions) {
        mAddressSuggestionAdapter.setData(suggestions);
    }

    @Override
    public void closeSearchDialog() {
        mSearch.setText("");
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
    //                                SuggestionClickListener Impl
    //=============================================================================================

    @Override
    public void OnSuggestionClick(Prediction predictionSuggestion) {
        mPresenter.onSuggestionֻSelected(predictionSuggestion);
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
        mGpsButton = (ImageView) findViewById(R.id.gps_button);
        mAddressSuggestionAdapter = new AddressSuggestionAdapter(this, this);
        mSearch = (EditText) findViewById(R.id.address_search);
        mFocusThief = (EditText) findViewById(R.id.focus_thief);
        mMagnifyingGlass = findViewById(R.id.magnifying_glass);
        mSuggestionsList = (RecyclerView) findViewById(R.id.suggestion_list);
        mSuggestionsList.setAdapter(mAddressSuggestionAdapter);
        mSuggestionsList.setLayoutManager(new LinearLayoutManager(this));
        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPresenter.onSearchForAddress(editable.toString());
            }
        });
        mGpsButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mPresenter.onGpsButtonClick();
            }
        });
        mSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                int expandWidth = (int) (getResources().getDimension(R.dimen.address_suggestion_width));
                final float toAlpha = mSearch.getLayoutParams().width == expandWidth ? 1 : 0;
                final float fromAlpha = mSearch.getLayoutParams().width == expandWidth ? 0 : 1;
                if (mSearch.getLayoutParams().width == expandWidth) {

                    expandWidth = (int) (getResources().getDimension(R.dimen.address_suggestion_width_collapse));
                }
                WidthExpandAnimation anim = new WidthExpandAnimation(mSearch, expandWidth);
                anim.setDuration(400);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        AlphaAnimation anim = new AlphaAnimation(fromAlpha, toAlpha);
                        anim.setDuration(400);
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mMagnifyingGlass.setAlpha(toAlpha);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        mMagnifyingGlass.startAnimation(anim);

                        if(toAlpha == 1) mSearch.setHint(null);

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if(toAlpha == 0) mSearch.setHint(R.string.address_lookup);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mSearch.startAnimation(anim);
            }
        });
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
