package net.league.security.handlers.backend.imp;

import lombok.Getter;
import lombok.Setter;
import net.league.security.LeagueSecurity;
import net.league.security.handlers.backend.Backend;
import net.league.security.utilities.Utilities;
import net.league.security.utilities.storage.ConfigurationFile;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class MySQLBackend extends Backend {

    private Connection connection;

    public MySQLBackend(LeagueSecurity plugin) {
        super(plugin);
    }

    @Override
    public void connect() {
        ConfigurationFile configuration = plugin.getStorageConfiguration();

        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" +
                            configuration.getString("mysql.database.host") + ":" +
                            configuration.getInt("mysql.database.port") + "/" +
                            configuration.getString("mysql.database.database") +
                            "?characterEncoding=latin1&useConfigs=maxPerformance",
                    configuration.getString("mysql.database.user"),
                    configuration.getString("mysql.database.password"));
        } catch (SQLException ex) {
            ex.printStackTrace();
            Utilities.log("&cThere was an error trying to connect to your MySQL Database, LeagueSecurity will disable now..", true);
            Bukkit.getServer().getPluginManager().disablePlugin(this.plugin);
        }

        this.tablesToCreate().forEach(executable -> {
            PreparedStatement statement;

            try {
                statement = this.connection.prepareStatement(executable);
                statement.executeUpdate();
                statement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void close() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void close(AutoCloseable... closeables) {
        Arrays.stream(closeables).filter(Objects::nonNull).forEach(autoCloseable -> {
            try {
                autoCloseable.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private List<String> tablesToCreate() {
        List<String> executables = new ArrayList<>();

        executables.add("CREATE TABLE IF NOT EXISTS users("
                + "uuid VARCHAR(64) NOT NULL,"
                + "name VARCHAR(16) NOT NULL,"
                + "nameLowerCase VARCHAR(16) NOT NULL,"

                + "privateKey LONGBLOB NOT NULL,"
                + "address LONGBLOB NOT NULL,"
                + "nextAuth BIGINT NOT NULL"
                + ")");

        return executables;
    }
}
