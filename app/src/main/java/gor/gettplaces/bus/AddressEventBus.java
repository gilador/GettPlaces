package gor.gettplaces.bus;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by gor on 12/05/2017.
 */

/**
 * This enum behaves as event bus .
 * ADDRESS_LOOKUP_UPDATE will deliver events regarding address lookup.
 */

public enum AddressEventBus {
    ADDRESS_LOOKUP_UPDATE(PublishSubject.create());

    public PublishSubject<Object> event_queue;

    AddressEventBus(PublishSubject<Object> objectPublishSubject) {
        event_queue = objectPublishSubject;
        event_queue.subscribeOn(Schedulers.io());
        event_queue.observeOn(AndroidSchedulers.mainThread());
    }

    public void subscribe(Observer observer) {
        event_queue.subscribe(observer);
    }

    public void onNext(String address) {
        event_queue.onNext(address);
    }
}
