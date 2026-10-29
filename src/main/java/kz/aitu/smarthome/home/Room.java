package kz.aitu.smarthome.home;

import java.util.List;
import java.util.Objects;

public record Room(String id, List<Device> devices) implements Container {

    public Room {
        Objects.requireNonNull(id, "id");
        devices = List.copyOf(devices);
    }

    @Override
    public List<Device> children() {
        return devices;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitRoom(this);
    }
}
