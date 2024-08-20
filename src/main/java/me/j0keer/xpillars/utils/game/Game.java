package me.j0keer.xpillars.utils.game;

import lombok.Getter;
import lombok.Setter;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.player.GamePlayer;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Game {
    private final XPillars plugin;

    @Setter private GameState state;
    private int maxPlayers, minPlayers, countdown, startingCountdown, endingCountdown;
    private long playTime;

    private List<GamePlayer> players = new ArrayList<>();

    public Game(XPillars plugin) {
        this.plugin = plugin;
    }

    public abstract void addPlayer(GamePlayer player);
    public abstract void removePlayer(GamePlayer player);

    public abstract void start();
    public abstract void stop();
    public abstract void end();

    public abstract void restart();
    public abstract void reset();

    public int getPlayersCount() {
        return players.size();
    }

    public int getAlivePlayers() {
        return players.stream().filter(GamePlayer::isAlive).toArray().length;
    }

}
