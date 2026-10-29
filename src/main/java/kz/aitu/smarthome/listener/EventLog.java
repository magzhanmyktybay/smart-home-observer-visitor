package kz.aitu.smarthome.listener;

import kz.aitu.smarthome.event.EventType;
import kz.aitu.smarthome.event.HomeEvent;
import kz.aitu.smarthome.observer.Observer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/** Counts every event it receives, per type. Used for statistics and the stress test. */
public final class EventLog implements Observer<HomeEvent> {

    private final Map<EventType, LongAdder> counts = new ConcurrentHashMap<>();

    @Override
    public void onEvent(HomeEvent event) {
        counts.computeIfAbsent(event.type(), type -> new LongAdder()).increment();
    }

    public long count(EventType type) {
        LongAdder adder = counts.get(type);
        return adder == null ? 0 : adder.sum();
    }

    public long total() {
        return counts.values().stream().mapToLong(LongAdder::sum).sum();
    }
}
