package kz.aitu.smarthome.home;

/**
 * Generic visitor. Second dispatch: inside accept() the element calls the method
 * for its exact type, so the visitor knows the concrete class without instanceof.
 *
 * @param <R> result type of the operation
 */
public interface Visitor<R> {
    R visitHome(Home home);

    R visitFloor(Floor floor);

    R visitRoom(Room room);

    R visitLamp(Lamp lamp);

    R visitThermostat(Thermostat thermostat);

    R visitCamera(Camera camera);

    R visitLock(Lock lock);
}
