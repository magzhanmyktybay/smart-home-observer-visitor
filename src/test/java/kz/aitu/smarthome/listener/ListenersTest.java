package kz.aitu.smarthome.listener;

import kz.aitu.smarthome.event.EventType;
import kz.aitu.smarthome.source.Sensor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListenersTest {

    @Test
    void energyTrackerAggregatesAndRepublishesSummaryToNextObserver() {
        Sensor powerMeter = new Sensor("power", EventType.POWER_READING);
        EnergyTracker tracker = new EnergyTracker(2);
        MobileApp phone = new MobileApp();
        powerMeter.subscribe(EventType.POWER_READING, tracker);
        tracker.subscribe(EventType.ENERGY_SUMMARY, phone);

        powerMeter.report("kitchen", 10);
        powerMeter.report("bedroom", 5);
        powerMeter.report("kitchen", 1);

        assertEquals(11.0, tracker.wattHours("kitchen"), 1e-9);
        assertEquals(16.0, tracker.totalWattHours(), 1e-9);
        // summary is published after every 2nd reading: 10 + 5 = 15
        assertEquals(List.of("Energy used: 15.0 Wh"), phone.notifications());
    }

    @Test
    void automationRulesReactToHeatAndMotion() {
        AutomationRules rules = new AutomationRules(26.0);
        Sensor climate = new Sensor("climate", EventType.TEMPERATURE);
        Sensor motion = new Sensor("motion", EventType.MOTION);
        climate.subscribe(EventType.TEMPERATURE, rules);
        motion.subscribe(EventType.MOTION, rules);

        climate.report("kitchen", 25);
        climate.report("bedroom", 28);
        motion.report("hall", 1);

        assertEquals(List.of("AC_ON bedroom", "LIGHTS_ON hall"), rules.commands());
    }
}
