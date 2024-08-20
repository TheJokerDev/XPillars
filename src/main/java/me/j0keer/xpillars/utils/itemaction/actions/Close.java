package me.j0keer.xpillars.utils.itemaction.actions;

import me.j0keer.xpillars.utils.itemaction.ItemActionExecutor;
import org.bukkit.entity.Player;

public class Close implements ItemActionExecutor {

    @Override
    public boolean onCommand(Player player, String label) {
        player.closeInventory();
        return false;
    }
}