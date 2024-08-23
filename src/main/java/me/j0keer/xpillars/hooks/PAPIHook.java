package me.j0keer.xpillars.hooks;

import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.player.GamePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class PAPIHook extends PlaceholderExpansion {
    private final XPillars xPillars;

    public PAPIHook(XPillars plugin) {
        this.xPillars = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return getXPillars().getDescription().getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return getXPillars().getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return getXPillars().getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        GamePlayer gamePlayer = getXPillars().getPlayer(player);

        String[] args = params.split("_");
        if (args.length == 1) {
            String one = args[0];
            switch (one.toLowerCase()) {
                case "prefix": {
                    return getXPillars().getUtils().getPrefix();
                }
                case "money": {
                    return String.valueOf(gamePlayer.getMoney());
                }
                case "kills": {
                    return String.valueOf(gamePlayer.getKills());
                }
                case "deaths": {
                    return String.valueOf(gamePlayer.getDeaths());
                }
                case "wins": {
                    return String.valueOf(gamePlayer.getWins());
                }
                case "played": {
                    return String.valueOf(gamePlayer.getPlayed());
                }
                case "time-played": {
                    return getXPillars().getUtils().formatTime(gamePlayer.getPlayTime());
                }
            }
        }
        return super.onPlaceholderRequest(player, params);
    }
}
