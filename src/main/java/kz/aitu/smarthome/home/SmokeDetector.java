package kz.aitu.smarthome.home;

/** Smoke detector, always powered. Added later to show the cost of a new element type. */
public record SmokeDetector(String id, double watts, boolean armed) implements Device {

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitSmokeDetector(this);
    }
}
