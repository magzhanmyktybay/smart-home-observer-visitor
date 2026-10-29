package kz.aitu.smarthome.listener;

import kz.aitu.smarthome.event.EventType;
import kz.aitu.smarthome.event.HomeEvent;
import kz.aitu.smarthome.observer.EventSource;
import kz.aitu.smarthome.observer.Observer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * Chain of observers: listens to POWER_READING and is itself a subject that republishes
 * aggregated ENERGY_SUMMARY events every {@code summaryEvery} readings.
 */
public final class EnergyTracker extends EventSource<EventType, HomeEvent> implements Observer<HomeEvent> {

    public static final String SOURCE_ID = "energy-tracker";

    private final int summaryEvery;
    private final Map<String, DoubleAdder> wattHoursByRoom = new ConcurrentHashMap<>();
    private final AtomicLong readings = new AtomicLong();

    public EnergyTracker(int summaryEvery) {
        if (summaryEvery <= 0) {
            throw new IllegalArgumentException("summaryEvery must be positive");
        }
        this.summaryEvery = summaryEvery;
    }

    @Override
    public void onEvent(HomeEvent reading) {
        wattHoursByRoom.computeIfAbsent(reading.roomId(), room -> new DoubleAdder()).add(reading.value());
        if (readings.incrementAndGet() % summaryEvery == 0) {
            publish(HomeEvent.of(EventType.ENERGY_SUMMARY, SOURCE_ID, "all", totalWattHours()));
        }
    }

    public double totalWattHours() {
        return wattHoursByRoom.values().stream().mapToDouble(DoubleAdder::sum).sum();
    }

    public double wattHours(String roomId) {
        DoubleAdder adder = wattHoursByRoom.get(roomId);
        return adder == null ? 0.0 : adder.sum();
    }

    public long readings() {
        return readings.get();
    }
}
