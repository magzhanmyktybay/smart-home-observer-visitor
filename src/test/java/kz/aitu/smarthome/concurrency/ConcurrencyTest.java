package kz.aitu.smarthome.concurrency;

import kz.aitu.smarthome.event.EventType;
import kz.aitu.smarthome.listener.EnergyTracker;
import kz.aitu.smarthome.listener.EventLog;
import kz.aitu.smarthome.source.Sensor;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConcurrencyTest {

    private static final List<String> ROOMS = List.of("kitchen", "bedroom", "hall");

    private static List<SensorProducer> producers(Sensor sensor, int count, int eventsEach) {
        List<SensorProducer> producers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            producers.add(new SensorProducer(sensor, ROOMS, eventsEach, () -> 1.0));
        }
        return producers;
    }

    @Test
    void stressFourThreadsTimesThousandEventsLosesNothing() throws InterruptedException {
        Sensor powerMeter = new Sensor("power", EventType.POWER_READING);
        EnergyTracker tracker = new EnergyTracker(1000);
        EventLog log = new EventLog();
        powerMeter.subscribe(EventType.POWER_READING, tracker);
        powerMeter.subscribe(EventType.POWER_READING, log);
        tracker.subscribe(EventType.ENERGY_SUMMARY, log);
        ProducerPool pool = new ProducerPool(Executors.newFixedThreadPool(4), Duration.ofSeconds(5));

        pool.runAll(producers(powerMeter, 4, 1000));
        pool.shutdown();

        assertEquals(4000, log.count(EventType.POWER_READING));
        assertEquals(4, log.count(EventType.ENERGY_SUMMARY), "one summary per 1000 readings");
        assertEquals(4000.0, tracker.totalWattHours(), 1e-9);
    }

    @Test
    void unsubscribingWhileOtherThreadsPublishIsSafe() throws InterruptedException {
        Sensor motion = new Sensor("motion", EventType.MOTION);
        EventLog leaving = new EventLog();
        EventLog staying = new EventLog();
        motion.subscribe(EventType.MOTION, leaving);
        motion.subscribe(EventType.MOTION, staying);
        ProducerPool pool = new ProducerPool(Executors.newFixedThreadPool(5), Duration.ofSeconds(5));

        List<Runnable> tasks = new ArrayList<>(producers(motion, 4, 1000));
        tasks.add(() -> motion.unsubscribe(EventType.MOTION, leaving));
        pool.runAll(tasks);
        long receivedBeforeLeaving = leaving.total();
        motion.report("hall", 1);
        pool.shutdown();

        assertEquals(0, motion.failedDeliveries());
        assertEquals(4001, staying.total());
        assertTrue(receivedBeforeLeaving <= 4000);
        assertEquals(receivedBeforeLeaving, leaving.total(), "no events after unsubscribe has returned");
    }

    @Test
    void gracefulShutdownLetsSubmittedWorkFinishAndStopsThreads() throws InterruptedException {
        Sensor powerMeter = new Sensor("power", EventType.POWER_READING);
        EventLog log = new EventLog();
        powerMeter.subscribe(EventType.POWER_READING, log);
        ExecutorService executor = Executors.newFixedThreadPool(3);
        ProducerPool pool = new ProducerPool(executor, Duration.ofSeconds(5));

        pool.runAll(producers(powerMeter, 3, 500));
        boolean stopped = pool.shutdown();

        assertTrue(stopped);
        assertTrue(executor.isTerminated());
        assertEquals(1500, log.total());
    }
}
