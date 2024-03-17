package dev.pixl.plugins.viperbows.util;

import dev.pixl.plugins.viperbows.ViperBowsPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ItemNBT {
  private ItemNBT() {
  }

  private static boolean hasIntTag(ItemMeta itemMeta, NamespacedKey key) {
    if (itemMeta != null) {
      return itemMeta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER);
    }
    return false;
  }

  private static boolean hasFloatTag(ItemMeta itemMeta, NamespacedKey key) {
    if (itemMeta != null) {
      return itemMeta.getPersistentDataContainer().has(key, PersistentDataType.FLOAT);
    }
    return false;
  }

  private static boolean hasStringTag(ItemMeta itemMeta, NamespacedKey key) {
    if (itemMeta != null) {
      return itemMeta.getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }
    return false;
  }

  public static boolean hasIntTag(ItemStack item, String key) {
    NamespacedKey namespacedKey = new NamespacedKey(ViperBowsPlugin.getInstance(), key);
    return hasIntTag(item.getItemMeta(), namespacedKey);
  }

  public static boolean hasFloatTag(ItemStack item, String key) {
      NamespacedKey namespacedKey = new NamespacedKey(ViperBowsPlugin.getInstance(), key);
      return hasFloatTag(item.getItemMeta(), namespacedKey);
  }

  public static boolean hasStringTag(ItemStack item, String key) {
      NamespacedKey namespacedKey = new NamespacedKey(ViperBowsPlugin.getInstance(), key);
      return hasStringTag(item.getItemMeta(), namespacedKey);
  }

  public static void setTag(ItemStack item, String key, int value) {
    NamespacedKey namespacedKey = new NamespacedKey(ViperBowsPlugin.getInstance(), key);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, value);
      item.setItemMeta(meta);
    }
  }

  public static void setTag(ItemStack item, String key, float value) {
    NamespacedKey namespacedKey = new NamespacedKey(ViperBowsPlugin.getInstance(), key);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.FLOAT, value);
      item.setItemMeta(meta);
    }
  }

  public static void setTag(ItemStack item, String key, String value) {
    NamespacedKey namespacedKey = new NamespacedKey(ViperBowsPlugin.getInstance(), key);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
      item.setItemMeta(meta);
    }
  }

  public static int getIntTag(ItemStack item, String key) {
    NamespacedKey namespacedKey = new NamespacedKey(ViperBowsPlugin.getInstance(), key);
    ItemMeta itemMeta = item.getItemMeta();

    if (!hasIntTag(itemMeta, namespacedKey)) {
      return 0;
    }

    Integer result = itemMeta.getPersistentDataContainer().get(namespacedKey,
            PersistentDataType.INTEGER);

    if (result != null) {
      return result;
    }

    return 0;
  }

  public static float getFloatTag(ItemStack item, String key) {
    NamespacedKey namespacedKey = new NamespacedKey(ViperBowsPlugin.getInstance(), key);
    ItemMeta itemMeta = item.getItemMeta();

    if (!hasFloatTag(itemMeta, namespacedKey)) {
      return 0;
    }

    Float result = itemMeta.getPersistentDataContainer().get(namespacedKey,
            PersistentDataType.FLOAT);

    if (result != null) {
      return result;
    }

    return 0;
  }

  public static String getStringTag(ItemStack item, String key) {
    NamespacedKey namespacedKey = new NamespacedKey(ViperBowsPlugin.getInstance(), key);
    ItemMeta itemMeta = item.getItemMeta();

    if (!hasStringTag(itemMeta, namespacedKey)) {
      return "";
    }

    String result = itemMeta.getPersistentDataContainer().get(namespacedKey,
            PersistentDataType.STRING);

    if (result != null) {
      return result;
    }

    return "";
  }

  public static void setName(ItemStack item, String name) {
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(name);
      item.setItemMeta(meta);
    }
  }

  public static void setLore(ItemStack item, String[] lore) {
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.setLore(List.of(lore));
      item.setItemMeta(meta);
    }
  }

  public static void setUnbreakable(ItemStack item, boolean unbreakable) {
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.setUnbreakable(unbreakable);
      item.setItemMeta(meta);
    }
  }
}
