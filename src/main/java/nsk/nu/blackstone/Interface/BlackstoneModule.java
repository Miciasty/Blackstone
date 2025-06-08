package nsk.nu.blackstone.Interface;

import nsk.nu.blackstone.Entity.DTO.DScenario;
import nsk.nu.blackstone.Service.ClientService;
import nsk.nu.blackstone.Service.ScenarioService;
import org.bukkit.configuration.file.FileConfiguration;

public interface BlackstoneModule<S> {

    void initialize(S scenario);
    void reload();

    void loadConfig();
    FileConfiguration getConfig();

    String name();
    DScenario getScenario();
    ScenarioService getScenarioService();
    ClientService getClientService();

    boolean isEnabled();

    void enable();
    void disable();
}
