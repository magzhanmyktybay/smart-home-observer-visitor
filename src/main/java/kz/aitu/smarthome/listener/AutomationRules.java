package kz.aitu.smarthome.listener;

import kz.aitu.smarthome.event.HomeEvent;
import kz.aitu.smarthome.observer.Observer;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/** Simple "if this then that" rules: hot room turns on AC, motion turns on lights. */
public final class AutomationRules implements Observer<HomeEvent> {

    private final double coolingThresholdCelsius;
    private final Queue<String> commands = new ConcurrentLinkedQueue<>();

    public AutomationRules(double coolingThresholdCelsius) {
        this.coolingThresholdCelsius = coolingThresholdCelsius;
    }

    @Override
    public void onEvent(HomeEvent event) {
        switch (event.type()) {
            case TEMPERATURE -> {
                if (event.value() > coolingThresholdCelsius) {
                    commands.add("AC_ON " + event.roomId());
                }
            }
            case MOTION -> commands.add("LIGHTS_ON " + event.roomId());
            default -> { }
        }
    }

    public List<String> commands() {
        return List.copyOf(commands);
    }
}
