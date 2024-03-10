package dev.pixl.plugins.viperbows.util;

import dev.pixl.plugins.viperbows.ViperBowsPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class NbtItem {
  ItemStack item;

  /**
   * NBT item constructor using material.
   *
   * @param material Item material
   */
  public NbtItem(Material material) {
    item = new ItemStack(material, 1);
  }

  /**
   * NBT item constructor using item.
   *
   * @param item Item to wrap
   */
  public NbtItem(ItemStack item) {
    this.item = item;
  }

  /**
   * Checks if the item has a specified tag of type integer.
   *
   * @param key Item tag key
   * @return Whether the item has the tag
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private boolean hasIntTag(NamespacedKey key) {
    ItemMeta itemMeta = item.getItemMeta();
    if (itemMeta != null) {
      return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER);
    }
    return false;
  }

  /**
   * Checks if the item has a specified tag of type float
   *
   * @param key Item tag key
   * @return Whether the item has the tag
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private boolean hasFloatTag(NamespacedKey key) {
    ItemMeta itemMeta = item.getItemMeta();
    if (itemMeta != null) {
      return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.FLOAT);
    }
    return false;
  }

  /**
   * Checks if the item has a specified tag of type string
   *
   * @param key Item tag key
   * @return Whether the item has the tag
   */
  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  private boolean hasStringTag(NamespacedKey key) {
    ItemMeta itemMeta = item.getItemMeta();
    if (itemMeta != null) {
      return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }
    return false;
  }

  /**
   * Sets item tag to integer value.
   *
   * @param key Tag key
   * @param value Tag integer value
   */
  public void setTag(String key, int value) {
    NamespacedKey namespacedKey = new NamespacedKey(ViperBowsPlugin.getInstance(), key);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, value);
      item.setItemMeta(meta);
    }
  }

  /**
   * Sets item tag to float value.
   *
   * @param key Tag key
   * @param value Tag float value
   */
  public void setTag(String key, float value) {
    NamespacedKey namespacedKey = new NamespacedKey(ViperBowsPlugin.getInstance(), key);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.FLOAT, value);
      item.setItemMeta(meta);
    }
  }

  public void setTag(String key, String value) {
    NamespacedKey namespacedKey = new NamespacedKey(ViperBowsPlugin.getInstance(), key);
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
      item.setItemMeta(meta);
    }
  }

  /**
   * Returns the value of an item integer tag.
   *
   * @param key Tag key
   * @return Tag integer value, 0 if the tag doesn't exist
   */
  public int getInt(String key) {
    NamespacedKey namespacedKey = new NamespacedKey(ViperBowsPlugin.getInstance(), key);

    if (!hasIntTag(namespacedKey)) {
      return 0;
    }

    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      Integer result =  meta.getPersistentDataContainer().get(namespacedKey,
              PersistentDataType.INTEGER);
      if (result != null) {
        return result;
      }
    }
    return 0;
  }

  /**
   * Returns the value of an item integer tag.
   *
   * @param key Tag key
   * @return Tag integer value, 0 if the tag doesn't exist
   */
  public float getFloat(String key) {
    NamespacedKey namespacedKey = new NamespacedKey(ViperBowsPlugin.getInstance(), key);

    if (!hasFloatTag(namespacedKey)) {
      return 0;
    }

    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      Float result =  meta.getPersistentDataContainer().get(namespacedKey,
              PersistentDataType.FLOAT);
      if (result != null) {
        return result;
      }
    }
    return 0;
  }

  public String getString(String key) {
    NamespacedKey namespacedKey = new NamespacedKey(ViperBowsPlugin.getInstance(), key);

    if (!hasStringTag(namespacedKey)) {
      return "";
    }

    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      String result =  meta.getPersistentDataContainer().get(namespacedKey,
              PersistentDataType.STRING);
      if (result != null) {
        return result;
      }
    }
    return "";
  }

  /**
   * Sets the display name of the item.
   *
   * @param name New display name
   */
  public void setName(String name) {
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.setDisplayName(name);
      item.setItemMeta(meta);
    }
  }

  public void setLore(String[] lore) {
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.setLore(List.of(lore));
      item.setItemMeta(meta);
    }
  }

  /**
   * Sets whether the item is breakable.
   *
   * @param unbreakable Whether the item is breakable
   */
  public void setUnbreakable(boolean unbreakable) {
    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      meta.setUnbreakable(unbreakable);
      item.setItemMeta(meta);
    }
  }

  public ItemStack getItem() {
    return item;
  }
}
