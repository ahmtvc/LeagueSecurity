package net.league.security.listeners;

import net.league.security.LeagueSecurity;
import net.league.security.handlers.Handler;
import net.league.security.handlers.player.PlayerData;
import net.league.security.language.Language;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class SecurityListener extends Handler implements Listener {

    public SecurityListener(LeagueSecurity plugin) {
        super(plugin);
    }

    private boolean isAuth(Player player) {
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData().get(player.getUniqueId());

        return playerData != null && (playerData.isVerify() || playerData.isSetupSecurity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerDataHandler().getPlayerData().get(player.getUniqueId());

        Location from = event.getFrom();
        Location to = event.getTo();

        if (this.isAuth(player)) {
            if ((from.getX() != to.getX()) || (from.getZ() != to.getZ())) {
                player.teleport(from);
                this.plugin.sendAuthMessage(player, playerData);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(this.isAuth(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(this.isAuth(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(this.isAuth((Player) event.getWhoClicked()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCreative(InventoryCreativeEvent event) {
        event.setCancelled(this.isAuth((Player) event.getWhoClicked()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;

        event.setCancelled(this.isAuth((Player) event.getEntered()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        if (this.isAuth(event.getPlayer()) && !event.getMessage().startsWith("/2fa")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Language.CANT_EXECUTE_COMMAND.toString());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        event.setCancelled(this.isAuth(player));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();

        event.setCancelled(this.isAuth(player));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(this.isAuth(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        event.setCancelled(this.isAuth(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (!this.isAuth(player)) return;

        event.setCancelled(true);

        event.getPlayer().sendMessage(Language.CANT_USE_CHAT.toString());
    }
}
