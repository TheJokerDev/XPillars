package me.j0keer.xpillars.utils.itemaction.actions;

import com.cryptomorin.xseries.XSound;
import me.j0keer.xpillars.utils.itemaction.ItemActionExecutor;
import org.bukkit.entity.Player;

import java.util.Optional;

public class Sound implements ItemActionExecutor {

    @Override
    public boolean onCommand(Player player, String label) {
        float volume = 1.0F;
        float pitch = 1.0F;

        if (label.contains(",")) {
            String[] split = label.split(",");
            if (split.length > 1) {
                volume = Float.parseFloat(split[1]);
            }
            if (split.length > 2) {
                pitch = Float.parseFloat(split[2]);
            }
            label = split[0];
        }

        Optional<XSound> sound = XSound.matchXSound(label);
        if (sound.isPresent()) {
            sound.get().play(player, volume, pitch);
            return true;
        }
        return false;
    }
}