package net.frostyservices.frostyboxes.configuration;

import net.frostyservices.frostyboxes.util.StringUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import java.io.*;
import java.lang.reflect.Field;

@RequiredArgsConstructor
public class ConfigurationLoader<T extends IDeepCloneable> {
    private final Plugin plugin;
    private final String configName;
    private final T defaultConfig;

    @Getter
    private T configData;

    private File configFile;
    private YamlConfiguration bukkitPluginConfig;

    public void loadConfiguration() {
        configData = (T) defaultConfig.clone();

        if(!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        configFile = new File(plugin.getDataFolder(), configName);
        try {
            bukkitPluginConfig = YamlConfiguration.loadConfiguration(configFile);
            writeMissingConfigFields();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        readConfiguration();
    }

    public void writeMissingConfigFields() throws IOException {
        for(Field field : defaultConfig.getClass().getDeclaredFields()) {
            String yamlName = StringUtil.convertToSnakeCase(field.getName());
            if(bukkitPluginConfig.isSet(yamlName)) continue;
            field.setAccessible(true);
            try {
                bukkitPluginConfig.set(yamlName, ParamParser.serialize(field.get(defaultConfig)));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        bukkitPluginConfig.save(configFile);
    }

    private void readConfiguration() {
        bukkitPluginConfig = YamlConfiguration.loadConfiguration(configFile);
        for(Field field : configData.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = ParamParser.deserialize(bukkitPluginConfig.get(StringUtil.convertToSnakeCase(field.getName())), field);
                field.set(configData, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }



}
