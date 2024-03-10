package dev.pixl.plugins.viperbows;

import dev.pixl.plugins.viperbows.ability.Ability;
import dev.pixl.plugins.viperbows.ability.ExplosiveAbility;
import dev.pixl.plugins.viperbows.util.NbtItem;
import dev.pixl.plugins.viperbows.viperbow.ViperBowManager;
import dev.pixl.plugins.viperbows.viperbow.ViperBowSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Main plugin class.
 *
 * @author P1xl__
 * @since 1.0.0
 */
public final class ViperBowsPlugin extends JavaPlugin implements Listener {

  private ViperBowManager viperBowManager;
  private ViperBowSerializer viperBowSerializer;

  private static ViperBowsPlugin instance;

  @Override
  public void onEnable() {
    if (instance == null) {
      instance = this;
    }

    viperBowManager = new ViperBowManager();
    viperBowSerializer = new ViperBowSerializer(viperBowManager);

    getServer().getPluginManager().registerEvents(this, this);
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, String[] args) {

    if (command.getName().equalsIgnoreCase("bowid")) {
      if (!(sender instanceof Player player)) {
        sender.sendMessage("Only players can use this command.");
        return true;
      }

      ItemStack heldItem = player.getInventory().getItemInMainHand();
      NbtItem heldItemNbt = new NbtItem(heldItem);
      String uuidString = heldItemNbt.getString("uuid");
      if (uuidString.isEmpty()) {
        sender.sendMessage(ChatColor.DARK_RED + "Bow not registered");
      } else {
        UUID uuid = UUID.fromString(uuidString);

        sender.sendMessage(ChatColor.GREEN + "UUID: " + ChatColor.LIGHT_PURPLE + uuidString);
        sender.sendMessage(ChatColor.GREEN + "Abilities:");
        for (Ability ability : viperBowManager.getAbilities(uuid)) {
          sender.sendMessage(ChatColor.GREEN + " - " + ChatColor.LIGHT_PURPLE + ability.getClass().getName());
        }
      }
    }

    if (command.getName().equalsIgnoreCase("createbow")) {
      if (!(sender instanceof Player player)) {
        sender.sendMessage("Only players can use this command.");
        return true;
      }

      NbtItem bow = new NbtItem(Material.BOW);
      bow.setName(ChatColor.RED + "Explosive Bow");
      bow.setLore(new String[]{ ChatColor.DARK_GRAY + "Explodes on impact" });
      viperBowManager.registerAbility(bow, new ExplosiveAbility());

      player.getInventory().addItem(bow.getItem());
    }

    return true;
  }

  public static ViperBowsPlugin getInstance() {
    return instance;
  }
}
