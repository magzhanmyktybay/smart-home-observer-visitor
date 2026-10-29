package kz.aitu.smarthome.visitor;

import kz.aitu.smarthome.home.Camera;
import kz.aitu.smarthome.home.Container;
import kz.aitu.smarthome.home.Floor;
import kz.aitu.smarthome.home.Home;
import kz.aitu.smarthome.home.Lamp;
import kz.aitu.smarthome.home.Lock;
import kz.aitu.smarthome.home.Room;
import kz.aitu.smarthome.home.Thermostat;
import kz.aitu.smarthome.home.Visitor;

import java.util.HashMap;
import java.util.Map;

/**
 * Accumulates state across the whole tree (counters), result is read with {@link #stats()}.
 * Stateful, so use one instance per traversal.
 */
public final class DeviceStatsVisitor implements Visitor<Void> {

    private int floors;
    private int rooms;
    private int devices;
    private int activeDevices;
    private final Map<String, Integer> devicesByType = new HashMap<>();

    @Override
    public Void visitHome(Home home) {
        return visitChildren(home);
    }

    @Override
    public Void visitFloor(Floor floor) {
        floors++;
        return visitChildren(floor);
    }

    @Override
    public Void visitRoom(Room room) {
        rooms++;
        return visitChildren(room);
    }

    @Override
    public Void visitLamp(Lamp lamp) {
        return countDevice("lamp", lamp.on());
    }

    @Override
    public Void visitThermostat(Thermostat thermostat) {
        return countDevice("thermostat", thermostat.on());
    }

    @Override
    public Void visitCamera(Camera camera) {
        return countDevice("camera", camera.recording());
    }

    @Override
    public Void visitLock(Lock lock) {
        return countDevice("lock", lock.locked());
    }

    public DeviceStats stats() {
        return new DeviceStats(floors, rooms, devices, activeDevices, devicesByType);
    }

    private Void visitChildren(Container container) {
        container.children().forEach(child -> child.accept(this));
        return null;
    }

    private Void countDevice(String type, boolean active) {
        devices++;
        if (active) {
            activeDevices++;
        }
        devicesByType.merge(type, 1, Integer::sum);
        return null;
    }
}
