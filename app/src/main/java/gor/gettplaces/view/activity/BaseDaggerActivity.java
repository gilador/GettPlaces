package gor.gettplaces.view.activity;

import android.support.v7.app.AppCompatActivity;

import gor.gettplaces.presenter.IPresenter;
import gor.gettplaces.view.IView;

/**
 * Created by gor on 12/05/2017.
 */

public abstract class BaseDaggerActivity extends AppCompatActivity implements IView {

    abstract IPresenter<IView> getPresenter();

    protected void onResume() {
        super.onResume();
        getPresenter().onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPresenter().onPause();
    }

}
