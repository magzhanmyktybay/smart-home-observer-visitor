package kz.aitu.smarthome.observer;

/** A listener that receives pushed events. */
@FunctionalInterface
public interface Observer<E> {
    void onEvent(E event);
}
