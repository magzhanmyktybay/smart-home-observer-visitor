package kz.aitu.smarthome.concurrency;

import kz.aitu.smarthome.source.Sensor;

import java.util.List;
import java.util.function.DoubleSupplier;

/** Producer task: makes a sensor report {@code events} values, cycling through the rooms. */
public final class SensorProducer implements Runnable {

    private final Sensor sensor;
    private final List<String> roomIds;
    private final int events;
    private final DoubleSupplier values;

    public SensorProducer(Sensor sensor, List<String> roomIds, int events, DoubleSupplier values) {
        if (roomIds.isEmpty()) {
            throw new IllegalArgumentException("roomIds must not be empty");
        }
        this.sensor = sensor;
        this.roomIds = List.copyOf(roomIds);
        this.events = events;
        this.values = values;
    }

    @Override
    public void run() {
        for (int i = 0; i < events; i++) {
            sensor.report(roomIds.get(i % roomIds.size()), values.getAsDouble());
        }
    }
}
