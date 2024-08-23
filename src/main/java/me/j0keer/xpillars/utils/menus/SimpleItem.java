package me.j0keer.xpillars.utils.menus;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.PlaceholderAPI;
import me.j0keer.xpillars.items.ItemsManager;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Getter
@Setter
public class SimpleItem implements Cloneable {
    private XMaterial material;
    private ItemMeta meta;
    private int amount = 1;
    private List<String> lore;
    private String displayName;
    private String skinTexture;
    private List<String> skinsTexture = new ArrayList<>();
    private short data = 0;
    private HashMap<XEnchantment, Integer> enchantments;
    private List<ItemFlag> flags;
    private final HashMap<String, String> placeholders;
    public boolean unbreakable = false;
    private Color color;
    private ItemStack item = null;
    private boolean glowing;
    private String metaData;

    public SimpleItem setColor(Color var1) {
        if (material.name().contains("LEATHER_")) {
            this.color = var1;
        }

        return this;
    }

    public SimpleItem setSkinsTexture(List<String> skinsTexture) {
        this.skinsTexture.addAll(skinsTexture);
        return this;
    }

    private FireworkEffectMeta fireworkEffectMeta = null;

    public SimpleItem(ItemStack itemStack) {
        this.lore = new ArrayList<>();
        this.enchantments = new HashMap<>();
        this.flags = new ArrayList<>();
        this.material = XMaterial.matchXMaterial(itemStack);
        this.amount = itemStack.getAmount();
        this.data = itemStack.getDurability();
        if (itemStack.getItemMeta() != null && itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                skinTexture = skullMeta.getOwner();
            }
            this.lore = meta.getLore();
            this.displayName = meta.getDisplayName();
            this.flags = new ArrayList<>(meta.getItemFlags());
        }

        this.enchantments = new HashMap(itemStack.getEnchantments());
        this.placeholders = new HashMap<>();
    }

    public SimpleItem(Boolean skull, String skinTexture) {
        this.material = XMaterial.PLAYER_HEAD;
        this.data = 3;
        this.skinTexture = skinTexture;
        this.enchantments = new HashMap<>();
        this.flags = new ArrayList<>();
        this.lore = new ArrayList<>();
        this.placeholders = new HashMap<>();
    }

    public SimpleItem(XMaterial material) {
        this.data = material.getData();
        this.material = material;
        this.enchantments = new HashMap<>();
        this.flags = new ArrayList<>();
        this.lore = new ArrayList<>();
        this.placeholders = new HashMap<>();
    }

    public SimpleItem(String displayName) {
        this.material = XMaterial.STONE;
        this.data = material.getData();
        this.displayName = displayName;
        this.enchantments = new HashMap<>();
        this.flags = new ArrayList<>();
        this.lore = new ArrayList<>();
        this.placeholders = new HashMap<>();
    }

    public SimpleItem addPlaceholder(String key, String value) {
        this.placeholders.put(key, value);
        return this;
    }

    public SimpleItem removePlaceholder(String key) {
        this.placeholders.remove(key);
        return this;
    }

    public SimpleItem setMaterial(XMaterial material) {
        this.material = material;
        this.data = material.getData();
        return this;
    }

    public SimpleItem setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public SimpleItem setLore(List<String> lore) {
        this.lore = new ArrayList<>(lore);
        return this;
    }

    public SimpleItem setLore(String... lore) {
        this.lore = toList(lore);
        return this;
    }

    public SimpleItem setFirstLoreLine(String text) {
        List<String> lore = new ArrayList<>();
        lore.add(text);
        lore.addAll(this.lore);
        this.lore = lore;
        return this;
    }

    public SimpleItem addLoreLine(String text) {
        this.lore.add(text);
        return this;
    }

    public SimpleItem setDurability(int i) {
        this.data = (short) i;
        return this;
    }

    public SimpleItem addLoreLines(String... lore) {
        this.lore.addAll(toList(lore));
        return this;
    }

    @SafeVarargs
    public static <T> List<T> toList(T... array) {
        List<T> list = new ArrayList<>();
        Collections.addAll(list, array);
        return list;
    }

    public SimpleItem addLoreLines(List<String> lore) {
        this.lore.addAll(lore);
        return this;
    }

    public SimpleItem setLoreLine(int index, String text) {
        if (index < 0) {
            index = this.lore.size();
        }

        this.lore.set(index, text);
        return this;
    }

    public SimpleItem removeLastLoreLine() {
        this.lore.remove(this.lore.size() - 1);
        return this;
    }

    public SimpleItem removeLoreLine(int index) {
        if (index >= this.lore.size()) {
            index = this.lore.size() - 1;
        }

        if (index < 0) {
            index = 0;
        }

        this.lore.remove(index);
        return this;
    }

    public SimpleItem setSkin(String skin) {
        this.skinTexture = skin;
        return this;
    }

    public SimpleItem addEnchantment(XEnchantment enchantment) {
        return this.addEnchantment(enchantment, 1);
    }

    public SimpleItem addEnchantment(XEnchantment enchantment, int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    public SimpleItem removeEnchantment(XEnchantment enchantment) {
        this.enchantments.remove(enchantment);
        return this;
    }

    public SimpleItem addFlag(ItemFlag flag) {
        this.flags.add(flag);
        return this;
    }

    public SimpleItem addFlags(ItemFlag... flags) {
        this.flags.addAll(toList(flags));
        return this;
    }

    public SimpleItem removeFlag(ItemFlag flag) {
        this.flags.remove(flag);
        return this;
    }

    public SimpleItem setShowEnchantments(boolean showEnchantments) {
        ItemFlag flag = ItemFlag.HIDE_ENCHANTS;
        if (showEnchantments) {
            this.flags.remove(flag);
        } else {
            this.flags.add(flag);
        }

        return this;
    }

    public SimpleItem setGlowing(boolean glowing) {
        ItemFlag flag = ItemFlag.HIDE_ENCHANTS;
        this.glowing = glowing;
        if (glowing) {
            this.addFlag(flag);
            this.addEnchantment(XEnchantment.UNBREAKING);
        } else {
            this.removeFlag(flag);
            this.removeEnchantment(XEnchantment.UNBREAKING);
        }

        return this;
    }

    public SimpleItem setHideFlags(boolean b) {
        setShowAttributes(!b);
        return this;
    }

    public SimpleItem setShowAttributes(boolean showAttributes) {
        ItemFlag flag = ItemFlag.HIDE_ATTRIBUTES;
        if (showAttributes) {
            this.flags.remove(flag);
        } else {
            this.flags.add(flag);
        }

        return this;
    }

    public SimpleItem setDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    public SimpleItem setEnchantments(HashMap<XEnchantment, Integer> enchantments) {
        this.enchantments = new HashMap(enchantments);
        return this;
    }

    public SimpleItem setFlags(List<ItemFlag> flags) {
        this.flags = new ArrayList(flags);
        return this;
    }

    public short getDurability() {
        return this.data;
    }

    public String getSkinTexture() {
        if (skinsTexture.size() > 0) {
            skinTexture = skinsTexture.get(new Random().nextInt(skinsTexture.size()));
        }
        return this.skinTexture;
    }

    public boolean hasEnchantments() {
        return !this.getEnchantments().isEmpty();
    }

    public boolean hasFlags() {
        return !this.getFlags().isEmpty();
    }

    public boolean hasSkin() {
        return this.getSkinTexture() != null;
    }

    public SimpleItem duplicate() {
        return (new SimpleItem(this.getMaterial())).setAmount(this.getAmount()).setLore(this.getLore()).setDisplayName(this.getDisplayName()).setSkin(this.getSkinTexture()).setEnchantments(this.getEnchantments()).setFlags(this.getFlags());
    }

    @Override
    public SimpleItem clone() {
        try {
            return (SimpleItem) super.clone();
        } catch (CloneNotSupportedException var2) {
            return null;
        }
    }

    public ItemStack build(Player player) {
        if (material == XMaterial.AIR) {
            return material.parseItem();
        }
        if (this.getDisplayName() != null) {
            this.setDisplayName(placeholders.isEmpty() ? this.displayName : apply(displayName));
        } else {
            this.setDisplayName("&cDisplayName is null!. You can also set just '& 7'");
        }
        if (item == null) {
            item = material.parseItem();
        }
        if (item != null && material != null) {
            item.setDurability(material.getData());
        }
        Object meta;
        if (getMeta() != null) {
            meta = getMeta();
        } else {
            meta = item.getItemMeta();
        }
        if (meta instanceof LeatherArmorMeta && this.color != null) {
            LeatherArmorMeta LMeta = (LeatherArmorMeta) meta;
            LMeta.setColor(this.color);
            item.setItemMeta(LMeta);
        }
        if (material == XMaterial.PLAYER_HEAD && getSkinTexture() != null) {
            skinTexture = apply(getSkinTexture());
            if (player != null) {
                skinTexture = PlaceholderAPI.setPlaceholders(player.getPlayer(), skinTexture);
            }
            ItemStack secItem = XMaterial.PLAYER_HEAD.parseItem();
            try {
                if (skinTexture.startsWith("base-")) {
                    skinTexture = skinTexture.replace("base-", "");
                    secItem = XSkull.createItem().profile(Profileable.detect(skinTexture)).apply();
                } else if (skinTexture.startsWith("uuid-")) {
                    skinTexture = skinTexture.replace("uuid-", "");
                    UUID uuid = UUID.fromString(skinTexture);
                    secItem = XSkull.createItem().profile(Profileable.of(uuid)).apply();
                } else if (skinTexture.startsWith("name-")) {
                    skinTexture = skinTexture.replace("name-", "");
                    secItem = XSkull.createItem().profile(Profileable.username(skinTexture)).apply();
                } else if (skinTexture.startsWith("url-")) {
                    skinTexture = skinTexture.replace("url-", "");
                    skinTexture = "http://textures.minecraft.net/texture/" + skinTexture;
                    secItem = XSkull.createItem().profile(Profileable.detect(skinTexture)).apply();
                }
            } catch (Exception ignored) {
            }
            item = secItem;
        }
        if (meta instanceof FireworkEffectMeta && fireworkEffectMeta != null) {
            item.setItemMeta(fireworkEffectMeta);
        }
        meta = item.getItemMeta();
        if (meta != null) {
            ((ItemMeta) meta).setDisplayName(this.apply(ct(this.getDisplayName())));
            ((ItemMeta) meta).setLore(this.apply(ct(this.getLore())));
            Object finalMeta = meta;
            this.getFlags().forEach((xva$0) -> ((ItemMeta) finalMeta).addItemFlags(xva$0));
            item.setDurability(this.data);
        }
        ((ItemMeta) meta).addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
        item.setItemMeta(((ItemMeta) meta));
        if (!enchantments.isEmpty()) {
            for (Map.Entry<XEnchantment, Integer> ench : getEnchantments().entrySet()) {
                assert ench.getKey().getEnchant() != null;
                item.addUnsafeEnchantment(ench.getKey().getEnchant(), ench.getValue());
            }
        }
        item.setAmount(this.getAmount());
        if (unbreakable) {
            ItemMeta meta1 = item.getItemMeta();
            meta1.setUnbreakable(unbreakable);
            item.setItemMeta(meta1);
        }
        return ItemsManager.setPlaceHolders(item, player);
    }

    String ct(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    List<String> ct(List<String> in) {
        return in.stream().map(this::ct).collect(Collectors.toList());
    }

    private String apply(String text) {
        AtomicReference<String> r = new AtomicReference<>(text);
        this.placeholders.forEach((k, v) -> {
            r.set(r.get().replace(k, v));
        });
        return r.get();
    }

    private List<String> apply(List<String> list) {
        List<String> r = new ArrayList<>();
        list.forEach((s) -> {
            r.add(this.apply(s));
        });
        return r;
    }

    public static boolean isNumeric(String var0) {
        try {
            Integer.parseInt(var0);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    public boolean hasLore() {
        return this.getLore() != null;
    }

    public boolean hasDisplayName() {
        return this.getDisplayName() != null;
    }

    public boolean hasDurability() {
        return this.getDurability() != 0;
    }
}