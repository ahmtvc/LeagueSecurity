package net.league.security.handlers.player.impl;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import net.league.security.LeagueSecurity;
import net.league.security.handlers.backend.imp.MongoBackend;
import net.league.security.handlers.player.PlayerData;
import org.bson.Document;

import java.util.UUID;

public class MongoPlayerData extends PlayerData {

    public MongoPlayerData(LeagueSecurity plugin, UUID uniqueId, String name) {
        super(plugin, uniqueId, name);
    }

    @Override
    public void save() {
        Document document = new Document();

        document.put("uuid", this.uniqueId.toString());
        document.put("name", this.name);
        document.put("lname", this.name.toLowerCase());
        document.put("address", this.address);
        document.put("privateKey", this.key);
        document.put("nextAuth", this.nextAuth);

        MongoBackend backend = (MongoBackend) this.plugin.getBackend();
        backend.getUsers().replaceOne(Filters.eq("uuid", this.uniqueId.toString()), document, new UpdateOptions().upsert(true));
    }

    @Override
    public void load() {
        MongoBackend backend = (MongoBackend) this.plugin.getBackend();
        Document document = backend.getUsers().find(Filters.eq("uuid", this.uniqueId.toString())).first();

        if (document == null) return;

        this.address = document.getString("address");
        this.key = document.getString("privateKey");
        this.nextAuth = document.getLong("nextAuth");
    }
}
