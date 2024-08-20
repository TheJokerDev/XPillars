package me.j0keer.xpillars.utils.itemaction.actions;

import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.player.GamePlayer;
import me.j0keer.xpillars.utils.itemaction.ItemActionExecutor;
import me.j0keer.xpillars.utils.menus.Menu;
import org.bukkit.entity.Player;

public class Open implements ItemActionExecutor {

    @Override
    public boolean onCommand(Player player, String label) {
        GamePlayer gamePlayer = XPillars.getInstance().getPlayer(player);
        if (gamePlayer == null) return true;

        Menu menu = gamePlayer.getMenu(label);
        if (menu == null) {
            String msg = gamePlayer.getPlugin().getUtils().getMessage("menus.notExist");
            msg = msg.replace("{menu}", label);
            gamePlayer.sendMSG(msg);
            return true;
        }
        menu.open();
        return false;
    }
}