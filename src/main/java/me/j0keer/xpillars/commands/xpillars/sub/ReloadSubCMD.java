package me.j0keer.xpillars.commands.xpillars.sub;

import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.utils.commands.SenderTypes;
import me.j0keer.xpillars.utils.commands.SubCMD;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadSubCMD extends SubCMD {
    public ReloadSubCMD(XPillars plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "xpillars.admin";
    }

    @Override
    public SenderTypes getSenderType() {
        return SenderTypes.BOTH;
    }

    @Override
    public String getUsage() {
        return "commands.xpillars.reload.usage";
    }

    @Override
    public boolean onCommand(CommandSender sender, String alias, String[] args) {
        getPlugin().reloadConfig();
        sendMSG(sender, "commands.xpillars.reload.success");
        return true;
    }

    @Override
    public List<String> onTab(CommandSender sender, String alias, String[] args) {
        return List.of();
    }
}
