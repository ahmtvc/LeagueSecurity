package net.league.security.handlers.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.league.security.LeagueSecurity;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public abstract class PlayerData {

    public final LeagueSecurity plugin;
    public final UUID uniqueId;
    public final String name;

    public String address = "";
    public String key = "";

    public boolean setupSecurity = false;
    public boolean verify = false;

    public long nextAuth = -1;

    private int lastItemSlot;
    private ItemStack lastItem;

    public abstract void save();

    public abstract void load();
}
