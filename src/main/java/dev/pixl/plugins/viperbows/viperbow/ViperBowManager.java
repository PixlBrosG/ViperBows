package dev.pixl.plugins.viperbows.viperbow;

import dev.pixl.plugins.viperbows.ability.Ability;
import dev.pixl.plugins.viperbows.util.NbtItem;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.*;

public class ViperBowManager {
  Map<UUID, List<Ability>> bowAbilities;
  Map<UUID, UUID> projectiles;

  public ViperBowManager() {
    bowAbilities = new HashMap<>();
    projectiles = new HashMap<>();
  }

  public void registerAbility(NbtItem item, Ability ability) {
    UUID uuid = null;

    // TODO: Rewrite this
    String uuidString = item.getString("uuid");
    if (!uuidString.isEmpty()) {
      uuid = UUID.fromString(uuidString);
    } else {
      uuid = UUID.randomUUID();
      bowAbilities.put(uuid, new ArrayList<>());
      item.setTag("uuid", uuid.toString());
    }

    List<Ability> abilities = bowAbilities.get(uuid);
    if (abilities.stream().anyMatch(a -> a.getClass().equals(ability.getClass()))) {
      // TODO: Handle this
      return;
    }

    abilities.add(ability);
  }

  public List<Ability> getAbilities(UUID uuid) {
    return bowAbilities.get(uuid);
  }

  public void onShoot(NbtItem item, EntityShootBowEvent event) {
    String uuidString = item.getString("uuid");
    if (uuidString.isEmpty()) {
      return;
    }

    UUID uuid = UUID.fromString(uuidString);
    if (bowAbilities.containsKey(uuid)) {
      projectiles.put(event.getProjectile().getUniqueId(), uuid);

      bowAbilities.get(uuid).forEach(ability -> ability.onShoot(uuid, event));
    }
  }

  public void onHit(ProjectileHitEvent event) {
    UUID uuid = event.getEntity().getUniqueId();

    if (projectiles.containsKey(uuid)) {
      UUID bowID = projectiles.get(uuid);
      projectiles.remove(uuid);

      bowAbilities.get(bowID).forEach(ability -> ability.onHit(bowID, event));
    }
  }

  public void registerProjectile(UUID projectileID, UUID bowID) {
    projectiles.put(projectileID, bowID);
  }
}
