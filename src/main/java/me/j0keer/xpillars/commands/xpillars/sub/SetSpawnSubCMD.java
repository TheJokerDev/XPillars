package me.j0keer.xpillars.commands.xpillars.sub;

import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.utils.commands.SenderTypes;
import me.j0keer.xpillars.utils.commands.SubCMD;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetSpawnSubCMD extends SubCMD {
    public SetSpawnSubCMD(XPillars plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "setspawn";
    }

    @Override
    public String getPermission() {
        return "xpillars.admin";
    }

    @Override
    public SenderTypes getSenderType() {
        return SenderTypes.PLAYER;
    }

    @Override
    public String getUsage() {
        return "commands.xpillars.setspawn.usage";
    }

    @Override
    public boolean onCommand(CommandSender sender, String alias, String[] args) {
        Player player = (Player) sender;
        Location location = player.getLocation();

        getPlugin().getConfig().set("settings.spawn", location);
        getPlugin().saveConfig();
        sendMSG(sender, "commands.xpillars.setspawn.success");
        return true;
    }

    @Override
    public List<String> onTab(CommandSender sender, String alias, String[] args) {
        return List.of();
    }
}
