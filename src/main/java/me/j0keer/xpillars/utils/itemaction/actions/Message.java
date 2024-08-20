package me.j0keer.xpillars.utils.itemaction.actions;

import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.utils.itemaction.ItemActionExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Message implements ItemActionExecutor {

    @Override
    public boolean onCommand(Player player, String label) {
        XPillars.getInstance().getUtils().sendMSG(player, label);
        return false;
    }
}