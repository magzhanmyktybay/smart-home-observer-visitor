package kz.aitu.smarthome.home;

public record Lamp(String id, double watts, boolean on) implements Device {

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitLamp(this);
    }
}
