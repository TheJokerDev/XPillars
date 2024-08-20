package me.j0keer.xpillars.player.events;

import lombok.Getter;
import me.j0keer.xpillars.api.events.GameEvent;
import me.j0keer.xpillars.player.GamePlayer;
import me.j0keer.xpillars.utils.game.Game;

@Getter
public class PlayerLeaveArena extends GameEvent {
    private GamePlayer player;
    private Game game;

    public PlayerLeaveArena(GamePlayer player, Game game) {
        this.player = player;
        this.game = game;
    }
}
