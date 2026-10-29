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
import kz.aitu.smarthome.home.Device;

import java.util.List;

/** Filter: returns the devices that are a security weak point (unlocked locks, cameras not recording). */
public final class SecurityAuditVisitor implements Visitor<List<Device>> {

    @Override
    public List<Device> visitHome(Home home) {
        return collectChildren(home);
    }

    @Override
    public List<Device> visitFloor(Floor floor) {
        return collectChildren(floor);
    }

    @Override
    public List<Device> visitRoom(Room room) {
        return collectChildren(room);
    }

    @Override
    public List<Device> visitLamp(Lamp lamp) {
        return List.of();
    }

    @Override
    public List<Device> visitThermostat(Thermostat thermostat) {
        return List.of();
    }

    @Override
    public List<Device> visitCamera(Camera camera) {
        return camera.recording() ? List.of() : List.of(camera);
    }

    @Override
    public List<Device> visitLock(Lock lock) {
        return lock.locked() ? List.of() : List.of(lock);
    }

    @Override
    public List<Device> visitSmokeDetector(SmokeDetector smokeDetector) {
        return smokeDetector.armed() ? List.of() : List.of(smokeDetector);
    }

    private List<Device> collectChildren(Container container) {
        return container.children().stream()
                .flatMap(child -> child.accept(this).stream())
                .toList();
    }
}
