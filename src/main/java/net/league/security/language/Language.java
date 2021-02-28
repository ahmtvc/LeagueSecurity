package net.league.security.language;

import lombok.Getter;
import lombok.Setter;
import net.league.security.utilities.storage.ConfigurationFile;

public enum Language {

    PREFIX("PREFIX", "&7[&aLeague Security&7] "),

    NUMBER_FORMAT_WRONG("WRONG-NUMBER-FORMAT", "{prefix} &cYou've entered wrong number format!"),
    PLAYER_OFFLINE("PLAYER-OFFLINE", "{prefix} &cPlayer by name &c&l<player> &cis offline!"),

    CANT_EXECUTE_COMMAND("CANT-EXECUTE-COMMANDS", "{prefix} &eYou must authenticate yourself with &6/2fa &ebefore executing any other commands!"),
    CANT_USE_CHAT("CANT-USE-CHAT", "{prefix} &eYou must authenticate yourself with &6/2fa &ebefore using chat!"),
    NO_NEED_TO_VERIFY("NO-NEED-TO-VERIFY", "{prefix} &aYou're already authenticated and don't need to do it now!"),
    WRONG_KEY("WRONG-KEY", "{prefix} &cThe code you've entered is wrong!"),
    VERIFIED("VERIFIED", "%n                   &7(( &a&lVerified &7))%n&aYou have been authenticated with your code! %n "),

    END("", "");

    @Getter
    private String path;
    @Getter
    private String value;
    @Setter
    private static ConfigurationFile config;

    Language(String path, String value) {
        this.path = path;
        this.value = value;
    }

    public String toString() {
        return config.getString(this.path).replace("{prefix} ", config.getString("PREFIX"))
                .replace("%n", "\n");
    }
}
