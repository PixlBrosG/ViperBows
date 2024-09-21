package dev.pixl.plugins.viperbows;

import dev.pixl.plugins.viperbows.ability.Ability;
import dev.pixl.plugins.viperbows.util.ItemNBT;
import dev.pixl.plugins.viperbows.viperbow.ViperBowManager;
import dev.pixl.plugins.viperbows.viperbow.ViperBowSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandHandler {
  private static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "ViperBows" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;
  private static final String ONLY_PLAYERS = PREFIX + ChatColor.RED  + "Only players can use this command.";
  // private static final String ONLY_CONSOLE = PREFIX + ChatColor.RED + "Only the console can use this command.";
  // private static final String NO_PERMISSION = PREFIX + ChatColor.RED + "You do not have permission to use this command.";

  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String[] args) {
    switch (command.getName()) {
      case "vbinfo" -> commandInfo(sender);
      case "vbcreate" -> commandCreate(sender);
      case "vbsave" -> commandSave(sender);
      case "vbload" -> commandLoad(sender);
      case "vbedit" -> commandEdit(sender, args);
      default -> { return false; }
    }

    return true;
  }

  private void commandInfo(CommandSender sender) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage(ONLY_PLAYERS);
      return;
    }

    ViperBowsPlugin plugin = JavaPlugin.getPlugin(ViperBowsPlugin.class);
    ViperBowManager viperBowManager = plugin.getViperBowManager();

    ItemStack heldItem = player.getInventory().getItemInMainHand();
    UUID uuid = viperBowManager.getBowID(heldItem);

    if (uuid == null) {
      sender.sendMessage(PREFIX + ChatColor.RED + "No UUID found!");
      return;
    }

    if (!viperBowManager.isBow(uuid)) {
      sender.sendMessage(PREFIX + ChatColor.RED + "Invalid UUID!");
      return;
    }

    sender.sendMessage(ChatColor.GREEN + "UUID: " + ChatColor.LIGHT_PURPLE + uuid);
    sender.sendMessage(ChatColor.GREEN + "Abilities:");
    for (Ability ability : viperBowManager.getAbilities(uuid)) {
      sender.sendMessage(ChatColor.GREEN + " - " + ChatColor.LIGHT_PURPLE + ability.getClass().getName());
    }
  }

  private void commandCreate(CommandSender sender) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage(ONLY_PLAYERS);
      return;
    }

    ViperBowsPlugin plugin = JavaPlugin.getPlugin(ViperBowsPlugin.class);
    ViperBowManager viperBowManager = plugin.getViperBowManager();

    ItemStack bow = new ItemStack(Material.BOW);
    ItemNBT.setName(bow, ChatColor.RED + "Viper Bow");
    ItemNBT.setLore(bow, new String[]{ ChatColor.GRAY + "Custom bow created with ViperBows" });

    UUID bowID = viperBowManager.registerBow(bow);
    player.getInventory().addItem(bow);

    sender.sendMessage(PREFIX + ChatColor.GREEN + "Created bow with UUID: " + ChatColor.LIGHT_PURPLE + bowID);
  }

  private void commandSave(CommandSender sender) {
    ViperBowsPlugin plugin = JavaPlugin.getPlugin(ViperBowsPlugin.class);
    ViperBowSerializer viperBowSerializer = plugin.getViperBowSerializer();

    // TODO: viperBowSerializer.serializeAbilities();
    viperBowSerializer.serializeBows();

    sender.sendMessage(PREFIX + ChatColor.GREEN + "Saved ViperBows data.");
  }

  private void commandLoad(CommandSender sender) {
    ViperBowsPlugin plugin = JavaPlugin.getPlugin(ViperBowsPlugin.class);
    ViperBowSerializer viperBowSerializer = plugin.getViperBowSerializer();

    viperBowSerializer.deserializeAbilities();
    viperBowSerializer.deserializeBows();

    sender.sendMessage(PREFIX + ChatColor.GREEN + "Loaded ViperBows data.");
  }

  private void commandEdit(CommandSender sender, String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage(ONLY_PLAYERS);
      return;
    }

    ViperBowsPlugin plugin = JavaPlugin.getPlugin(ViperBowsPlugin.class);
    ViperBowManager viperBowManager = plugin.getViperBowManager();

    if (args.length == 0) {
      ItemStack item = player.getInventory().getItemInMainHand();
      if (item.getType() != Material.BOW) {
        player.sendMessage(PREFIX + ChatColor.RED + "You must be holding a bow to use this command.");
        return;
      }

      UUID uuid = viperBowManager.getBowID(item);
      plugin.getViperBowEditor().open(player, uuid);
      return;
    }

    try {
      UUID uuid = UUID.fromString(args[0]);
      if (viperBowManager.isBow(uuid)) {
        player.sendMessage(PREFIX + ChatColor.RED + "No bow found with that UUID.");
        return;
      }

      plugin.getViperBowEditor().open(player, uuid);
    } catch (IllegalArgumentException e) {
      player.sendMessage(PREFIX + ChatColor.RED + "Invalid UUID");
    }
  }
}
