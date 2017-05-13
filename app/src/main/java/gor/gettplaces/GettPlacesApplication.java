package gor.gettplaces;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import gor.gettplaces.DaggerModule.ModelModule;
import gor.gettplaces.DaggerModule.PresenterModule;
import gor.gettplaces.view.activity.MainActivity;

/**
 * Created by gor on 11/05/2017.
 */

public class GettPlacesApplication extends Application {

    private ApplicationComponent component;

    /**
     * Newly added modules must be added to the @Component annotation here. You must also provide
     * further inject() methods for new classes that want to perform injection.
     */
    @Singleton
    @Component(modules = {PresenterModule.class, ModelModule.class})
    public interface ApplicationComponent {
        void inject(MainActivity mainActivity);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // This setups up the component which is used by other views (activities/fragments/etc., not Android views) for injection.
        // This pulls all modules which have statically declared @Provides methods automatically.
        component = DaggerGettPlacesApplication_ApplicationComponent.builder().build();
    }

    public ApplicationComponent getComponent() {
        return component;
    }
}
