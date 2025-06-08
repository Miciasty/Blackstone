package nsk.nu.blackstone;

import nsk.nu.blackstone.Command.BlackstoneCommand;
import nsk.nu.blackstone.Event.PlayerJoinListener;
import nsk.nu.blackstone.Event.PlayerQuitListener;
import nsk.nu.blackstone.Factory.BlackstoneDatabaseFactory;
import nsk.nu.blackstone.Factory.BlackstoneDefaultFactory;
import nsk.nu.blackstone.Repository.ClientRepository;
import nsk.nu.blackstone.Repository.ScenarioRepository;
import nsk.nu.blackstone.Service.ClientService;
import nsk.nu.blackstone.Service.Loader.ModuleLoader;
import nsk.nu.blackstone.Service.Loader.ScenarioLoader;
import nsk.nu.blackstone.Service.ScenarioService;
import nsk.nu.dev.Configuration.Config;
import nsk.nu.dev.Configuration.HibernateUtil;
import nsk.nu.dev.Logs.LogLevels;
import nsk.nu.dev.NskLogger;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Plugin extends JavaPlugin {

    private NskLogger logger;
    private ExecutorService blackstoneExecutor;
    private ExecutorService blackstoneDatabaseExecutor;

    private ClientService clientService;
    private ScenarioService scenarioService;

    private ClientRepository clientRepository;
    private ScenarioRepository scenarioRepository;

    private ScenarioLoader scenarioLoader;

    private ModuleLoader moduleLoader;

    @Override
    public void onEnable() {
        enableInstances();
        enableCommands();
        enableEvents();
    }

    @Override
    public void onDisable() {
        logger.log(LogLevels.FINE, "Shutting down BlackstoneFactory...");
        BlackstoneDefaultFactory.shutdown();
        BlackstoneDatabaseFactory.shutdown();
    }

    private void enableInstances() {
        PluginInstance.setInstance(this);

        NskLogger.set(this);
        this.logger = NskLogger.get();
        logger.log(LogLevels.FINE, "Logger enabled.");

        Config.set(this);
        Config.loadConfig();
        Config.loadTags();
        Config.loadTranslations();
        Config.loadDatabase();

        logger.log(LogLevels.FINE, "Config enabled.");

        HibernateUtil.initialize();
        logger.log(LogLevels.FINE, "Hibernate initialized.");

        this.initializeThreadFactory();

        this.initializeRepositories();
        this.initializeServices();
        this.initializeModules();

        this.initializeScenarioLoader();

    }

    private void initializeRepositories() {
        SessionFactory session = HibernateUtil.getSessionFactory();
        this.clientRepository           =   new ClientRepository(session);
        this.scenarioRepository         =   new ScenarioRepository(session);
    }

    private void initializeServices() {
        this.clientService              =   new ClientService(clientRepository);
        this.scenarioService            =   new ScenarioService(scenarioRepository);

        ScenarioLoader.syncScenarios(this.scenarioService);
    }

    private void initializeModules() {
        this.moduleLoader = new ModuleLoader(this.scenarioService, this.clientService);
        // this.moduleLoader.loadModulesForAllScenarios();
    }

    private void initializeScenarioLoader() {
        this.scenarioLoader = new ScenarioLoader(this.scenarioService, this.moduleLoader);
    }

    private void initializeThreadFactory() {

        int poolSize = Config.getConfig().getInt("Configuration.BlackstoneFactory.default-async-workers", 3);

        this.blackstoneExecutor         = Executors.newFixedThreadPool(poolSize, new BlackstoneDefaultFactory());
        this.blackstoneDatabaseExecutor = Executors.newFixedThreadPool(1, new BlackstoneDatabaseFactory());
        BlackstoneDefaultFactory.create(this.blackstoneExecutor);
        BlackstoneDatabaseFactory.create(this.blackstoneDatabaseExecutor);

        logger.log(LogLevels.FINE, "Blackstone thread factory initialized.");

    }

    private void enableCommands() {
        PluginCommand command = getCommand("blackstone");
        if (command != null) {
            BlackstoneCommand mainCommand = new BlackstoneCommand();
            mainCommand.initialize(this.scenarioLoader);
            command.setExecutor(mainCommand);
            command.setTabCompleter(mainCommand);

            logger.log(LogLevels.FINE, "Command 'blackstone' registered.");
        } else {
            logger.log(LogLevels.CRITICAL, "Command 'blackstone' not registered.");
        }
    }

    private void enableEvents() {
        PlayerJoinListener playerJoinListener = new PlayerJoinListener();
        getServer().getPluginManager().registerEvents(playerJoinListener, this);
        playerJoinListener.initialize(this.clientService);

        PlayerQuitListener playerQuitListener = new PlayerQuitListener();
        getServer().getPluginManager().registerEvents(playerQuitListener, this);
        playerQuitListener.initialize(this.clientService);

        // getServer().getPluginManager().registerEvents(new SupplyListener(), this);
        logger.log(LogLevels.FINE, "Events enabled.");

    }
}
