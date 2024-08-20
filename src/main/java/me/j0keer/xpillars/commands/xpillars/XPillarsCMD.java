package me.j0keer.xpillars.commands.xpillars;

import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.commands.xpillars.sub.ArenaSubCMD;
import me.j0keer.xpillars.commands.xpillars.sub.ModifySubCMD;
import me.j0keer.xpillars.commands.xpillars.sub.ReloadSubCMD;
import me.j0keer.xpillars.commands.xpillars.sub.SetSpawnSubCMD;
import me.j0keer.xpillars.utils.commands.CMD;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class XPillarsCMD extends CMD {
    public XPillarsCMD(XPillars plugin) {
        super(plugin);

        addSubCMD(new ArenaSubCMD(plugin));
        addSubCMD(new ReloadSubCMD(plugin));
        addSubCMD(new SetSpawnSubCMD(plugin));
        addSubCMD(new ModifySubCMD(plugin));
    }

    @Override
    public String getName() {
        return "xpillars";
    }

    @Override
    public String getDescription() {
        return "Main command for XPillars plugin.";
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public String getPermissionError() {
        return "";
    }

    @Override
    public List<String> getAliases() {
        return List.of("pillars", "x-pillars");
    }

    @Override
    public boolean isTabComplete() {
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return executeCMD(commandSender, s, strings);
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return execute(commandSender, s, strings);
    }
}
