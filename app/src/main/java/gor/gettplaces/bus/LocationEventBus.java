package gor.gettplaces.bus;

import com.google.android.gms.maps.model.LatLng;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by gor on 12/05/2017.
 */

/**
 * This enum behaves as event bus divided into channels.
 * each type represent a channel that an observers can subscribe to.
 * -START_LOCATION_UPDATE will deliver events regarding the user location.
 * -LOCATIONS_UPDATE will deliver events containing the surrounding locations.
 * -GEOCODING_UPDATE will deliver events regarding geocoding requests.
 */
public enum LocationEventBus {
    START_LOCATION_UPDATE(PublishSubject.create()),
    LOCATIONS_UPDATE(PublishSubject.create()),
    GEOCODING_UPDATE(PublishSubject.create());

    public PublishSubject<Object> event_queue;

    LocationEventBus(PublishSubject<Object> objectPublishSubject) {
        event_queue = objectPublishSubject;
        event_queue.subscribeOn(Schedulers.io());
        event_queue.observeOn(AndroidSchedulers.mainThread());
    }

    public void subscribe(Observer observer) {
        event_queue.subscribe(observer);
    }

    public void onNext(LatLng currentLocation) {
        event_queue.onNext(currentLocation);
    }
}
