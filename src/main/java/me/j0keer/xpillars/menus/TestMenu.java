package me.j0keer.xpillars.menus;

import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.player.GamePlayer;
import me.j0keer.xpillars.utils.menus.Menu;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class TestMenu extends Menu {
    public TestMenu(XPillars plugin, GamePlayer gamePlayer) {
        super(plugin, gamePlayer, "test");
        onReload();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        if (!canOpen()) return;
        onUpdate();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        click(event);
    }

    @Override
    public void onUpdate() {
        update();
    }

    @Override
    public void onReload() {
        loadButtons();
    }
}
