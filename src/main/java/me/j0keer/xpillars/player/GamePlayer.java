package me.j0keer.xpillars.player;

import fr.mrmicky.fastboard.FastBoard;
import lombok.Getter;
import lombok.Setter;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.items.Button;
import me.j0keer.xpillars.items.ButtonClickEvent;
import me.j0keer.xpillars.menus.TestMenu;
import me.j0keer.xpillars.utils.game.Game;
import me.j0keer.xpillars.utils.game.GameState;
import me.j0keer.xpillars.utils.menus.Menu;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class GamePlayer extends PlayerData {
    private final XPillars plugin;
    private final String name;
    private final UUID uuid;
    private FastBoard board;
    private Game game;

    public GamePlayer(XPillars plugin, Player player) {
        this.plugin = plugin;
        this.name = player.getName();
        this.uuid = player.getUniqueId();
        load();
        init();
    }

    public GamePlayer(XPillars plugin, String name, UUID uuid) {
        this.plugin = plugin;
        this.name = name;
        this.uuid = uuid;
        load();
    }

    public void init() {
        setItems();
        loadMenus();
    }

    public Player getPlayer() {
        return getPlugin().getServer().getPlayer(uuid);
    }

    public boolean isAlive() {
        if (getPlayer() == null || getGame() == null) return false;

        return getPlayer().getGameMode() == GameMode.SURVIVAL;
    }

    public boolean isPlaying() {
        return game != null && game.getState() == GameState.INGAME;
    }

    public FastBoard getBoard() {
        if (board == null && getPlayer() != null) {
            board = new FastBoard(getPlayer());
        }
        return board;
    }

    public void load() {
        new BukkitRunnable() {
            @Override
            public void run() {
                getPlugin().getDatabaseManager().getDatabase().loadUser(GamePlayer.this);
            }
        }.runTaskAsynchronously(getPlugin());
    }

    public void remove() {
        save();
        if (getBoard() != null) {
            getBoard().delete();
        }
        getMenus().clear();
        getActualButtons().clear();
        getGame().removePlayer(this);
    }

    public void save() {
        this.save(true);
    }

    public void save(boolean async) {
        if (isSaved()) return;

        if (async) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    getPlugin().getDatabaseManager().getDatabase().saveUser(GamePlayer.this);
                }
            }.runTaskAsynchronously(getPlugin());
        } else {
            getPlugin().getDatabaseManager().getDatabase().saveUser(this);
        }

        getBoard().delete();

        setSaved(true);
    }

    //Menus cache system
    private ConcurrentHashMap<String, Menu> menus = new ConcurrentHashMap<>();

    public void setMenu(Menu menu) {
        menus.put(menu.getId(), menu);
    }

    public Menu getMenu(String id) {
        return menus.get(id);
    }

    //Utils methods
    public void sendMSG(String... msg) {
        if (getPlayer() == null) return;
        for (String s : msg) {
            if (s.isEmpty()) continue;
            getPlugin().getUtils().sendMSG(getPlayer(), s);
        }
    }

    //Inventory & Items methods
    public void setItems() {
        String stage = "lobby";

        if (getGame() != null) {
            stage = getGame().getState().name().toLowerCase();
        }

        setItems(stage);
    }

    public void loadMenus() {
        setMenu(new TestMenu(getPlugin(), this));
    }

    private List<Button> actualButtons = new ArrayList<>();
    public void setItems(String stage) {
        if (stage.isEmpty()) return;

        if (stage.equals("clear")) {
            getPlayer().getInventory().clear();
            return;
        }

        List<Button> buttons = new ArrayList<>(getPlugin().getItemsManager().getButtons().get(stage));
        if (buttons.isEmpty() || getPlayer() == null) return;

        actualButtons.clear();
        getPlayer().getInventory().clear();

        buttons.forEach(b -> {
            var newButton = b.clone();
            newButton.getSlots().forEach(s -> {
                ItemStack item = newButton.getItem().build(getPlayer());
                getPlayer().getInventory().setItem(s, item);
                newButton.setItemStack(item);
            });
            actualButtons.add(newButton);
        });
    }
}
