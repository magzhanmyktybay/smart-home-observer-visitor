package kz.aitu.smarthome.source;

import kz.aitu.smarthome.event.EventType;
import kz.aitu.smarthome.event.HomeEvent;
import kz.aitu.smarthome.observer.EventSource;

/**
 * A live data source. One class is enough for power meters, climate and motion sensors:
 * they differ only in the event type they report, so separate classes would be duplication.
 */
public final class Sensor extends EventSource<EventType, HomeEvent> {

    private final String id;
    private final EventType reportedType;

    public Sensor(String id, EventType reportedType) {
        this.id = id;
        this.reportedType = reportedType;
    }

    public void report(String roomId, double value) {
        publish(HomeEvent.of(reportedType, id, roomId, value));
    }

    public String id() {
        return id;
    }
}
