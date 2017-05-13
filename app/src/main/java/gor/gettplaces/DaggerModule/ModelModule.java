package gor.gettplaces.DaggerModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gor.gettplaces.model.ILocationModel;
import gor.gettplaces.model.LocationModelImpl;

/**
 * Created by gor on 10/05/2017.
 */

@Module
public class ModelModule {

    @Provides
    @Singleton
    ILocationModel providesLocationModel(){
        return new LocationModelImpl();
    }

}
