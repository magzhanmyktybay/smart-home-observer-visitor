package kz.aitu.smarthome.app;

/**
 * Configurable thread count and load. Usage: {@code java -jar app.jar [threads] [eventsPerProducer]}.
 */
public record SimulationConfig(int threads, int eventsPerProducer) {

    public static final SimulationConfig DEFAULT = new SimulationConfig(3, 1000);

    public SimulationConfig {
        if (threads <= 0 || eventsPerProducer <= 0) {
            throw new IllegalArgumentException("threads and eventsPerProducer must be positive");
        }
    }

    public static SimulationConfig fromArgs(String[] args) {
        int threads = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT.threads();
        int events = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT.eventsPerProducer();
        return new SimulationConfig(threads, events);
    }
}
