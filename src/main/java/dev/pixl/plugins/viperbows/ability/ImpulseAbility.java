package dev.pixl.plugins.viperbows.ability;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ImpulseAbility implements Ability {
  private int radius = 1;
  private int power = 1;

  @Override
  public void onShoot(@NotNull UUID bowID, @NotNull EntityShootBowEvent event) {
    // No special behavior on shoot
  }

  @Override
  public void onHit(@NotNull UUID bowID, @NotNull ProjectileHitEvent event) {
    for (Entity e : event.getEntity().getWorld().getNearbyEntities(event.getEntity().getLocation(), radius, radius, radius)) {
      Vector entityLocation = e.getLocation().toVector();
      entityLocation.setY(entityLocation.getY() + 1);
      Vector v = entityLocation.subtract(event.getEntity().getLocation().toVector()).multiply(power);
      e.setVelocity(v);
    }
  }

  public int getRadius() {
    return radius;
  }

  public void setRadius(int radius) throws IllegalArgumentException {
    if (radius < 1 || radius > 25) {
      throw new IllegalArgumentException("Radius must be between 1 and 25");
    }
    this.radius = radius;
  }

  public int getPower() {
      return power;
  }

  public void setPower(int power) throws IllegalArgumentException {
    if (power < 1 || power > 25) {
        throw new IllegalArgumentException("Power must be between 1 and 25");
    }
    this.power = power;
  }
}
