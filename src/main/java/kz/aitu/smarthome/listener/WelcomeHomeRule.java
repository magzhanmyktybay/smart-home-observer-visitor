package kz.aitu.smarthome.listener;

import kz.aitu.smarthome.event.EventType;
import kz.aitu.smarthome.event.HomeEvent;
import kz.aitu.smarthome.observer.Observer;
import kz.aitu.smarthome.observer.Subject;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * One-shot rule: greets the owner on the first motion and then leaves the subject
 * from inside its own handler. compareAndSet guarantees "only once" even if several
 * producer threads deliver motion events at the same moment.
 */
public final class WelcomeHomeRule implements Observer<HomeEvent> {

    private final Subject<EventType, HomeEvent> motionSource;
    private final AtomicReference<String> welcomedRoom = new AtomicReference<>();

    public WelcomeHomeRule(Subject<EventType, HomeEvent> motionSource) {
        this.motionSource = motionSource;
    }

    @Override
    public void onEvent(HomeEvent motion) {
        if (welcomedRoom.compareAndSet(null, motion.roomId())) {
            motionSource.unsubscribe(EventType.MOTION, this);
        }
    }

    public Optional<String> welcomedRoom() {
        return Optional.ofNullable(welcomedRoom.get());
    }
}
