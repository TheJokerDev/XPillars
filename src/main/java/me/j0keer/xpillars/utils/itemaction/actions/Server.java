package me.j0keer.xpillars.utils.itemaction.actions;

import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.utils.itemaction.ItemActionExecutor;
import org.bukkit.entity.Player;

public class Server implements ItemActionExecutor {

    @Override
    public boolean onCommand(Player player, String label) {
        XPillars.getInstance().sendPlayer(player, label);
        return false;
    }
}