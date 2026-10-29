package kz.aitu.smarthome.home;

public record Camera(String id, double watts, boolean recording) implements Device {

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitCamera(this);
    }
}
