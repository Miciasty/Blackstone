package nsk.nu.blackstone.Service.Loader;

import nsk.nu.blackstone.Entity.DTO.DScenario;
import nsk.nu.blackstone.Factory.BlackstoneDefaultFactory;
import nsk.nu.blackstone.PluginInstance;
import nsk.nu.blackstone.Service.ScenarioService;
import nsk.nu.dev.Logs.LogLevels;
import nsk.nu.dev.NskLogger;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ScenarioLoader {

    private final ScenarioService scenarioService;
    private final ModuleLoader moduleLoader;
    private DScenario scenario;

    private final NskLogger logger = NskLogger.get();

    public ScenarioLoader(ScenarioService scenarioService, ModuleLoader moduleLoader) {
        this.scenarioService = scenarioService;
        this.moduleLoader = moduleLoader;

        logger.log(LogLevels.WARNING, "No scenario loaded.");
    }

    public CompletableFuture<Void> loadScenario(String name) {
        return scenarioService.getByNameAsync(name)
                .thenAcceptAsync(loadedScenario -> {
                    if (loadedScenario == null) {
                        logger.log(LogLevels.CRITICAL, "Scenario " + name + " not found!");
                        return;
                    }
                    this.scenario = loadedScenario;
                    logger.log(LogLevels.INFO, "Loaded scenario: " + scenario.getName());
                    moduleLoader.loadModulesForScenario(loadedScenario);
                }, BlackstoneDefaultFactory.getExecutor());
    }

    public CompletableFuture<Void> unloadScenario(String name) {
        return CompletableFuture.runAsync(() -> {
            logger.log(LogLevels.INFO, "Unloading scenario: " + name + "... ");

            moduleLoader.unloadModulesForScenario(name);

            if (scenario != null && scenario.getName().equals(name)) {
                scenario = null;
                logger.log(LogLevels.INFO, "Unloaded scenario: " + name + ".");
            } else {
                logger.log(LogLevels.INFO, "Scenario " + name + " not loaded.");
            }
        }, BlackstoneDefaultFactory.getExecutor());
    }

    public CompletableFuture<Void> unloadActiveScenario() {
        if (getActiveScenario() == null) {
            logger.log(LogLevels.WARNING, "No active scenario to unload!");
            return CompletableFuture.completedFuture(null);
        }
        return unloadScenario(getActiveScenario().getName());
    }

    public DScenario getActiveScenario() { return this.scenario; }

    public static void syncScenarios(ScenarioService scenarioService) {
        CompletableFuture.runAsync(() -> {

            NskLogger.get().log(LogLevels.INFO, "Synchronizing scenarios...");
            File scenariosDir = new File(PluginInstance.getInstance().getDataFolder(), "scenarios/");
            if (!scenariosDir.exists() || !scenariosDir.isDirectory()) return;

            try {
                List<DScenario> databaseScenarios = scenarioService.getAll();
                Set<String> dbNames = databaseScenarios.stream()
                        .map(DScenario::getName)
                        .collect(Collectors.toSet());

                File[] folders = scenariosDir.listFiles(File::isDirectory);
                if (folders == null) return;

                for (File folder : folders) {
                    String folderName = folder.getName();
                    if (dbNames.contains(folderName)) return;

                    DScenario newScenario = new DScenario();
                    newScenario.setName(folderName);

                    try {
                        scenarioService.save(newScenario);
                        NskLogger.get().log(LogLevels.INFO, "Saved new scenario: " + folderName + " in database.");
                    } catch (Exception ex) {
                        NskLogger.get().log(LogLevels.SEVERE, "Failed to save scenario " + folderName + ": " + ex.getMessage());
                        return;
                    }

                }
            } catch (Exception ex) {
                NskLogger.get().log(LogLevels.SEVERE, "Failed to synchronize scenarios: " + ex.getMessage());
                return;
            }

        }, BlackstoneDefaultFactory.getExecutor());
    }

}
