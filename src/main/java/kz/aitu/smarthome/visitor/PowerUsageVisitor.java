package kz.aitu.smarthome.visitor;

import kz.aitu.smarthome.home.Camera;
import kz.aitu.smarthome.home.Container;
import kz.aitu.smarthome.home.Floor;
import kz.aitu.smarthome.home.Home;
import kz.aitu.smarthome.home.Lamp;
import kz.aitu.smarthome.home.Lock;
import kz.aitu.smarthome.home.Room;
import kz.aitu.smarthome.home.SmokeDetector;
import kz.aitu.smarthome.home.Thermostat;
import kz.aitu.smarthome.home.Visitor;

/** Numeric calculation: current power draw in watts of the visited subtree. */
public final class PowerUsageVisitor implements Visitor<Double> {

    @Override
    public Double visitHome(Home home) {
        return sumChildren(home);
    }

    @Override
    public Double visitFloor(Floor floor) {
        return sumChildren(floor);
    }

    @Override
    public Double visitRoom(Room room) {
        return sumChildren(room);
    }

    @Override
    public Double visitLamp(Lamp lamp) {
        return lamp.on() ? lamp.watts() : 0.0;
    }

    @Override
    public Double visitThermostat(Thermostat thermostat) {
        return thermostat.on() ? thermostat.watts() : 0.0;
    }

    @Override
    public Double visitCamera(Camera camera) {
        return camera.recording() ? camera.watts() : 0.0;
    }

    @Override
    public Double visitLock(Lock lock) {
        return lock.watts();
    }

    @Override
    public Double visitSmokeDetector(SmokeDetector smokeDetector) {
        return smokeDetector.watts();
    }

    private double sumChildren(Container container) {
        return container.children().stream().mapToDouble(child -> child.accept(this)).sum();
    }
}
