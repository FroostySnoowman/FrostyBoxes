package net.frostyservices.frostyboxes;

import net.frostyservices.frostyboxes.commands.MainCommand;
import net.frostyservices.frostyboxes.configuration.FBConfig;
import net.frostyservices.frostyboxes.configuration.ConfigurationLoader;
import net.frostyservices.frostyboxes.listeners.InteractListener;
import net.frostyservices.frostyboxes.listeners.InventoryCloseListener;
import net.frostyservices.frostyboxes.manager.ShulkerManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Main extends JavaPlugin {
    private static Main instance;
    private ShulkerManager shulkerManager;
    private ConfigurationLoader<FBConfig> configurationLoader;


    @Override
    public void onEnable() {
        instance = this;
        shulkerManager = new ShulkerManager(this);
        configurationLoader = new ConfigurationLoader<>(this, "config.yml", new FBConfig());
        configurationLoader.loadConfiguration();
        new InteractListener(this);
        new InventoryCloseListener(this);
        new MainCommand(this);

        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "FrostyBoxes " + ChatColor.GREEN + "Loaded!");
    }


    @Override
    public void onDisable() {
        shulkerManager.closeAllInventories(true);
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "FrostyBoxes " + ChatColor.DARK_RED + "Disabled!");
    }

    public FBConfig getBSBConfig() {
        return this.getConfigurationLoader().getConfigData();
    }

    public static Main getInstance() {
        return instance;
    }
}
