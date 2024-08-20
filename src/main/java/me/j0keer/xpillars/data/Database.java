package me.j0keer.xpillars.data;

import lombok.Getter;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.player.GamePlayer;

public abstract class Database {
    @Getter public final XPillars plugin;

    public Database(XPillars plugin) {
        this.plugin = plugin;
    }

    protected abstract String getType();

    public abstract void connect();

    public abstract void disconnect();

    public abstract void loadUser(GamePlayer user);
    public abstract void saveUser(GamePlayer user);
}