package dev.pixl.plugins.viperbows.ability;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LightningAbility implements Ability {
  private int amount = 1;

  @Override
  public void onShoot(@NotNull UUID bowID, @NotNull EntityShootBowEvent event) {
    // No special event on shoot
  }

  @Override
  public void onHit(@NotNull UUID bowID, @NotNull ProjectileHitEvent event) {
    Location location = event.getEntity().getLocation();
    World world = location.getWorld();
    if (world == null) {
      return;
    }

    for (int i = 0; i < amount; ++i) {
      world.strikeLightning(location);
    }
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    if (amount < 1 || amount > 10) {
      throw new IllegalArgumentException("Amount must be between 1 (inclusive) and 10 (inclusive)");
    }
    this.amount = amount;
  }
}
