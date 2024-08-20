package me.j0keer.xpillars.player.events;

import lombok.Getter;
import me.j0keer.xpillars.api.events.GameEvent;
import me.j0keer.xpillars.player.GamePlayer;
import me.j0keer.xpillars.utils.game.Game;

@Getter
public class PlayerJoinArena extends GameEvent {
    private final GamePlayer player;
    private final Game game;

    public PlayerJoinArena(GamePlayer player, Game game) {
        this.player = player;
        this.game = game;
    }
}
