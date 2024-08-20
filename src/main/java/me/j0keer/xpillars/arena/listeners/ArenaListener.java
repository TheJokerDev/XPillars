package me.j0keer.xpillars.arena.listeners;

import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.arena.Arena;
import me.j0keer.xpillars.arena.events.ArenaTickEvent;
import me.j0keer.xpillars.utils.game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArenaListener implements Listener {
    private final XPillars plugin;

    public ArenaListener(XPillars plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onArenaTick(ArenaTickEvent event) {
        Arena arena = event.getArena();
        GameState state = arena.getState();

        if (state == GameState.WAITING) {
            if (arena.getLocalCountdown() > 0 && arena.getPlayersCount() < 1) {
                arena.setLocalCountdown(arena.getCountdown());
            } else if (arena.getPlayersCount() > arena.getMinPlayers()) {
                arena.tick();
            }
            if (arena.getLocalCountdown() <= 10) {
                arena.setState(GameState.STARTING);
            }
        }
        if (state == GameState.STARTING) {
            if (arena.getPlayersCount() < arena.getMinPlayers()) {
                arena.setLocalCountdown(arena.getCountdown());
                arena.setState(GameState.WAITING);
            } else {
                arena.tick();
            }
            if (arena.getLocalCountdown() <= 0) {
                arena.start();
            }
        }
        if (state == GameState.INGAME) {
            arena.tick();
            if (arena.getPlayersCount() < 1) {
                arena.setState(GameState.ENDING);
            }
        }
        if (state == GameState.ENDING) {
            arena.tick();
            if (arena.getLocalCountdown() <= 0) {
                arena.reset();
            }
        }
    }
}
