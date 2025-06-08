package nsk.nu.dev.Configuration;

import org.bukkit.configuration.file.FileConfiguration;

public class Environment {

    public static String getUrl() {
        FileConfiguration db = Config.getDatabase();
        String host = db.getString("database.address", "localhost");
        String port = db.getString("database.port", "3306");
        String name = db.getString("database.database", "blackstone");
        return "jdbc:mysql://" + host + ":" + port + "/" + name;
    }

    public static String getUser() {
        return Config.getDatabase().getString("database.username", "root");
    }

    public static String getPassword() {
        return Config.getDatabase().getString("database.password", "");
    }

    public static String getDialect() {
        return Config.getDatabase().getString("database.dialect", "org.hibernate.dialect.MySQL8Dialect");
    }

    public static String getDriver() {
        return Config.getDatabase().getString("database.driver", "com.mysql.cj.jdbc.Driver");
    }

    public static String getHbm2ddl() {
        return Config.getDatabase().getString("database.hbm2ddl", "update");
    }

}
