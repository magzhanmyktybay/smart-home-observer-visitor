package kz.aitu.smarthome.home;

import java.util.List;
import java.util.Objects;

public record Home(String id, List<Floor> floors) implements Container {

    public Home {
        Objects.requireNonNull(id, "id");
        floors = List.copyOf(floors);
    }

    @Override
    public List<Floor> children() {
        return floors;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitHome(this);
    }
}
