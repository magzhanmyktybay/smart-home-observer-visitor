package kz.aitu.smarthome.observer;

import kz.aitu.smarthome.event.EventType;
import kz.aitu.smarthome.event.HomeEvent;
import kz.aitu.smarthome.listener.EventLog;
import kz.aitu.smarthome.listener.WelcomeHomeRule;
import kz.aitu.smarthome.source.Sensor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventSourceTest {

    @Test
    void deliversOnlyToListenersOfThatEventType() {
        Sensor climate = new Sensor("climate", EventType.TEMPERATURE);
        EventLog temperatureLog = new EventLog();
        EventLog motionLog = new EventLog();
        climate.subscribe(EventType.TEMPERATURE, temperatureLog);
        climate.subscribe(EventType.MOTION, motionLog);

        climate.report("kitchen", 21.5);

        assertEquals(1, temperatureLog.total());
        assertEquals(0, motionLog.total());
    }

    @Test
    void unsubscribedListenerReceivesNothing() {
        Sensor motion = new Sensor("motion", EventType.MOTION);
        EventLog log = new EventLog();
        motion.subscribe(EventType.MOTION, log);
        motion.report("kitchen", 1);

        motion.unsubscribe(EventType.MOTION, log);
        motion.report("kitchen", 1);

        assertEquals(1, log.total());
    }

    @Test
    void listenerCanUnsubscribeItselfInsideItsHandler() {
        Sensor motion = new Sensor("motion", EventType.MOTION);
        WelcomeHomeRule welcome = new WelcomeHomeRule(motion);
        EventLog log = new EventLog();
        motion.subscribe(EventType.MOTION, welcome);
        motion.subscribe(EventType.MOTION, log);

        motion.report("hall", 1);
        motion.report("bedroom", 1);

        assertEquals(Optional.of("hall"), welcome.welcomedRoom());
        assertEquals(2, log.total(), "other listeners still get every event");
        assertEquals(0, motion.failedDeliveries());
    }

    @Test
    void listenerAddedDuringPublishGetsOnlyLaterEvents() {
        Sensor motion = new Sensor("motion", EventType.MOTION);
        EventLog lateLog = new EventLog();
        motion.subscribe(EventType.MOTION, event -> motion.subscribe(EventType.MOTION, lateLog));

        motion.report("hall", 1);
        motion.report("hall", 1);

        assertEquals(1, lateLog.total());
    }

    @Test
    void subscribingTheSameListenerTwiceDeliversOnce() {
        Sensor motion = new Sensor("motion", EventType.MOTION);
        EventLog log = new EventLog();
        motion.subscribe(EventType.MOTION, log);
        motion.subscribe(EventType.MOTION, log);

        motion.report("hall", 1);

        assertEquals(1, log.total());
    }

    @Test
    void failingListenerDoesNotStopDeliveryToOthers() {
        Sensor motion = new Sensor("motion", EventType.MOTION);
        List<HomeEvent> received = new ArrayList<>();
        motion.subscribe(EventType.MOTION, event -> {
            throw new IllegalStateException("broken listener");
        });
        motion.subscribe(EventType.MOTION, received::add);

        motion.report("hall", 1);

        assertEquals(1, received.size());
        assertEquals(1, motion.failedDeliveries());
    }

    @Test
    void eventIsAnImmutableValueWithRequiredType() {
        HomeEvent event = HomeEvent.of(EventType.MOTION, "motion", "hall", 1);

        assertEquals(HomeEvent.of(EventType.MOTION, "motion", "hall", 1), event);
        assertThrows(NullPointerException.class, () -> HomeEvent.of(null, "motion", "hall", 1));
    }
}
