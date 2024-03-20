package dev.pixl.plugins.viperbows.ability.property;

import dev.pixl.plugins.viperbows.ViperBowsPlugin;
import dev.pixl.plugins.viperbows.ability.Ability;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.logging.Level;

public abstract class AbilityProperty {
  private String name;
  private List<String> lore;
  private boolean modifiable;
  private Material material;

  // Getter and setter for value
  private MethodHandle setterMethod;
  private MethodHandle getterMethod;

  protected AbilityProperty(Class<? extends Ability> abilityClass, String name) {
    this.name = name;

    MethodHandles.Lookup lookup = MethodHandles.lookup();
    String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);

    try {
      MethodType getterType = MethodType.methodType(getType());
      String getterName = (getType().equals(boolean.class) ? "is" : "get") + capitalizedName;
      getterMethod = lookup.findVirtual(abilityClass, getterName, getterType);
    } catch (NoSuchMethodException e) {
      String message = "Could not find getter for " + name + " in " + abilityClass.getName();
      JavaPlugin.getPlugin(ViperBowsPlugin.class).getLogger().log(Level.SEVERE, message, e);
    } catch (IllegalAccessException e) {
      String message = "Could not access getter for " + name + " in " + abilityClass.getName();
      JavaPlugin.getPlugin(ViperBowsPlugin.class).getLogger().log(Level.SEVERE, message, e);
    }

    try {
      MethodType setterType = MethodType.methodType(void.class, getType());
      String setterName = "set" + capitalizedName;
      setterMethod = lookup.findVirtual(abilityClass, setterName, setterType);
    } catch (NoSuchMethodException e) {
      String message = "Could not find setter for " + name + " in " + abilityClass.getName();
      JavaPlugin.getPlugin(ViperBowsPlugin.class).getLogger().log(Level.SEVERE, message, e);
    } catch (IllegalAccessException e) {
      String message = "Could not access setter for " + name + " in " + abilityClass.getName();
      JavaPlugin.getPlugin(ViperBowsPlugin.class).getLogger().log(Level.SEVERE, message, e);
    }
  }

  public abstract Class<?> getType();
  public abstract boolean validateSetValue(Object value);

  public Object getValue(Ability ability) {
    try {
      return getterMethod.bindTo(ability).invoke();
    } catch (Throwable e) {
      String message = "Could not get value for " + name + " in " + ability.getClass().getName();
      JavaPlugin.getPlugin(ViperBowsPlugin.class).getLogger().log(Level.SEVERE, message, e);
      return null;
    }
  }

  public void setValue(Ability ability, Object value) {
    try {
      if (modifiable && validateSetValue(value)) {
        setterMethod.bindTo(ability).invoke(value);
      } else {
        String message = "Invalid value for " + name + " in " + ability.getClass().getName();
        JavaPlugin.getPlugin(ViperBowsPlugin.class).getLogger().log(Level.SEVERE, message);
      }
    } catch (Throwable e) {
      String message = "Could not set value for " + name + " in " + ability.getClass().getName();
      JavaPlugin.getPlugin(ViperBowsPlugin.class).getLogger().log(Level.SEVERE, message, e);
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getLore() {
    return lore;
  }

  public void setLore(List<String> lore) {
    this.lore = lore;
  }

  public boolean isModifiable() {
    return modifiable;
  }

  public void setModifiable(boolean modifiable) {
    this.modifiable = modifiable;
  }

  public Material getMaterial() {
    return material;
  }

  public void setMaterial(Material material) {
    this.material = material;
  }
}
