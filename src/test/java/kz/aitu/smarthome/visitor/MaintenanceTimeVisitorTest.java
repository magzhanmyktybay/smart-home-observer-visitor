package kz.aitu.smarthome.visitor;

import kz.aitu.smarthome.app.SampleHome;
import kz.aitu.smarthome.home.Home;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaintenanceTimeVisitorTest {

    private final Home home = SampleHome.create();

    @Test
    void wholeHomeMaintenanceTime() {
        // 3 lamps * 5 + 1 thermostat * 30 + 2 cameras * 15 + 2 locks * 10 = 95
        int minutes = home.accept(new MaintenanceTimeVisitor());

        assertEquals(95, minutes);
    }

    @Test
    void kitchenMaintenanceTime() {
        // lamp 5 + thermostat 30 + lock 10 = 45
        int minutes = home.accept(new FindByIdVisitor("kitchen")).orElseThrow().accept(new MaintenanceTimeVisitor());

        assertEquals(45, minutes);
    }
}
