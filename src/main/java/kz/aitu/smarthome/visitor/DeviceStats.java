package kz.aitu.smarthome.visitor;

import java.util.Map;

/** Immutable result of {@link DeviceStatsVisitor}. */
public record DeviceStats(int floors, int rooms, int devices, int activeDevices, Map<String, Integer> devicesByType) {

    public DeviceStats {
        devicesByType = Map.copyOf(devicesByType);
    }
}
