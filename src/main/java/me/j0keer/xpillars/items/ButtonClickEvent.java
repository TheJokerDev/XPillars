package me.j0keer.xpillars.items;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ButtonClickEvent {
    private final Event event;

    private final Player player;
    private final List<String> actions = new ArrayList<>();

    public ButtonClickEvent(Event event) {
        this.event = event;

        if (event instanceof PlayerInteractEvent) {
            PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
            player = interactEvent.getPlayer();
            actions.add(interactEvent.getAction().name());
        } else if (event instanceof InventoryClickEvent) {
            InventoryClickEvent clickEvent = (InventoryClickEvent) event;
            player = (Player) clickEvent.getWhoClicked();
            actions.add("LEFT_CLICK");
        } else {
            player = null;
        }
    }

    public void setCanceled(boolean canceled) {
        if (event instanceof InventoryClickEvent) {
            InventoryClickEvent clickEvent = (InventoryClickEvent) event;
            clickEvent.setCancelled(canceled);
        } else if (event instanceof PlayerInteractEvent) {
            PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
            interactEvent.setCancelled(canceled);
        }
    }
}
