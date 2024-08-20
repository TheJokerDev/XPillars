package me.j0keer.xpillars.arena.events;

import lombok.Getter;
import me.j0keer.xpillars.api.events.GameEvent;
import me.j0keer.xpillars.arena.Arena;

@Getter
public class ArenaTickEvent extends GameEvent {
    private Arena arena;

    public ArenaTickEvent(Arena arena) {
        this.arena = arena;
    }
}
