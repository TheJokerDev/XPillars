package me.j0keer.xpillars.utils.menus;

import lombok.Getter;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.player.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.io.File;

@Getter
public class MenusManager implements Listener {

    private final XPillars plugin;

    public File folder;

    public MenusManager(XPillars plugin) {
        this.plugin = plugin;
        folder = new File(plugin.getDataFolder() + "/menus");
        if (!folder.exists()) {
            folder.mkdir();
        }
        plugin.listener(this);
    }

    public void reload() {
        new Thread(() -> {
            getPlugin().getPlayerManager().getPlayers().forEach(gamePlayer -> {
                gamePlayer.getMenus().values().forEach(Menu::reload);
            });
        }).start();
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player p) {
            GamePlayer gamePlayer = getPlugin().getPlayer(p);
            for (Menu menu : gamePlayer.getMenus().values()) {
                if (event.getView().getTitle().equals(menu.getTitle())) {
                    menu.onOpen(event);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        if (event.getPlayer() instanceof Player p) {
            GamePlayer gamePlayer = getPlugin().getPlayer(p);
            for (Menu menu : gamePlayer.getMenus().values()) {
                if (event.getView().getTitle().equals(menu.getTitle())) {
                    menu.onClose(event);
                }
            }
        }

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.getWhoClicked() instanceof Player p) {
            GamePlayer gamePlayer = getPlugin().getPlayer(p);
            for (Menu menu : gamePlayer.getMenus().values()) {
                if (event.getView().getTitle().equals(menu.getTitle())) {
                    menu.onClick(event);
                }
            }
        }

    }
}
