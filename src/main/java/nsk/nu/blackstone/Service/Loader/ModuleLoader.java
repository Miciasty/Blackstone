package nsk.nu.blackstone.Service.Loader;

import nsk.nu.blackstone.Entity.DTO.DScenario;
import nsk.nu.blackstone.Interface.BlackstoneModule;
import nsk.nu.blackstone.Modules.Lifesteal.LifestealModule;
import nsk.nu.blackstone.PluginInstance;
import nsk.nu.blackstone.Service.ClientService;
import nsk.nu.blackstone.Service.ScenarioService;
import nsk.nu.dev.Logs.LogLevels;
import nsk.nu.dev.NskLogger;

import java.io.File;
import java.util.*;

public class ModuleLoader {

    private final NskLogger logger = NskLogger.get();

    private final ScenarioService scenarioService;
    private final ClientService clientService;
    private final Map<String, List<BlackstoneModule<?>>> modulesByScenario = new HashMap<>();

    public ModuleLoader(ScenarioService scenarioService, ClientService clientService) {
        this.scenarioService = scenarioService;
        this.clientService = clientService;
    }

    public void loadModulesForAllScenarios() {
        List<DScenario> scenarios = scenarioService.getAllAsync().join();
        for (DScenario scenario : scenarios) {
            List<BlackstoneModule<?>> modules = loadModulesForScenario(scenario);
            modulesByScenario.put(scenario.getName(), modules);
        }
    }

    public List<BlackstoneModule<?>> loadModulesForScenario(DScenario scenario) {
        List<BlackstoneModule<?>> modules = new ArrayList<>();

        File parent = new File(PluginInstance.getInstance().getDataFolder(), "scenarios/" + scenario.getName());
        if (!parent.isDirectory()) {
            modulesByScenario.put(scenario.getName(), modules);
            return modules;
        };

        File[] files = parent.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return modules;

        for (File file : files) {
            String moduleName = file.getName().replace(".yml", "");
            BlackstoneModule<?> module = createModuleInstance(moduleName, scenario);
            if (module != null && module.isEnabled()) {
                modules.add(module);
            }
        }

        modulesByScenario.put(scenario.getName(), modules);
        return modules;
    }

    private BlackstoneModule<?> createModuleInstance(String moduleName, DScenario scenario) {
        switch (moduleName.toLowerCase()) {
            case "lifesteal":
                LifestealModule lifesteal = new LifestealModule(scenarioService, clientService, moduleName);
                lifesteal.initialize(scenario);
                return lifesteal;

            default:
                return null;
        }
    }

    public List<BlackstoneModule<?>> getModulesForScenario(String scenarioName) {
        return modulesByScenario.getOrDefault(scenarioName, new ArrayList<>());
    }

    public void unloadModulesForScenario(String scenarioName) {
        List<BlackstoneModule<?>> modules = modulesByScenario.remove(scenarioName);
        if (modules == null) return;

        for (BlackstoneModule<?> module : modules) {
            try { module.disable();
            } catch (Exception e) {
                logger.log(LogLevels.SEVERE, "Failed to disable module " + module.name() + ", of scenario " + scenarioName + ": " + e.getMessage());
            }
        }

    }

    public void unloadAll() {
        for (String scenarioName : new HashSet<>(modulesByScenario.keySet())) {
            unloadModulesForScenario(scenarioName);
        }
    }

}
