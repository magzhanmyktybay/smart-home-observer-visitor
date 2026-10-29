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

/**
 * Minutes a technician needs for yearly maintenance of the visited subtree.
 * Added later to prove that a new operation touches no element class.
 */
public final class MaintenanceTimeVisitor implements Visitor<Integer> {

    private static final int LAMP_MINUTES = 5;
    private static final int THERMOSTAT_MINUTES = 30;
    private static final int CAMERA_MINUTES = 15;
    private static final int LOCK_MINUTES = 10;
    private static final int SMOKE_DETECTOR_MINUTES = 10;

    @Override
    public Integer visitHome(Home home) {
        return sumChildren(home);
    }

    @Override
    public Integer visitFloor(Floor floor) {
        return sumChildren(floor);
    }

    @Override
    public Integer visitRoom(Room room) {
        return sumChildren(room);
    }

    @Override
    public Integer visitLamp(Lamp lamp) {
        return LAMP_MINUTES;
    }

    @Override
    public Integer visitThermostat(Thermostat thermostat) {
        return THERMOSTAT_MINUTES;
    }

    @Override
    public Integer visitCamera(Camera camera) {
        return CAMERA_MINUTES;
    }

    @Override
    public Integer visitLock(Lock lock) {
        return LOCK_MINUTES;
    }

    @Override
    public Integer visitSmokeDetector(SmokeDetector smokeDetector) {
        return SMOKE_DETECTOR_MINUTES;
    }

    private int sumChildren(Container container) {
        return container.children().stream().mapToInt(child -> child.accept(this)).sum();
    }
}
