package net.league.security.handlers.backend.imp;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import net.league.security.LeagueSecurity;
import net.league.security.handlers.backend.Backend;
import net.league.security.utilities.Utilities;
import net.league.security.utilities.storage.ConfigurationFile;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
public class MongoBackend extends Backend {

    private MongoClient client;
    private MongoDatabase database;

    private MongoCollection<Document> users;

    public MongoBackend(LeagueSecurity plugin) {
        super(plugin);
    }

    @Override
    public void connect() {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);
        ConfigurationFile configuration = plugin.getStorageConfiguration();

        try {
            if (configuration.getBoolean("mongodb.authentication.enabled")) {
                MongoCredential credential = MongoCredential.createCredential(
                        configuration.getString("mongodb.authentication.info.username"),
                        configuration.getString("mongodb.database"),
                        configuration.getString("mongodb.authentication.info.password").toCharArray()
                );
                this.client = new MongoClient(new ServerAddress(configuration.getString("mongodb.address"), configuration.getInt("mongodb.port")), Collections.singletonList(credential));
            } else {
                this.client = new MongoClient(configuration.getString("mongodb.address"), configuration.getInt("mongodb.port"));
            }
            this.database = client.getDatabase(configuration.getString("mongodb.database"));

            this.users = this.database.getCollection("Users");
        } catch (Exception ex) {
            ex.printStackTrace();
            Utilities.log("&cThere was an error trying to connect to your Mongo Database, AquaUHC will disable now..", true);
            Bukkit.getServer().getPluginManager().disablePlugin(this.plugin);
        }
    }
}
