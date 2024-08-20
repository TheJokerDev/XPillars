package me.j0keer.xpillars.commands.xpillars.sub;

import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.player.GamePlayer;
import me.j0keer.xpillars.player.PlayerData;
import me.j0keer.xpillars.utils.commands.SenderTypes;
import me.j0keer.xpillars.utils.commands.SubCMD;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModifySubCMD extends SubCMD {
    public ModifySubCMD(XPillars plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "modify";
    }

    @Override
    public String getPermission() {
        return "xpillars.admin";
    }

    @Override
    public SenderTypes getSenderType() {
        return SenderTypes.BOTH;
    }

    @Override
    public String getUsage() {
        return "commands.xpillars.modify.usage";
    }

    @Override
    public boolean onCommand(CommandSender sender, String alias, String[] args) {
        if (args.length < 3) {
            sendMSG(sender, getUsage());
            return false;
        }
        String nick = args[0];
        String modify = args[1];
        String value = args[2];

        Player player = getPlugin().getServer().getPlayer(nick);
        if (player == null) {
            String msg = getPlugin().getUtils().getMessage("general.playerNotFound");
            sendMSG(sender, msg.replace("{player}", nick));
            return false;
        }

        GamePlayer gamePlayer = getPlugin().getPlayerManager().getPlayer(player);
        HashMap<String, PlayerData.Data> map = gamePlayer.getSchema();
        if (!map.containsKey(modify)) {
            String msg = getPlugin().getUtils().getMessage("commands.xpillars.modify.notFound");
            sendMSG(sender, msg.replace("{modify}", modify));
            return false;
        }

        PlayerData.Data data = map.get(modify);
        var dataType = data.getType();

        try {
            if (dataType == Boolean.class) {
                boolean boolValue = Boolean.parseBoolean(value);
                data.set(boolValue);
            } else if (dataType == Integer.class) {
                int intValue = Integer.parseInt(value);
                data.set(intValue);
            } else if (dataType == Double.class) {
                double doubleValue = Double.parseDouble(value);
                data.set(doubleValue);
            } else if (dataType == Long.class) {
                long longValue = Long.parseLong(value);
                data.set(longValue);
            } else {
                data.set(value);
            }
        } catch (Exception e) {
            String msg = getPlugin().getUtils().getMessage("commands.xpillars.modify.invalidValue");
            sendMSG(sender, msg.replace("{modifier}", modify).replace("{value}", value));
            return false;
        }

        gamePlayer.setSaved(false);

        String msg = getPlugin().getUtils().getMessage("commands.xpillars.modify.success");
        sendMSG(sender, msg.replace("{player}", player.getName()).replace("{modifier}", modify).replace("{value}", value));
        return true;
    }

    @Override
    public List<String> onTab(CommandSender sender, String alias, String[] args) {
        if (args.length == 1) {
            return null;
        }
        HashMap<String, PlayerData.Data> schema = new PlayerData().getSchema();
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], schema.keySet(), new ArrayList<>());
        }
        if (args.length == 3) {
            String modify = args[1];
            if (!schema.containsKey(modify)) {
                return List.of();
            }
            return List.of(schema.get(modify).getDefaultValue() + "");

        }
        return List.of();
    }
}
