package net.league.security.handlers.player.impl;

import net.league.security.LeagueSecurity;
import net.league.security.handlers.backend.imp.MySQLBackend;
import net.league.security.handlers.player.PlayerData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLPlayerData extends PlayerData {

    public MySQLPlayerData(LeagueSecurity plugin, UUID uniqueId, String name) {
        super(plugin, uniqueId, name);
    }

    @Override
    public void save() {
        MySQLBackend backend = (MySQLBackend) this.plugin.getBackend();
        try {
            PreparedStatement statement = backend.getConnection().prepareStatement("SELECT * FROM users WHERE uuid=?");
            statement.setString(1, this.uniqueId.toString());

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                PreparedStatement update = backend.getConnection().prepareStatement("UPDATE users SET "
                        + "name=?,uuid=?,nameLowerCase=?,address=?,privateKey,nextAuth WHERE uuid=?");

                update.setString(1, this.name);
                update.setString(2, this.uniqueId.toString());
                update.setString(3, this.name.toLowerCase());
                update.setString(4, this.address);
                update.setString(5, this.key);
                update.setLong(6, this.nextAuth);

                update.setString(7, this.uniqueId.toString());

                update.executeUpdate();
                update.close();
            } else {
                PreparedStatement insert = backend.getConnection().prepareStatement("INSERT INTO users ("
                        + "name,uuid,nameLowerCase,address,privateKey,nextAuth) VALUES ("
                        + "?,?,?,?,?,?)");

                insert.setString(1, this.name);
                insert.setString(2, this.uniqueId.toString());
                insert.setString(3, this.name.toLowerCase());
                insert.setString(4, this.address);
                insert.setString(5, this.key);
                insert.setLong(6, this.nextAuth);

                insert.executeUpdate();
                insert.close();
            }

            backend.close(statement, result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        MySQLBackend backend = (MySQLBackend) this.plugin.getBackend();

        try {
            PreparedStatement statement = backend.getConnection().prepareStatement("SELECT * FROM users where uuid=?");
            statement.setString(1, this.uniqueId.toString());
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                this.address = result.getString("address");
                this.key = result.getString("privateKee");
                this.nextAuth = result.getLong("nextAuth");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
