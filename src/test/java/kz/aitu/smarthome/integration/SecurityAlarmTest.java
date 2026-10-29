package kz.aitu.smarthome.integration;

import kz.aitu.smarthome.app.SampleHome;
import kz.aitu.smarthome.event.EventType;
import kz.aitu.smarthome.event.HomeEvent;
import kz.aitu.smarthome.listener.MobileApp;
import kz.aitu.smarthome.source.Sensor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityAlarmTest {

    private Sensor motion;
    private SecurityAlarm alarm;
    private List<HomeEvent> reports;
    private MobileApp phone;

    @BeforeEach
    void wire() {
        motion = new Sensor("motion", EventType.MOTION);
        alarm = new SecurityAlarm(SampleHome.create());
        reports = new ArrayList<>();
        phone = new MobileApp();
        motion.subscribe(EventType.MOTION, alarm);
        alarm.subscribe(EventType.SECURITY_REPORT, reports::add);
        alarm.subscribe(EventType.SECURITY_REPORT, phone);
    }

    @Test
    void motionInArmedHomeInspectsOnlyThatRoomAndPublishesReport() {
        alarm.arm();

        motion.report("bedroom", 1);

        assertEquals(List.of(new HomeEvent(EventType.SECURITY_REPORT, SecurityAlarm.SOURCE_ID, "bedroom", 2,
                "balcony-door, bedroom-camera")), reports);
        assertEquals(List.of("Security check in bedroom: balcony-door, bedroom-camera"), phone.notifications());
    }

    @Test
    void secureRoomGivesEmptyReport() {
        alarm.arm();

        motion.report("kitchen", 1);

        assertEquals(1, reports.size());
        assertEquals(0.0, reports.get(0).value(), 1e-9);
        assertEquals(List.of("Security check in kitchen: all secure"), phone.notifications());
    }

    @Test
    void disarmedAlarmIgnoresMotion() {
        motion.report("bedroom", 1);

        assertTrue(reports.isEmpty());
    }
}
