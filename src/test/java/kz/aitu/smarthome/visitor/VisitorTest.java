package kz.aitu.smarthome.visitor;

import kz.aitu.smarthome.app.SampleHome;
import kz.aitu.smarthome.home.Home;
import kz.aitu.smarthome.home.Lamp;
import kz.aitu.smarthome.home.Lock;
import kz.aitu.smarthome.home.Node;
import kz.aitu.smarthome.home.Room;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Expected values are calculated by hand from the tree drawn in {@link SampleHome}. */
class VisitorTest {

    private final Home home = SampleHome.create();

    private Node find(String id) {
        return home.accept(new FindByIdVisitor(id)).orElseThrow();
    }

    @Test
    void powerUsageOfWholeHome() {
        // kitchen 60 + 1500 + 5 = 1565, living 0 + 8 = 8, bedroom 25 + 5 + 0 = 30
        assertEquals(1603.0, home.accept(new PowerUsageVisitor()), 1e-9);
    }

    @Test
    void powerUsageOfSubtrees() {
        assertEquals(1573.0, find("floor-1").accept(new PowerUsageVisitor()), 1e-9);
        assertEquals(1565.0, find("kitchen").accept(new PowerUsageVisitor()), 1e-9);
    }

    @Test
    void statsVisitorAccumulatesCountersOverWholeTree() {
        DeviceStatsVisitor visitor = new DeviceStatsVisitor();
        home.accept(visitor);

        // active: kitchen-lamp, kitchen-thermostat, back-door, living-camera, bedroom-lamp
        assertEquals(new DeviceStats(2, 3, 8, 5,
                Map.of("lamp", 3, "thermostat", 1, "camera", 2, "lock", 2)), visitor.stats());
    }

    @Test
    void securityAuditFiltersWeakPointsOfWholeHome() {
        List<String> ids = home.accept(new SecurityAuditVisitor()).stream().map(Node::id).toList();

        assertEquals(List.of("balcony-door", "bedroom-camera"), ids);
    }

    @Test
    void securityAuditOfSecureRoomIsEmpty() {
        assertTrue(find("kitchen").accept(new SecurityAuditVisitor()).isEmpty());
    }

    @Test
    void jsonExportOfRoomHasCorrectNesting() {
        Room hall = new Room("hall", List.of(new Lamp("hall-lamp", 40, true), new Lock("front-door", 5, false)));

        assertEquals("{\"type\":\"room\",\"id\":\"hall\",\"children\":["
                        + "{\"type\":\"lamp\",\"id\":\"hall-lamp\",\"watts\":40.0,\"on\":true},"
                        + "{\"type\":\"lock\",\"id\":\"front-door\",\"watts\":5.0,\"locked\":false}]}",
                hall.accept(new JsonExportVisitor()));
    }

    @Test
    void jsonExportOfWholeHomeContainsEveryNodeOnce() {
        String json = home.accept(new JsonExportVisitor());

        // 1 home + 2 floors + 3 rooms + 8 devices = 14 objects
        assertEquals(14, json.split("\"type\":", -1).length - 1);
        assertEquals(count(json, '{'), count(json, '}'));
        assertEquals(count(json, '['), count(json, ']'));
        assertTrue(json.startsWith("{\"type\":\"home\",\"id\":\"home\",\"children\":[{\"type\":\"floor\""));
    }

    @Test
    void findByIdReturnsNodeOrEmpty() {
        assertEquals("bedroom", find("bedroom").id());
        assertEquals(Optional.empty(), home.accept(new FindByIdVisitor("garage")));
    }

    private static long count(String text, char symbol) {
        return text.chars().filter(c -> c == symbol).count();
    }
}
