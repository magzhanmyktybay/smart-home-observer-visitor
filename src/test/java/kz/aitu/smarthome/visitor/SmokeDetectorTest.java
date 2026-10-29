package kz.aitu.smarthome.visitor;

import kz.aitu.smarthome.home.Lamp;
import kz.aitu.smarthome.home.Node;
import kz.aitu.smarthome.home.Room;
import kz.aitu.smarthome.home.SmokeDetector;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SmokeDetectorTest {

    private final Room kitchen = new Room("kitchen", List.of(
            new Lamp("kitchen-lamp", 60, true),
            new SmokeDetector("kitchen-smoke", 2, false)));

    @Test
    void everyVisitorSupportsTheNewElement() {
        int minutes = kitchen.accept(new MaintenanceTimeVisitor());
        DeviceStatsVisitor stats = new DeviceStatsVisitor();
        kitchen.accept(stats);

        assertEquals(62.0, kitchen.accept(new PowerUsageVisitor()), 1e-9);
        assertEquals(15, minutes);
        assertEquals(new DeviceStats(0, 1, 2, 1, Map.of("lamp", 1, "smokeDetector", 1)), stats.stats());
        assertEquals(List.of("kitchen-smoke"),
                kitchen.accept(new SecurityAuditVisitor()).stream().map(Node::id).toList());
        assertEquals("kitchen-smoke", kitchen.accept(new FindByIdVisitor("kitchen-smoke")).orElseThrow().id());
    }

    @Test
    void jsonExportIncludesSmokeDetector() {
        assertEquals("{\"type\":\"room\",\"id\":\"kitchen\",\"children\":["
                        + "{\"type\":\"lamp\",\"id\":\"kitchen-lamp\",\"watts\":60.0,\"on\":true},"
                        + "{\"type\":\"smokeDetector\",\"id\":\"kitchen-smoke\",\"watts\":2.0,\"armed\":false}]}",
                kitchen.accept(new JsonExportVisitor()));
    }
}
