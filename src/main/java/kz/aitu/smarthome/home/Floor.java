package kz.aitu.smarthome.home;

import java.util.List;
import java.util.Objects;

public record Floor(String id, List<Room> rooms) implements Container {

    public Floor {
        Objects.requireNonNull(id, "id");
        rooms = List.copyOf(rooms);
    }

    @Override
    public List<Room> children() {
        return rooms;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitFloor(this);
    }
}
