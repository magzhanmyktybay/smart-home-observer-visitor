package kz.aitu.smarthome.app;

import kz.aitu.smarthome.home.Camera;
import kz.aitu.smarthome.home.Floor;
import kz.aitu.smarthome.home.Home;
import kz.aitu.smarthome.home.Lamp;
import kz.aitu.smarthome.home.Lock;
import kz.aitu.smarthome.home.Room;
import kz.aitu.smarthome.home.Thermostat;

import java.util.List;

/**
 * The demo home. Shared by the application and the tests, so hand-calculated
 * values in the tests always match what the app shows.
 *
 * <pre>
 * home
 * |- floor-1
 * |  |- kitchen:     kitchen-lamp 60W on, kitchen-thermostat 1500W on (22C), back-door lock 5W locked
 * |  |- living-room: living-lamp 40W off, living-camera 8W recording
 * |- floor-2
 *    |- bedroom:     bedroom-lamp 25W on, balcony-door lock 5W UNLOCKED, bedroom-camera 8W NOT recording
 * </pre>
 */
public final class SampleHome {

    private SampleHome() {
    }

    public static Home create() {
        Room kitchen = new Room("kitchen", List.of(
                new Lamp("kitchen-lamp", 60, true),
                new Thermostat("kitchen-thermostat", 1500, true, 22),
                new Lock("back-door", 5, true)));
        Room livingRoom = new Room("living-room", List.of(
                new Lamp("living-lamp", 40, false),
                new Camera("living-camera", 8, true)));
        Room bedroom = new Room("bedroom", List.of(
                new Lamp("bedroom-lamp", 25, true),
                new Lock("balcony-door", 5, false),
                new Camera("bedroom-camera", 8, false)));
        return new Home("home", List.of(
                new Floor("floor-1", List.of(kitchen, livingRoom)),
                new Floor("floor-2", List.of(bedroom))));
    }
}
