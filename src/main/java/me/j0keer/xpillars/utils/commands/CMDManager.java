package me.j0keer.xpillars.utils.commands;

import lombok.Getter;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.commands.xpillars.XPillarsCMD;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CMDManager {
    private final XPillars plugin;
    public List<CMD> commands = new ArrayList<>();

    public CMDManager(XPillars plugin) {
        this.plugin = plugin;

        commands.add(new XPillarsCMD(plugin));

        if (!commands.isEmpty()) commands.forEach(this::registerCommand);
    }


    public void registerCommand(CMD cmd) {
        if (plugin.getCommand(cmd.getName()) == null) {
            PluginCommand command = getCommand(cmd.getName(), plugin);
            if (cmd.getPermission() != null || !cmd.getPermission().equals("none")) {
                command.setPermission(cmd.getPermission());
            }
            if (cmd.getDescription() != null) command.setDescription(cmd.getDescription());
            if (cmd.getAliases().size() > 0) command.setAliases(cmd.getAliases());
            if (cmd.getPermissionError() != null) command.setPermissionMessage(cmd.getPermissionError());
            try {
                getCommandMap().register(plugin.getDescription().getName(), command);
            } catch (Exception e) {
                return;
            }
            plugin.getCommand(cmd.getName()).setExecutor(cmd);
            if (cmd.isTabComplete()) {
                plugin.getCommand(cmd.getName()).setTabCompleter(cmd);
            }
            plugin.console("   &b→ &fComando cargado: &a" + cmd.getName());
        }
    }

    private PluginCommand getCommand(String name, XPillars plugin) {
        PluginCommand command = null;
        try {
            Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            c.setAccessible(true);
            command = c.newInstance(name, plugin);
        } catch (SecurityException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return command;
    }

    private CommandMap getCommandMap() {
        CommandMap commandMap = null;
        try {
            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
                Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);
                commandMap = (CommandMap) f.get(Bukkit.getPluginManager());
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return commandMap;
    }


}