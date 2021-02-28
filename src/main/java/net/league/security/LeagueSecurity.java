package net.league.security;


import lombok.Getter;
import lombok.Setter;
import net.league.security.commands.SecurityCommand;
import net.league.security.handlers.backend.Backend;
import net.league.security.handlers.backend.imp.FlatBackend;
import net.league.security.handlers.backend.imp.MongoBackend;
import net.league.security.handlers.backend.imp.MySQLBackend;
import net.league.security.handlers.player.PlayerData;
import net.league.security.handlers.player.PlayerDataHandler;
import net.league.security.language.Language;
import net.league.security.listeners.PlayerListener;
import net.league.security.listeners.SecurityListener;
import net.league.security.utilities.Utilities;
import net.league.security.utilities.storage.ConfigurationFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter
@Setter
public class LeagueSecurity extends JavaPlugin {

    public static LeagueSecurity INSTANCE;

    private PlayerDataHandler playerDataHandler;

    private ConfigurationFile languageConfiguration, configuration, storageConfiguration;

    private Backend backend;

    @Override
    public void onEnable() {
        INSTANCE = this;

        this.languageConfiguration = new ConfigurationFile(this, "messages.yml");
        this.configuration = new ConfigurationFile(this, "config.yml");
        this.storageConfiguration = new ConfigurationFile(this, "storage.yml");

        switch (this.storageConfiguration.getString("storage-type").toLowerCase()) {
            case "mysql":
                this.backend = new MySQLBackend(this);
                break;
            case "mongo":
                this.backend = new MongoBackend(this);
                break;
            default:
                this.backend = new FlatBackend(this);
        }

        this.backend.connect();

        Language.setConfig(this.languageConfiguration);
        this.loadLanguages();

        this.playerDataHandler = new PlayerDataHandler(this);

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new SecurityListener(this), this);

        new SecurityCommand(this);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new MessageUpdate(), 80L, 80L);
    }

    private class MessageUpdate implements Runnable {

        @Override
        public void run() {
            Utilities.getOnlinePlayers().forEach(player -> {
                PlayerData playerData = playerDataHandler.getPlayerData().get(player.getUniqueId());

                if (playerData != null) {
                    sendAuthMessage(player, playerData);
                }
            });
        }
    }

    private void loadLanguages() {
        if (this.languageConfiguration == null) {
            return;
        }
        Arrays.stream(Language.values()).forEach(language -> {
            if (this.languageConfiguration.getString(language.getPath(), null) == null) {
                this.languageConfiguration.set(language.getPath(), language.getValue());
            }
        });
        this.languageConfiguration.save();
    }

    public void sendAuthMessage(Player player, PlayerData data) {
        if (data.isSetupSecurity() && !data.isVerify()) {
            this.configuration.getStringList("setup-auth-message").forEach(player::sendMessage);
        } else if (data.isVerify()) {
            this.configuration.getStringList("enter-auth-message").forEach(player::sendMessage);
        }
    }
}
