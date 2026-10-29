package kz.aitu.smarthome.home;

import java.util.List;

/** A node with children. Lets visitors treat Home, Floor and Room the same way without duplication. */
public interface Container extends Node {
    List<? extends Node> children();
}
