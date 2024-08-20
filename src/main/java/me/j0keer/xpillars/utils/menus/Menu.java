package me.j0keer.xpillars.utils.menus;

import lombok.Getter;
import lombok.Setter;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.items.Button;
import me.j0keer.xpillars.items.ButtonClickEvent;
import me.j0keer.xpillars.player.GamePlayer;
import me.j0keer.xpillars.utils.FileUtils;
import me.j0keer.xpillars.utils.TextUtils;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Menu {
    private final GamePlayer gamePlayer;
    private final Player player;
    private final XPillars plugin;
    private final String id;

    private int size;
    private String title;
    private Inventory inventory;

    private String permission = "";

    private List<Button> buttons;

    private FileUtils config;

    public Menu(XPillars plugin, GamePlayer gamePlayer, String id) {
        this.plugin = plugin;
        this.gamePlayer = gamePlayer;
        this.player = gamePlayer.getPlayer();
        this.id = id;

        File folder = new File(plugin.getDataFolder() + "/menus");
        this.config = new FileUtils(folder,  id + ".yml");
        this.size = config.getInt("settings.rows", 3);
        this.title = config.getString("settings.title", "Change this title in settings.title");
        this.title = TextUtils.processPlaceholders(getPlayer(), title);

        init();
    }

    public void init() {
        buttons = new ArrayList<>();
    }

    public Inventory getInventory() {
        if (inventory == null) {
            inventory = plugin.getServer().createInventory(null, size * 9, title);
        }
        return inventory;
    }

    public abstract void onOpen(InventoryOpenEvent event);

    public abstract void onClose(InventoryCloseEvent event);

    public abstract void onClick(InventoryClickEvent event);

    public abstract void onUpdate();

    public abstract void onReload();

    public void setItem(Button button) {
        for (Integer slot : button.getSlots()) {
            setItem(slot, button.getItem());
        }
    }

    public void setItem(int slot, SimpleItem item) {
        getInventory().setItem(slot, item.build(getPlayer()));
    }

    public void addButton(Button button) {
        buttons.add(button);
    }

    public void update() {
        getInventory().clear();
        buttons.forEach(this::setItem);
    }

    public void open() {
        getPlayer().openInventory(getInventory());
    }

    public void close() {
        getPlayer().closeInventory();
    }

    public void reload() {
        getConfig().reload();
        onReload();
        update();
    }

    public void clear() {
        getInventory().clear();
    }

    public void clearButtons() {
        buttons.clear();
    }

    public boolean canOpen() {
        boolean open = getPermission().isEmpty() || getPermission().equalsIgnoreCase("none");

        if (!open) {
            open = getPlayer().hasPermission(getPermission());
        }

        if (!open) {
            getGamePlayer().sendMSG("general.noPermission");
            close();
        }

        return open;
    }

    public void loadButtons() {
        if (getConfig().get("extra-items") != null) {
            getConfig().getSection("extra-items").getKeys(false).forEach(key -> {
                Button button = new Button(plugin, getConfig().getSection("extra-items." + key));
                addButton(button);
            });
        }
    }

    public void click(InventoryClickEvent event) {
        if (!getButtons().isEmpty()) {
            getButtons().forEach(b -> {
                if (b.getSlots().contains(event.getSlot())) {
                    b.onClick(new ButtonClickEvent(event));
                }
            });
        }
    }
}