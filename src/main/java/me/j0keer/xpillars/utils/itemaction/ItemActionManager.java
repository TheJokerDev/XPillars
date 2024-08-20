package me.j0keer.xpillars.utils.itemaction;

import lombok.Getter;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.utils.itemaction.actions.*;
import org.bukkit.entity.Player;

import java.util.HashMap;

@Getter
public class ItemActionManager {
    private final XPillars plugin;

    private final HashMap<String, ItemActionExecutor> commands = new HashMap<>();

    public ItemActionManager(XPillars plugin) {
        this.plugin = plugin;
        registerDefaults();
    }

    public void registerDefaults() {
        register("sound", new Sound());
        register("close", new Close());
        register("server", new Server());
        register("cmd", new Command());
        register("op", new CommandOp());
        register("console", new CommandConsole());
        register("msg", new Message());
        register("open", new Open());
        register("title", new Title());
    }

    public void register(String name, ItemActionExecutor executor) {
        commands.put("[" + name + "]", executor);
    }

    public void execute(Player player, String label) {
        String name = label.split("]")[0] + "]";
        ItemActionExecutor executor = commands.get(name.toLowerCase());

        label = label.replace(name, "");
        while (label.startsWith(" ")) {
            label = label.substring(1);
        }
        if (executor == null) return;
        executor.onCommand(player, label);
    }

}