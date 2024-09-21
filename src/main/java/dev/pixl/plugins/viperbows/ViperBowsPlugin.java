package dev.pixl.plugins.viperbows;

import dev.pixl.plugins.viperbows.gui.ViperBowEditor;
import dev.pixl.plugins.viperbows.viperbow.ViperBowManager;
import dev.pixl.plugins.viperbows.viperbow.ViperBowSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Main plugin class.
 *
 * @author P1xl__
 * @since 1.0.0
 */
public final class ViperBowsPlugin extends JavaPlugin {

  private ViperBowManager viperBowManager;
  private ViperBowSerializer viperBowSerializer;
  private ViperBowEditor viperBowEditor;
  private CommandHandler commandHandler;

  @Override
  public void onEnable() {
    viperBowManager = new ViperBowManager();
    viperBowSerializer = new ViperBowSerializer(viperBowManager);
    viperBowEditor = new ViperBowEditor(viperBowManager);
    commandHandler = new CommandHandler();

    getServer().getPluginManager().registerEvents(viperBowManager, this);

    viperBowSerializer.deserializeAbilities();
    viperBowSerializer.deserializeBows();
  }

  @Override
  public void onDisable() {
    // viperBowSerializer.serializeAbilities();
    viperBowSerializer.serializeBows();
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, String[] args) {
    return commandHandler.onCommand(sender, command, args);
  }

  public ViperBowManager getViperBowManager() {
    return viperBowManager;
  }

  public ViperBowSerializer getViperBowSerializer() {
    return viperBowSerializer;
  }

  public ViperBowEditor getViperBowEditor() {
    return viperBowEditor;
  }
}
