package gor.gettplaces.presenter;

import gor.gettplaces.view.IView;

/**
 * Created by gor on 12/05/2017.
 */

public interface IPresenter<T> {

    public void onResume(T view);
    public void onPause();
}
