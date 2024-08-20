package me.j0keer.xpillars.arena;

import lombok.Getter;
import lombok.Setter;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.player.GamePlayer;
import me.j0keer.xpillars.utils.game.Game;
import me.j0keer.xpillars.utils.game.GameState;

@Getter @Setter
public class Arena extends Game {
    private int localCountdown = 0;

    public Arena(XPillars plugin) {
        super(plugin);
    }

    @Override
    public void addPlayer(GamePlayer player) {

    }

    @Override
    public void removePlayer(GamePlayer player) {

    }

    @Override
    public void start() {

    }

    public void tick() {
        if (getState() == GameState.INGAME){
            localCountdown++;
        } else {
            localCountdown--;
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void end() {

    }

    @Override
    public void restart() {

    }

    @Override
    public void reset() {

    }
}
