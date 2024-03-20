package dev.pixl.plugins.viperbows.ability;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ExplosiveAbility implements Ability {
  private int force = 1;
  private boolean setFire = false;
  private boolean breakBlocks = false;

  @Override
  public void onShoot(@NotNull UUID bowID, @NotNull EntityShootBowEvent event) {
    // No special event on shoot
  }

  @Override
  public void onHit(@NotNull UUID bowID, @NotNull ProjectileHitEvent event) {
    Projectile projectile = event.getEntity();
    projectile.getWorld().createExplosion(projectile.getLocation(), force, setFire, breakBlocks, (Entity)projectile.getShooter());
  }

  public int getForce() {
    return force;
  }

  public void setForce(int force) {
    this.force = force;
  }

  public boolean isSetFire() {
    return setFire;
  }

  public void setSetFire(boolean setFire) {
    this.setFire = setFire;
  }

  public boolean isBreakBlocks() {
    return breakBlocks;
  }

  public void setBreakBlocks(boolean breakBlocks) {
    this.breakBlocks = breakBlocks;
  }
}
