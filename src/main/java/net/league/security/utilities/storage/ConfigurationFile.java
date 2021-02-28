package net.league.security.utilities.storage;

import net.league.security.utilities.chat.CC;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigurationFile extends YamlConfiguration {

    private File file;
    private JavaPlugin plugin;
    private String name;

    public ConfigurationFile(JavaPlugin plugin, String name) {
        this.file = new File(plugin.getDataFolder(), name);
        this.plugin = plugin;
        this.name = name;

        if (!this.file.exists()) {
            plugin.saveResource(name, false);
        }

        try {
            this.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        this.file = new File(plugin.getDataFolder(), name);

        if (!this.file.exists()) {
            plugin.saveResource(name, false);
        }
        try {
            this.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            this.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getInt(String path) {
        return super.getInt(path, 0);
    }

    @Override
    public double getDouble(String path) {
        return super.getDouble(path, 0.0);
    }

    @Override
    public boolean getBoolean(String path) {
        return super.getBoolean(path, false);
    }

    @Override
    public String getString(String path) {
        return CC.translate(super.getString(path, "String at path '" + path + "' not found.")).replace("|", "\u2503");
    }

    @Override
    public List<String> getStringList(String path) {
        return super.getStringList(path).stream().map(CC::translate).collect(Collectors.toList());
    }

    public List<String> getStringList(String path, boolean check) {
        if (!super.contains(path)) return null;
        return super.getStringList(path).stream().map(CC::translate).collect(Collectors.toList());
    }
}