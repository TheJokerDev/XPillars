package me.j0keer.xpillars.data.type;

import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.data.Database;
import me.j0keer.xpillars.player.GamePlayer;
import me.j0keer.xpillars.player.PlayerData;
import me.j0keer.xpillars.utils.FileUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class YamlData extends Database {
    private FileUtils config;

    public YamlData(XPillars plugin) {
        super(plugin);
    }

    @Override
    protected String getType() {
        return "YAML";
    }

    @Override
    public void connect() {
        config = new FileUtils(getFile());
        plugin.console("{prefix}&7Connected to &eYAML &7database.");
    }

    public File getFile() {
        File file = new File(plugin.getDataFolder(), "data.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    @Override
    public void disconnect() {
        config.save();
        plugin.console("{prefix}&7Disconnected from &eYAML &7database.");
    }

    @Override
    public void loadUser(GamePlayer user) {
        String uuid = user.getUuid().toString();
        ConfigurationSection section = config.getSection(uuid);

        if (section == null) {
            user.setSaved(false);
            return;
        }

        HashMap<String, PlayerData.Data> data = user.getSchema();

        data.forEach((key, value1) -> {
            Object value = section.get(key);
            if (value != null) {
                Object type = value1.getType();
                getPlugin().console("Setting value " + value + " with type " + type + " for key " + key);
                value1.set(value);
            }
            System.currentTimeMillis();
        });
    }

    @Override
    public void saveUser(GamePlayer user) {
        String uuid = user.getUuid().toString();
        ConfigurationSection section = config.get(uuid) != null ? config.getSection(uuid) : config.createSection(uuid);

        if (section == null) {
            getPlugin().console("{prefix}&cError saving data for &e" + user.getName() + " &c(" + uuid + ")");
            return;
        }

        HashMap<String, PlayerData.Data> data = user.getSchema();

        data.forEach((key, value1) -> {
            Object value = value1.getActualValue();
            var type = value1.getType();

            if (type == Long.class) {
                section.set(key, (Long) value);
            } else if (type == Integer.class) {
                section.set(key, (Integer) value);
            } else if (type == Double.class) {
                section.set(key, (Double) value);
            } else if (type == Float.class) {
                section.set(key, (Float) value);
            } else if (type == String.class) {
                section.set(key, (String) value);
            } else if (type == Boolean.class) {
                section.set(key, (Boolean) value);
            } else {
                section.set(key, value);
            }
        });

        config.set(uuid, section);
    }
}
