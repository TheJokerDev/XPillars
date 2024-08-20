package me.j0keer.xpillars.utils;

import lombok.Getter;
import lombok.Setter;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.data.DatabaseManager;
import me.j0keer.xpillars.hooks.PAPIHook;
import me.j0keer.xpillars.items.ItemsManager;
import me.j0keer.xpillars.listeners.LobbyListener;
import me.j0keer.xpillars.player.GamePlayer;
import me.j0keer.xpillars.player.PlayerManager;
import me.j0keer.xpillars.utils.board.BoardManager;
import me.j0keer.xpillars.utils.commands.CMDManager;
import me.j0keer.xpillars.utils.itemaction.ItemActionManager;
import me.j0keer.xpillars.utils.menus.MenusManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Arrays;

@Getter
@Setter
public abstract class PluginUtils extends JavaPlugin {
    private Utils utils;

    public void onEnable(XPillars plugin) {
        utils = new Utils(plugin);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    public abstract void onDisable();

    public void console(String... msg) {
        Arrays.stream(msg).forEach(m -> getUtils().sendMSG(getServer().getConsoleSender(), m));
    }

    public void debug(String... msg) {
        if (getConfig().getBoolean("settings.debug", false)) {
            Arrays.stream(msg).forEach(m -> getUtils().sendMSG(getServer().getConsoleSender(), "&6[DEBUG]&7: &e" + m));
        }
    }

    public void sendPlayer(Player player, String server) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
            b.close();
            out.close();
        } catch (Exception ignored) {
        }
    }


    @Override
    public void reloadConfig() {
        super.reloadConfig();

        if (getPapiHook() != null) {
            getPapiHook().unregister();
            getPapiHook().register();
        }

        if (getBoardManager() != null) {
            getBoardManager().setTask();
        }

        if (getItemsManager() != null) {
            getItemsManager().reload();
        }

        if (getLobbyListener() != null) {
            getLobbyListener().reload();
        }

        if (getMenusManager() != null) {
            getMenusManager().reload();
        }
    }

    public void listener(Listener... listeners) {
        Arrays.stream(listeners).forEach(l -> getServer().getPluginManager().registerEvents(l, this));
    }

    private LobbyListener lobbyListener;

    public void loadListeners(XPillars plugin) {
        lobbyListener = new LobbyListener(plugin);
        lobbyListener.reload();
    }

    private DatabaseManager databaseManager;
    private PlayerManager playerManager;
    private CMDManager cmdManager;
    private BoardManager boardManager;
    private ItemActionManager itemActionManager;
    private ItemsManager itemsManager;
    private MenusManager menusManager;

    public void loadManagers(XPillars plugin) {
        itemsManager = new ItemsManager(plugin);
        menusManager = new MenusManager(plugin);
        databaseManager = new DatabaseManager(plugin);
        playerManager = new PlayerManager(plugin);
        cmdManager = new CMDManager(plugin);
        boardManager = new BoardManager(plugin);
        itemActionManager = new ItemActionManager(plugin);

        getServer().getOnlinePlayers().forEach(p -> getPlayerManager().getPlayer(p));
    }

    public GamePlayer getPlayer(Player player) {
        if (player == null) return null;
        return getPlayerManager().getPlayer(player);
    }

    private PAPIHook papiHook;

    public boolean checkDependencies(XPillars plugin) {
        PluginManager pm = getServer().getPluginManager();
        boolean enabled = true;

        if (pm.isPluginEnabled("PlaceholderAPI")) {
            console("{prefix}&ePlaceholderAPI &7found. Starting hook...");
            papiHook = new PAPIHook(plugin);
            papiHook.register();
            console("{prefix}&ePlaceholderAPI &7hooked.");
        } else {
            console("{prefix}&ePlaceholderAPI &7not found. Disabling plugin...");
            enabled = false;
        }

        return enabled;
    }
}
