package nsk.nu.dev.Configuration;

import nsk.nu.blackstone.Plugin;
import nsk.nu.dev.Logs.LogLevels;
import nsk.nu.dev.NskLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {

    /* --- --- --- --- --- --- --- --- --- --- */

    private static Plugin instance;
    private static NskLogger logger;

    private static File configFile;
    private static FileConfiguration config;

    private static File databaseFile;
    private static FileConfiguration database;

    private static File tagsFile;
    private static FileConfiguration tags;

    private static File translationsFile;
    private static FileConfiguration translations;

    /* --- --- --- --- --- --- --- --- --- --- */

    public static void set(Plugin plugin) {
        instance = plugin;
        logger = NskLogger.get();
    }

    /* --- --- --- --- --- --- --- --- --- --- */

    public static void loadConfig() {
        logger.log(LogLevels.INFO, "Loading configuration...");
        configFile = new File(instance.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            instance.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static void loadDatabase() {
        logger.log(LogLevels.INFO, "Loading database configuration...");
        databaseFile = new File(instance.getDataFolder(), "database.yml");
        if (!databaseFile.exists()) {
            databaseFile.getParentFile().mkdirs();
            instance.saveResource("database.yml", false);
        }

        database = YamlConfiguration.loadConfiguration(databaseFile);
    }

    public static void loadTags() {
        logger.log(LogLevels.INFO, "Loading tags...");
        tagsFile = new File(instance.getDataFolder(), "tags.yml");
        if (!tagsFile.exists()) {
            tagsFile.getParentFile().mkdirs();
            instance.saveResource("tags.yml", false);
        }

        tags = YamlConfiguration.loadConfiguration(tagsFile);
    }

    public static void loadTranslations() {
        logger.log(LogLevels.INFO, "Loading translations...");
        translationsFile = new File(instance.getDataFolder(), "translations.yml");
        if (!translationsFile.exists()) {
            translationsFile.getParentFile().mkdirs();
            instance.saveResource("translations.yml", false);
        }

        translations = YamlConfiguration.loadConfiguration(translationsFile);
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static FileConfiguration getDatabase() { return database; }

    public static FileConfiguration getTags() {
        return tags;
    }

    public static FileConfiguration getTranslations() {
        return translations;
    }

    /* --- --- --- --- --- --- --- --- --- --- */

}
