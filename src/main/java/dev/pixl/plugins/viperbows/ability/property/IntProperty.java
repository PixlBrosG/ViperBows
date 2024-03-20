package dev.pixl.plugins.viperbows.ability.property;

import dev.pixl.plugins.viperbows.ability.Ability;

public class IntProperty extends AbilityProperty {
  private int min;
  private int max;
  private int step;
  private int defaultValue;

  public IntProperty(Class<? extends Ability> abilityClass, String name) {
    super(abilityClass, name);
  }

  @Override
  public Class<?> getType() {
    return int.class;
  }

  @Override
  public boolean validateSetValue(Object value) {
    if (!(value instanceof Integer intValue)) {
      return false;
    }
    
    return intValue >= min && intValue <= max && (intValue - min) % step == 0;
  }

  public int getMin() {
    return min;
  }

  public void setMin(int min) {
    this.min = min;
  }

  public int getMax() {
    return max;
  }

  public void setMax(int max) {
    this.max = max;
  }

  public int getStep() {
    return step;
  }

  public void setStep(int step) {
    this.step = step;
  }

  public int getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(int defaultValue) {
    this.defaultValue = defaultValue;
  }
}
