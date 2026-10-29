package kz.aitu.smarthome.observer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.LongAdder;

/**
 * Base class for every subject. Holds a thread-safe listener registry.
 *
 * <ul>
 *   <li>{@link ConcurrentHashMap} keyed by event type: subscribing to one type never blocks
 *       publishing of another type, the subject as a whole is never locked.</li>
 *   <li>{@link CopyOnWriteArrayList} per type: {@link #publish} iterates over a snapshot, so a
 *       listener may subscribe or unsubscribe (even itself, inside its own handler) while other
 *       threads are publishing, without {@code ConcurrentModificationException}.</li>
 * </ul>
 *
 * {@code publish} is protected: only the subject itself decides when an event happens.
 */
public abstract class EventSource<K, E extends Event<K>> implements Subject<K, E> {

    private final Map<K, CopyOnWriteArrayList<Observer<? super E>>> listeners = new ConcurrentHashMap<>();
    private final LongAdder failedDeliveries = new LongAdder();

    @Override
    public void subscribe(K type, Observer<? super E> observer) {
        listeners.computeIfAbsent(type, key -> new CopyOnWriteArrayList<>()).addIfAbsent(observer);
    }

    @Override
    public void unsubscribe(K type, Observer<? super E> observer) {
        List<Observer<? super E>> forType = listeners.get(type);
        if (forType != null) {
            forType.remove(observer);
        }
    }

    protected void publish(E event) {
        List<Observer<? super E>> forType = listeners.get(event.type());
        if (forType == null) {
            return;
        }
        for (Observer<? super E> observer : forType) {
            deliver(observer, event);
        }
    }

    /** A failing listener must not stop delivery to the others, so its exception is counted. */
    private void deliver(Observer<? super E> observer, E event) {
        try {
            observer.onEvent(event);
        } catch (RuntimeException e) {
            failedDeliveries.increment();
        }
    }

    public long failedDeliveries() {
        return failedDeliveries.sum();
    }
}
