package dev.pixl.plugins.viperbows.ability;

import dev.pixl.plugins.viperbows.ability.property.AbilityProperty;
import org.bukkit.Material;
import org.bukkit.permissions.Permission;

import java.util.List;

public class AbilityMetadata {
  private Class<? extends Ability> abilityClass;

  private String name;
  private List<String> lore;

  private boolean enabled;
  private boolean modifiable;
  private boolean hidden;
  private Material material;

  private Permission usePermission;
  private Permission modifyPermission;

  private List<AbilityProperty> properties;

  public AbilityMetadata() {
  }

  public Class<? extends Ability> getAbilityClass() {
    return abilityClass;
  }

  public void setAbilityClass(Class<? extends Ability> abilityClass) {
    this.abilityClass = abilityClass;
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

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isModifiable() {
    return modifiable;
  }

  public void setModifiable(boolean modifiable) {
    this.modifiable = modifiable;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

  public Material getMaterial() {
    return material;
  }

  public void setMaterial(Material material) {
    this.material = material;
  }

  public Permission getUsePermission() {
    return usePermission;
  }

  public void setUsePermission(Permission usePermission) {
    this.usePermission = usePermission;
  }

  public Permission getModifyPermission() {
    return modifyPermission;
  }

  public void setModifyPermission(Permission modifyPermission) {
    this.modifyPermission = modifyPermission;
  }

  public List<AbilityProperty> getProperties() {
    return properties;
  }

  public void setProperties(List<AbilityProperty> properties) {
    this.properties = properties;
  }
}
