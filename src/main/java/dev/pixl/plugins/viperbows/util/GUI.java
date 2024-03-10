package dev.pixl.plugins.viperbows.util;

import dev.pixl.plugins.viperbows.ViperBowsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUI implements Listener, Cloneable {
  private final Inventory inventory;

  @Override
  public GUI clone() throws CloneNotSupportedException {
    return (GUI)super.clone();
  }

  public GUI(int rows, String name) {
    this.inventory = Bukkit.createInventory(null, 9 * rows, name);
    Bukkit.getPluginManager().registerEvents(this, ViperBowsPlugin.getInstance());
  }

  public void setItem(int slot, ItemStack item) {
    this.inventory.setItem(slot, item);
  }

  public void openInventory(Player player) {
    player.openInventory(this.inventory);
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (event.getClickedInventory() != inventory) {
      return;
    }

    event.setCancelled(true);

    if (event.getCurrentItem() == null) {
      return;
    }

    // TODO: Implement
  }

  @EventHandler
  public void onInventoryDrag(InventoryClickEvent event) {
    if (event.getInventory() == inventory) {
      event.setCancelled(true);
    }
  }
}
