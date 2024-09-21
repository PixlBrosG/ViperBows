package dev.pixl.plugins.viperbows.gui;

import dev.pixl.plugins.viperbows.abilities.AbilityMetadata;
import dev.pixl.plugins.viperbows.util.ItemNBT;
import dev.pixl.plugins.viperbows.viperbow.ViperBowManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class ViperBowEditor {
  private final ViperBowManager viperBowManager;

  public ViperBowEditor(ViperBowManager viperBowManager) {
    this.viperBowManager = viperBowManager;
  }

  private void open(Player player, UUID bowID, int page) {
    List<AbilityMetadata> abilities = viperBowManager.getRegisteredAbilities();

    int startIndex = 0;
    int visibleAbilities = 0;
    for (int i = 0; i < abilities.size(); ++i) {
      if (!abilities.get(i).isHidden() && abilities.get(i).isEnabled()) {
        ++visibleAbilities;

        if (visibleAbilities == 2*9*page) {
          startIndex = i + 1;
          break;
        }
      }
    }

    int maxPages = (int) Math.ceil((double) visibleAbilities / (2*9));
    if (page >= maxPages) {
      return;
    }

    GUI gui = new GUI(3, ChatColor.DARK_RED + "ViperBow Editor"
            + ChatColor.DARK_GRAY + " (" + ChatColor.RED + (page+1) + "/" + maxPages + ChatColor.DARK_GRAY + ")");

    int slot = 0;
    for (int i = startIndex; i < abilities.size() && slot < 2*9; ++i) {
      AbilityMetadata abilityMetadata = abilities.get(2*9*page+i);
      if (!abilityMetadata.isHidden() && abilityMetadata.isEnabled()) {
        ItemStack item = getGUIItem(bowID, abilityMetadata);

        gui.setItem(slot, item, event -> {
          viperBowManager.toggleAbility(bowID, abilityMetadata.getAbilityClass());
          gui.updateItem(event.getSlot(), getGUIItem(bowID, abilityMetadata));
        });

        ++slot;
      }
    }

    // Bottom row
    ItemStack barrierItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    ItemNBT.setName(barrierItem, " ");
    for (int i = 2*9; i < 3*9; ++i) {
      gui.setItem(i, barrierItem);
    }

    ItemStack bowItem = new ItemStack(Material.BOW);
    ItemNBT.setName(bowItem, ChatColor.RED + "ViperBow");
    gui.setItem(2*9+4, bowItem);

    ItemStack previousPageItem = new ItemStack(Material.ARROW);
    ItemNBT.setName(previousPageItem, ChatColor.RED + "Previous Page");
    if (page > 0) {
      gui.setItem(2*9+3, previousPageItem, event -> open(player, bowID, page-1));
    }

    ItemStack nextPageItem = new ItemStack(Material.ARROW);
    ItemNBT.setName(nextPageItem, ChatColor.GREEN + "Next Page");
    if (page < maxPages-1) {
      gui.setItem(2*9+5, nextPageItem, event -> open(player, bowID, page+1));
    }

    gui.openInventory(player);
  }

  public void open(Player player, UUID bowID) {
    open(player, bowID, 0);
  }

  private ItemStack getGUIItem(UUID bowID, AbilityMetadata abilityMetadata) {
    ItemStack item = new ItemStack(abilityMetadata.getMaterial());

    ItemMeta meta = item.getItemMeta();
    if (meta != null) {
      if (viperBowManager.hasAbility(bowID, abilityMetadata.getAbilityClass())) {
        meta.setDisplayName(ChatColor.GREEN + abilityMetadata.getName());

        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
      } else {
        meta.setDisplayName(ChatColor.RED + abilityMetadata.getName());
      }

      meta.setLore(abilityMetadata.getLore());
      item.setItemMeta(meta);
    }

    return item;
  }
}
