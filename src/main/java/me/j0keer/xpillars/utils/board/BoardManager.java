package me.j0keer.xpillars.utils.board;

import com.sun.tools.jconsole.JConsoleContext;
import fr.mrmicky.fastboard.FastBoard;
import lombok.Getter;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.player.GamePlayer;
import me.j0keer.xpillars.utils.FileUtils;
import me.j0keer.xpillars.utils.TextUtils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BoardManager {
    private final XPillars plugin;
    private BukkitTask task;

    public BoardManager(XPillars plugin) {
        this.plugin = plugin;
        setTask();
    }

    public void setTask() {
        if (task != null) {
            task.cancel();
        }
        task = new BukkitRunnable() {
            @Override
            public void run() {
                updateBoard();
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20L * getBoards().getLong("settings.update", 1));
    }

    public void updateBoard() {
        for (GamePlayer player : getPlugin().getPlayerManager().getPlayers()) {
            FastBoard board = player.getBoard();
            if (board == null) continue;
            String key = "";
            if (player.getGame() == null) {
                Location spawn = getPlugin().getSpawn();
                if (spawn == null || player.getPlayer().getLocation().getWorld() != spawn.getWorld()) {
                    if (!board.isDeleted()) {
                        board.delete();
                    }
                    continue;
                }
                key = "lobby";
            }

            if (key.isEmpty()) continue;

            if (board.isDeleted()) {
                board = new FastBoard(player.getPlayer());
                player.setBoard(board);
            }

            String title = getBoards().getString("boards." + key + ".title");
            List<String> lines = getLines(player.getPlayer(), key);

            title = TextUtils.processPlaceholders(player.getPlayer(), title);

            if (board.isDeleted()) continue;
            board.updateTitle(title);
            board.updateLines(lines);
        }
    }

    public FileUtils getBoards() {
        return new FileUtils(getPlugin().getDataFolder(), "boards.yml");
    }

    public List<String> getLines(Player player, String key) {
        List<String> lines = new ArrayList<>();

        Object obj = getBoards().get("boards." + key + ".lines");
        if (obj == null) return lines;
        if (obj instanceof List) {
            lines.addAll(getBoards().getStringList("boards." + key + ".lines"));
        } else {
            String line = getBoards().getString("boards." + key + ".lines");
            line = line.replace("\\n", "\n");
            lines.addAll(List.of(line.split("\n")));
        }
        if (!lines.isEmpty()) {
            lines.replaceAll(l -> TextUtils.processPlaceholders(player, l));
        }
        return lines;
    }

}
