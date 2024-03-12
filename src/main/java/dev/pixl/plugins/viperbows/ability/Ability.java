package dev.pixl.plugins.viperbows.ability;

import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Ability {
  void onShoot(@NotNull UUID bowID, @NotNull EntityShootBowEvent event);
  void onHit(@NotNull UUID bowID, @NotNull ProjectileHitEvent event);
}
