package dev.pixl.plugins.viperbows.viperbow;

import dev.pixl.plugins.viperbows.ViperBowsPlugin;
import dev.pixl.plugins.viperbows.ability.Ability;
import dev.pixl.plugins.viperbows.util.NbtItem;
import net.md_5.bungee.api.ChatColor;

import java.util.*;

public class ViperBowManager {
  Map<UUID, List<Ability>> bowAbilities;

  public ViperBowManager() {
    this.bowAbilities = new HashMap<>();
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
}
