package kz.aitu.smarthome.home;

/** Element of the smart home Composite tree. Elements hold data only. */
public interface Node {
    String id();

    /** First dispatch: the real class of the node picks its own visitXxx method. */
    <R> R accept(Visitor<R> visitor);
}
