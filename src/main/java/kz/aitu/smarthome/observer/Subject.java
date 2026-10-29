package kz.aitu.smarthome.observer;

/**
 * A data source that listeners can join or leave at runtime, per event type.
 *
 * @param <K> event type key
 * @param <E> event class
 */
public interface Subject<K, E extends Event<K>> {
    void subscribe(K type, Observer<? super E> observer);

    void unsubscribe(K type, Observer<? super E> observer);
}
