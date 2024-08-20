package me.j0keer.xpillars.arena;

import lombok.Getter;
import me.j0keer.xpillars.XPillars;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ArenaManager {
    private final XPillars plugin;
    private List<Arena> arenas = new ArrayList<>();

    public ArenaManager(XPillars plugin) {
        this.plugin = plugin;
        new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(plugin, 0, 20L);
    }

    public void tick() {
        getArenas().forEach(Arena::tick);
    }
}
