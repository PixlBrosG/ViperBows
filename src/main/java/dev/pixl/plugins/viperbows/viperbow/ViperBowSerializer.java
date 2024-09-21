package dev.pixl.plugins.viperbows.viperbow;

import dev.pixl.plugins.viperbows.ViperBowsPlugin;
import dev.pixl.plugins.viperbows.ability.Ability;
import dev.pixl.plugins.viperbows.abilities.AbilityMetadata;
import dev.pixl.plugins.viperbows.ability.property.AbilityProperty;
import dev.pixl.plugins.viperbows.ability.property.BooleanProperty;
import dev.pixl.plugins.viperbows.ability.property.IntProperty;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class ViperBowSerializer {
  private final ViperBowManager viperBowManager;

  public ViperBowSerializer(ViperBowManager viperBowManager) {
    this.viperBowManager = viperBowManager;
  }

  public void serializeBows() {
    JavaPlugin plugin = JavaPlugin.getPlugin(ViperBowsPlugin.class);
    File file = new File(plugin.getDataFolder(), "bows.yml");
    FileConfiguration config = new YamlConfiguration();

    Map<UUID, List<Ability>> bows = viperBowManager.getBows();

    for (Map.Entry<UUID, List<Ability>> entry : bows.entrySet()) {
      UUID uuid = entry.getKey();
      List<Ability> abilityList = entry.getValue();

      config.set(uuid.toString(), null); // Clear the section (if it exists)
      ConfigurationSection section = config.createSection(uuid.toString());

      for (Ability ability : abilityList) {
        String sectionName = ability.getClass().getName().replace('.', '-');
        ConfigurationSection abilitySection = section.createSection(sectionName);

        AbilityMetadata metadata = viperBowManager.getAbilityMetadata(ability.getClass());
        for (AbilityProperty property : metadata.getProperties()) {
          abilitySection.set(property.getName(), property.getValue(ability));
        }
      }
    }

    try {
      config.save(file);
    } catch (Exception e) {
      plugin.getLogger().log(Level.SEVERE, "Could not save bows.yml", e);
    }
  }

  public void deserializeBows() {
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
      ConfigurationSection section = config.getConfigurationSection(key);
      if (section == null) {
        // TODO: Find a way to not need this
        continue;
      }

      UUID uuid = UUID.fromString(key);
      List<Ability> abilityList = new ArrayList<>();

      for (String abilityName : section.getKeys(false)) {
        Class<? extends Ability> abilityClass = getAbilityClass(abilityName);
        ConfigurationSection abilitySection = section.getConfigurationSection(abilityName);

        if (abilityClass == null || abilitySection == null) {
          continue;
        }

        try {
          Ability ability = abilityClass.getDeclaredConstructor().newInstance();
          AbilityMetadata metadata = viperBowManager.getAbilityMetadata(abilityClass);

          for (AbilityProperty property : metadata.getProperties()) {
            property.setValue(ability, abilitySection.get(property.getName()));
          }
        } catch (NoSuchMethodException e) {
          String message = "Could not find constructor for " + abilityName;
          plugin.getLogger().log(Level.SEVERE, message, e);
        } catch (Exception e) {
          String message = "Could not instantiate " + abilityName;
          plugin.getLogger().log(Level.SEVERE, message, e);
        }
      }

      abilities.put(uuid, abilityList);
    }

    viperBowManager.setBows(abilities);
  }

  public void deserializeAbilities() {
    viperBowManager.clearAbilities();

    JavaPlugin plugin = JavaPlugin.getPlugin(ViperBowsPlugin.class);
    File file = new File(plugin.getDataFolder(), "abilities.yml");
    FileConfiguration config = new YamlConfiguration();
    try {
      config.load(file);
    } catch (Exception e) {
      plugin.getLogger().log(Level.SEVERE, "Could not load abilities.yml", e);
      return;
    }

    for (String key : config.getKeys(false)) {
      ConfigurationSection section = config.getConfigurationSection(key);
      if (section == null) {
        continue;
      }

      AbilityMetadata metadata = new AbilityMetadata();
      Class<? extends Ability> abilityClass = getAbilityClass(key);
      if (abilityClass == null) {
        continue;
      }

      if (viperBowManager.getAbilityMetadata(metadata.getAbilityClass()) != null) {
        String message = "Ability " + key + " already registered";
        plugin.getLogger().log(Level.SEVERE, message);
        continue;
      }

      metadata.setAbilityClass(abilityClass);
      metadata.setName(section.getString("name"));
      metadata.setLore(section.getStringList("lore"));
      metadata.setEnabled(section.getBoolean("enabled"));
      metadata.setModifiable(section.getBoolean("modifiable"));
      metadata.setHidden(section.getBoolean("hidden"));
      metadata.setMaterial(Material.valueOf(section.getString("material")));
      metadata.setUsePermission(new Permission(section.getString("usePermission")));
      metadata.setModifyPermission(new Permission(section.getString("modifyPermission")));

      List<AbilityProperty> properties = new ArrayList<>();
      ConfigurationSection propertiesSection = section.getConfigurationSection("properties");
      if (propertiesSection != null) {
        for (String propertyName : propertiesSection.getKeys(false)) {
          ConfigurationSection propertySection = propertiesSection.getConfigurationSection(propertyName);
          AbilityProperty property = deserializeProperty(abilityClass, propertyName, propertySection);
          if (property != null) {
            properties.add(property);
          }
        }
      }

      metadata.setProperties(properties);
      viperBowManager.registerAbility(metadata);
    }
  }

  private AbilityProperty deserializeProperty(Class<? extends Ability> abilityClass, String propertyName, ConfigurationSection section) {
    if (section == null) {
      return null;
    }

    String type = section.getString("type");
    if (type == null) {
      return null;
    }

    AbilityProperty property;

    switch (type) {
      case "boolean":
        property = new BooleanProperty(abilityClass, propertyName);
        ((BooleanProperty) property).setDefaultValue(section.getBoolean("defaultValue"));
        break;
      case "int":
        property = new IntProperty(abilityClass, propertyName);
        ((IntProperty) property).setMin(section.getInt("min"));
        ((IntProperty) property).setMax(section.getInt("max"));
        ((IntProperty) property).setStep(section.getInt("step"));
        ((IntProperty) property).setDefaultValue(section.getInt("defaultValue"));
        break;
      default:
        throw new IllegalArgumentException("Invalid property type");
    }

    property.setLore(section.getStringList("lore"));
    property.setModifiable(section.getBoolean("modifiable"));
    property.setMaterial(Material.valueOf(section.getString("material")));
    return property;
  }

  private Class<? extends Ability> getAbilityClass(String abilityName) {
    try {
      Class<?> clazz = Class.forName(abilityName.replace('-', '.'));
      if (!Ability.class.isAssignableFrom(clazz)) {
        String message = "Class " + abilityName + " does not extend Ability";
        JavaPlugin.getPlugin(ViperBowsPlugin.class).getLogger().severe(message);
        return null;
      }

      @SuppressWarnings("unchecked")
      Class<? extends Ability> abilityClass = (Class<? extends Ability>)clazz;
      return abilityClass;
    } catch (ClassNotFoundException e) {
      String message = "Could not load ability class " + abilityName;
      JavaPlugin.getPlugin(ViperBowsPlugin.class).getLogger().log(Level.SEVERE, message, e);
      return null;
    }
  }
}
