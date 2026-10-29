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
import kz.aitu.smarthome.home.Node;

import java.util.Optional;

/** Finds a node by id; stops as soon as it is found. Used to pick the subtree for an event. */
public final class FindByIdVisitor implements Visitor<Optional<Node>> {

    private final String targetId;

    public FindByIdVisitor(String targetId) {
        this.targetId = targetId;
    }

    @Override
    public Optional<Node> visitHome(Home home) {
        return search(home);
    }

    @Override
    public Optional<Node> visitFloor(Floor floor) {
        return search(floor);
    }

    @Override
    public Optional<Node> visitRoom(Room room) {
        return search(room);
    }

    @Override
    public Optional<Node> visitLamp(Lamp lamp) {
        return match(lamp);
    }

    @Override
    public Optional<Node> visitThermostat(Thermostat thermostat) {
        return match(thermostat);
    }

    @Override
    public Optional<Node> visitCamera(Camera camera) {
        return match(camera);
    }

    @Override
    public Optional<Node> visitLock(Lock lock) {
        return match(lock);
    }

    private Optional<Node> search(Container container) {
        if (container.id().equals(targetId)) {
            return Optional.of(container);
        }
        for (Node child : container.children()) {
            Optional<Node> found = child.accept(this);
            if (found.isPresent()) {
                return found;
            }
        }
        return Optional.empty();
    }

    private Optional<Node> match(Node node) {
        return node.id().equals(targetId) ? Optional.of(node) : Optional.empty();
    }
}
