package gor.gettplaces.DaggerModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gor.gettplaces.model.ILocationModel;
import gor.gettplaces.presenter.MainPresenter;
import gor.gettplaces.presenter.MainPresenterImpl;

/**
 * Created by gor on 10/05/2017.
 */

@Module
public class PresenterModule {


    @Provides
    @Singleton
    MainPresenter providesMainPresenter(ILocationModel locationModel){
        return new MainPresenterImpl(locationModel);
    }

}
