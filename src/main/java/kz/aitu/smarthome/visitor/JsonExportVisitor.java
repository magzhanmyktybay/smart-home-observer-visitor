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

import java.util.stream.Collectors;

/** Hand-written JSON export. Containers nest their children inside a "children" array. */
public final class JsonExportVisitor implements Visitor<String> {

    @Override
    public String visitHome(Home home) {
        return container("home", home);
    }

    @Override
    public String visitFloor(Floor floor) {
        return container("floor", floor);
    }

    @Override
    public String visitRoom(Room room) {
        return container("room", room);
    }

    @Override
    public String visitLamp(Lamp lamp) {
        return leaf("lamp", lamp.id(), "\"watts\":" + lamp.watts() + ",\"on\":" + lamp.on());
    }

    @Override
    public String visitThermostat(Thermostat thermostat) {
        return leaf("thermostat", thermostat.id(), "\"watts\":" + thermostat.watts()
                + ",\"on\":" + thermostat.on() + ",\"targetCelsius\":" + thermostat.targetCelsius());
    }

    @Override
    public String visitCamera(Camera camera) {
        return leaf("camera", camera.id(), "\"watts\":" + camera.watts() + ",\"recording\":" + camera.recording());
    }

    @Override
    public String visitLock(Lock lock) {
        return leaf("lock", lock.id(), "\"watts\":" + lock.watts() + ",\"locked\":" + lock.locked());
    }

    private String container(String type, Container container) {
        String children = container.children().stream()
                .map(child -> child.accept(this))
                .collect(Collectors.joining(","));
        return "{" + header(type, container.id()) + ",\"children\":[" + children + "]}";
    }

    private String leaf(String type, String id, String fields) {
        return "{" + header(type, id) + "," + fields + "}";
    }

    private String header(String type, String id) {
        return "\"type\":" + quote(type) + ",\"id\":" + quote(id);
    }

    private String quote(String text) {
        return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
