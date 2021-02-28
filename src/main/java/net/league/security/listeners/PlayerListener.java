package net.league.security.listeners;

import net.league.security.LeagueSecurity;
import net.league.security.handlers.Handler;
import net.league.security.handlers.backend.imp.MongoBackend;
import net.league.security.handlers.backend.imp.MySQLBackend;
import net.league.security.handlers.player.PlayerData;
import net.league.security.handlers.player.impl.FlatPlayerData;
import net.league.security.handlers.player.impl.MongoPlayerData;
import net.league.security.handlers.player.impl.MySQLPlayerData;
import net.league.security.utilities.Utilities;
import net.league.security.utilities.time.TimeFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener extends Handler implements Listener {

    public PlayerListener(LeagueSecurity plugin) {
        super(plugin);
    }

    @EventHandler
    public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
        if (this.plugin.getBackend() instanceof MySQLBackend) {
            this.plugin.getPlayerDataHandler().getPlayerData().putIfAbsent(event.getUniqueId(), new MySQLPlayerData(this.plugin, event.getUniqueId(), event.getName()));
        } else if (this.plugin.getBackend() instanceof MongoBackend) {
            this.plugin.getPlayerDataHandler().getPlayerData().putIfAbsent(event.getUniqueId(), new MongoPlayerData(this.plugin, event.getUniqueId(), event.getName()));
        } else {
            this.plugin.getPlayerDataHandler().getPlayerData().putIfAbsent(event.getUniqueId(), new FlatPlayerData(this.plugin, event.getUniqueId(), event.getName()));
        }

        PlayerData playerData = this.plugin.getPlayerDataHandler().getPlayerData().get(event.getUniqueId());
        playerData.load();

        if (playerData.getAddress().equals("")) {
            playerData.setSetupSecurity(true);
        } else {
            if (System.currentTimeMillis() >= playerData.getNextAuth() || !playerData.getAddress().equals(event.getAddress().getHostAddress())) {
                playerData.setVerify(true);
            }
        }
        playerData.setAddress(event.getAddress().getHostAddress());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerDataHandler().getPlayerData().get(player.getUniqueId());

        Utilities.removeQrMapFromInventory(player);

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            playerData.save();
            this.plugin.getPlayerDataHandler().getPlayerData().remove(player.getUniqueId());
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = this.plugin.getPlayerDataHandler().getPlayerData().get(player.getUniqueId());

        if (playerData != null) {
            this.plugin.sendAuthMessage(player, playerData);
            this.plugin.sendAuthMessage(player, playerData);

            if (!playerData.isVerify() && playerData.getNextAuth() != -1) {
                this.plugin.getConfiguration().getStringList("next-auth-note").forEach(message -> {
                    player.sendMessage(message.replace("<time>",
                            TimeFormatUtils.getMoreDetailedTime(playerData.getNextAuth() - System.currentTimeMillis())));
                });
            }
        }
    }
}
