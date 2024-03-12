package dev.pixl.plugins.viperbows;

import dev.pixl.plugins.viperbows.ability.Ability;
import dev.pixl.plugins.viperbows.ability.ExplosiveAbility;
import dev.pixl.plugins.viperbows.ability.LightningAbility;
import dev.pixl.plugins.viperbows.ability.ShotgunAbility;
import dev.pixl.plugins.viperbows.util.NbtItem;
import dev.pixl.plugins.viperbows.viperbow.ViperBowManager;
import dev.pixl.plugins.viperbows.viperbow.ViperBowSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
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

  @EventHandler
  public void onEntityShootBow(EntityShootBowEvent event) {
    if (event.getEntityType() != EntityType.PLAYER) {
      return;
    }

    NbtItem bow = new NbtItem(event.getBow());
    viperBowManager.onShoot(bow, event);
  }

  @EventHandler
  public void onProjectileHit(ProjectileHitEvent event) {
    viperBowManager.onHit(event);
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
      bow.setName(ChatColor.RED + "Explosive Shotgun Bow");
      bow.setLore(new String[]{ ChatColor.DARK_GRAY + "Explodes on impact", ChatColor.DARK_GRAY + "Shoots like a shotgun!" });
      viperBowManager.registerAbility(bow, new ExplosiveAbility());
      viperBowManager.registerAbility(bow, new ShotgunAbility());
      viperBowManager.registerAbility(bow, new LightningAbility());

      player.getInventory().addItem(bow.getItem());
    }

    return true;
  }

  public ViperBowManager getViperBowManager() {
    return viperBowManager;
  }

  public static ViperBowsPlugin getInstance() {
    return instance;
  }
}
