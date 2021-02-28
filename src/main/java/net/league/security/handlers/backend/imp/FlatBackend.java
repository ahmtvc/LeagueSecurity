package net.league.security.handlers.backend.imp;

import lombok.Getter;
import lombok.Setter;
import net.league.security.LeagueSecurity;
import net.league.security.handlers.backend.Backend;

import java.io.File;
import java.util.UUID;

@Getter
@Setter
public class FlatBackend extends Backend {

    private File dataDirectory;

    public FlatBackend(LeagueSecurity plugin) {
        super(plugin);
    }

    @Override
    public void connect() {
        try {
            this.dataDirectory = new File(plugin.getDataFolder(), plugin.getStorageConfiguration().getString("flat-data.directory"));

            if (!this.dataDirectory.exists()) {
                this.dataDirectory.mkdir();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public File getPlayerDataFile(UUID uuid) {
        return new File(this.dataDirectory, uuid.toString() + ".yml");
    }
}
