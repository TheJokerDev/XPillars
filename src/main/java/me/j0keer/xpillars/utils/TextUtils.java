package me.j0keer.xpillars.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    private static final String MINI_MESSAGE_HEX = "<color:{color}>";
    private static final Pattern HEX_PATTERN = Pattern.compile("(#|&#)([A-Fa-f0-9]){6}");


    public static String colorize(String text) {

        text = transformLegacyHex(text);
        text = text.replaceAll("§", "&");

        MiniMessage mm = MiniMessage.miniMessage();
        text = LegacyComponentSerializer.legacySection().serialize(mm.deserialize(text));

        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String transformLegacyHex(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);

        while (matcher.find()) {
            String hex = matcher.group().replace("<", "").replace(">", "").replace("&", "");

            text = text.replace(matcher.group(), MINI_MESSAGE_HEX.replace("{color}", hex));
        }

        return text;
    }

    public static String processPlaceholders(Player player, String text) {
        return colorize(PlaceholderAPI.setPlaceholders(player, text));
    }

    @SuppressWarnings("unused")
    public static Component toComponent(String text) {
        text = text.replace("\n", "<br>").replace(LegacyComponentSerializer.SECTION_CHAR, LegacyComponentSerializer.AMPERSAND_CHAR);
        Component initial = LegacyComponentSerializer.legacyAmpersand().deserialize(text);
        String deserializedAsMini = MiniMessage.miniMessage().serialize(initial).replace("\\", "");

        return MiniMessage.miniMessage().deserialize(deserializedAsMini);
    }
}