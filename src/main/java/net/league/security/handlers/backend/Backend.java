package net.league.security.handlers.backend;

import lombok.AllArgsConstructor;
import net.league.security.LeagueSecurity;

@AllArgsConstructor
public abstract class Backend {

    public LeagueSecurity plugin;

    public abstract void connect();
}
