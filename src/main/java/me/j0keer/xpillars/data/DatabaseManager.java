package me.j0keer.xpillars.data;

import lombok.Getter;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.data.type.MariaDBData;
import me.j0keer.xpillars.data.type.MongoDBData;
import me.j0keer.xpillars.data.type.SQLData;
import me.j0keer.xpillars.data.type.YamlData;

@Getter
public class DatabaseManager {
    private final Database database;
    public final XPillars plugin;

    public DatabaseManager(XPillars plugin) {
        this.plugin = plugin;
        String databaseType = plugin.getConfig().getString("database.type", "yaml");
        databaseType = databaseType.toLowerCase();
        switch (databaseType) {
            case "sqlite":
            case "sql": {
                database = new SQLData(plugin);
                break;
            }
            case "mariadb":
            case "mysql": {
                database = new MariaDBData(plugin);
                break;
            }
            case "mongo":
            case "mongodb": {
                database = new MongoDBData(plugin);
                break;
            }
            default: {
                database = new YamlData(plugin);
                break;
            }
        }
        plugin.console("{prefix}&7Database type set to &e" + database.getType());
        plugin.console("{prefix}&7Connecting to database...");
        database.connect();
    }

    public void disconnect() {
        database.disconnect();
    }
}