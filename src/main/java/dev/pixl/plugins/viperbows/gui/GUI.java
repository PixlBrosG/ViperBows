package dev.pixl.plugins.viperbows.gui;

import dev.pixl.plugins.viperbows.ViperBowsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GUI implements Listener {
  private final Inventory inventory;
  private final Map<Integer, Consumer<ItemStack>> onClickEvents;

  public GUI(int rows, String name) {
    this.inventory = Bukkit.createInventory(null, 9 * rows, name);
    this.onClickEvents = new HashMap<>();

    Bukkit.getPluginManager().registerEvents(this, ViperBowsPlugin.getInstance());
  }

  public void setItem(int slot, ItemStack item, Consumer<ItemStack> onClick) {
    if (onClick != null) {
      onClickEvents.put(slot, onClick);
    }

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

    ItemStack item = event.getCurrentItem();
    if (item != null && onClickEvents.containsKey(event.getSlot())) {
      onClickEvents.get(event.getSlot()).accept(item);
    }
  }

  @EventHandler
  public void onInventoryDrag(InventoryClickEvent event) {
    if (event.getInventory() == inventory) {
      event.setCancelled(true);
    }
  }
}
