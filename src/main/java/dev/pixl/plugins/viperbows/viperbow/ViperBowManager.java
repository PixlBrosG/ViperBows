package dev.pixl.plugins.viperbows.viperbow;

import dev.pixl.plugins.viperbows.ViperBowsPlugin;
import dev.pixl.plugins.viperbows.ability.Ability;
import dev.pixl.plugins.viperbows.abilities.AbilityMetadata;
import dev.pixl.plugins.viperbows.ability.property.AbilityProperty;
import dev.pixl.plugins.viperbows.util.ItemNBT;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ViperBowManager implements Listener {
  Map<UUID, List<Ability>> bows;
  Map<UUID, UUID> projectiles;

  List<AbilityMetadata> abilities;

  public ViperBowManager() {
    bows = new HashMap<>();
    projectiles = new HashMap<>();
    abilities = new ArrayList<>();
  }

  public void clearAbilities() {
    abilities.clear();
  }

  public void registerAbility(AbilityMetadata metadata) {
    if (abilities.stream().anyMatch(a -> a.getAbilityClass().equals(metadata.getAbilityClass()))) {
      throw new IllegalArgumentException("Ability already registered");
    }
    abilities.add(metadata);
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
    if (!isAbilityEnabled(ability.getClass())) {
      return;
    }

    List<Ability> bowAbilities = bows.get(uuid);
    if (hasAbility(uuid, ability.getClass())) {
      // TODO: Handle this
      return;
    }

    bowAbilities.add(ability);
  }

  private boolean isAbilityEnabled(Class<? extends Ability> abilityClass) {
    return abilities.stream().anyMatch(a -> a.getAbilityClass().equals(abilityClass) && a.isEnabled());
  }

  private boolean isAbilityRegistered(Class<? extends Ability> abilityClass) {
    return abilities.stream().anyMatch(a -> a.getAbilityClass().equals(abilityClass));
  }

  public List<AbilityProperty> getProperties(Class<? extends Ability> ability) {
    return abilities.stream().filter(a -> a.getAbilityClass().equals(ability)).findFirst().map(AbilityMetadata::getProperties).orElse(null);
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

  public void toggleAbility(UUID bowID, Class<? extends Ability> abilityClass) {
    if (hasAbility(bowID, abilityClass)) {
      bows.get(bowID).removeIf(a -> a.getClass().equals(abilityClass));
    } else {
      try {
        Ability ability = abilityClass.getDeclaredConstructor().newInstance();
        addAbility(bowID, ability);
      } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
        JavaPlugin.getPlugin(ViperBowsPlugin.class).getLogger().severe("Failed to instantiate ability " + abilityClass.getName());
      }
    }
  }

  public AbilityMetadata getAbilityMetadata(Class<? extends Ability> abilityClass) {
    return abilities.stream().filter(a -> a.getAbilityClass().equals(abilityClass)).findFirst().orElse(null);
  }

  public List<AbilityMetadata> getRegisteredAbilities() {
    return abilities;
  }
}
