package max.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class DatabaseConfigurer {
    private static final Logger LOG = LogManager.getLogger(DatabaseConfigurer.class);
    private static final String url = "jdbc:postgresql://pg:5432/studs";
    private static final String user = "s282351";
    private static final String password = "cpv449";
    private final Map<String, String> config;
    private final Scanner sc;
    private Connection dbConnection = null;

    public DatabaseConfigurer() {
        sc = new Scanner(System.in);
        config = new LinkedHashMap<>();
        config.put("host", "");
        config.put("port", "");
        config.put("db_name", "");
        config.put("user", "");
        config.put("password", "");
    }

    public void loadProperties() {
        try {
            setConfig(url, user, password);

            LOG.info("Database properties have been loaded from resource 'db.properties'");
        } catch (IllegalArgumentException e) {
            LOG.error("Unable to load default max.database settings", e);
            System.exit(-1);
        }
    }

    public boolean needReadProperties() {
        System.out.print("Do you want to edit the db config parameters?[y/n]: ");
        String r = sc.nextLine().toLowerCase();
        return r.equals("y") || r.equals("yes");
    }

    public void readCustomProperties() {
        for (String parameter : config.keySet()) {
            String input = "";
            do {
                System.out.print(parameter + ": ");
                input = sc.nextLine();
                input = input.isEmpty() ? null : input;
            } while (input == null);
            config.replace(parameter, input);
        }
        sc.close();
    }

    public void setConnection() {
        try {
            dbConnection = DriverManager.getConnection(getDBUrl(), getDBUser(), getDBPass());
            LOG.info("DB Connection successfully mounted on: " + getDBUrl());
        } catch (SQLException e) {
            LOG.error("Unable to connect to max.database, Check logs for details.", e);
            System.exit(-1);
        }
    }

    public void setConfig(String url, String user, String pass) {
        this.config.put("url", url);
        this.config.put("user", user);
        this.config.put("password", pass);
    }

    public void disconnect() {
        LOG.info("Disconnecting the max.database...");
        try {
            dbConnection.close();
        } catch (SQLException ex) {
            LOG.error("error disconnecting the max.database", ex);
        }
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public String getDBUrl() {
        return "jdbc:postgresql://pg:5432/studs";
    }

    public String getDBUser() {
        return "s282351";
    }

    public String getDBPass() {
        return "cpv449";
    }

    public Connection getDbConnection() {
        return dbConnection;
    }
}