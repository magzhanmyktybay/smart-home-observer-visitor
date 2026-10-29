package kz.aitu.smarthome.integration;

import kz.aitu.smarthome.event.EventType;
import kz.aitu.smarthome.event.HomeEvent;
import kz.aitu.smarthome.home.Device;
import kz.aitu.smarthome.home.Home;
import kz.aitu.smarthome.home.Node;
import kz.aitu.smarthome.observer.EventSource;
import kz.aitu.smarthome.observer.Observer;
import kz.aitu.smarthome.visitor.FindByIdVisitor;
import kz.aitu.smarthome.visitor.SecurityAuditVisitor;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * The integration link between the Observer side and the Visitor side:
 * MOTION event (alert) -> SecurityAuditVisitor on that room only (inspection)
 * -> SECURITY_REPORT event (report) published back into the system.
 */
public final class SecurityAlarm extends EventSource<EventType, HomeEvent> implements Observer<HomeEvent> {

    public static final String SOURCE_ID = "security-alarm";

    private final Home home;
    private final AtomicBoolean armed = new AtomicBoolean(false);

    public SecurityAlarm(Home home) {
        this.home = home;
    }

    public void arm() {
        armed.set(true);
    }

    public void disarm() {
        armed.set(false);
    }

    @Override
    public void onEvent(HomeEvent motion) {
        if (!armed.get()) {
            return;
        }
        home.accept(new FindByIdVisitor(motion.roomId()))
                .ifPresent(room -> publishReport(motion.roomId(), room.accept(new SecurityAuditVisitor())));
    }

    private void publishReport(String roomId, List<Device> weakPoints) {
        String detail = weakPoints.stream().map(Node::id).collect(Collectors.joining(", "));
        publish(new HomeEvent(EventType.SECURITY_REPORT, SOURCE_ID, roomId, weakPoints.size(), detail));
    }
}
