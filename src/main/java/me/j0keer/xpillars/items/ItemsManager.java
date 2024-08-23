package me.j0keer.xpillars.items;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.player.GamePlayer;
import me.j0keer.xpillars.utils.FileUtils;
import me.j0keer.xpillars.utils.menus.SimpleItem;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

@Getter
public class ItemsManager implements Listener {
    private final XPillars plugin;
    private File fileItems;
    private FileUtils config;
    public HashMap<String, List<Button>> buttons = new HashMap<>();

    public ItemsManager(XPillars plugin) {
        this.plugin = plugin;
        this.fileItems = new File(plugin.getDataFolder(), "items.yml");
        loadItems();
        plugin.listener(this);
    }

    public void reload() {
        Thread thread = new Thread(() -> {
            getConfig().reload();
            loadItems();
            getPlugin().debug("Items reloaded");
            getPlugin().getPlayerManager().getPlayers().forEach(GamePlayer::setItems);
        });

        thread.start();
    }

    public void loadItems() {
        buttons.clear();

        List<String> keys = new ArrayList<>(Collections.singletonList("lobby"));

        for (String key : keys) {
            List<Button> list = new ArrayList<>();
            if (getConfig().get(key) == null) {
                continue;
            }
            for (String s : getConfig().getSection(key).getKeys(false)) {
                list.add(new Button(plugin, getConfig().getSection(key + "." + s)));
            }
            buttons.put(key, list);
        }
    }

    public static ItemStack setPlaceHolders(ItemStack item, Player p) {
        if (item.getType() == Material.AIR) {
            return item;
        }
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(PlaceholderAPI.setPlaceholders(p, meta.getDisplayName()));
        List<String> lore;
        if (meta.hasLore()) {
            lore = new ArrayList<>();
            for (String s : meta.getLore()) {
                lore.add(PlaceholderAPI.setPlaceholders(p, s));
            }
        }
        item.setItemMeta(meta);
        return item;
    }

    public static SimpleItem setPlaceHolders(SimpleItem item, Player p) {
        SimpleItem simpleItem = item.clone();
        if (simpleItem.hasDisplayName()) {
            simpleItem.setDisplayName(PlaceholderAPI.setPlaceholders(p, simpleItem.getDisplayName()));
        }
        if (simpleItem.hasLore()) {
            List<String> lore;
            if (simpleItem.hasLore()) {
                lore = new ArrayList<>();
                for (String s : simpleItem.getLore()) {
                    s = PlaceholderAPI.setPlaceholders(p, s);
                    s = s.replace("\\n", "\n");
                    if (s.contains("\n")) {
                        lore.addAll(Arrays.asList(s.split("\n")));
                    } else {
                        lore.add(s);
                    }
                }
                simpleItem.setLore(lore);
            }
        }
        return simpleItem;
    }

    public FileUtils getConfig() {
        if (config == null) {
            config = new FileUtils(fileItems);
        }
        return config;
    }

    //Listeners
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        GamePlayer player = plugin.getPlayerManager().getPlayer(p);

        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR || event.getHand() != EquipmentSlot.HAND || player == null || player.getPlayer() == null)
            return;

        player.getActualButtons().forEach(b -> {
            if (b.getItemStack().isSimilar(item)) {
                b.onClick(new ButtonClickEvent(event));
            }
        });
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        GamePlayer player = plugin.getPlayerManager().getPlayer(p);

        ItemStack item = event.getItemDrop().getItemStack();
        if (item.getType() == Material.AIR || player == null || player.getPlayer() == null)
            return;

        event.setCancelled(player.isPlaying() || (player.getGame() == null && p.getGameMode() != GameMode.CREATIVE));
    }

    @EventHandler
    public void onPick(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player p = (Player) event.getEntity();
        GamePlayer player = plugin.getPlayerManager().getPlayer(p);

        ItemStack item = event.getItem().getItemStack();
        if (item.getType() == Material.AIR || player == null || player.getPlayer() == null)
            return;

        event.setCancelled(player.isPlaying() || (player.getGame() == null && p.getGameMode() != GameMode.CREATIVE));
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player p = event.getPlayer();
        GamePlayer player = plugin.getPlayerManager().getPlayer(p);

        ItemStack item = event.getOffHandItem();
        if (item == null || item.getType() == Material.AIR || player == null || player.getPlayer() == null)
            return;

        event.setCancelled(player.isPlaying() || (player.getGame() == null && p.getGameMode() != GameMode.CREATIVE));
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        GamePlayer player = plugin.getPlayerManager().getPlayer(p);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR || player == null || player.getPlayer() == null)
            return;

        event.setCancelled(player.isPlaying() || (player.getGame() == null && p.getGameMode() != GameMode.CREATIVE));
    }
}
