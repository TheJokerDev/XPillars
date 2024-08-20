package me.j0keer.xpillars.items;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import lombok.Setter;
import me.j0keer.xpillars.XPillars;
import me.j0keer.xpillars.utils.menus.SimpleItem;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Getter
public class Button implements Cloneable {
    private final XPillars plugin;
    private ConfigurationSection section;
    private SimpleItem item;
    @Setter
    private ItemStack itemStack;
    private final List<Integer> slots = new ArrayList<>(List.of(0));
    private final HashMap<String, String> placeholders = new HashMap<>();

    private int cooldown = 0;


    public Button(XPillars plugin) {
        this.plugin = plugin;
    }

    public Button(XPillars plugin, ConfigurationSection section) {
        this.plugin = plugin;
        setItem(section);
        setSlots(section.getString("slot"));
        if (section.contains("cooldown")) {
            int cooldown = section.getInt("cooldown");
            if (cooldown > 0) {
                this.cooldown = cooldown;
            }
        }
        this.section = section;
    }

    public SimpleItem getItem() {
        return item.clone();
    }

    public void setItem(ConfigurationSection section) {
        this.item = createItem(section);
    }

    public void onClick(ButtonClickEvent event) {
        if (getSection() != null) {
            onClick(event, getSection());
        }
    }

    public void onClick(ButtonClickEvent event, ConfigurationSection section) {
        event.setCanceled(true);
        clickAction(event, section);
    }

    Cache<String, Long> cooldowns = CacheBuilder.newBuilder().build();

    public void clickAction(ButtonClickEvent event, ConfigurationSection section) {
        List<String> actionsType = new ArrayList<>(event.getActions());
        Player player = event.getPlayer();

        if (player == null) return;
        if (section.get("actions") == null) return;

        if (cooldown > 0) {
            if (cooldowns.getIfPresent(player.getName()) != null) {
                long time = cooldowns.getIfPresent(player.getName());
                long now = System.currentTimeMillis();
                long diff = now - time;
                if (diff < cooldown * 1000L) {
                    String msg = getPlugin().getUtils().getMessage("general.cooldown");
                    msg = msg.replace("{time}", getPlugin().getUtils().formatTime((cooldown * 1000L) - diff + 1000L));
                    getPlugin().getUtils().sendMSG(player, msg);
                    return;
                } else {
                    cooldowns.invalidate(player.getName());
                }
            }
            cooldowns.put(player.getName(), System.currentTimeMillis());
        }

        List<String> actions = new ArrayList<>();

        if (section.get("actions") instanceof List<?>) {
            actions.addAll(section.getStringList("actions"));
        } else {
            section = section.getConfigurationSection("actions");
            actions.addAll(section.getStringList("multiclick"));
        }

        for (String s : actionsType) {
            if (s.contains("LEFT")) {
                actions.addAll(section.getStringList("leftclick"));
            }
            if (s.contains("RIGHT")) {
                actions.addAll(section.getStringList("rightclick"));
            }
            if (s.contains("SHIFT") || player.isSneaking()) {
                actions.addAll(section.getStringList("shiftclick"));
            }
        }

        actions.forEach(action -> XPillars.getInstance().getItemActionManager().execute(player, action));
    }

    public void setSlots(String key) {
        slots.clear();

        try {
            if (key.contains(",")) {
                String[] s = key.split(",");
                for (String s1 : s) {
                    slots.addAll(rangeSlots(s1));
                }
            } else {
                slots.addAll(rangeSlots(key));
            }
        } catch (Exception ignored) {
        }

        if (slots.isEmpty()) {
            slots.add(0);
        }
    }

    private static List<Integer> rangeSlots(String key) {
        List<Integer> slots = new ArrayList<>();
        if (key.contains("-")) {
            String[] s = key.split("-");
            int start = Integer.parseInt(s[0]);
            int end = Integer.parseInt(s[1]);
            for (int i = start; i <= end; i++) {
                slots.add(i);
            }
        } else {
            slots.add(Integer.parseInt(key));
        }
        return slots;
    }

    public SimpleItem createItem(ConfigurationSection section) {
        HashMap<String, String> placeholders = getPlaceholders();
        boolean hasMeta = section.get("meta") != null;
        boolean hasName = section.get("meta.name") != null;
        boolean hasAmount = section.get("amount") != null;
        boolean hasLore = section.get("meta.lore") != null;
        boolean hasSkullData = section.get("skull") != null;
        boolean isGlow = section.get("glow") != null;
        boolean hideFlags = section.get("hideFlags") != null;
        boolean hasFireWork = section.get("firework") != null;
        boolean hasPotion = section.get("potion") != null;
        boolean hasColor = section.get("color") != null;
        boolean hasMaterial = section.get("material") != null;
        boolean hasData = section.get("data") != null;
        boolean hasPlaceholder = section.get("placeholders") != null;
        boolean hasMetaData = section.get("metadata") != null;
        boolean isUnbreakable = section.get("unbreakable") != null;

        SimpleItem item = new SimpleItem(XMaterial.BARRIER);

        if (hasMaterial) {
            XMaterial material = XMaterial.valueOf(section.getString("material").toUpperCase());
            item.setMaterial(material);
            item.setDurability(material.getData());
        }
        if (hasData) {
            int data = section.getInt("data");
            item.setDurability(data);
        }
        if (placeholders != null && !placeholders.isEmpty()) {
            placeholders.forEach(item::addPlaceholder);
        }
        if (hasPlaceholder) {
            List<String> pl = section.getStringList("placeholders");
            for (String s : pl) {
                if (s.contains(",")) {
                    String[] s2 = s.split(",");
                    item.addPlaceholder(s2[0], s2[1]);
                }
            }
        }
        if (isUnbreakable) {
            item.setUnbreakable(section.getBoolean("unbreakable"));
        }
        if (hasMeta) {
            if (hasName) {
                String name = section.getString("meta.name");
                item.setDisplayName(name);
            }
            if (hasLore) {
                List<String> lore = new ArrayList<>();
                Object obj = section.get("meta.lore");
                if (obj instanceof List) {
                    lore.addAll(section.getStringList("meta.lore"));
                } else {
                    String loreString = section.getString("meta.lore");
                    loreString = loreString.replace("\\n", "\n");
                    if (loreString.contains("\n")) {
                        lore.addAll(Arrays.asList(loreString.split("\n")));
                    } else {
                        lore.add(loreString);
                    }
                }
                item.setLore(lore);
            }
        }
        if (hasMetaData) {
            item.setMetaData(section.getString("metadata"));
            if (item.getMetaData().equals("tpbow")) {
                item.addEnchantment(XEnchantment.INFINITY, 1);
            }
        }
        if (hasAmount) {
            item.setAmount(section.getInt("amount"));
        }
        if (hasSkullData) {
            if (section.get("skull") instanceof List) {
                List<String> skull = section.getStringList("skull");
                item.setSkinsTexture(skull);
                item.setSkin(skull.get(0));
            } else {
                String skull = section.getString("skull");
                item.setSkin(skull);
            }

        }
        if (isGlow) {
            item.setGlowing(section.getBoolean("glow"));
        }
        if (hideFlags) {
            item.setShowAttributes(!section.getBoolean("hideFlags"));
        }
        if (hasFireWork) {
            String color;
            String[] var1;
            ItemMeta meta = item.getMeta();
            FireworkEffectMeta metaFw = (FireworkEffectMeta) meta;
            color = section.getString("firework");
            Color color1;
            var1 = color.split("-");
            color1 = getColor(var1);
            FireworkEffect effect = FireworkEffect.builder().withColor(color1).build();
            metaFw.setEffect(effect);
            item.setFireworkEffectMeta(metaFw);
        }
        if (hasColor) {
            String color = section.getString("color");
            String[] var1 = color.split("-");
            item.setColor(getColor(var1));
        }
        if (hasPotion) {
            String id = section.getString("potion.id").toUpperCase();
            PotionEffectType potEffect = XPotion.valueOf(id).getPotionEffectType();
            int multiplier = section.getInt("potion.multiplier") - 1;
            int time = section.getInt("potion.time") * 20;
            boolean showParticles = section.getBoolean("potion.showParticles");
            boolean ambient = section.getBoolean("potion.ambient");
            PotionMeta potionMeta = (PotionMeta) item.getMeta();
            potionMeta.addCustomEffect(new PotionEffect(potEffect, time, multiplier, ambient, showParticles), true);
            item.setMeta(potionMeta);
        }
        return item;
    }

    public Color getColor(String[] var1a) {
        int int1;
        int int2;
        int int3;
        Color color1;
        if (var1a.length == 3) {
            int1 = SimpleItem.isNumeric(var1a[0]) ? Integer.parseInt(var1a[0]) : 0;
            int2 = SimpleItem.isNumeric(var1a[1]) ? Integer.parseInt(var1a[1]) : 0;
            int3 = SimpleItem.isNumeric(var1a[2]) ? Integer.parseInt(var1a[2]) : 0;
        } else {
            int1 = 0;
            int2 = 0;
            int3 = 0;
        }
        color1 = Color.fromRGB(int1, int2, int3);
        return color1;
    }

    @Override
    public Button clone() {
        try {
            return (Button) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
