package kz.aitu.smarthome.home;

public record Thermostat(String id, double watts, boolean on, double targetCelsius) implements Device {

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitThermostat(this);
    }
}
