package me.j0keer.xpillars.utils;

import dev.vankka.enhancedlegacytext.EnhancedLegacyText;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.j0keer.xpillars.XPillars;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Getter
public class Utils {
    private XPillars plugin;
    private BukkitAudiences adventure;
    private EnhancedLegacyText mm;

    public Utils(XPillars plugin) {
        this.plugin = plugin;

        adventure = BukkitAudiences.create(plugin);
        mm = EnhancedLegacyText.get();
    }

    public String getPrefix() {
        return getPlugin().getConfig().getString("settings.prefix", "&b&lXPillars &8» &7");
    }

    public String ct(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    public void sendMSG(CommandSender sender, String... msg) {
        Arrays.stream(msg).forEach(m -> sendMSG(sender, m));
    }

    public void sendMSG(CommandSender sender, List<String> msg) {
        msg.forEach(m -> sendMSG(sender, m));
    }

    public void sendMSG(CommandSender sender, String msg) {
        boolean console = !(sender instanceof Player);
        Player player = console ? null : (Player) sender;
        Audience audience = console ? adventure.console() : adventure.player(player);
        if (msg.isEmpty()) return;
        audience.sendMessage(getMSG(sender, msg));
    }

    public Component getMSG(CommandSender sender, String msg) {
        boolean console = !(sender instanceof Player);
        Player player = console ? null : (Player) sender;
        if (msg.contains(".") && !msg.contains(" ")) {
            msg = getMessage(msg);
        }
        msg = PlaceholderAPI.setPlaceholders(console ? null : player, msg.replace("{prefix}", getPrefix()));
        return mm.buildComponent(msg).build();
    }

    public String getMessage(String key) {
        return getMessages().getString(key, getMessages().getString("general.noTranslation").replace("{key}", key));
    }

    public FileUtils getMessages() {
        return new FileUtils(new File(getPlugin().getDataFolder(), "messages.yml"));
    }

    public String formatTime(long time) {
        String second = getMessage("placeholders.date.second");
        String minute = getMessage("placeholders.date.minute");
        String hour = getMessage("placeholders.date.hour");
        String day = getMessage("placeholders.date.day");
        String week = getMessage("placeholders.date.week");
        String month = getMessage("placeholders.date.month");
        String year = getMessage("placeholders.date.year");

        String timeStr = "";
        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long months = weeks / 4;
        long years = months / 12;

        if (years > 0) {
            year = year.formatted(years);
            timeStr += year + " ";
            months = months % 12;
        }
        if (months > 0) {
            month = month.formatted(months);
            timeStr += month + " ";
            weeks = weeks % 4;
        }
        if (weeks > 0) {
            week = week.formatted(weeks);
            timeStr += week + " ";
            days = days % 7;
        }
        if (days > 0) {
            day = day.formatted(days);
            timeStr += day + " ";
            hours = hours % 24;
        }
        if (hours > 0) {
            hour = hour.formatted(hours);
            timeStr += hour + " ";
            minutes = minutes % 60;
        }
        if (minutes > 0) {
            minute = minute.formatted(minutes);
            timeStr += minute + " ";
            seconds = seconds % 60;
        }
        if (seconds >= 0) {
            second = second.formatted(seconds);
            timeStr += second;
        }
        return TextUtils.colorize(timeStr);
    }

    public String ct(List<String> lore) {
        return lore.stream().map(this::ct).toList().toString();
    }
}
