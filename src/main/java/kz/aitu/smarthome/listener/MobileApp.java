package kz.aitu.smarthome.listener;

import kz.aitu.smarthome.event.HomeEvent;
import kz.aitu.smarthome.observer.Observer;

import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/** Turns events into push notifications for the owner's phone. */
public final class MobileApp implements Observer<HomeEvent> {

    private final Queue<String> notifications = new ConcurrentLinkedQueue<>();

    @Override
    public void onEvent(HomeEvent event) {
        notifications.add(textOf(event));
    }

    private String textOf(HomeEvent event) {
        return switch (event.type()) {
            case ENERGY_SUMMARY -> String.format(Locale.ROOT, "Energy used: %.1f Wh", event.value());
            case SECURITY_REPORT -> "Security check in " + event.roomId() + ": "
                    + (event.value() == 0 ? "all secure" : event.detail());
            case TEMPERATURE -> String.format(Locale.ROOT, "Temperature in %s: %.1f C", event.roomId(), event.value());
            default -> event.type() + " in " + event.roomId();
        };
    }

    public List<String> notifications() {
        return List.copyOf(notifications);
    }
}
