package me.j0keer.xpillars.utils.commands;

import lombok.Getter;
import me.j0keer.xpillars.XPillars;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class SubCMD {
    private final XPillars plugin;

    public SubCMD(XPillars plugin) {
        this.plugin = plugin;
    }

    public abstract String getName();

    public abstract String getPermission();

    public List<String> getAliases() {
        return new ArrayList<>();
    }

    public abstract SenderTypes getSenderType();

    public abstract String getUsage();

    public abstract boolean onCommand(CommandSender sender, String alias, String[] args);

    public abstract List<String> onTab(CommandSender sender, String alias, String[] args);


    public boolean check(CommandSender sender) {
        return check(sender, getPermission());
    }

    public static boolean check(CommandSender sender, String permission) {
        return CMD.check(sender, permission);
    }

    public void sendMSG(CommandSender sender, String msg) {
        getPlugin().getUtils().sendMSG(sender, msg);
    }
}