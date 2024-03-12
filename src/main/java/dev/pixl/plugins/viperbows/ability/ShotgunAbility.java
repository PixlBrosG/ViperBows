package dev.pixl.plugins.viperbows.ability;

import dev.pixl.plugins.viperbows.ViperBowsPlugin;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;

public class ShotgunAbility implements Ability {
  private static final Random random = new Random();

  private int amount = 7;
  private int spread = 15;
  private boolean registerNewArrows = true;

  @Override
  public void onShoot(@NotNull UUID bowID, EntityShootBowEvent event) {
    Vector velocity = event.getProjectile().getVelocity();

    for (int i = 0; i < amount; ++i) {
      Vector newVelocity = new Vector();

      double theta = Math.atan2(velocity.getY(), velocity.getX());
      double phi   = Math.atan2(velocity.getZ(),
              Math.sqrt(velocity.getX() * velocity.getX() + velocity.getY() * velocity.getY()));

      double a  = Math.toRadians(random.nextFloat(-1, 1) * spread);
      double b  = Math.toRadians(random.nextFloat(-1, 1) * spread);
      double ab = Math.sqrt(a * a + b * b);

      theta += a * Math.abs(a) / ab;
      phi   += b * Math.abs(b) / ab;

      newVelocity.setX(Math.cos(theta) * Math.cos(phi));
      newVelocity.setY(Math.sin(theta) * Math.cos(phi));
      newVelocity.setZ(Math.sin(phi));

      newVelocity.multiply(velocity.length());

      Projectile newArrow = event.getEntity().launchProjectile(Arrow.class, newVelocity);

      if (registerNewArrows) {
        ViperBowsPlugin.getInstance().getViperBowManager().registerProjectile(newArrow.getUniqueId(), bowID);
      }
    }
  }

  @Override
  public void onHit(@NotNull UUID bowID, @NotNull ProjectileHitEvent event) {
    // No special event on hit
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) throws IllegalArgumentException {
    if (amount < 0 || amount > 100) {
      throw new IllegalArgumentException("Amount should be between 0 (inclusive) and 100 (inclusive)");
    }
    this.amount = amount;
  }

  public int getSpread() {
    return spread;
  }

  public void setSpread(int spread) throws IllegalArgumentException {
    if (spread < 0 || spread > 180) {
      throw new IllegalArgumentException("Spread should be between 0 (inclusive) and 180 (inclusive)");
    }
    this.spread = spread;
  }

  public boolean isRegisterNewArrows() {
    return registerNewArrows;
  }

  public void setRegisterNewArrows(boolean registerNewArrows) {
    this.registerNewArrows = registerNewArrows;
  }
}
