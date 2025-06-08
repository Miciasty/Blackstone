package nsk.nu.blackstone.Abstract;

import nsk.nu.blackstone.Entity.DTO.DScenario;
import nsk.nu.blackstone.Interface.BlackstoneModule;
import nsk.nu.blackstone.Interface.BlackstoneRepository;
import nsk.nu.blackstone.Interface.BlackstoneService;
import nsk.nu.blackstone.Plugin;
import nsk.nu.blackstone.PluginInstance;
import nsk.nu.blackstone.Service.ClientService;
import nsk.nu.blackstone.Service.ScenarioService;
import nsk.nu.dev.Logs.LogLevels;
import nsk.nu.dev.NskLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

abstract public class AbstractBlackstoneModule<D,T,S extends BlackstoneService<T, Long>,R extends BlackstoneRepository<D, Long>> implements BlackstoneModule<DScenario> {

    protected final ScenarioService scenarioService;
    protected final ClientService clientService;
    protected final NskLogger logger;

    protected final List<Listener> registeredListeners = new ArrayList<>();
    protected final String name;

    protected R repository;
    protected S service;

    protected DScenario scenario;

    protected File configFile;
    protected FileConfiguration config;

    protected boolean enabled = false;

    public AbstractBlackstoneModule(ScenarioService scenarioService, ClientService clientService, String name) {
        this.scenarioService = scenarioService;
        this.clientService = clientService;
        this.logger = NskLogger.get();

        this.name = name;
    }

    protected abstract R createRepository();
    protected abstract S createService(R repository);

    @Override
    public void initialize(DScenario scenario) {
        this.scenario = scenario;
        this.reload();

        this.repository = createRepository();
        this.service = createService(repository);

        this.registerEvents();
        this.loadPlayerData();
        this.enabled = true;
    }

    @Override
    public void reload() {
        this.initializeConfig();
        this.loadConfig();
    }

    @Override
    public FileConfiguration getConfig() { return config; }

    @Override
    public String name() { return name; }

    @Override
    public DScenario getScenario() { return scenario; }

    @Override
    public ScenarioService getScenarioService() { return scenarioService; }

    @Override
    public ClientService getClientService() { return clientService; }

    @Override
    public boolean isEnabled() { return enabled; }

    @Override
    public void enable() {
        String capitalized = this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
        if (enabled) {
            logger.log(LogLevels.WARNING, capitalized + " module is already enabled.");
            return;
        }
        enabled = true;
        registerEvents();
        logger.log(LogLevels.INFO, capitalized + " module enabled.");
    }

    @Override
    public void disable() {
        String capitalized = this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
        if (!enabled) {
            logger.log(LogLevels.WARNING, capitalized + " module is already disabled.");
            return;
        }
        enabled = false;
        savePlayerData();
        unregisterEvents();
        logger.log(LogLevels.INFO, capitalized + " module disabled.");
    }

    protected void initializeConfig() {
        Plugin instance = PluginInstance.getInstance();
        String path = "scenarios/" + scenario.getName() + "/" + name + ".yml";
        configFile = new File(instance.getDataFolder(), path);

        logger.log(LogLevels.INFO, "Initializing " + this.name + " module...");

        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();

            try (InputStream in = instance.getResource("modules/" + this.name + ".yml");
                 OutputStream out = new FileOutputStream(configFile)) {

                if (in == null) throw new FileNotFoundException("Resource not found in JAR: modules/" + this.name + ".yml");
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                logger.log(LogLevels.INFO, "Default " + this.name + ".yml copied for scenario: " + scenario.getName());
            } catch (IOException e) {
                logger.log(LogLevels.CRITICAL, "Failed to copy " + this.name + " module config: " + e.getMessage());
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    protected abstract void registerEvents();

    protected void unregisterEvents() {
        for (Listener l : registeredListeners) HandlerList.unregisterAll(l);
        registeredListeners.clear();
        String capitalized = this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
        logger.log(LogLevels.FINE, capitalized + " module events are now unregistered.");
    }

    protected void loadPlayerData() {
        logger.log(LogLevels.INFO, "Loading " + this.name + " players data...");
        Collection<? extends Player> onlinePlayers = PluginInstance.getInstance().getServer().getOnlinePlayers();
        List<Player> playersToSave = new ArrayList<>(onlinePlayers);
        logger.log(LogLevels.INFO, "Found <blue>" + playersToSave.size() + "</blue> online players.");
        Bukkit.getScheduler().runTask( PluginInstance.getInstance(),
                () -> {
                    for (Player player : playersToSave) {
                        logger.log(LogLevels.INFO, "Loading " + this.name + " data for player: " + player.getName() + ".");
                        registerPlayerEvent(player);
                    }
                }
        );
    }

    protected void savePlayerData() {
        logger.log(LogLevels.INFO, "Saving " + this.name + " players data...");
        Collection<? extends Player> onlinePlayers = PluginInstance.getInstance().getServer().getOnlinePlayers();
        List<Player> playersToSave = new ArrayList<>(onlinePlayers);
        logger.log(LogLevels.INFO, "Found <blue>" + playersToSave.size() + "</blue> online players.");

        Bukkit.getScheduler().runTask( PluginInstance.getInstance(),
                () -> {
                    for (Player player : playersToSave) {
                        logger.log(LogLevels.INFO, "Saving " + this.name + " data for player: " + player.getName() + ".");
                        registerPlayerEvent(player);
                    }
                }
        );
    }

    protected abstract void registerPlayerEvent(Player player);

}
