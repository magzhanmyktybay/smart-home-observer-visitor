package kz.aitu.smarthome.home;

/** Smart door lock. It is always powered (standby), locked or not. */
public record Lock(String id, double watts, boolean locked) implements Device {

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitLock(this);
    }
}
