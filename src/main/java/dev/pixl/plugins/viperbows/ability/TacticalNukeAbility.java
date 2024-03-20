package dev.pixl.plugins.viperbows.ability;

import dev.pixl.plugins.viperbows.ViperBowsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TacticalNukeAbility implements Ability {
  private int power = 5;
  private boolean setFire = true;
  private boolean breakBlocks = true;
  private int delay = 5;

  private class NukeMeta {
    public boolean active = false;
    // Scheduled task
    public int taskID = -1;

    public void start(UUID arrowID) {
      active = false;
      taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(ViperBowsPlugin.class), () -> {
        active = true;
        Entity arrow = Bukkit.getEntity(arrowID);
        if (arrow != null) {
          arrow.setVelocity(new Vector(0, 0, 0));
        }
      }, delay * 20L);
    }
  }

  private final Map<UUID, NukeMeta> nukeMeta;

  public TacticalNukeAbility() {
    nukeMeta = new HashMap<>();
  }

  @Override
  public void onShoot(@NotNull UUID bowID, @NotNull EntityShootBowEvent event) {
    NukeMeta meta = new NukeMeta();
    meta.start(event.getProjectile().getUniqueId());
    nukeMeta.put(event.getProjectile().getUniqueId(), meta);
  }

  @Override
  public void onHit(@NotNull UUID bowID, @NotNull ProjectileHitEvent event) {
    UUID arrowID = event.getEntity().getUniqueId();
    NukeMeta meta = nukeMeta.get(arrowID);
    if (meta.active) {
      Projectile projectile = event.getEntity();
      projectile.getWorld().createExplosion(projectile.getLocation(), power, setFire, breakBlocks, (Entity)projectile.getShooter());
      nukeMeta.remove(arrowID);
    } else {
      Bukkit.getScheduler().cancelTask(meta.taskID);
      nukeMeta.remove(arrowID);
    }
  }

  public int getPower() {
    return power;
  }

  public void setPower(int power) {
    this.power = power;
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

  public int getDelay() {
    return delay;
  }

  public void setDelay(int delay) {
    this.delay = delay;
  }
}
