package kz.aitu.smarthome.concurrency;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Runs producer tasks on an injected ExecutorService (thread count is chosen in the
 * composition root). Events are delivered synchronously in the producer thread, so when
 * every producer has finished, every event has reached its listeners: nothing is lost.
 */
public final class ProducerPool {

    private final ExecutorService executor;
    private final Duration shutdownTimeout;

    public ProducerPool(ExecutorService executor, Duration shutdownTimeout) {
        this.executor = executor;
        this.shutdownTimeout = shutdownTimeout;
    }

    /** Submits all producers and waits until each of them has finished. */
    public void runAll(List<? extends Runnable> producers) throws InterruptedException {
        List<Future<?>> futures = new ArrayList<>();
        for (Runnable producer : producers) {
            futures.add(executor.submit(producer));
        }
        for (Future<?> future : futures) {
            awaitProducer(future);
        }
    }

    private void awaitProducer(Future<?> future) throws InterruptedException {
        try {
            future.get();
        } catch (ExecutionException e) {
            throw new IllegalStateException("Producer failed", e.getCause());
        }
    }

    /**
     * Graceful shutdown: no new tasks, already submitted tasks are allowed to finish.
     * Only if they do not finish in time are they interrupted.
     *
     * @return true if every thread has stopped
     */
    public boolean shutdown() {
        executor.shutdown();
        try {
            if (executor.awaitTermination(shutdownTimeout.toMillis(), TimeUnit.MILLISECONDS)) {
                return true;
            }
            executor.shutdownNow();
            return executor.awaitTermination(shutdownTimeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
