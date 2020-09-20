package javaProject.database;

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
    private static final String DB_PROPS_FILE = "db.properties";
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
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(DB_PROPS_FILE)) {
            Properties dbProps = new Properties();
            dbProps.load(inputStream);
            setConfig(dbProps.getProperty("host"), dbProps.getProperty("port"), dbProps.getProperty("db_name"), dbProps.getProperty("user"), dbProps.getProperty("password"));

            LOG.info("Database properties have been loaded from resource 'db.properties'");
        } catch (IllegalArgumentException | IOException e) {
            LOG.error("Unable to load default database settings", e);
            System.exit(-1);
        }
    }

    public boolean needReadProperties() {
        System.out.print("Do you want to edit the db config parameters?[y/n]: ");
        String r = sc.nextLine().toLowerCase();
        return r.equals("y") || r.equals("yes");
    }

    public void readCustomProperties() {
        for( String parameter : config.keySet()) {
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
            LOG.error("Unable to connect to database, Check logs for details.", e);
            System.exit(-1);
        }
    }

    public void setConfig(String host, String port, String dbName, String user, String pass) {
        config.put("host", host);
        config.put("port", port);
        config.put("db_name", dbName);
        config.put("user", user);
        config.put("password", pass);
    }

    public void disconnect() {
        LOG.info("Disconnecting the database...");
        try {
            dbConnection.close();
        } catch (SQLException ex) {
            LOG.error("error disconnecting the database", ex);
        }
    }

    public Map<String, String> getConfig() {
        return config;
    }
    public String getDBUrl() {
        return "jdbc:postgresql://" + config.get("host") + ":" + config.get("port") + "/" + config.get("db_name");
    }
    public String getDBUser() {
        return config.get("user");
    }
    public String getDBPass() {
        return config.get("password");
    }
    public Connection getDbConnection() {
        return dbConnection;
    }
}
