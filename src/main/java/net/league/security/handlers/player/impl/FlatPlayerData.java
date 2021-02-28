package net.league.security.handlers.player.impl;

import net.league.security.LeagueSecurity;
import net.league.security.handlers.backend.imp.FlatBackend;
import net.league.security.handlers.player.PlayerData;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FlatPlayerData extends PlayerData {

    public FlatPlayerData(LeagueSecurity plugin, UUID uniqueId, String name) {
        super(plugin, uniqueId, name);
    }

    @Override
    public void save() {
        FlatBackend backend = (FlatBackend) plugin.getBackend();
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(backend.getPlayerDataFile(this.getUniqueId()));

        configuration.set("uuid", this.uniqueId.toString());
        configuration.set("name", this.name);
        configuration.set("lname", this.name.toLowerCase());
        configuration.set("address", this.address);
        configuration.set("privateKey", this.key);
        configuration.set("nextAuth", this.nextAuth);

        try {
            configuration.save(backend.getPlayerDataFile(this.getUniqueId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        FlatBackend backend = (FlatBackend) plugin.getBackend();
        File dataFile = backend.getPlayerDataFile(this.getUniqueId());

        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.save();
        } else {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(dataFile);

            this.key = configuration.getString("privateKey");
            this.address = configuration.getString("address");
            this.nextAuth = configuration.getLong("nextAuth");
        }
    }
}
