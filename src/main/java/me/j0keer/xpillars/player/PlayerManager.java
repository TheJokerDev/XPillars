package me.j0keer.xpillars.player;

import me.j0keer.xpillars.XPillars;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager implements Listener {
    private final XPillars plugin;
    private final ConcurrentHashMap<UUID, GamePlayer> players;

    public PlayerManager(XPillars plugin) {
        this.plugin = plugin;

        plugin.listener(this);

        players = new ConcurrentHashMap<>();
    }

    public GamePlayer getPlayer(Player p) {
        return players.computeIfAbsent(p.getUniqueId(), uuid -> new GamePlayer(plugin, p));
    }

    public int getPlayersCount() {
        return players.size();
    }

    public List<GamePlayer> getPlayers() {
        return new ArrayList<>(players.values());
    }

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent e) {
        players.put(e.getUniqueId(), new GamePlayer(plugin, e.getName(), e.getUniqueId()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        GamePlayer player = getPlayer(event.getPlayer());
        player.init();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        players.remove(e.getPlayer().getUniqueId()).remove();
    }

    public void onDisable() {
        players.values().forEach(p -> p.save(false));
    }
}
