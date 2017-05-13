package gor.gettplaces.service;

import android.location.Location;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by gor on 12/05/2017.
 */

public enum CurrentLocationEvent {
    START_LOCATION_UPDATE(PublishSubject.create()),
    LOCATIONS_UPDATE(PublishSubject.create());

    public PublishSubject<Object> event_queue;

    CurrentLocationEvent(PublishSubject<Object> objectPublishSubject) {
        event_queue = objectPublishSubject;
        event_queue.subscribeOn(Schedulers.io());
        event_queue.observeOn(AndroidSchedulers.mainThread());
    }

    public void subscribe(Observer observer) {
        event_queue.subscribe(observer);
    }

    public void onNext(Location currentLocation) {
        event_queue.onNext(currentLocation);
    }
}
