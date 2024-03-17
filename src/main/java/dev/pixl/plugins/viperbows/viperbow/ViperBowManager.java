package dev.pixl.plugins.viperbows.viperbow;

import dev.pixl.plugins.viperbows.ability.Ability;
import dev.pixl.plugins.viperbows.gui.GUI;
import dev.pixl.plugins.viperbows.util.ItemNBT;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ViperBowManager implements Listener {
  Map<UUID, List<Ability>> bows;
  Map<UUID, UUID> projectiles;

  List<Class<? extends Ability>> abilities;

  public ViperBowManager() {
    bows = new HashMap<>();
    projectiles = new HashMap<>();
    abilities = new ArrayList<>();
  }

  public void registerAbility(Class<? extends Ability> ability){
    if (abilities.contains(ability)) {
      // TODO: Handle this
      return;
    }
    abilities.add(ability);
  }

  public UUID registerBow(ItemStack item)  {
    UUID uuid = UUID.randomUUID();
    bows.put(uuid, new ArrayList<>());
    ItemNBT.setTag(item, "uuid", uuid.toString());
    return uuid;
  }

  public boolean isBow(UUID bowID) {
    return bows.containsKey(bowID);
  }

  public boolean isBow(ItemStack item) {
    UUID uuid = getBowID(item);
    if (uuid == null) {
      return false;
    }

    return isBow(uuid);
  }

  public UUID getBowID(ItemStack item) {
    String uuidString = ItemNBT.getStringTag(item, "uuid");
    if (uuidString.isEmpty()) {
      return null;
    }

    return UUID.fromString(uuidString);
  }

  public boolean hasAbility(UUID uuid, Class<? extends Ability> ability) {
    return bows.get(uuid).stream().anyMatch(a -> a.getClass().equals(ability));
  }

  public void addAbility(UUID uuid, Ability ability) {
    // NOTE: This should probably validate that the ability class is registered

    List<Ability> bowAbilities = bows.get(uuid);
    if (hasAbility(uuid, ability.getClass())) {
      // TODO: Handle this
      return;
    }

    bowAbilities.add(ability);
  }

  public List<Ability> getAbilities(UUID uuid) {
    return bows.get(uuid);
  }

  /**
   * Get all the abilities for all the bows<br>
   * does not clone the map
   *
   * @return A map of all the abilities for all the bows
   */
  public Map<UUID, List<Ability>> getBows() {
    return bows;
  }

  /**
   * Set the abilities for all the bows<br>
   * does not clone the map
   *
   * @param bows A map of all the abilities for all the bows
   */
  public void setBows(Map<UUID, List<Ability>> bows) {
    this.bows = bows;
  }

  @EventHandler
  public void onShoot(EntityShootBowEvent event) {
    ItemStack item = event.getBow();
    if (item == null) {
      return;
    }

    UUID uuid = getBowID(item);
    if (uuid == null) {
      return;
    }

    if (bows.containsKey(uuid)) {
      projectiles.put(event.getProjectile().getUniqueId(), uuid);

      bows.get(uuid).forEach(ability -> ability.onShoot(uuid, event));
    }
  }

  @EventHandler
  public void onHit(ProjectileHitEvent event) {
    UUID uuid = event.getEntity().getUniqueId();

    if (projectiles.containsKey(uuid)) {
      UUID bowID = projectiles.get(uuid);
      projectiles.remove(uuid);

      bows.get(bowID).forEach(ability -> ability.onHit(bowID, event));
    }
  }

  public void registerProjectile(UUID projectileID, UUID bowID) {
    projectiles.put(projectileID, bowID);
  }

  private void openEditor(Player player, UUID bowID, int page) {
    GUI gui = new GUI(3, ChatColor.DARK_RED + "ViperBow Editor");

    for (int i = 0; i < 2*9 && 2*9*page+i < abilities.size(); ++i) {
      Class<? extends Ability> abilityClass = abilities.get(2*9*page+i);

      Material material = hasAbility(bowID, abilityClass) ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;

      ItemStack item = new ItemStack(material);
      ItemNBT.setName(item, ChatColor.AQUA + abilityClass.getSimpleName());

      gui.setItem(i, item, clickedItem -> {
        if (hasAbility(bowID, abilityClass)) {
          bows.get(bowID).removeIf(a -> a.getClass().equals(abilityClass));
        } else {
          try {
            addAbility(bowID, abilityClass.getDeclaredConstructor().newInstance());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        openEditor(player, bowID, page);
      });
    }

    // Bottom row
    {
      ItemStack bowItem = new ItemStack(Material.BOW);
      ItemStack barrierItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemStack previousPageItem = new ItemStack(Material.ARROW);
      ItemStack nextPageItem = new ItemStack(Material.ARROW);

      ItemNBT.setName(barrierItem, " ");
      ItemNBT.setName(bowItem, ChatColor.RED + "ViperBow");
      ItemNBT.setName(previousPageItem, ChatColor.RED + "Previous Page");
      ItemNBT.setName(nextPageItem, ChatColor.GREEN + "Next Page");

      for (int i = 2*9; i < 3*9; ++i) {
        gui.setItem(i, barrierItem, null);
      }

      gui.setItem(2*9+3, previousPageItem, null);
      gui.setItem(2*9+4, bowItem, null);
      gui.setItem(2*9+5, nextPageItem, null);
    }

    gui.openInventory(player);
  }

  public void openEditor(Player player, UUID bowID) {
    openEditor(player, bowID, 0);
  }
}
