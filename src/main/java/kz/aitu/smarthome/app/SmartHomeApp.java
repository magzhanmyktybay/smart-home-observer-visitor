package kz.aitu.smarthome.app;

import kz.aitu.smarthome.concurrency.ProducerPool;
import kz.aitu.smarthome.concurrency.SensorProducer;
import kz.aitu.smarthome.event.EventType;
import kz.aitu.smarthome.home.Home;
import kz.aitu.smarthome.integration.SecurityAlarm;
import kz.aitu.smarthome.listener.AutomationRules;
import kz.aitu.smarthome.listener.EnergyTracker;
import kz.aitu.smarthome.listener.EventLog;
import kz.aitu.smarthome.listener.MobileApp;
import kz.aitu.smarthome.listener.WelcomeHomeRule;
import kz.aitu.smarthome.source.Sensor;
import kz.aitu.smarthome.visitor.DeviceStatsVisitor;
import kz.aitu.smarthome.visitor.JsonExportVisitor;
import kz.aitu.smarthome.visitor.PowerUsageVisitor;
import kz.aitu.smarthome.visitor.SecurityAuditVisitor;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/** Composition root: the only place where objects are created and wired together. */
public final class SmartHomeApp {

    private static final List<String> ROOMS = List.of("kitchen", "living-room", "bedroom");

    private SmartHomeApp() {
    }

    public static void main(String[] args) throws InterruptedException {
        SimulationConfig config = SimulationConfig.fromArgs(args);
        Home home = SampleHome.create();

        // subjects (data sources)
        Sensor powerMeter = new Sensor("power-meter", EventType.POWER_READING);
        Sensor climateSensor = new Sensor("climate-sensor", EventType.TEMPERATURE);
        Sensor motionSensor = new Sensor("motion-sensor", EventType.MOTION);

        // listeners (two of them are also subjects)
        EnergyTracker energyTracker = new EnergyTracker(500);
        SecurityAlarm securityAlarm = new SecurityAlarm(home);
        MobileApp mobileApp = new MobileApp();
        AutomationRules automationRules = new AutomationRules(26.0);
        EventLog eventLog = new EventLog();
        WelcomeHomeRule welcomeHomeRule = new WelcomeHomeRule(motionSensor);

        // wiring
        powerMeter.subscribe(EventType.POWER_READING, energyTracker);
        powerMeter.subscribe(EventType.POWER_READING, eventLog);
        climateSensor.subscribe(EventType.TEMPERATURE, automationRules);
        climateSensor.subscribe(EventType.TEMPERATURE, eventLog);
        motionSensor.subscribe(EventType.MOTION, securityAlarm);
        motionSensor.subscribe(EventType.MOTION, automationRules);
        motionSensor.subscribe(EventType.MOTION, welcomeHomeRule);
        motionSensor.subscribe(EventType.MOTION, eventLog);
        energyTracker.subscribe(EventType.ENERGY_SUMMARY, mobileApp);
        energyTracker.subscribe(EventType.ENERGY_SUMMARY, eventLog);
        securityAlarm.subscribe(EventType.SECURITY_REPORT, mobileApp);
        securityAlarm.subscribe(EventType.SECURITY_REPORT, eventLog);
        securityAlarm.arm();

        ProducerPool pool = new ProducerPool(Executors.newFixedThreadPool(config.threads()), Duration.ofSeconds(10));
        int events = config.eventsPerProducer();
        pool.runAll(List.of(
                new SensorProducer(powerMeter, ROOMS, events, () -> randomBetween(5, 50)),
                new SensorProducer(climateSensor, ROOMS, events, () -> randomBetween(18, 30)),
                new SensorProducer(motionSensor, ROOMS, Math.max(1, events / 100), () -> 1)));
        boolean stopped = pool.shutdown();

        System.out.println("=== Observer side (" + config.threads() + " threads) ===");
        for (EventType type : EventType.values()) {
            System.out.println(type + ": " + eventLog.count(type));
        }
        System.out.println("Total events: " + eventLog.total() + ", all threads stopped: " + stopped);
        System.out.printf("Energy tracked: %.1f Wh%n", energyTracker.totalWattHours());
        System.out.println("Welcomed in: " + welcomeHomeRule.welcomedRoom().orElse("-"));
        System.out.println("Automation commands: " + automationRules.commands().size());
        List<String> notifications = mobileApp.notifications();
        System.out.println("Phone notifications: " + notifications.size() + ", first ones:");
        notifications.stream().limit(4).forEach(text -> System.out.println("  " + text));

        System.out.println("=== Visitor side ===");
        System.out.println("Power now: " + home.accept(new PowerUsageVisitor()) + " W");
        DeviceStatsVisitor statsVisitor = new DeviceStatsVisitor();
        home.accept(statsVisitor);
        System.out.println("Stats: " + statsVisitor.stats());
        System.out.println("Security weak points: " + home.accept(new SecurityAuditVisitor()));
        System.out.println("JSON: " + home.accept(new JsonExportVisitor()));
    }

    private static double randomBetween(double from, double to) {
        return ThreadLocalRandom.current().nextDouble(from, to);
    }
}
