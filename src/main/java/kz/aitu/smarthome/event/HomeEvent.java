package kz.aitu.smarthome.event;

import kz.aitu.smarthome.observer.Event;

import java.util.Objects;

/**
 * Immutable smart home event (push model: everything a listener needs is inside the event).
 *
 * @param type     what happened
 * @param sourceId which subject published it
 * @param roomId   room id in the home tree ("all" for whole-home events)
 * @param value    measured value: Wh, degrees Celsius, number of problems...
 * @param detail   optional human readable details, never null
 */
public record HomeEvent(EventType type, String sourceId, String roomId, double value, String detail)
        implements Event<EventType> {

    public HomeEvent {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(sourceId, "sourceId");
        Objects.requireNonNull(roomId, "roomId");
        Objects.requireNonNull(detail, "detail");
    }

    public static HomeEvent of(EventType type, String sourceId, String roomId, double value) {
        return new HomeEvent(type, sourceId, roomId, value, "");
    }
}
