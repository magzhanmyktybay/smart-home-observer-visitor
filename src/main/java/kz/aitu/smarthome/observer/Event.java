package kz.aitu.smarthome.observer;

/**
 * An immutable event that carries its own type, so listeners can subscribe per type.
 *
 * @param <K> the type key (usually an enum)
 */
public interface Event<K> {
    K type();
}
