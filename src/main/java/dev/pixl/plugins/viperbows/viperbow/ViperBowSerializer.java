package dev.pixl.plugins.viperbows.viperbow;

import dev.pixl.plugins.viperbows.ViperBowsPlugin;
import dev.pixl.plugins.viperbows.ability.Ability;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;

public class ViperBowSerializer {
  private final ViperBowManager viperBowManager;

  public ViperBowSerializer(ViperBowManager viperBowManager) {
    this.viperBowManager = viperBowManager;
  }

  public void serialize() {
    JavaPlugin plugin = JavaPlugin.getPlugin(ViperBowsPlugin.class);
    File file = new File(plugin.getDataFolder(), "bows.yml");
    FileConfiguration config = new YamlConfiguration();

    Map<UUID, List<Ability>> abilities = viperBowManager.getAbilities();

    for (Map.Entry<UUID, List<Ability>> entry : abilities.entrySet()) {
      UUID uuid = entry.getKey();
      List<Ability> abilityList = entry.getValue();

      config.set(uuid.toString(), null); // Clear the section (if it exists)
      ConfigurationSection section = config.createSection(uuid.toString());

      for (Ability ability : abilityList) {
        String sectionName = ability.getClass().getName().replace('.', '-');
        ConfigurationSection abilitySection = section.createSection(sectionName);

        for (Field field : ability.getClass().getDeclaredFields()) {
          serializeField(field, ability, abilitySection);
        }
      }
    }

    try {
      config.save(file);
    } catch (Exception e) {
      plugin.getLogger().log(Level.SEVERE, "Could not save bows.yml", e);
    }
  }

  private void serializeField(Field field, Ability ability, ConfigurationSection section) {
    if (!Modifier.isStatic(field.getModifiers())) {
      try {
        Object value = null;

        if (Modifier.isPrivate(field.getModifiers())) {
          String getterName = (field.getType().equals(boolean.class) ? "is" : "get") +
                  field.getName().substring(0, 1).toUpperCase() +
                  field.getName().substring(1);

          Method getter = ability.getClass().getMethod(getterName);
          value = getter.invoke(ability);
        } else {
          value = field.get(ability);
        }

        section.set(field.getName(), value);
      } catch (Exception e) {
        String message = "Could not save field " + field.getName() + " in " + section.getCurrentPath();
        JavaPlugin.getPlugin(ViperBowsPlugin.class).getLogger().log(Level.SEVERE, message, e);
      }
    }
  }

  public void deserialize() {
    JavaPlugin plugin = JavaPlugin.getPlugin(ViperBowsPlugin.class);
    File file = new File(plugin.getDataFolder(), "bows.yml");
    FileConfiguration config = new YamlConfiguration();
    try {
      config.load(file);
    } catch (Exception e) {
      plugin.getLogger().log(Level.SEVERE, "Could not load bows.yml", e);
      return;
    }

    Map<UUID, List<Ability>> abilities = new HashMap<>();

    for (String key : config.getKeys(false)) {
      UUID uuid = UUID.fromString(key);
      ConfigurationSection section = config.getConfigurationSection(key);
      List<Ability> abilityList = new ArrayList<>();

      for (String abilityName : section.getKeys(false)) {
        try {
          Class<?> abilityClass = Class.forName(abilityName.replace('-', '.'));
          Ability ability = (Ability) abilityClass.getDeclaredConstructor().newInstance();


          for (Field field : abilityClass.getDeclaredFields()) {
            deserializeField(field, ability, section.getConfigurationSection(abilityName));
          }

          abilityList.add(ability);
        } catch (Exception e) {
          String message = "Could not load ability " + abilityName;
          plugin.getLogger().log(Level.SEVERE, message, e);
        }
      }

      abilities.put(uuid, abilityList);
    }

    viperBowManager.setAbilities(abilities);
  }

  private void deserializeField(Field field, Ability ability, ConfigurationSection section) {
    if (!Modifier.isStatic(field.getModifiers())) {
      try {
        Object value = section.get(field.getName());

        if (Modifier.isPrivate(field.getModifiers())) {
          String setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
          Method setter = ability.getClass().getMethod(setterName, field.getType());
          setter.invoke(ability, value);
        } else {
          field.set(ability, value);
        }
      } catch (Exception e) {
        String message = "Could not load field " + field.getName() + " in " + section.getCurrentPath();
        JavaPlugin.getPlugin(ViperBowsPlugin.class).getLogger().log(Level.SEVERE, message, e);
      }
    }
  }
}
