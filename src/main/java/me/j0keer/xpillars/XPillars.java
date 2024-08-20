package me.j0keer.xpillars;

import lombok.Getter;
import me.j0keer.xpillars.utils.PluginUtils;
import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public final class XPillars extends PluginUtils {
    @Getter
    private static XPillars instance;

    @Override
    public void onEnable() {
        //Remove this line
        List<String> files = new ArrayList<>(List.of("messages.yml", "boards.yml"));
        for (String file : files) {
            File tempFile = new File(getDataFolder(), file);
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
        instance = this;
        double ms = System.currentTimeMillis();
        super.onEnable(this);
        saveDefaultConfig();

        console("{prefix}&7Loading plugin...");

        console("{prefix}&7Checking dependencies...");
        if (!checkDependencies(this)) {
            console("{prefix}&cPlugin disabled due to missing dependencies.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        console("{prefix}&aDependencies loaded.");

        console("{prefix}&7Enabling managers:");
        loadManagers(this);
        console("{prefix}&aManagers loaded.");

        console("{prefix}&7Loading listeners:");
        loadListeners(this);
        console("{prefix}&aListeners loaded.");

        if (getSpawn() == null) {
            console("{prefix}&cSpawn location not found in config. Please set it with /xpillars setspawn.");
        }

        ms = System.currentTimeMillis() - ms;
        console("{prefix}&7Loaded in &b" + ms + "ms.");
    }

    public Location getSpawn() {
        return getConfig().getLocation("settings.spawn", null);
    }

    @Override
    public void onDisable() {
        double ms = System.currentTimeMillis();
        console("{prefix}&7Disabling plugin...");

        console("{prefix}&7Disabling managers:");
        getPlayerManager().onDisable();
        getDatabaseManager().disconnect();

        ms = System.currentTimeMillis() - ms;
        console("{prefix}&7Disabled in &b" + ms + "ms.");
    }
}
