package me.j0keer.xpillars.utils.itemaction;

import org.bukkit.entity.Player;

public interface ItemActionExecutor {

    boolean onCommand(Player player, String label);

}