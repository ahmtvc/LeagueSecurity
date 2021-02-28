package net.league.security.utilities.chat;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class CC {

    public static String translate(String source) {
        return ChatColor.translateAlternateColorCodes('&', source);
    }

    public static List<String> translate(List<String> source) {
        return source.stream().map(CC::translate).collect(Collectors.toList());
    }
}
