package dev.pixl.plugins.viperbows.ability.property;

import dev.pixl.plugins.viperbows.ability.Ability;

public class BooleanProperty extends AbilityProperty {
  private boolean defaultValue;

  public BooleanProperty(Class<? extends Ability> abilityClass, String name) {
    super(abilityClass, name);
  }

  @Override
  public Class<?> getType() {
    return boolean.class;
  }

  @Override
  public boolean validateSetValue(Object value) {
    return true;
  }

  public boolean getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(boolean defaultValue) {
    this.defaultValue = defaultValue;
  }
}
