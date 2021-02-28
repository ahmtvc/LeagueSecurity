package net.league.security.utilities;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import net.league.security.utilities.chat.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utilities {

    public static List<Player> getOnlinePlayers() {
        List<Player> players = new ArrayList<>();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            players.add(player);
        }
        return players;
    }

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(CC.translate(message));
    }

    public static void log(String message, boolean prefix) {
        log(!prefix ? message : "&7[&cLog&7] " + message);
    }

    public static String getQrImageURL(String key) {
        return "https://chart.googleapis.com/chart?chs=128x128&cht=qr&chl=200x200&chld=M|0&cht=qr&chl=otpauth://totp/LeagueSecurity?secret=" + key;
    }

    public static int getHotbarSlotOfItem(ItemStack item, Player player) {
        if (item == null) return -1;
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getItem(i) != null && player.getInventory().getItem(i).equals(item)) {
                return i;
            }
        }
        return 0;
    }

    public static void removeQrMapFromInventory(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);

            if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().get(0).contains("QR")) {
                player.getInventory().remove(item);
            }
        }
    }

    public static boolean checkCode(String key, int code) {
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        return googleAuthenticator.authorize(key, code);
    }
}
