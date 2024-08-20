package me.j0keer.xpillars.utils.itemaction.actions;

import com.cryptomorin.xseries.messages.Titles;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.utils.TextUtils;
import me.j0keer.xpillars.utils.itemaction.ItemActionExecutor;
import org.bukkit.entity.Player;

public class ActionBar implements ItemActionExecutor {

    @Override
    public boolean onCommand(Player player, String label) {
        String[] split = label.split("`");
        String message = "";
        long duration = 60;
        if (split.length == 1) {
            message = split[0];
        }
        if (split.length == 2) {
            message = split[0];
            duration = Long.parseLong(split[1]);
        }

        if (!message.isEmpty() || duration > 0) {
            com.cryptomorin.xseries.messages.ActionBar.sendActionBar(XPillars.getInstance(), player, formatMSG(player, message), duration);
        }
        return false;
    }

    public String formatMSG(Player player, String message) {
        return TextUtils.processPlaceholders(player, message);
    }
}