package dev.pixl.plugins.viperbows;

import dev.pixl.plugins.viperbows.ability.Ability;
import dev.pixl.plugins.viperbows.ability.ExplosiveAbility;
import dev.pixl.plugins.viperbows.ability.LightningAbility;
import dev.pixl.plugins.viperbows.ability.ShotgunAbility;
import dev.pixl.plugins.viperbows.util.ItemNBT;
import dev.pixl.plugins.viperbows.viperbow.ViperBowManager;
import dev.pixl.plugins.viperbows.viperbow.ViperBowSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CommandHandler {
  private static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "ViperBows" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;
  private static final String ONLY_PLAYERS = PREFIX + ChatColor.RED  + "Only players can use this command.";
  // private static final String ONLY_CONSOLE = PREFIX + ChatColor.RED + "Only the console can use this command.";
  // private static final String NO_PERMISSION = PREFIX + ChatColor.RED + "You do not have permission to use this command.";

  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String[] args) {
    return switch (command.getName()) {
      case "vbinfo" -> command_vbinfo(sender, args);
      case "vbcreate" -> command_vbcreate(sender, args);
      case "vbsave" -> command_vbsave(sender, args);
      case "vbload" -> command_vbload(sender, args);
      case "vbedit" -> command_vbedit(sender, args);
      default -> false;
    };
  }

  private boolean command_vbinfo(CommandSender sender, String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage(ONLY_PLAYERS);
      return true;
    }

    ViperBowsPlugin plugin = ViperBowsPlugin.getInstance();
    ViperBowManager viperBowManager = plugin.getViperBowManager();

    ItemStack heldItem = player.getInventory().getItemInMainHand();
    UUID uuid = viperBowManager.getBowID(heldItem);

    if (uuid == null) {
      sender.sendMessage(PREFIX + ChatColor.RED + "No UUID found!");
      return true;
    }

    if (!viperBowManager.isBow(uuid)) {
      sender.sendMessage(PREFIX + ChatColor.RED + "Invalid UUID!");
      return true;
    }

    sender.sendMessage(ChatColor.GREEN + "UUID: " + ChatColor.LIGHT_PURPLE + uuid);
    sender.sendMessage(ChatColor.GREEN + "Abilities:");
    for (Ability ability : viperBowManager.getAbilities(uuid)) {
      sender.sendMessage(ChatColor.GREEN + " - " + ChatColor.LIGHT_PURPLE + ability.getClass().getName());
    }

    return true;
  }

  private boolean command_vbcreate(CommandSender sender, String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage(ONLY_PLAYERS);
      return true;
    }

    ViperBowsPlugin plugin = ViperBowsPlugin.getInstance();
    ViperBowManager viperBowManager = plugin.getViperBowManager();

    ItemStack bow = new ItemStack(Material.BOW);
    ItemNBT.setName(bow, ChatColor.RED + "Explosive Shotgun Bow");
    ItemNBT.setLore(bow, new String[]{ ChatColor.DARK_GRAY + "Explodes on impact", ChatColor.DARK_GRAY + "Shoots like a shotgun!" });

    UUID bowID = viperBowManager.registerBow(bow);
    viperBowManager.addAbility(bowID, new ExplosiveAbility());
    viperBowManager.addAbility(bowID, new ShotgunAbility());
    viperBowManager.addAbility(bowID, new LightningAbility());

    player.getInventory().addItem(bow);

    sender.sendMessage(PREFIX + ChatColor.GREEN + "Created bow with UUID: " + ChatColor.LIGHT_PURPLE + bowID);

    return true;
  }

  private boolean command_vbsave(CommandSender sender, String[] args) {
    ViperBowsPlugin plugin = ViperBowsPlugin.getInstance();
    ViperBowSerializer viperBowSerializer = plugin.getViperBowSerializer();

    viperBowSerializer.serialize();

    sender.sendMessage(PREFIX + ChatColor.GREEN + "Saved ViperBows data.");

    return true;
  }

  private boolean command_vbload(CommandSender sender, String[] args) {
    ViperBowsPlugin plugin = ViperBowsPlugin.getInstance();
    ViperBowSerializer viperBowSerializer = plugin.getViperBowSerializer();

    viperBowSerializer.deserialize();

    sender.sendMessage(PREFIX + ChatColor.GREEN + "Loaded ViperBows data.");

    return true;
  }

  private boolean command_vbedit(CommandSender sender, String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage(ONLY_PLAYERS);
      return true;
    }

    ViperBowsPlugin plugin = ViperBowsPlugin.getInstance();
    ViperBowManager viperBowManager = plugin.getViperBowManager();

    if (args.length == 0) {
      ItemStack item = player.getInventory().getItemInMainHand();
      if (item.getType() != Material.BOW) {
        player.sendMessage(PREFIX + ChatColor.RED + "You must be holding a bow to use this command.");
        return true;
      }

      UUID uuid = viperBowManager.getBowID(item);
      viperBowManager.openEditor(player, uuid);

      return true;
    }

    try {
      UUID uuid = UUID.fromString(args[0]);
      if (viperBowManager.isBow(uuid)) {
        player.sendMessage(PREFIX + ChatColor.RED + "No bow found with that UUID.");
        return true;
      }

      viperBowManager.openEditor(player, uuid);
    } catch (IllegalArgumentException e) {
      player.sendMessage(PREFIX + ChatColor.RED + "Invalid UUID");
    }
    return true;
  }
}
