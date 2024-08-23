package me.j0keer.xpillars.listeners;

import lombok.Getter;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.player.GamePlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LobbyListener implements Listener {
    private final XPillars plugin;
    
    public LobbyListener(XPillars plugin) {
        this.plugin = plugin;
    }
    
    public void reload() {
        if (!plugin.getConfig().getBoolean("settings.lobbyListener", true)){
            HandlerList.unregisterAll(this);
        } else {
            plugin.listener(this);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.getSpawn() != null) {
            player.teleport(plugin.getSpawn());
        }
    }

    @EventHandler
    public void onRespawn(PlayerSpawnLocationEvent event) {
        Player player = event.getPlayer();
        if (plugin.getSpawn() != null) {
            if (player.getWorld().equals(plugin.getSpawn().getWorld())) {
                event.setSpawnLocation(plugin.getSpawn());
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            GamePlayer player = plugin.getPlayerManager().getPlayer(p);
            event.setCancelled(!player.isPlaying());
        }
        if (plugin.getSpawn() != null) {
            if (event.getEntity().getWorld().equals(plugin.getSpawn().getWorld()) || event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getEntity().teleport(plugin.getSpawn());
                    }
                }.runTaskLater(plugin, 1L);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            GamePlayer player = plugin.getPlayerManager().getPlayer(p);
            event.setCancelled(!player.isPlaying());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(player);
        event.setCancelled(gamePlayer.getGame() != null && !gamePlayer.isPlaying() || gamePlayer.getGame() == null && player.getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(player);

        event.setCancelled(gamePlayer.getGame() != null && !gamePlayer.isPlaying() || gamePlayer.getGame() == null && player.getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(player);
        event.setCancelled(gamePlayer.getGame() != null && !gamePlayer.isPlaying() || gamePlayer.getGame() == null && player.getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        List<CreatureSpawnEvent.SpawnReason> allowedReasons = new ArrayList<>(Arrays.asList(CreatureSpawnEvent.SpawnReason.CUSTOM, CreatureSpawnEvent.SpawnReason.COMMAND));
        event.setCancelled(plugin.getSpawn() != null && event.getEntity().getWorld().equals(plugin.getSpawn().getWorld()) && allowedReasons.contains(event.getSpawnReason()));
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            GamePlayer player = plugin.getPlayerManager().getPlayer(p);
            if (!player.isPlaying()) {
                event.getEntity().setFoodLevel(20);
                event.setCancelled(true);
            }
        }
    }
}
