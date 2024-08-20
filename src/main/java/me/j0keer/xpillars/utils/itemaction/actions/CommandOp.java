package me.j0keer.xpillars.utils.itemaction.actions;

import me.j0keer.xpillars.utils.itemaction.ItemActionExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandOp implements ItemActionExecutor {

    @Override
    public boolean onCommand(Player player, String label) {
        Bukkit.dispatchCommand(player, label);
        return false;
    }
}