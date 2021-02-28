package net.league.security.handlers.player;

import lombok.Getter;
import lombok.Setter;
import net.league.security.LeagueSecurity;
import net.league.security.handlers.Handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class PlayerDataHandler extends Handler {

    private Map<UUID, PlayerData> playerData = new HashMap<>();

    public PlayerDataHandler(LeagueSecurity plugin) {
        super(plugin);
    }
}
